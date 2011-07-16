/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.applications.worldwindow.features;

import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.applications.worldwindow.core.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author tag
 * @version $Id$
 */
public class OpenURL extends AbstractOpenResourceFeature
{
    public OpenURL(Registry registry)
    {
        super("Open URL...", Constants.FEATURE_OPEN_URL, null, registry);
    }

    @Override
    public void initialize(Controller controller)
    {
        super.initialize(controller);

        WWMenu fileMenu = (WWMenu) this.getController().getRegisteredObject(Constants.FILE_MENU);
        if (fileMenu != null)
            fileMenu.addMenu(this.getFeatureID());
    }

    @Override
    protected void doActionPerformed(ActionEvent actionEvent)
    {
        try
        {
            String status = JOptionPane.showInputDialog(getController().getFrame(), "URL");
            if (!WWUtil.isEmpty(status))
            {
                this.runOpenThread(status);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.doActionPerformed(actionEvent);
    }
}
