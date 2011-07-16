/*
Copyright (C) 2001, 2007 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.applications.sar.actions;

import gov.nasa.worldwindx.applications.sar.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author tag
 * @version $Id$
 */
public class DeletePositionsAction extends AbstractAction
{
    protected final PositionTable table;

    public DeletePositionsAction(final PositionTable table)
    {
        this.table = table;

        int numSelectedPositions = table.getSelectedRowCount();
        if (numSelectedPositions <= 1)
            putValue(NAME, "Delete Selected Position");
        else
            putValue(NAME, "Delete Selected Positions");

        putValue(LONG_DESCRIPTION, "Remove Positions from Track");

        if (numSelectedPositions == 0)
            this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e)
    {
        SARTrack st = table.getSarTrack();
        if (st != null)
            st.removePositions(this.table.getSelectedRows());
    }
}
