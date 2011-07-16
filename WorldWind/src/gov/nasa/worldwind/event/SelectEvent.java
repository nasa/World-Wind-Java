/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.event;

import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.event.*;

/**
 * This class signals that an object or terrain is under the cursor and identifies that object and the operation that
 * caused the signal. See the <em>Field Summary</em> for a description of the possible operations. When a
 * <code>SelectEvent</code> occurs, all select- event listeners registered with the associated {@link
 * gov.nasa.worldwind.WorldWindow} are called. Select event listeners are registered by calling {@link
 * gov.nasa.worldwind.WorldWindow#addSelectListener(SelectListener)}. The select event contains the top-most visible
 * object of all object at the cursor position.
 * <p/>
 * A <code>SelectEvent</code> is generated when the cursor is over a visible object either because the user moved it
 * there or because the World Window was repainted and a visible object was found to be under the cursor. For some event
 * types a <code>SelectEvent</code> is generated when the cursor becomes no longer under the cursor.
 * <p/>
 * If a select listener performs some action in response to a select event, it should call the event's {@link
 * #consume()} method in order to indicate to subsequently called listeners that the event has been responded to and no
 * further action should be taken.
 * <p/>
 * If no object is under the cursor but the cursor is over terrain, the select event will identify the terrain as the
 * picked object and will include the corresponding geographic position. See {@link
 * gov.nasa.worldwind.pick.PickedObject#isTerrain()}.
 *
 * @author tag
 * @version $Id$
 */
@SuppressWarnings( {"StringEquality"})
public class SelectEvent extends WWEvent
{
    /** The user clicked the left mouse button while the cursor was over picked object. */
    public static final String LEFT_CLICK = "gov.nasa.worldwind.SelectEvent.LeftClick";
    /** The user double-clicked the left mouse button while the cursor was over picked object. */
    public static final String LEFT_DOUBLE_CLICK = "gov.nasa.worldwind.SelectEvent.LeftDoubleClick";
    /** The user clicked the right mouse button while the cursor was over picked object. */
    public static final String RIGHT_CLICK = "gov.nasa.worldwind.SelectEvent.RightClick";
    /** The user pressed the left mouse button while the cursor was over picked object. */
    public static final String LEFT_PRESS = "gov.nasa.worldwind.SelectEvent.LeftPress";
    /** The user pressed the right mouse button while the cursor was over picked object. */
    public static final String RIGHT_PRESS = "gov.nasa.worldwind.SelectEvent.RightPress";
    /**
     * The cursor has moved over the picked object and become stationary, or has moved off the object of the most recent
     * <code>HOVER</code> event. In the latter case, the picked object will be null.
     */
    public static final String HOVER = "gov.nasa.worldwind.SelectEvent.Hover";
    /**
     * The cursor has moved over the object or has moved off the object most recently rolled over. In the latter case
     * the picked object will be null.
     */
    public static final String ROLLOVER = "gov.nasa.worldwind.SelectEvent.Rollover";
    /** The user is attempting to drag the picked object. */
    public static final String DRAG = "gov.nasa.worldwind.SelectEvent.Drag";
    /** The user has stopped dragging the picked object. */
    public static final String DRAG_END = "gov.nasa.worldwind.SelectEvent.DragEnd";

    private final String eventAction;
    private final java.awt.Point pickPoint;
    private final MouseEvent mouseEvent;
    private final PickedObjectList pickedObjects;

    public SelectEvent(Object source, String eventAction, MouseEvent mouseEvent, PickedObjectList pickedObjects)
    {
        super(source);
        this.eventAction = eventAction;
        this.pickPoint = mouseEvent != null ? mouseEvent.getPoint() : null;
        this.mouseEvent = mouseEvent;
        this.pickedObjects = pickedObjects;
    }

    public SelectEvent(Object source, String eventAction, java.awt.Point pickPoint, PickedObjectList pickedObjects)
    {
        super(source);
        this.eventAction = eventAction;
        this.pickPoint = pickPoint;
        this.mouseEvent = null;
        this.pickedObjects = pickedObjects;
    }

    @Override
    public void consume()
    {
        super.consume();

        if (this.getMouseEvent() != null)
            this.getMouseEvent().consume();
    }

    public String getEventAction()
    {
        return this.eventAction != null ? this.eventAction : "gov.nasa.worldwind.SelectEvent.UnknownEventAction";
    }

    public Point getPickPoint()
    {
        return pickPoint;
    }

    public MouseEvent getMouseEvent()
    {
        return mouseEvent;
    }

    public boolean hasObjects()
    {
        return this.pickedObjects != null && this.pickedObjects.size() > 0;
    }

    public PickedObjectList getObjects()
    {
        return this.pickedObjects;
    }

    public PickedObject getTopPickedObject()
    {
        return this.hasObjects() ? this.pickedObjects.getTopPickedObject() : null;
    }

    public Object getTopObject()
    {
        PickedObject tpo = this.getTopPickedObject();
        return tpo != null ? tpo.getObject() : null;
    }

    public boolean isRollover()
    {
        return this.getEventAction() == ROLLOVER;
    }

    public boolean isHover()
    {
        return this.getEventAction() == HOVER;
    }

    public boolean isDragEnd()
    {
        return this.getEventAction() == DRAG_END;
    }

    public boolean isDrag()
    {
        return this.getEventAction() == DRAG;
    }

    public boolean isRightPress()
    {
        return this.getEventAction() == RIGHT_PRESS;
    }

    public boolean isRightClick()
    {
        return this.getEventAction() == RIGHT_CLICK;
    }

    public boolean isLeftDoubleClick()
    {
        return this.getEventAction() == LEFT_DOUBLE_CLICK;
    }

    public boolean isLeftClick()
    {
        return this.getEventAction() == LEFT_CLICK;
    }

    public boolean isLeftPress()
    {
        return this.getEventAction() == LEFT_PRESS;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(this.getClass().getName() + " "
            + (this.eventAction != null ? this.eventAction : Logging.getMessage("generic.Unknown")));
        if (this.pickedObjects != null && this.pickedObjects.getTopObject() != null)
            sb.append(", ").append(this.pickedObjects.getTopObject().getClass().getName());

        return sb.toString();
    }
}
