/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.geom.Vec4;

import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import java.util.*;

/**
 * GLUTessellatorSupport is a utility class for configuring and using a {@link javax.media.opengl.glu.GLUtessellator} to
 * tessellate complex polygons into triangles.
 * <p/>
 * The standard pattern for using GLUTessellatorSupport to prepare a GLUtessellator is as follows: <code> GLU glu = new
 * GLU();<br/> GLUTessellatorSupport glts = new GLUTessellatorSupport();<br/> GLUtessellatorCallback cb = ...; //
 * Reference to an implementation of GLUtessellatorCallback.<br/> Vec4 normal = new Vec4(0, 0, 1); // The polygon's
 * normal. This example shows an appropriate normal for tessellating x-y coordinates.<br/> <br/><br/>
 * glts.beginTessellation(glu, cb, new Vec4(0, 0, 1));<br/> try<br/> {<br/> GLUtessellator tess =
 * glts.getGLUtessellator();<br/> }<br/> finally<br/> {<br/> glts.endTessellation(glu);<br/> }<br/> </code>
 *
 * @author dcollins
 * @version $Id$
 */
public class GLUTessellatorSupport
{
    protected GLUtessellator tess;

    /** Creates a new GLUTessellatorSupport, but otherwise does nothing. */
    public GLUTessellatorSupport()
    {
    }

    /**
     * Returns this GLUTessellatorSupport's internal {@link javax.media.opengl.glu.GLUtessellator} instance. This
     * returns a valid GLUtessellator instance if called between {@link #beginTessellation(javax.media.opengl.glu.GLU,
     * javax.media.opengl.glu.GLUtessellatorCallback, gov.nasa.worldwind.geom.Vec4)} and {@link
     * #endTessellation(javax.media.opengl.glu.GLU)}. This returns null if called from outside a
     * beginTessellation/endTessellation block.
     *
     * @return the internal GLUtessellator instance, or null if called from outside a beginTessellation/endTessellation
     *         block.
     */
    public GLUtessellator getGLUtessellator()
    {
        return this.tess;
    }

    /**
     * Prepares this GLUTessellatorSupport's internal GLU tessellator for use. This initializes the internal
     * GLUtessellator to a new instance by invoking {@link javax.media.opengl.glu.GLU#gluNewTess()}, and configures the
     * tessellator with the specified callback and normal with calls to {@link javax.media.opengl.glu.GLU#gluTessCallback(javax.media.opengl.glu.GLUtessellator,
     * int, javax.media.opengl.glu.GLUtessellatorCallback)} and {@link javax.media.opengl.glu.GLU#gluTessNormal(javax.media.opengl.glu.GLUtessellator,
     * double, double, double)}, respectively.
     *
     * @param glu      a GLU context.
     * @param callback the callback to configure the GLU tessellator with.
     * @param normal   the normal to configure the GLU tessellator with.
     *
     * @throws IllegalArgumentException if any of the GLU, the callback, or the normal is null.
     */
    public void beginTessellation(GLU glu, GLUtessellatorCallback callback, Vec4 normal)
    {
        if (glu == null)
        {
            String message = Logging.getMessage("nullValue.GLUIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (callback == null)
        {
            String message = Logging.getMessage("nullValue.CallbackIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (normal == null)
        {
            String message = Logging.getMessage("nullValue.NormalIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.tess = glu.gluNewTess();
        glu.gluTessNormal(this.tess, normal.x, normal.y, normal.z);
        glu.gluTessCallback(this.tess, GLU.GLU_TESS_BEGIN, callback);
        glu.gluTessCallback(this.tess, GLU.GLU_TESS_VERTEX, callback);
        glu.gluTessCallback(this.tess, GLU.GLU_TESS_END, callback);
        glu.gluTessCallback(this.tess, GLU.GLU_TESS_COMBINE, callback);
    }

    /**
     * Frees any GLU resources used by this GLUTessellatorSupport, and invalidates this instance's internal GLU
     * tessellator.
     *
     * @param glu a GLU context.
     *
     * @throws IllegalArgumentException if the GLU is null.
     */
    public void endTessellation(GLU glu)
    {
        if (glu == null)
        {
            String message = Logging.getMessage("nullValue.GLUIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        glu.gluTessCallback(this.tess, GLU.GLU_TESS_BEGIN, null);
        glu.gluTessCallback(this.tess, GLU.GLU_TESS_VERTEX, null);
        glu.gluTessCallback(this.tess, GLU.GLU_TESS_END, null);
        glu.gluTessCallback(this.tess, GLU.GLU_TESS_COMBINE, null);
        this.tess = null;
    }

    /**
     * Creates a new {@link javax.media.opengl.glu.GLUtessellatorCallback} that draws tessellated polygons as OpenGL
     * primitives by calling glBegin, glEnd, and glVertex.
     *
     * @param gl the GL context to draw into.
     *
     * @return a new GLUtessellatorCallback for drawing tessellated polygons as OpenGL primtives.
     *
     * @throws IllegalArgumentException if the GL is null.
     */
    public static GLUtessellatorCallback createOGLDrawPrimitivesCallback(GL gl)
    {
        if (gl == null)
        {
            String message = Logging.getMessage("nullValue.GLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return new OGLDrawPrimitivesCallback(gl);
    }

    protected static class OGLDrawPrimitivesCallback extends GLUtessellatorCallbackAdapter
    {
        protected final GL gl;

        public OGLDrawPrimitivesCallback(GL gl)
        {
            if (gl == null)
            {
                String message = Logging.getMessage("nullValue.GLIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            this.gl = gl;
        }

        public void begin(int type)
        {
            this.gl.glBegin(type);
        }

        public void vertex(Object vertexData)
        {
            double[] coords = (double[]) vertexData;
            this.gl.glVertex3f((float) coords[0], (float) coords[1], (float) coords[2]);
        }

        public void end()
        {
            this.gl.glEnd();
        }

        public void combine(double[] coords, Object[] data, float[] weight, Object[] outData)
        {
            outData[0] = coords;
        }
    }

    /** Provides the callback class used to capture the shapes determined by the tessellator. */
    public static class CollectIndexListsCallback extends GLUtessellatorCallbackAdapter
    {
        protected int numIndices;
        protected int currentType;
        protected List<Integer> currentPrim;
        protected List<List<Integer>> prims = new ArrayList<List<Integer>>();
        protected List<Integer> primTypes = new ArrayList<Integer>();

        public List<List<Integer>> getPrims()
        {
            return prims;
        }

        public List<Integer> getPrimTypes()
        {
            return primTypes;
        }

        public int getNumIndices()
        {
            return this.numIndices;
        }

        public void begin(int type)
        {
            this.currentType = type;
            this.currentPrim = new ArrayList<Integer>();
        }

        public void vertex(Object vertexData)
        {
            this.currentPrim.add((Integer) vertexData);
            ++this.numIndices;
        }

        @Override
        public void end()
        {
            this.primTypes.add(this.currentType);
            this.prims.add(this.currentPrim);

            this.currentPrim = null;
        }

        public void combine(double[] coords, Object[] data, float[] weight, Object[] outData)
        {
//            System.out.println("COMBINE CALLED");
            outData[0] = data[0];
        }
    }
}
