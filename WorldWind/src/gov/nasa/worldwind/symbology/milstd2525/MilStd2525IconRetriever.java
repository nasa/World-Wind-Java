/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * @author ccrick
 * @version $Id: MilStd2525IconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public class MilStd2525IconRetriever extends AbstractIconRetriever
{
    protected static final String PATH_SUFFIX = ".png";
    protected static final String OVERLAY_PATH_PREFIX = "overlays/";
    protected static final Map<String, String> schemeToPathPrefixMap = new HashMap<String, String>();

    static
    {
        schemeToPathPrefixMap.put("S", "war/"); // Tactical Graphics scheme.
        schemeToPathPrefixMap.put("I", "sigint/"); // Signals Intelligence scheme.
        schemeToPathPrefixMap.put("O", "stbops/");// Stability Operations scheme.
        schemeToPathPrefixMap.put("E", "ems/"); // Emergency Management scheme.
    }

    // TODO: add more error checking

    public MilStd2525IconRetriever(String retrieverPath)
    {
        super(retrieverPath);
    }

    public BufferedImage createIcon(String symbolIdentifier)
    {
        if (symbolIdentifier == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        AVListImpl params = new AVListImpl();

        return createIcon(symbolIdentifier, params);
    }

    public BufferedImage createIcon(String symbolIdentifier, AVList params)
    {
        if (symbolIdentifier == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        boolean showFrame = true;
        if (params != null && params.getValue(SymbologyConstants.SHOW_FRAME) != null)
            showFrame = (Boolean) params.getValue(SymbologyConstants.SHOW_FRAME);

        boolean showFill = true;
        if (params != null && params.getValue(SymbologyConstants.SHOW_FILL) != null)
            showFill = (Boolean) params.getValue(SymbologyConstants.SHOW_FILL);

        boolean showIcon = true;
        if (params != null && params.getValue(SymbologyConstants.SHOW_ICON) != null)
            showIcon = (Boolean) params.getValue(SymbologyConstants.SHOW_ICON);

        // return null if neither frame nor icon are showing
        if (!showFrame && !showIcon)
            return null;

        // if Icon visibility is OFF, use symbolIdentifier sans function ID
        // TODO: opportunity for optimization here
        if (!showIcon)
        {
            char battleDim = symbolIdentifier.charAt(2);
            if (battleDim == 'g' || battleDim == 'G')   // Ground Battle Dimension
            {
                symbolIdentifier = symbolIdentifier.substring(0, 5) + "----------";
                // add 'H' modifier to indicate an installation
                if (symbolIdentifier.charAt(4) == 'i' || symbolIdentifier.charAt(4) == 'I')
                    symbolIdentifier = symbolIdentifier.substring(0, 5) + "-----H----";
            }
            else
            {
                symbolIdentifier = symbolIdentifier.substring(0, 4) + "-----------";
            }
        }

        // retrieve desired symbol and convert to bufferedImage
        SymbolCode symbolCode = new SymbolCode(symbolIdentifier);

        String path = this.composeIconPath(symbolCode, params);
        BufferedImage img = this.retrieveImageFromURL(path, null);

        if (img == null)
        {
            String msg = Logging.getMessage("Symbology.SymbolIconNotFound", symbolCode);
            Logging.logger().severe(msg);
            throw new MissingResourceException(msg, BufferedImage.class.getName(), path);
        }

        // if unframed, remove frame where necessary
        if (!showFrame)
        {
            // Check if image actually does have a frame to remove, i.e. the prefix # is < 4.
            // Ignore special cases of <Warfighting, Sea Surface, Own Track> and <Warfighting, Subsurface, Non-submarine Diver> icons,
            // which have no fill or black outline
            String filename = WWIO.getFilename(path);
            if (Integer.parseInt(filename.substring(0, 1)) < 4 &&
                symbolCode.getFunctionId() != null &&
                !(symbolCode.getScheme().equals("S") &&
                    symbolCode.getBattleDimension().equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE) &&
                    symbolCode.getFunctionId().equals("O-----")) &&
                !(symbolCode.getScheme().equals("S") &&
                    symbolCode.getBattleDimension().equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE) &&
                    symbolCode.getFunctionId().equals("ND----")))
            {
                // remove the frame
                // 1. retrieve the relevant overlay to use as a transparency mask
                BufferedImage mask = this.retrieveOverlay(symbolCode);

                // 2. make the icon border transparent
                img = applyInverseTransparencyMask(img, mask);

                // handle the special cases of <Warfighting, Subsurface, Sea Mine>,
                // <Warfighting, Subsurface, Underwater Decoy, Sea Mine Decoy>, and S*UPE, S*UPV, S*UPX
                // which have a standard identity-colored frame and no fill color.
                if (symbolCode.getScheme().equals("S") &&
                    symbolCode.getBattleDimension().equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE) &&
                    symbolCode.getFunctionId() != null &&
                    (symbolCode.getFunctionId().substring(0, 2).equals("WM") ||
                        symbolCode.getFunctionId().substring(0, 3).equals("WDM") ||
                        symbolCode.getFunctionId().equals("E-----") ||
                        symbolCode.getFunctionId().equals("V-----") ||
                        symbolCode.getFunctionId().equals("X-----")))
                {
                    // 3. change the icon color to match the standard identity
                    img = changeIconFillColor(img,
                        symbolCode.getStandardIdentityColor(symbolCode.getStandardIdentity()));
                }
                else
                {
                    // 3. remove the icon fill (if present)
                    img = removeIconFillColor(img);

                    // 4. change the icon outline color to the color that corresponds
                    // to its Standard Identity (usually the fill color, except when Joker or Faker)
                    img = changeIconOutlineColor(img,
                        symbolCode.getStandardIdentityColor(symbolCode.getStandardIdentity()));
                }
            }
        }       // Framed, but no Fill
        // handle special cases of S*UPE, S*UPV, and S*UPX
        // ignore special case S*SPO.
        else if (!showFill)
        {
            if (symbolCode.getScheme().equals("S") &&
                symbolCode.getBattleDimension().equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE) &&
                symbolCode.getFunctionId() != null &&
                (symbolCode.getFunctionId().equals("E-----") ||
                    symbolCode.getFunctionId().equals("V-----") ||
                    symbolCode.getFunctionId().equals("X-----")))
            {
                img = changeIconFillColor(img,
                    symbolCode.getStandardIdentityColor(symbolCode.getStandardIdentity()));
            }
            else if (!(symbolCode.getScheme().equals("S") &&
                symbolCode.getBattleDimension().equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE) &&
                symbolCode.getFunctionId() != null &&
                symbolCode.getFunctionId().equals("O-----")))
            {
                // 1. remove fill color
                img = removeIconFillColor(img);

                // 2. change frame & icon to fill color
                img = changeIconOutlineColor(img,
                    symbolCode.getStandardIdentityColor(symbolCode.getStandardIdentity()));
            }
        }

        // apply dotted border where required by Standard Identity (cases P, A, S, G, M)
        String stdid = symbolCode.getStandardIdentity();
        if ("PASGMpasgm".indexOf(stdid.charAt(0)) > -1)
        {
            BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dest.createGraphics();
            g.setComposite(AlphaComposite.SrcOver);

            if (!showFrame)
            {
                // set background color to grey
                g.setBackground(new Color(128, 128, 128, 255));
                g.clearRect(0, 0, dest.getWidth(), dest.getHeight());
                g.drawImage(img, 0, 0, null);
                g.dispose();
            }
            else
            {
                // set background color to clear
                g.setBackground(new Color(0, 0, 0, 0));
                g.clearRect(0, 0, dest.getWidth(), dest.getHeight());
                g.drawImage(img, 0, 0, null);

                // now overlay dotted line
                BufferedImage overlay = this.retrieveOverlay(symbolCode);
                g.drawImage(overlay, 0, 0, null);
                g.dispose();
            }

            img = dest;
        }

        // handle Joker and Faker, which should have a Friend frame, but with fill color red
        if ("JKjk".indexOf(stdid.charAt(0)) > -1)
        {
            img = changeIconFillColor(img, SymbologyConstants.COLOR_LIGHT_RED);
        }

        // TODO: remove this code
        // if exercise, add exercise amplifying overlay
        if (stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_PENDING) ||
            stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_UNKNOWN) ||
            stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_FRIEND) ||
            stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_NEUTRAL) ||
            stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_ASSUMED_FRIEND) ||
            stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_JOKER) ||
            stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_FAKER))
        {
            String overlayPath = this.composeExerciseAmplifyingOverlayPath(symbolCode);
            BufferedImage overlay = this.retrieveImageFromURL(overlayPath, null);

            if (overlay == null)
            {
                String msg = Logging.getMessage("Symbology.SymbolIconOverlayNotFound", symbolCode);
                Logging.logger().severe(msg);
                throw new MissingResourceException(msg, BufferedImage.class.getName(), overlayPath);
            }

            Graphics2D g = img.createGraphics();
            g.setComposite(AlphaComposite.SrcOver);
            g.drawImage(overlay, 0, 0, null);
            g.dispose();
        }

        return img;
    }

    protected BufferedImage retrieveOverlay(SymbolCode symbolCode)
    {
        String path = this.composeOverlayPath(symbolCode);
        BufferedImage img = this.retrieveImageFromURL(path, null);

        if (img == null)
        {
            String msg = Logging.getMessage("Symbology.SymbolIconOverlayNotFound", path);
            Logging.logger().severe(msg);
            throw new MissingResourceException(msg, BufferedImage.class.getName(), path);
        }

        return img;
    }

    protected String composeIconPath(SymbolCode code, AVList params)
    {
        String scheme = code.getScheme();
        String functionID = code.getFunctionId();
        String battleDim = code.getBattleDimension();
        String standardID = code.getStandardIdentity();
        standardID = standardID.toLowerCase();

        int prefix = 0;
        char stdid = 'u';
        // See Table I and TABLE II, p.15 of MIL-STD-2525C for standard identities that use
        // similarly shaped frames.
        switch (standardID.charAt(0))
        {
            case 'p':      // PENDING
            case 'u':      // UNKNOWN
            case 'g':      // EXERCISE PENDING
            case 'w':      // EXERCISE UNKNOWN
                prefix = 0;
                stdid = 'u';
                break;
            case 'f':      // FRIEND
            case 'a':      // ASSUMED FRIEND
            case 'd':      // EXERCISE FRIEND
            case 'm':      // EXERCISE ASSUMED FRIEND
            case 'j':      // JOKER
            case 'k':      // FAKER
                prefix = 1;
                stdid = 'f';
                break;
            case 'n':      // NEUTRAL
            case 'l':      // EXERCISE NEUTRAL
                prefix = 2;
                stdid = 'n';
                break;
            case 'h':      // HOSTILE
            case 's':      // SUSPECT
                prefix = 3;
                stdid = 'h';
                break;
            default:
                String msg = Logging.getMessage("Symbology.InvalidSymbolCode", standardID);
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
        }

        // Unframed icons:
        boolean showFrame = true;
        if (params != null && params.getValue(SymbologyConstants.SHOW_FRAME) != null)
            showFrame = (Boolean) params.getValue(SymbologyConstants.SHOW_FRAME);

        // Some icons already have an unframed version available. In these cases, you must
        // add 4 to the prefix to get the unframed version of the icon.
        // In all other cases, the unframed version must be derived later from the framed icon.
        if (!showFrame && functionID != null)
        {
            if (SymbologyConstants.SCHEME_WARFIGHTING.equals(scheme) &&
                // Warfighting, Ground, Equipment (S*GPE)
                ((SymbologyConstants.BATTLE_DIMENSION_GROUND.equals(battleDim) && functionID.charAt(0) == 'E') ||
                    // Warfighting, Sea Surface, Nonmilitary (S*SPX)
                    (SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE.equals(battleDim) && functionID.charAt(0) == 'X')))
            {
                prefix += 4;
            }
        }

        // virtually all icon filenames have no Modifiers, Country Code or Order of Battle indicated
        // and so their last 5 digits should just be padded with dashes.
        String padding = "-----";
        // installations are an exception to this rule, and are indicated by a 'H' in position 11
        // (except for Emergency Management icons, which do not have the 'H' in the filename)
        if (code.getSymbolModifier() != null && "Hh".indexOf((code.getSymbolModifier()).charAt(0)) > -1
            && !SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT.equals(scheme))
        {
            padding = "h----";
        }

        // position 3 in the filename indicates the icon's  Battle Dimension
        String pos3 = battleDim;
        // Stability Operations and Emergency Management schemes
        // use Category instead of Battle Dimension
        if (SymbologyConstants.SCHEME_STABILITY_OPERATIONS.equalsIgnoreCase(scheme) ||
            SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT.equalsIgnoreCase(scheme))
        {
            pos3 = code.getCategory();
        }

        String functionId = code.getFunctionId();
        if (functionId == null)
            functionId = "------";

        StringBuilder sb = new StringBuilder();

        String s = schemeToPathPrefixMap.get(code.getScheme().toUpperCase());
        if (s != null)
            sb.append(s);

        sb.append(prefix);
        sb.append(".");
        sb.append(code.getScheme().toLowerCase());
        sb.append(stdid);
        sb.append(pos3.toLowerCase());
        sb.append("p");
        sb.append(functionId.toLowerCase());
        sb.append(padding);
        sb.append(PATH_SUFFIX);

        return sb.toString();
    }

    protected String composeOverlayPath(SymbolCode symbolCode)
    {
        String filename = null;
        String scheme = symbolCode.getScheme();
        String stdID = symbolCode.getStandardIdentity();
        String battleDim = symbolCode.getBattleDimension();
        String functionID = symbolCode.getFunctionId();
        String symbolModifier = symbolCode.getSymbolModifier();

        if (stdID.equals(SymbologyConstants.STANDARD_IDENTITY_PENDING) ||
            stdID.equals(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_PENDING) ||
            stdID.equals(SymbologyConstants.STANDARD_IDENTITY_UNKNOWN) ||
            stdID.equals(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_UNKNOWN))
        {
            if (battleDim == null)
                filename = "clover_overlay";
            else if (battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN) ||
                battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND) ||
                battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE) ||
                battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SOF))
            {
                // 1. clover
                filename = "clover_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_AIR))
            {
                // 2. cloverTop
                filename = "clovertop_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SPACE))
            {
                // 2b. black-topped cloverTop
                filename = "blacktop_clovertop_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE))
            {
                // 3. cloverBottom
                filename = "cloverbottom_overlay";
            }
        }
        else if
            (stdID.equals(SymbologyConstants.STANDARD_IDENTITY_ASSUMED_FRIEND) ||
                stdID.equals(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_ASSUMED_FRIEND) ||
                stdID.equals(SymbologyConstants.STANDARD_IDENTITY_FRIEND) ||
                stdID.equals(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_FRIEND) ||
                stdID.equals(SymbologyConstants.STANDARD_IDENTITY_JOKER) ||
                stdID.equals(SymbologyConstants.STANDARD_IDENTITY_FAKER))
        {
            if (battleDim == null)
                filename = "rectangle_overlay";
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN) ||
                battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE))
            {
                // 4. circle
                filename = "circle_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_AIR))
            {
                // 5. arch
                filename = "arch_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SPACE))
            {
                // 5b. black-topped arch
                filename = "blacktop_arch_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE))
            {
                // 6. smile
                filename = "smile_overlay";
            }
            else if
                (battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND))
            {
                if (functionID != null
                    && "E".equalsIgnoreCase(functionID.substring(0, 1)))   // special case of Ground Equipment
                {
                    // 4. circle
                    filename = "circle_overlay";
                }
                else        // Units and Installations
                {
                    // 7. rectangle
                    filename = "rectangle_overlay";
                }
            }
            else if
                (battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SOF))
            {
                // 7. rectangle
                filename = "rectangle_overlay";
            }
        }
        else if (stdID.equals(SymbologyConstants.STANDARD_IDENTITY_SUSPECT) ||
            stdID.equals(SymbologyConstants.STANDARD_IDENTITY_HOSTILE))
        {
            if (battleDim == null)
                filename = "diamond_overlay";
            else if (battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN) ||
                battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND) ||
                battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE) ||
                battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SOF))
            {
                // 8. diamond
                filename = "diamond_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_AIR))
            {
                // 9. tent
                filename = "tent_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SPACE))
            {
                // 9b. black-topped tent
                filename = "blacktop_tent_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE))
            {
                // 10. top
                filename = "top_overlay";
            }
        }
        else if (stdID.equals(SymbologyConstants.STANDARD_IDENTITY_NEUTRAL) ||
            stdID.equals(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_NEUTRAL))
        {
            if (battleDim == null)
                filename = "square_overlay";
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN) ||
                battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_GROUND) ||
                battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE) ||
                battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SOF))
            {
                // 11. square
                filename = "square_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_AIR))
            {
                // 12. hat
                filename = "hat_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SPACE))
            {
                // 12b. black-topped hat
                filename = "blacktop_hat_overlay";
            }
            else if (battleDim.equals(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE))
            {
                // 13. bucket
                filename = "bucket_overlay";
            }
        }

        // handle the special case of overlays for installations (Ground battle dimension only)
        if (battleDim != null && battleDim.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
            && functionID != null && "I".equalsIgnoreCase(functionID.substring(0, 1)))
        {
            filename = "installation_" + filename;
        }
        // handle case of Emergency Management installations, which have a Category but no Battle Dimension
        else if (SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT.equals(scheme) &&
            symbolModifier.substring(0, 1).equals("H"))
        {
            filename = "installation_" + filename;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(OVERLAY_PATH_PREFIX);
        sb.append(filename);
        sb.append(PATH_SUFFIX);

        return sb.toString();
    }

    protected String composeExerciseAmplifyingOverlayPath(SymbolCode symbolCode)
    {
        String stdid = symbolCode.getStandardIdentity();

        String filename;
        if (stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_JOKER))
            filename = "j_overlay";
        else if (stdid.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_FAKER))
            filename = "k_overlay";
        else
            filename = "x_overlay";

        StringBuilder sb = new StringBuilder();
        sb.append(OVERLAY_PATH_PREFIX);
        sb.append(filename);
        sb.append(PATH_SUFFIX);

        return sb.toString();
    }
}

