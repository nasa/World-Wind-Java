/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.*;
import java.util.List;

/**
 * @author tag
 * @version $Id$
 */
public class FBOTexture extends FramebufferTexture
{
    public FBOTexture(WWTexture imageSource, Sector sector, List<LatLon> corners)
    {
        super(imageSource, sector, corners);

        this.width = 1024;
        this.height = 1024;
    }

    protected Texture initializeTexture(DrawContext dc)
    {
        // Bind actually binds the source texture only if the image source is available, otherwise it initiates image
        // source retrieval. If bind returns false, the image source is not yet available.
        if (this.sourceTexture == null || !this.sourceTexture.bind(dc))
            return null;

        // Ensure that the source texture size is available so that the FBO can be sized to match the source image.
        if (sourceTexture.getWidth(dc) < 1 || sourceTexture.getHeight(dc) < 1)
            return null;

        // Limit FBO size to the max OGL size or 4k, whichever is smaller
        int maxSize = Math.min(dc.getGLRuntimeCapabilities().getMaxTextureSize(), 4096);

        this.width = Math.min(maxSize, sourceTexture.getWidth(dc));
        this.height = Math.min(maxSize, sourceTexture.getHeight(dc));

        GL gl = GLContext.getCurrent().getGL();

        int[] previousFbo = new int[1];
        gl.glGetIntegerv(GL.GL_FRAMEBUFFER_BINDING_EXT, previousFbo, 0);

        int[] fbo = new int[1];
        gl.glGenFramebuffersEXT(1, fbo, 0);

        try
        {
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fbo[0]);
            
            TextureData td = new TextureData(GL.GL_RGBA, this.width, this.height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                false, false, true, BufferUtil.newByteBuffer(this.width * this.height * 4), null);
            Texture t = TextureIO.newTexture(td);
            t.bind();

            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

            gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D,
                t.getTextureObject(), 0);

            int status = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
            if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT)
            {
                this.generateTexture(dc, this.width, this.height);
            }
            else
            {
                String msg = Logging.getMessage("FBOTexture.TextureNotCreated");
                throw new IllegalStateException(msg);
            }

            dc.getTextureCache().put(this, t);

            return t;
        }
        finally
        {
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, previousFbo[0]);
            gl.glDeleteFramebuffersEXT(1, fbo, 0);
        }
    }
}
