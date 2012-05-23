/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.awt;

import java.awt.event.*;

/**
 * @author jym
 * @version $Id$
 */
public class ViewInputActionHandler implements KeyInputActionHandler, MouseInputActionHandler
{
    public boolean inputActionPerformed(AbstractViewInputHandler inputHandler, KeyEventState keys, String target,
        ViewInputAttributes.ActionAttributes viewAction)
    {
        return false;
    }

    public boolean inputActionPerformed(AbstractViewInputHandler inputHandler, KeyEvent event,
        ViewInputAttributes.ActionAttributes viewAction)
    {
        return false;
    }

    public boolean inputActionPerformed(KeyEventState keys, String target,
        ViewInputAttributes.ActionAttributes viewAction)
    {
        return false;
    }

    public boolean inputActionPerformed(AbstractViewInputHandler inputHandler,
        java.awt.event.MouseEvent mouseEvent, ViewInputAttributes.ActionAttributes viewAction)
    {
        return false;
    }

    public boolean inputActionPerformed(AbstractViewInputHandler inputHandler,
            java.awt.event.MouseWheelEvent mouseWheelEvent, ViewInputAttributes.ActionAttributes viewAction)
    {
        return false;
    }
}
