/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import javax.media.opengl.GL;

/**
 * @author tag
 * @version $Id$
 */
public class OGLStackHandler
{
    private boolean attribsPushed;
    private boolean clientAttribsPushed;
    private boolean modelviewPushed;
    private boolean projectionPushed;
    private boolean texturePushed;

    public void clear()
    {
        this.attribsPushed = false;
        this.clientAttribsPushed = false;
        this.modelviewPushed = false;
        this.projectionPushed = false;
        this.texturePushed = false;
    }

    public boolean isActive()
    {
        return this.attribsPushed || this.clientAttribsPushed || this.modelviewPushed || this.projectionPushed
            || this.texturePushed;
    }

    public void pushAttrib(GL gl, int mask)
    {
        gl.glPushAttrib(mask);
        this.attribsPushed = true;
    }

    public void pushClientAttrib(GL gl, int mask)
    {
        gl.glPushClientAttrib(mask);
        this.clientAttribsPushed = true;
    }

    public void pushModelview(GL gl)
    {
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        this.modelviewPushed = true;
    }

    public void pushProjection(GL gl)
    {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        this.projectionPushed = true;
    }

    public void pushTexture(GL gl)
    {
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glPushMatrix();
        this.texturePushed = true;
    }

    public void pop(GL gl)
    {
        if (this.attribsPushed)
        {
            gl.glPopAttrib();
            this.attribsPushed = false;
        }

        if (this.clientAttribsPushed)
        {
            gl.glPopClientAttrib();
            this.clientAttribsPushed = false;
        }

        if (this.modelviewPushed)
        {
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPopMatrix();
            this.modelviewPushed = false;
        }

        if (this.projectionPushed)
        {
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPopMatrix();
            this.projectionPushed = false;
        }

        if (this.texturePushed)
        {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPopMatrix();
            this.texturePushed = false;
        }
    }

    public void pushModelviewIdentity(GL gl)
    {
        gl.glMatrixMode(GL.GL_MODELVIEW);
        this.modelviewPushed = true;
        gl.glPushMatrix();
        gl.glLoadIdentity();
    }

    public void pushProjectionIdentity(GL gl)
    {
        gl.glMatrixMode(GL.GL_PROJECTION);
        this.projectionPushed = true;
        gl.glPushMatrix();
        gl.glLoadIdentity();
    }

    public void pushTextureIdentity(GL gl)
    {
        gl.glMatrixMode(GL.GL_TEXTURE);
        this.texturePushed = true;
        gl.glPushMatrix();
        gl.glLoadIdentity();
    }
}
