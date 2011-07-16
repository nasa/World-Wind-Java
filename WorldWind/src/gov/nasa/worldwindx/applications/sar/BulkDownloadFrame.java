package gov.nasa.worldwindx.applications.sar;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwindx.examples.BulkDownloadPanel;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author Patrick Murris
 * @version $Id$
 */
public class BulkDownloadFrame extends JFrame
{
    public BulkDownloadFrame(WorldWindow wwd)
    {
        final BulkDownloadPanel panel = new BulkDownloadPanel(wwd);
        final JFrame frameInstance = this;

        this.setTitle("Bulk Download");
        this.add(panel);
        this.pack();
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                // Check whether some downloads are active before closing the frame
                if(panel.hasActiveDownloads())
                {
                    int choice = JOptionPane.showConfirmDialog(frameInstance, "Cancel all active downloads?",
                        "Active downloads", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (choice == JOptionPane.CANCEL_OPTION)
                        return;

                    // Cancel active downloads and clear the monitor panel
                    panel.cancelActiveDownloads();
                    panel.clearInactiveDownloads();
                }
                // Clear sector selector
                panel.clearSector();
                // Close now
                setVisible(false);
            }
        });

    }
}
