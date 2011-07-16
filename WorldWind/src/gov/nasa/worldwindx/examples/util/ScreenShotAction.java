/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.examples.util;

import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.WorldWindow;

import javax.swing.*;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author tag
 * @version $Id$
 */
public class ScreenShotAction extends AbstractAction implements RenderingListener
{
    WorldWindow wwd;
    private File snapFile;
    JFileChooser fileChooser;

    public ScreenShotAction(WorldWindow wwd)
    {
        super("Screen Shot");

        this.wwd = wwd;
        this.fileChooser = new JFileChooser();
    }

    public void actionPerformed(ActionEvent event)
    {
        Component frame = wwd instanceof Component ? ((Component) wwd).getParent() : null;
        this.snapFile = this.chooseFile(frame);
    }

    private File chooseFile(Component parentFrame)
    {
        File outFile = null;

        try
        {
            while (true)
            {
                fileChooser.setDialogTitle("Save Screen Shot");
                fileChooser.setSelectedFile(new File(composeSuggestedName()));

                int status = fileChooser.showSaveDialog(parentFrame);
                if (status != JFileChooser.APPROVE_OPTION)
                    return null;

                outFile = fileChooser.getSelectedFile();
                if (outFile == null) // Shouldn't happen, but include a reaction just in case
                {
                    JOptionPane.showMessageDialog(parentFrame, "Please select a location for the image file.",
                        "No Location Selected", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                if (!outFile.getPath().endsWith(".png"))
                    outFile = new File(outFile.getPath() + ".png");

                if (outFile.exists())
                {
                    status = JOptionPane.showConfirmDialog(parentFrame,
                        "Replace existing file\n" + outFile.getName() + "?",
                        "Overwrite Existing File?", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (status == JOptionPane.NO_OPTION)
                        continue;
                    if (status != JOptionPane.YES_OPTION)
                        return null;
                }
                break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.wwd.removeRenderingListener(this); // ensure not to add a duplicate
        this.wwd.addRenderingListener(this);

        return outFile;
    }

    public void stageChanged(RenderingEvent event)
    {
        if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP) && this.snapFile != null)
        {
            try
            {
                GLAutoDrawable glad = (GLAutoDrawable) event.getSource();
                int[] viewport = new int[4];
                glad.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
                com.sun.opengl.util.Screenshot.writeToFile(this.snapFile, viewport[2] + 10, viewport[3], false);
                glad.getGL().glViewport(0, 0, glad.getWidth(), glad.getHeight());
                System.out.printf("Image saved to file %s\n", this.snapFile.getPath());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                this.snapFile = null;
                this.wwd.removeRenderingListener(this);
            }
        }
    }

    private String composeSuggestedName()
    {
        String baseName = "WWJSnapShot";
        String suffix = ".png";

        File currentDirectory = this.fileChooser.getCurrentDirectory();

        File candidate = new File(currentDirectory.getPath() + File.separatorChar + baseName + suffix);
        for (int i = 1; candidate.exists(); i++)
        {
            String sequence = String.format("%03d", i);
            candidate = new File(currentDirectory.getPath() + File.separatorChar + baseName + sequence + suffix);
        }

        return candidate.getPath();
    }
}
