/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Renderable;

/**
 * TacticalSymbol provides a common interface for displaying tactical point symbols from symbology sets. Implementations
 * of this interface provide support for point symbols within a specific symbology set. For example, class {@link
 * MilStd2525TacticalSymbol} provides support for tactical symbols from the MIL-STD-2525 symbology specification.
 * <p/>
 * <h2>Creating and Displaying Tactical Symbols</h2> To create a tactical symbol, instantiate a concrete implementation
 * appropriate for the desired symbology set. Pass a string identifier, the desired geographic position, and
 * (optionally) one or more symbol modifier key-value pairs to the symbol's constructor. The tactical symbol creates a
 * graphic appropriate for the string identifier and optional symbol modifiers, and draws that graphic at the specified
 * position when its render method is called. For example, a symbol implementation may display a 3D object at the
 * position, or display a screen space icon who's screen location tracks the position. MIL-STD-2525 tactical symbols
 * display a screen space icon with graphic and text modifiers surrounding the icon.
 * <p/>
 * The format of the string identifier and the modifier key-value pairs are implementation dependent. For MIL-STD-2525,
 * the string identifier must be a 15-character alphanumeric symbol identification code (SIDC), and the modifier keys
 * must be one of the constants defined by {@link gov.nasa.worldwind.symbology.milstd2525.SymbolCode}.
 * <p/>
 * Since TacticalSymbol extends the Renderable interface, a tactical symbol is displayed either by adding it to a layer,
 * or by calling its render method from within a custom layer or renderable object. The simplest way to display a
 * tactical symbol is to add it to a {@link gov.nasa.worldwind.layers.RenderableLayer}. Here's an example of creating
 * and displaying a tactical symbol for a MIL-STD-2525 friendly ground unit using a RenderableLayer:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit. Since the SIDC specifies a ground symbol, the
 * // tactical symbol's altitude mode is automatically configured as WorldWind.CLAMP_TO_GROUND.
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU----------", Position.fromDegrees(-120, 40, 0));
 *
 * // Create a renderable layer to display the tactical symbol. This example adds only a single symbol, but many
 * // symbols can be added to a single layer.
 * RenderableLayer symbolLayer = new RenderableLayer();
 * symbolLayer.addRenderable(symbol);
 *
 * // Add the layer to the world window's model and request that the window redraw itself. The world window draws the
 * // symbol on the globe at the specified position. Interactions between the symbol and the cursor are returned in the
 * // world window's picked object list, and reported to the world window's select listeners.
 * WorldWindow wwd = ... // A reference to your application's WorldWindow instance.
 * wwd.getModel().getLayers().add(symbolLayer);
 * wwd.redraw();
 * </pre>
 * <p/>
 * <h2>Tactical Symbol Modifiers</h2> Symbols modifiers are optional attributes that augment or change a symbol's
 * graphic. Which modifiers are recognized by a tactical symbol and how they affect the symbol's graphic is
 * implementation dependent. Symbol modifiers can be specified at construction by passing a list of key-value pairs, or
 * after construction by setting a key-value pair on the tactical symbol's AVList or calling an implementation defined
 * setter method. The modifier keys must be one of the constants defined by {@link
 * gov.nasa.worldwind.symbology.milstd2525.SymbolCode}. Each recognized modifier key corresponds an implementation
 * defined setter method. This enables callers to specify all modifiers either through the symbol's AVList or through an
 * implementation defined setter method. Here's an example of setting the the direction of movement modifier at
 * construction for a MIL-STD-2525 friendly ground unit:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit, specifying the optional direction of movement
 * // modifier by passing in a list of key-value pairs.
 * AVList modifiers = new AVListImpl();
 * modifiers.setValue(SymbolCode.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(45));
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU----------", Position.fromDegrees(-120, 40, 0),
 *     modifiers);
 * </pre>
 * <p/>
 * Here's an example of setting the same modifier after construction:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit.
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU----------", Position.fromDegrees(-120, 40, 0));
 *
 * // Once a symbol is constructed, there are two ways to specify optional symbol modifiers:
 * // 1) Specify the modifier as a key-value pair on the tactical symbol's AVList:
 * symbol.setValue(SymbolCode.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(45));
 * // 2) Specify the modifier using an implementation defined setter method:
 * ((MilStd2525TacticalSymbol) symbol).setDirectionOfMovement(Angle.fromDegrees(45));
 * </pre>
 * <p/>
 * Tactical symbol implementations apply modifiers from the string identifier specified during construction. For
 * example, given a MIL-STD-2525 symbol representing units, installation, or equipment, SIDC positions 11-12 specify the
 * echelon and task force modifiers (See MIL-STD-2525C, Appendix A). Here's an example of setting the echelon and task
 * force modifiers at construction for a MIL-STD-2525 friendly ground unit:
 * <p/>
 * <pre>
 * // Create a tactical symbol for a MIL-STD-2525 friendly ground unit. Specify the echelon modifier and task force
 * // modifiers by setting the SIDC characters 11-12 to "EA". This indicates that the ground unit is a team/crew task
 * // force (see MIL-STD-2525C, Appendix A, Table A-II).
 * TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFGPU-----EA---", Position.fromDegrees(-120, 40, 0));
 * </pre>
 * <p/>
 * <h2>Positioning Tactical Symbols</h2> A symbol's geographic position defines where the symbol displays its graphic.
 * Either the graphic's geometric center is displayed at the position, or a specific location within the graphic (such
 * as the bottom of a leader line) is displayed at the position. This behavior depends on the symbol implementation, the
 * string identifier, and the symbol modifiers (if any).
 * <p/>
 * A symbol's altitude mode defines how the altitude component if the position is interpreted. Altitude mode may be
 * specified by calling {@link #setAltitudeMode(int)}. Recognized modes are: <ul> <li>WorldWind.CLAMP_TO_GROUND -- the
 * symbol graphic is placed on the terrain at the latitude and longitude of its position.</li>
 * <li>WorldWind.RELATIVE_TO_GROUND -- the symbol graphic is placed above the terrain at the latitude and longitude of
 * its position and the distance specified by its elevation.</li> <li>WorldWind.ABSOLUTE -- the symbol graphic is placed
 * at its specified position.</li> </ul>
 * <p/>
 * Tactical symbol implementations configure the altitude mode from the string identifier specified during construction.
 * For example, specifying the MIL-STD-2525 SIDC "SFGPU----------" specifies a friendly ground unit symbol, and causes a
 * tactical symbol to configure the altitude mode as WorldWind.CLAMP_TO_GROUND. The automatically configured mode can be
 * overridden by calling setAltitudeMode.
 *
 * @author dcollins
 * @version $Id$
 */
public interface TacticalSymbol extends WWObject, Renderable
{
    /**
     * Indicates whether this symbol is drawn when in view.
     *
     * @return true if this symbol is drawn when in view, otherwise false.
     */
    boolean isVisible();

    /**
     * Specifies whether this symbol is drawn when in view.
     *
     * @param visible true if this symbol should be drawn when in view, otherwise false.
     */
    void setVisible(boolean visible);

    /**
     * Indicates this symbol's geographic position. See {@link #setPosition(gov.nasa.worldwind.geom.Position)} for a
     * description of how tactical symbols interpret their position.
     *
     * @return this symbol's current geographic position.
     */
    Position getPosition();

    /**
     * Specifies this symbol's geographic position. The specified position must be non-null, and defines where on the
     * globe this symbol displays its graphic. The type of graphic this symbol displays at the position is
     * implementation dependent.
     *
     * @param position this symbol's new position.
     *
     * @throws IllegalArgumentException if the position is <code>null</code>.
     */
    void setPosition(Position position);

    /**
     * Indicates this symbol's altitude mode. See {@link #setAltitudeMode(int)} for a description of the valid altitude
     * modes.
     *
     * @return this symbol's altitude mode.
     */
    int getAltitudeMode();

    /**
     * Specifies this symbol's altitude mode. Altitude mode defines how the altitude component of this symbol's position
     * is interpreted. Recognized modes are: <ul> <li>WorldWind.CLAMP_TO_GROUND -- this symbol's graphic is placed on
     * the terrain at the latitude and longitude of its position.</li> <li>WorldWind.RELATIVE_TO_GROUND -- this symbol's
     * graphic is placed above the terrain at the latitude and longitude of its position and the distance specified by
     * its elevation.</li> <li>WorldWind.ABSOLUTE -- this symbol's graphic is placed at its specified position.</li>
     * </ul>
     * <p/>
     * This symbol assumes the altitude mode WorldWind.ABSOLUTE if the specified mode is not recognized.
     *
     * @param altitudeMode this symbol's new altitude mode.
     */
    void setAltitudeMode(int altitudeMode);

    /**
     * Indicates a string identifier for this symbol. The format of the identifier depends on the symbol set to which
     * this symbol belongs. For symbols belonging to the MIL-STD-2525 symbol set, this returns a 15-character
     * alphanumeric symbol identification code (SIDC).
     *
     * @return an identifier for this symbol.
     */
    String getIdentifier();
}

