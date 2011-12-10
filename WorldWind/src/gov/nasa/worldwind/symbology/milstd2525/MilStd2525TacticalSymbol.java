/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.j2d.TextRenderer;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.ogc.kml.impl.KMLUtil;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.*;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.xml.xpath.XPath;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.List;

/**
 * Implementation of {@link gov.nasa.worldwind.symbology.TacticalSymbol} that provides support for tactical symbols from
 * the <a href="http://www.assistdocs.com/search/document_details.cfm?ident_number=114934">MIL-STD-2525</a> symbology
 * set. See the TacticalSymbol <a title="Tactical Symbol Usage Guide" href="http://goworldwind.org/developers-guide/symbology/tactical-symbols/"
 * target="_blank">Usage Guide</a> for instructions on using TacticalSymbol in an application.
 * <p/>
 * <strong>Note</strong>: MilStd2525TacticalSymbol is currently an in-development stub class, and does not yet implement
 * the TacticalSymbol interface or any of its functionality.
 *
 * @author dcollins
 * @version $Id$
 */
public class MilStd2525TacticalSymbol extends AbstractTacticalSymbol
{
    protected static class MilStd2525ModifierRetriever implements ModifierRetriever
    {
        protected static final String DEFAULT_FORMAT_SUFFIX = ".png";

        protected URL baseUrl;
        protected String formatSuffix = DEFAULT_FORMAT_SUFFIX;

        public MilStd2525ModifierRetriever(String baseUrl)
        {
            if (baseUrl == null)
            {
                String msg = Logging.getMessage("nullValue.URLIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            URL url = WWIO.makeURL(baseUrl);
            if (url == null)
            {
                String msg = Logging.getMessage("generic.MalformedURL", baseUrl);
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.baseUrl = url;
        }

        public BufferedImage createModifier(String modifier, Object value)
        {
            if (modifier == null)
            {
                String msg = Logging.getMessage("nullValue.ModifierIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            // Create a filename from the modifier and value.
            String filename = this.computeFilename(modifier, value);
            if (filename == null)
                return null;

            try
            {
                // Resolve the base URL and the filename to an absolute URL, then use the ImageIO utility to retrieve
                // and load the image at the resolved URL.
                URL url = this.baseUrl.toURI().resolve(filename).toURL();
                return ImageIO.read(url);
            }
            catch (Exception e)
            {
                String msg = Logging.getMessage("Symbology.ExceptionRetrievingGraphicModifier", filename);
                Logging.logger().severe(msg);
                return null;
            }
        }

        protected String computeFilename(String modifier, Object value)
        {
            StringBuilder sb = new StringBuilder();

            if (SymbologyConstants.ECHELON.equalsIgnoreCase(modifier))
            {
                if (WWUtil.isEmpty(value))
                {
                    String msg = Logging.getMessage("Symbology.InsufficientValuesForModifier", modifier);
                    Logging.logger().severe(msg);
                    throw new IllegalArgumentException(msg);
                }

                sb.append("echelon_").append(value.toString().toLowerCase());
            }
            else if (SymbologyConstants.TASK_FORCE.equalsIgnoreCase(modifier))
            {
                sb.append("task_force");

                if (!WWUtil.isEmpty(value))
                    sb.append("_").append(value.toString().toLowerCase());
            }
            else if (SymbologyConstants.MOBILITY.equalsIgnoreCase(modifier))
            {
                if (WWUtil.isEmpty(value))
                {
                    String msg = Logging.getMessage("Symbology.InsufficientValuesForModifier", modifier);
                    Logging.logger().severe(msg);
                    throw new IllegalArgumentException(msg);
                }

                sb.append("mobility_").append(value.toString().toLowerCase());
            }
            else if (SymbologyConstants.FEINT_DUMMY.equalsIgnoreCase(modifier))
            {
                if (WWUtil.isEmpty(value))
                {
                    String msg = Logging.getMessage("Symbology.InsufficientValuesForModifier", modifier);
                    Logging.logger().severe(msg);
                    throw new IllegalArgumentException(msg);
                }

                sb.append("feint_dummy_").append(value.toString().toLowerCase());
            }
            else if (SymbologyConstants.INSTALLATION.equalsIgnoreCase(modifier))
            {
                sb.append("installation");
            }
            else if (SymbologyConstants.AUXILIARY_EQUIPMENT.equalsIgnoreCase(modifier))
            {
                if (WWUtil.isEmpty(value))
                {
                    String msg = Logging.getMessage("Symbology.InsufficientValuesForModifier", modifier);
                    Logging.logger().severe(msg);
                    throw new IllegalArgumentException(msg);
                }

                sb.append("auxiliary_equipment_").append(value.toString().toLowerCase());
            }
            else if (SymbologyConstants.STATUS.equalsIgnoreCase(modifier))
            {
                if (WWUtil.isEmpty(value))
                {
                    String msg = Logging.getMessage("Symbology.InsufficientValuesForModifier", modifier);
                    Logging.logger().severe(msg);
                    throw new IllegalArgumentException(msg);
                }

                sb.append("status_").append(value.toString().toLowerCase());
            }
            else
            {
                return null;
            }

            sb.append(this.formatSuffix);

            return sb.toString();
        }
    }

    protected static class SymbolFrameType
    {
        protected Rectangle2D frameRect;
        protected Double frameWidthFactor;
        protected List<String> standardIdentities;
        protected List<String> battleDimensions;

        public SymbolFrameType(Rectangle2D frameRect, Double frameWidthFactor, List<String> standardIdentities,
            List<String> battleDimensions)
        {
            this.frameRect = frameRect;
            this.frameWidthFactor = frameWidthFactor;
            this.standardIdentities = standardIdentities;
            this.battleDimensions = battleDimensions;
        }

        public Rectangle2D getFrameRect()
        {
            return this.frameRect;
        }

        public Double getFrameWidthFactor()
        {
            return this.frameWidthFactor;
        }

        public List<String> getStandardIdentities()
        {
            return this.standardIdentities;
        }

        public List<String> getBattleDimensions()
        {
            return this.battleDimensions;
        }
    }

    protected static class ModifierLayout
    {
        protected String type;
        protected Offset offset;
        protected Offset hotspot;
        protected List<String> fieldIds;
        protected List<String> schemes;

        public ModifierLayout(String type, Offset offset, Offset hotspot, List<String> fieldIds, List<String> schemes)
        {
            this.type = type;
            this.offset = offset;
            this.hotspot = hotspot;
            this.fieldIds = fieldIds;
            this.schemes = schemes;
        }

        public String getType()
        {
            return this.type;
        }

        public Offset getOffset()
        {
            return this.offset;
        }

        public Offset getHotspot()
        {
            return this.hotspot;
        }

        public List<String> getFieldIds()
        {
            return this.fieldIds;
        }

        public List<String> getSchemes()
        {
            return this.schemes;
        }
    }

    protected static final String DEFAULT_RETRIEVER_BASE_URL = "http://worldwindserver.net/milstd2525/";
    /** Note that we use a static default retriever instance in order to cache the results it returns. */
    protected static final IconRetriever DEFAULT_ICON_RETRIEVER = new MilStd2525IconRetriever(
        DEFAULT_RETRIEVER_BASE_URL);
    /** Note that we use a static default retriever instance in order to cache the results it returns. */
    protected static final ModifierRetriever DEFAULT_MODIFIER_RETRIEVER = new MilStd2525ModifierRetriever(
        DEFAULT_RETRIEVER_BASE_URL);
    // TODO: make this configurable
    // Create a texture atlas with the default initial size and maximum size. The maximum size is either the default
    // maximum size or the maximum texture size, whichever is less.
    protected static final TextureAtlas DEFAULT_MODIFIER_ATLAS = new TextureAtlas(128, 128, 2048, 2048);
    protected static final double FONT_SIZE_FACTOR = 0.15;
    // TODO: make this configurable
    protected static final String SYMBOL_FRAME_TYPE_CONFIG_FILE = "config/milstd2525/MilStd2525SymbolFrameTypes.xml";
    // TODO: make this configurable
    protected static final String MODIFIER_LAYOUT_CONFIG_FILE = "config/milstd2525/MilStd2525SymbolModifiers.xml";

    protected static List<SymbolFrameType> symbolFrameTypes;
    protected static List<ModifierLayout> modifierLayouts;
    protected static boolean symbolFrameTypeReadFailed;
    protected static boolean modifierLayoutReadFailed;

    static
    {
        // Configure the atlas to remove old modifier elements that are likely no longer used to make room for new
        // modifiers when the atlas is full.
        DEFAULT_MODIFIER_ATLAS.setEvictOldElements(true);
    }

    /**
     * Indicates a string identifier for this symbol. The format of the identifier depends on the symbol set to which
     * this symbol belongs. For symbols belonging to the MIL-STD-2525 symbol set, this returns a 15-character
     * alphanumeric symbol identification code (SIDC). Calculated from the current modifiers at construction and during
     * each call to {@link #setModifier(String, Object)}. Initially <code>null</code>.
     */
    protected SymbolCode symbolCode;
    protected SymbolFrameType symbolFrameType;
    protected FloatBuffer directionOfMovementPoints;

    /**
     * Constructs a tactical symbol for the MIL-STD-2525 symbology set with the specified symbol identifier and
     * position. This constructor does not accept any supplemental modifiers, so the symbol contains only the attributes
     * specified by its symbol identifier.
     * <p/>
     * The symbolId specifies the tactical symbol's appearance. The symbolId must be a 15-character alphanumeric symbol
     * identification code (SIDC). The symbol's shape, fill color, outline color, and icon are all defined by the symbol
     * identifier. Use the '-' character to specify null entries in the symbol identifier.
     * <p/>
     * The position specifies the latitude, longitude, and altitude where the symbol is drawn on the globe. The
     * position's altitude component is interpreted according to the altitudeMode.
     *
     * @param symbolId a 15-character alphanumeric symbol identification code (SIDC).
     * @param position the latitude, longitude, and altitude where the symbol is drawn.
     *
     * @throws IllegalArgumentException if either the symbolId or the position are <code>null</code>, or if the symbolId
     *                                  is not a valid 15-character alphanumeric symbol identification code (SIDC).
     */
    public MilStd2525TacticalSymbol(String symbolId, Position position)
    {
        super(position);

        this.init(symbolId, null);
    }

    /**
     * Constructs a tactical symbol for the MIL-STD-2525 symbology set with the specified symbol identifier, position,
     * and list of modifiers.
     * <p/>
     * The symbolId specifies the tactical symbol's appearance. The symbolId must be a 15-character alphanumeric symbol
     * identification code (SIDC). The symbol's shape, fill color, outline color, and icon are all defined by the symbol
     * identifier. Use the '-' character to specify null entries in the symbol identifier.
     * <p/>
     * The position specifies the latitude, longitude, and altitude where the symbol is drawn on the globe. The
     * position's altitude component is interpreted according to this symbol's altitudeMode.
     * <p/>
     * The modifiers specify supplemental graphic and text attributes as key-value pairs. See the
     * MilStd2525TacticalSymbol class documentation for the list of recognized modifiers. In the case where both the
     * symbol identifier and the modifiers list specify the same attribute, the modifiers list has priority.
     *
     * @param symbolId  a 15-character alphanumeric symbol identification code (SIDC).
     * @param position  the latitude, longitude, and altitude where the symbol is drawn.
     * @param modifiers an optional list of key-value pairs specifying the symbol's modifiers. May be <code>null</code>
     *                  to specify that the symbol contains only the attributes in its symbol identifier.
     *
     * @throws IllegalArgumentException if either the symbolId or the position are <code>null</code>, or if the symbolId
     *                                  is not a valid 15-character alphanumeric symbol identification code (SIDC).
     */
    public MilStd2525TacticalSymbol(String symbolId, Position position, AVList modifiers)
    {
        super(position);

        this.init(symbolId, modifiers);
    }

    protected void init(String symbolId, AVList modifiers)
    {
        this.symbolCode = new SymbolCode(symbolId);

        // Apply the modifier key-value pairs from the symbol code and the caller-specified modifiers to our internal
        // modifier list. We apply the caller-specified modifiers last to give them precedence.
        this.symbolCode.getSymbolModifierParams(this.modifiers);
        if (modifiers != null)
            this.modifiers.setValues(modifiers);

        this.symbolFrameType = this.computeSymbolFrameType(this.symbolCode);

        // Initialize this tactical symbol's altitude mode and offset according to its scheme and battle dimension. This
        // step configures ground symbols to attach their position to the terrain surface, regardless of the position's
        // altitude value.
        Integer defaultAltitudeMode = this.computeDefaultAltitudeMode(this.symbolCode);
        if (defaultAltitudeMode != null)
            this.setAltitudeMode(defaultAltitudeMode);

        // TODO: there's likely a better way to share default values.
        // Configure this tactical symbol with the default tactical icon retriever, the default modifier retriever,
        // and the default modifier atlas.
        this.setIconRetriever(DEFAULT_ICON_RETRIEVER);
        this.setModifierRetriever(DEFAULT_MODIFIER_RETRIEVER);
        this.setModifierAtlas(DEFAULT_MODIFIER_ATLAS);
    }

    protected Integer computeDefaultAltitudeMode(SymbolCode symbolCode)
    {
        String scheme = symbolCode.getScheme();
        if (scheme != null && (scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING)
            || scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_INTELLIGENCE)))
        {
            // Tactical symbols from the unit, equipment, and installation (UEI) symbology set and the signals
            // intelligence (SIGINT) symbology set contain symbols located in the following dimensions: air, ground, sea
            // surface, sea subsurface, and special operations forces (SOF). We set the altitude mode to
            // WorldWind.CLAMP_TO_GROUND for ground symbols, and to WorldWind.ABSOLUTE for everything else.
            String s = symbolCode.getBattleDimension();
            if (s != null && s.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND))
                return WorldWind.CLAMP_TO_GROUND;
        }
        else if (scheme != null && (scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_STABILITY_OPERATIONS)
            || scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT)))
        {
            // Tactical symbols from the Stability Operations (SO) symbology set and the Emergency Management (EM)
            // symbology set contain symbols that are implicitly located in the ground dimension. We set the altitude
            // mode to WorldWind.CLAMP_TO_GROUND for all symbols in these sets.
            return WorldWind.CLAMP_TO_GROUND;
        }

        return null;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.symbolCode.toString();
    }

    /**
     * Indicates whether this symbol draws its frame and icon. See {@link #setShowFrameAndIcon(boolean)} for a
     * description of how this property is used.
     *
     * @return true if this symbol draws its frame and icon, otherwise false.
     */
    public boolean isShowFrameAndIcon()
    {
        return false; // TODO: replace with separate controls: isShowFrame, isShowFill, isShowIcon
    }

    /**
     * Specifies whether to draw this symbol's frame and icon. The showFrameAndIcon property provides control over this
     * tactical symbol's display option hierarchy as defined by MIL-STD-2525C, section 5.4.5 and table III.
     * <p/>
     * When true, this symbol's frame, icon, and fill are drawn, and any enabled modifiers are drawn on and around the
     * frame. This state corresponds to MIL-STD-2525C, table III, row 1.
     * <p/>
     * When false, this symbol's frame, icon, and modifiers are not drawn. Instead, a filled dot is drawn at this
     * symbol's position, and is colored according to this symbol's normal fill color. The TacticalSymbolAttributes'
     * scale property specifies the dot's diameter in screen pixels. This state corresponds to MIL-STD-2525C, table III,
     * row 7.
     *
     * @param showFrameAndIcon true to draw this symbol's frame and icon, otherwise false.
     */
    public void setShowFrameAndIcon(boolean showFrameAndIcon)
    {
        // TODO: replace with separate controls: setShowFrame, setShowFill, setShowIcon
    }

    @Override
    protected void layout(DrawContext dc)
    {
        super.layout(dc);

        if (this.mustDrawGraphicModifiers(dc))
            this.layoutStaticGraphics(dc);
    }

    @Override
    protected void layoutDynamicModifiers(DrawContext dc)
    {
        super.layoutDynamicModifiers(dc);

        if (this.mustDrawDirectionOfMovement(dc))
            this.layoutDirectionOfMovement(dc);
    }

    @Override
    protected Rectangle computeIconFrameRect(int imageWidth, int imageHeight)
    {
        if (this.symbolFrameType != null && this.symbolFrameType.getFrameRect() != null)
        {
            Rectangle2D scaleRect = this.symbolFrameType.getFrameRect();
            double x = scaleRect.getX() * imageWidth;
            double y = scaleRect.getY() * imageHeight;
            double w = scaleRect.getWidth() * imageWidth;
            double h = scaleRect.getHeight() * imageHeight;

            return new Rectangle((int) x, (int) y, (int) w, (int) h);
        }
        else
        {
            return super.computeIconFrameRect(imageWidth, imageHeight);
        }
    }

    protected void layoutStaticGraphics(DrawContext dc)
    {
        if (this.iconTexture == null || this.iconTexture.frameRect == null)
            return;

        Rectangle frameRect = this.iconTexture.frameRect;

        // Layout all of the graphic modifiers around the symbol's frame bounds. The location of each modifier is the
        // same regardless of whether the symbol is framed or unframed. See MIL-STD-2525C section 5.4.4., page 34.

        // Echelon.
        Object o = this.modifiers.getValue(SymbologyConstants.ECHELON);
        if (o != null && SymbologyConstants.ECHELON_ALL.contains(o.toString().toUpperCase()))
            this.layoutStaticGraphic(dc, SymbologyConstants.ECHELON, o, frameRect);

        // Task Force Indicator.
        o = this.modifiers.getValue(SymbologyConstants.TASK_FORCE);
        if (o != null && Boolean.TRUE.equals(o))
            this.layoutStaticGraphic(dc, SymbologyConstants.TASK_FORCE,
                this.modifiers.getValue(SymbologyConstants.ECHELON), frameRect);

        // Mobility Indicator.
        o = this.modifiers.getValue(SymbologyConstants.MOBILITY);
        if (o != null && SymbologyConstants.MOBILITY_ALL.contains(o.toString().toUpperCase()))
            this.layoutStaticGraphic(dc, SymbologyConstants.MOBILITY, o, frameRect);

        // Feint/Dummy Indicator.
        o = this.modifiers.getValue(SymbologyConstants.FEINT_DUMMY);
        if (o != null && Boolean.TRUE.equals(o) && this.symbolFrameType != null)
        {
            String frameWidthClass = Integer.toString((int) (100 * this.symbolFrameType.getFrameWidthFactor()));
            this.layoutStaticGraphic(dc, SymbologyConstants.FEINT_DUMMY, frameWidthClass, frameRect);
        }

        // Installation.
        o = this.modifiers.getValue(SymbologyConstants.INSTALLATION);
        if (o != null && Boolean.TRUE.equals(o))
            this.layoutStaticGraphic(dc, SymbologyConstants.INSTALLATION, o, frameRect);

        // Auxiliary Equipment Indicator.
        o = this.modifiers.getValue(SymbologyConstants.AUXILIARY_EQUIPMENT);
        if (o != null && SymbologyConstants.AUXILIARY_EQUIPMENT_ALL.contains(o.toString().toUpperCase()))
            this.layoutStaticGraphic(dc, SymbologyConstants.AUXILIARY_EQUIPMENT, o, frameRect);

        // TODO: check valid values.
        // TODO: handle alternate operational condition.
        // Operational Condition.
        o = this.symbolCode.getStatus();
        if (o != null && (SymbologyConstants.STATUS_PRESENT_DAMAGED.equalsIgnoreCase(o.toString())
            || SymbologyConstants.STATUS_PRESENT_DESTROYED.equalsIgnoreCase(o.toString())))
            this.layoutStaticGraphic(dc, SymbologyConstants.STATUS, o, frameRect);

        if (this.modifierVertices != null)
            this.modifierVertices.flip(); // Set the limit to the current position.
    }

    protected void layoutStaticGraphic(DrawContext dc, String key, Object value, Rectangle frameRect)
    {
        ModifierLayout layout = this.computeModifierLayout(this.symbolCode, key);
        if (layout == null)
            return;

        // TODO: once layout is performed only as necessary, we don't need a map of glyphs. Instead, we can just keep
        // the currently glyphs in a queue, load them every frame, and place the vertices and texture coordinates in a
        // buffer. The buffer must be updated when the layout is performed or when the texture atlas changes.
        ModifierGlyph glyph = this.modifierGlyphMap.get(key);

        // Re-create the modifier glyph if the glyph does not exist or its value has changed.
        if (glyph == null || !this.isGlyphValid(glyph, value))
        {
            glyph = this.createModifierGlyph(key, value);
            this.modifierGlyphMap.put(key, glyph);
        }

        if (glyph != null && glyph.load(dc))
        {
            Dimension glyphSize = glyph.getSize();
            Point2D offset = layout.getOffset().computeOffset(frameRect.getWidth(), frameRect.getHeight(), 1d, 1d);
            Point2D hotspot = layout.getHotspot().computeOffset(glyphSize.getWidth(), glyphSize.getHeight(), 1d, 1d);

            double x = frameRect.getX() + offset.getX() - hotspot.getX();
            double y = frameRect.getY() + offset.getY() - hotspot.getY();

            this.modifierVertices = this.addRectVertices(this.modifierVertices, (int) x, (int) y, glyphSize.width,
                glyphSize.height, glyph.getTexCoords());
        }
    }

    protected void layoutDirectionOfMovement(DrawContext dc)
    {
        Angle heading = null;
        Object o = this.modifiers.getValue(SymbologyConstants.DIRECTION_OF_MOVEMENT);
        if (o != null && o instanceof Angle)
            heading = (Angle) o;

        Double scale = 1d;
        o = this.modifiers.getValue(SymbologyConstants.SPEED_LEADER_SCALE);
        if (o != null && o instanceof Number)
            scale = ((Number) o).doubleValue();

        if (heading == null)
            return;

        if (this.iconTexture == null || this.iconTexture.frameRect == null)
            return;

        // The length of the direction of movement line is equal to the height of the symbol frame. See MIL-STD-2525C
        // section 5.3.4.1.c, page 33.
        Rectangle rect = this.iconTexture.frameRect;
        Angle angle = dc.getView().getHeading().add(Angle.POS90).subtract(heading);
        double dx = rect.getHeight() * scale * angle.cos();
        double dy = rect.getHeight() * scale * angle.sin();

        FloatBuffer points = this.directionOfMovementPoints;
        if (points == null || points.capacity() < 6)
            points = BufferUtil.newFloatBuffer(6);
        points.clear();

        String s = this.symbolCode.getBattleDimension();
        if (s != null && s.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND))
        {
            double x = rect.getCenterX();
            double y = rect.getMinY();
            double offset = this.getDirectionOfMovementGroundOffset();
            points.put((float) x).put((float) y); // Bottom of symbol frame.
            points.put((float) x).put((float) (y - offset)); // Terrain point beneath the symbol position.
            points.put((float) (x + dx)).put((float) (y - offset + dy)); // Point denoting the symbol heading.
        }
        else
        {
            double x = rect.getCenterX();
            double y = rect.getCenterY();
            points.put((float) x).put((float) y); // Center of symbol frame.
            points.put((float) (x + dx)).put((float) (y + dy)); // Point denoting the symbol heading.
        }

        // Flip the point buffer to mark the current position as the limit. This provides the direction of movement
        // rendering logic with a value indicating how many points to draw.
        this.directionOfMovementPoints = points;
        this.directionOfMovementPoints.flip();
    }

    protected double getDirectionOfMovementGroundOffset()
    {
        return this.iconTexture != null && this.iconTexture.frameRect != null
            ? this.iconTexture.frameRect.getHeight() / 2d : 0;
    }

    protected boolean isGlyphValid(ModifierGlyph glyph, Object newValue)
    {
        if (glyph == null)
            return false;

        Object oldValue = ((ModifierSource) glyph.getImageSource()).getModifierValue();
        return oldValue != null ? oldValue.equals(newValue) : newValue == null;
    }

    protected ModifierGlyph createModifierGlyph(String key, Object value)
    {
        if (this.getModifierAtlas() != null && this.getModifierRetriever() != null)
        {
            ModifierSource source = new ModifierSource(key, value, this.getModifierRetriever());
            return new ModifierGlyph(this.getModifierAtlas(), source);
        }

        return null;
    }

    @Override
    protected void computeScreenRect(DrawContext dc)
    {
        super.computeScreenRect(dc);

        if (this.mustDrawGraphicModifiers(dc) && this.mustDrawDirectionOfMovement(dc)
            && this.directionOfMovementPoints != null)
        {
            this.addToScreenRect(this.directionOfMovementPoints, 2);
        }
    }

    @Override
    protected void computeOffset(DrawContext dc)
    {
        super.computeOffset(dc);

        // If the direction of movement modifier is to be drawn and this is a ground symbol, then offset the
        // symbol such that is appears exactly 1/2 frame height above its terrain point in screen coordinates.
        // The causes the length of the direction of movement line for ground symbols extending from the bottom
        // of the symbol to the terrain point to have a length of 1/2 the frame height.
        if (this.mustDrawGraphicModifiers(dc) && this.mustDrawDirectionOfMovement(dc))
        {
            String s = this.symbolCode.getBattleDimension();
            if (s != null && s.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
                && iconTexture != null && this.iconTexture.frameRect != null)
            {
                this.dy = -this.iconTexture.frameRect.getY() + this.getDirectionOfMovementGroundOffset();
            }
        }
    }

    @Override
    protected void draw(DrawContext dc)
    {
        super.draw(dc);

        if (this.mustDrawTextModifiers(dc) && !dc.isPickingMode())
            this.drawTextModifiers(dc);

        // Draw the dynamic Direction of Movement and Speed Leader modifier.
        if (this.mustDrawGraphicModifiers(dc) && this.mustDrawDirectionOfMovement(dc))
            this.drawDirectionOfMovement(dc);
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean mustDrawDirectionOfMovement(DrawContext dc)
    {
        Object o = this.modifiers.getValue(SymbologyConstants.DIRECTION_OF_MOVEMENT);
        return o != null && o instanceof Angle;
    }

    protected void drawDirectionOfMovement(DrawContext dc)
    {
        if (this.directionOfMovementPoints == null || this.directionOfMovementPoints.remaining() < 2)
            return;

        // Use either the currently specified opacity or the default if no opacity is specified.
        Double opacity = this.getActiveAttributes().getOpacity();
        if (opacity == null)
            opacity = DEFAULT_OPACITY;

        GL gl = dc.getGL();

        try
        {
            // Disable the depth test and texturing. We draw the direction of movement as black lines in screen space.
            // Direction of movement lines for ground objects therefore likely penetrate the terrain, but must appear
            // on top to be visible.
            gl.glDisable(GL.GL_DEPTH_TEST);
            gl.glDisable(GL.GL_TEXTURE_2D);

            // Set the current color to black with the current opacity value as the alpha component. Blending is set to
            // pre-multiplied alpha mode, but we can just specify 0 for the RGB components because multiplying them by
            // the alpha component has no effect.
            if (!dc.isPickingMode())
                gl.glColor4f(0f, 0f, 0f, opacity.floatValue());

            gl.glVertexPointer(2, GL.GL_FLOAT, 0, this.directionOfMovementPoints);
            gl.glDrawArrays(GL.GL_LINE_STRIP, 0, this.directionOfMovementPoints.remaining() / 2);
        }
        finally
        {
            // Restore the depth test enable state and the texture 2D enable state to the values specified in
            // beginDrawing.
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glEnable(GL.GL_TEXTURE_2D);

            // Restore the current color to that specified in doDrawOrderedRenderable.
            if (!dc.isPickingMode())
                gl.glColor4f(opacity.floatValue(), opacity.floatValue(), opacity.floatValue(), opacity.floatValue());
        }
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean mustDrawTextModifiers(DrawContext dc)
    {
        return this.isShowTextModifiers();
    }

    protected void drawTextModifiers(DrawContext dc)
    {
        if (this.iconTexture == null || this.iconTexture.frameRect == null)
            return;

        List<ModifierLayout> layoutList = this.getModifierLayouts();
        if (layoutList == null)
            return;

        // Use either the currently specified opacity or the default if no opacity is specified.
        Double opacity = this.getActiveAttributes().getOpacity();
        if (opacity == null)
            opacity = DEFAULT_OPACITY;

        // Use either the currently specified text modifier material or the default if no material is specified.
        Material material = this.getActiveAttributes().getTextModifierMaterial();
        if (material == null)
            material = DEFAULT_TEXT_MODIFIER_MATERIAL;

        // Use either the currently specified text modifier font or compute a default if no font is specified. We compute
        // a default font rather than using a static default in order to choose a font size that is appropriate for the
        // symbol's frame height. According to the MIL-STD-2525C specification, the text modifier height must be 0.3x
        // the symbol's frame height.
        Font font = this.getActiveAttributes().getTextModifierFont();
        if (font == null || font == DEFAULT_TEXT_MODIFIER_FONT)
            font = this.computeDefaultTextModifierFont();

        Color color = material.getDiffuse();
        int alpha = (int) (255 * opacity + 0.5);
        Color fgColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        Color bgColor = WWUtil.computeContrastingColor(fgColor);

        Rectangle frameRect = this.iconTexture.frameRect;

        TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), font);
        StringBuilder sb = new StringBuilder();

        try
        {
            textRenderer.begin3DRendering();

            for (ModifierLayout layout : layoutList)
            {
                // TODO: determine a cleaner way to identify each text modifier and their related groups
                if (!layout.getType().equalsIgnoreCase("text"))
                    continue;

                for (String fieldId : layout.getFieldIds())
                {
                    Object o = this.modifiers.getValue(fieldId);
                    if (o != null)
                    {
                        if (sb.length() > 0)
                            sb.append(" ");
                        sb.append(o);
                    }
                }

                if (sb.length() == 0)
                    continue;

                Rectangle2D textRect = textRenderer.getBounds(sb.toString());
                Point2D offset = layout.getOffset().computeOffset(frameRect.getWidth(), frameRect.getHeight(), 1d, 1d);
                Point2D hotspot = layout.getHotspot().computeOffset(textRect.getWidth(), textRect.getHeight(), 1d, 1d);
                int x = (int) (frameRect.getX() + offset.getX() - hotspot.getX());
                int y = (int) (frameRect.getY() + offset.getY() - hotspot.getY());

                textRenderer.setColor(bgColor);
                textRenderer.draw(sb.toString(), x + 1, y - 1);
                textRenderer.setColor(fgColor);
                textRenderer.draw(sb.toString(), x, y);

                sb.delete(0, sb.length());
            }
        }
        finally
        {
            textRenderer.end3DRendering();
        }
    }

    protected Font computeDefaultTextModifierFont()
    {
        // TODO: compute the default font only when necessary
        // If the symbol's frame size is available, then compute a font with the default family and style but with a
        // size appropriate for the symbol's frame height.
        if (this.iconTexture != null && this.iconTexture.frameRect != null)
        {
            double textHeight = FONT_SIZE_FACTOR * this.iconTexture.frameRect.getHeight();
            Integer scaledSize = WWUtil.convertPixelsToFontSize((int) textHeight);
            if (scaledSize != null)
                return new Font(DEFAULT_TEXT_MODIFIER_FONT.getName(), DEFAULT_TEXT_MODIFIER_FONT.getStyle(),
                    scaledSize);
        }

        return DEFAULT_TEXT_MODIFIER_FONT;
    }

    protected List<SymbolFrameType> getSymbolFrameTypes()
    {
        if (symbolFrameTypeReadFailed)
            return null;

        if (symbolFrameTypes == null)
            symbolFrameTypes = readSymbolFrameTypes(SYMBOL_FRAME_TYPE_CONFIG_FILE);

        return symbolFrameTypes;
    }

    protected SymbolFrameType computeSymbolFrameType(SymbolCode symbolCode)
    {
        List<SymbolFrameType> typeList = getSymbolFrameTypes();
        if (typeList == null)
            return null;

        // Try to find a matching entry for our symbol code in the symbol frame types configuration file. Each entry
        // contains a list of accepted standard identity codes and battle dimension codes in upper case. We find a match
        // when both codes match an entry from the config file.
        for (SymbolFrameType type : typeList)
        {
            if (type.getStandardIdentities().contains(symbolCode.getStandardIdentity().toUpperCase())
                && type.getBattleDimensions().contains(symbolCode.getBattleDimension().toUpperCase()))
            {
                return type;
            }
        }

        return null;
    }

    protected static List<SymbolFrameType> readSymbolFrameTypes(Object source)
    {
        try
        {
            Document doc = WWXML.openDocument(source);
            XPath xpath = WWXML.makeXPath();

            Element[] elements = WWXML.getElements(doc.getDocumentElement(),
                "/MilStd2525SymbolFrameTypes/SymbolFrameType", xpath);
            if (elements == null || elements.length == 0)
                return Collections.emptyList();

            List<SymbolFrameType> list = new ArrayList<SymbolFrameType>();

            for (Element element : elements)
            {
                if (element == null)
                    continue;

                Rectangle2D frameRect = parseRectangle(element, "FrameRect", xpath);
                if (frameRect == null)
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement", "FrameRect");
                    Logging.logger().warning(msg);
                    continue;
                }

                Double frameWidthFactor = WWXML.getDouble(element, "FrameWidthFactor", xpath);
                if (frameWidthFactor == null)
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement", "FrameWidthFactor");
                    Logging.logger().warning(msg);
                    continue;
                }

                List<String> standardIdentities;
                String[] array = WWXML.getTextArray(element, "AcceptedStandardIdentities/StandardIdentity", xpath);
                if (array != null && array.length > 0)
                    standardIdentities = Arrays.asList(array);
                else
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement",
                        "AcceptedStandardIdentities/StandardIdentity");
                    Logging.logger().warning(msg);
                    continue;
                }

                List<String> battleDimensions;
                array = WWXML.getTextArray(element, "AcceptedBattleDimensions/BattleDimension", xpath);
                if (array != null && array.length > 0)
                    battleDimensions = Arrays.asList(array);
                else
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement",
                        "AcceptedBattleDimensions/BattleDimension");
                    Logging.logger().warning(msg);
                    continue;
                }

                list.add(new SymbolFrameType(frameRect, frameWidthFactor, standardIdentities, battleDimensions));
            }

            return list;
        }
        catch (Exception e)
        {
            String msg = Logging.getMessage("generic.ExceptionAttemptingToParseXml", source);
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
            symbolFrameTypeReadFailed = true;
            return null;
        }
    }

    protected List<ModifierLayout> getModifierLayouts()
    {
        if (modifierLayoutReadFailed)
            return null;

        if (modifierLayouts == null)
            modifierLayouts = readModifierLayout(MODIFIER_LAYOUT_CONFIG_FILE);

        return modifierLayouts;
    }

    protected ModifierLayout computeModifierLayout(SymbolCode symbolCode, String fieldId)
    {
        List<ModifierLayout> layoutList = getModifierLayouts();
        if (layoutList == null)
            return null;

        // Try to find a matching entry for our symbol code and field ID in the modifier layout configuration file. Each
        // entry contains a list of accepted modifier field IDs and scheme codes in upper case. We find a match when
        // both codes match an entry from the config file.
        for (ModifierLayout layout : layoutList)
        {
            if (layout.getFieldIds().contains(fieldId.toUpperCase()) &&
                (layout.getSchemes() == null || layout.getSchemes().contains(symbolCode.getScheme().toUpperCase())))
            {
                return layout;
            }
        }

        return null;
    }

    protected static List<ModifierLayout> readModifierLayout(Object source)
    {
        try
        {
            Document doc = WWXML.openDocument(source);
            XPath xpath = WWXML.makeXPath();

            Element[] elements = WWXML.getElements(doc.getDocumentElement(),
                "/MilStd2525SymbolModifierLayout/ModifierLayout", xpath);
            if (elements == null || elements.length == 0)
                return Collections.emptyList();

            List<ModifierLayout> list = new ArrayList<ModifierLayout>();

            for (Element element : elements)
            {
                if (element == null)
                    continue;

                String type = WWXML.getText(element, "@type", xpath);
                if (type == null)
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement", "@type");
                    Logging.logger().warning(msg);
                    continue;
                }

                Offset offset = parseOffset(element, "Offset", xpath);
                if (offset == null)
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement", "Offset");
                    Logging.logger().warning(msg);
                    continue;
                }

                Offset hotspot = parseOffset(element, "Hotspot", xpath);
                if (hotspot == null)
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement", "Hotspot");
                    Logging.logger().warning(msg);
                    continue;
                }

                List<String> fieldIds;
                String[] array = WWXML.getTextArray(element, "AcceptedFieldIds/FieldId", xpath);
                if (array != null && array.length > 0)
                    fieldIds = Arrays.asList(array);
                else
                {
                    String msg = Logging.getMessage("generic.MissingRequiredElement", "AcceptedFieldIds/FieldId");
                    Logging.logger().warning(msg);
                    continue;
                }

                List<String> schemes = null;
                array = WWXML.getTextArray(element, "AcceptedSchemes/Scheme", xpath);
                if (array != null && array.length > 0) // List of accepted schemes is not required.
                    schemes = Arrays.asList(array);

                list.add(new ModifierLayout(type, offset, hotspot, fieldIds, schemes));
            }

            return list;
        }
        catch (Exception e)
        {
            String msg = Logging.getMessage("generic.ExceptionAttemptingToParseXml", source);
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
            return null;
        }
    }

    protected static Rectangle2D parseRectangle(Element context, String path, XPath xpath)
    {
        Element el = path == null ? context : WWXML.getElement(context, path, xpath);
        if (el == null)
            return null;

        Double x = WWXML.getDouble(el, "@x", xpath);
        Double y = WWXML.getDouble(el, "@y", xpath);
        Double w = WWXML.getDouble(el, "@width", xpath);
        Double h = WWXML.getDouble(el, "@height", xpath);

        if (x == null || y == null || w == null || h == null)
            return null;

        return new Rectangle2D.Double(x, y, w, h);
    }

    protected static Offset parseOffset(Element context, String path, XPath xpath)
    {
        Element el = path == null ? context : WWXML.getElement(context, path, xpath);
        if (el == null)
            return null;

        Double x = WWXML.getDouble(el, "@x", xpath);
        Double y = WWXML.getDouble(el, "@y", xpath);
        String xunits = WWXML.getText(el, "@xunits", xpath);
        String yunits = WWXML.getText(el, "@xunits", xpath);

        if (x == null || y == null || xunits == null || xunits.length() == 0 || yunits == null || yunits.length() == 0)
            return null;

        String wwXUnits = KMLUtil.kmlUnitsToWWUnits(xunits);
        String wwYUnits = KMLUtil.kmlUnitsToWWUnits(yunits);

        if (wwXUnits == null || wwXUnits.length() == 0 || wwYUnits == null || wwYUnits.length() == 0)
            return null;

        return new Offset(x, y, wwXUnits, wwYUnits);
    }
}
