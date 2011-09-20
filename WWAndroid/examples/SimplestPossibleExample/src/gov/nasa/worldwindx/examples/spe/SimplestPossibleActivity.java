/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwindx.examples.spe;

import android.app.Activity;
import android.os.Bundle;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;

import java.io.File;

/**
 * @author dcollins
 * @version $Id$
 */
public class SimplestPossibleActivity extends Activity
{
    protected WorldWindowGLSurfaceView wwd;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // TODO: temporary method of setting the location of the file store on Android. Need to replace this with
        // TODO: something more flexible.
        File fileDir = getFilesDir();
        System.setProperty("gov.nasa.worldwind.platform.user.store", fileDir.getAbsolutePath());

        this.wwd = new WorldWindowGLSurfaceView(this);
        this.wwd.setModel((Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME));
        this.setContentView(this.wwd);
        this.setupView();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Pause the OpenGL ES rendering thread.
        this.wwd.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Resume the OpenGL ES rendering thread.
        this.wwd.onResume();
    }

    protected void setupView()
    {
        // TODO: this should be done during View initialization, not here in the application.
        BasicView view = (BasicView) this.wwd.getView();
        Globe globe = this.wwd.getModel().getGlobe();

        Position lookFromPosition = Position.fromDegrees(Configuration.getDoubleValue(AVKey.INITIAL_LATITUDE),
            Configuration.getDoubleValue(AVKey.INITIAL_LONGITUDE),
            Configuration.getDoubleValue(AVKey.INITIAL_ALTITUDE));
        view.setEyePosition(lookFromPosition, Angle.fromDegrees(0), Angle.fromDegrees(0), globe);
        view.setEyeTilt(Angle.fromDegrees(0), globe);
    }
}
