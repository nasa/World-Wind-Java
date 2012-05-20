/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.awt;

/**
 * @author jym
 * @version $Id$
 */
public interface KeyInputActionHandler
{
    public boolean inputActionPerformed(AbstractViewInputHandler inputHandler, KeyEventState keys, String target,
        ViewInputAttributes.ActionAttributes viewAction);
    public boolean inputActionPerformed(AbstractViewInputHandler inputHandler, java.awt.event.KeyEvent event,
        ViewInputAttributes.ActionAttributes viewAction);

}
