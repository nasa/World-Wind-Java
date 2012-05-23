package performance.VBORenderer;

import com.sun.opengl.util.BufferUtil;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.*;

/**
 * Class generates and renders a terrain mesh, either rendering it using Vertex Buffer Objects, or else Vertex Arrays.
 *
 * @author ccrick
 * @version $Id$
 */

class VBORenderer implements ActionListener, ChangeListener, GLEventListener
{
    // Mesh Generation Paramaters
    private static final float MESH_HEIGHTSCALE = 1.0f;     // Mesh Height Scale
    private static final long UPDATE_TIME = 1000;        // elapsed time after which to update the framerate

    private long currentTime;
    private long elapsedTime = 0;                               // time elapsed since last hit display()
    private long previousTime = System.currentTimeMillis();     // time when previosly hit display() method

    private int frameCount = 0;                             // total number of frames so far in this interval
    private long renderStartTime;                           // time in nanoseconds when render code began
    private long totalInterval = 0;                         // total rendering time so far in this interval
    private double fps = 0.0d;

    private Mesh mesh = null;                                           // Mesh Data
    private long drawInterval = 0;                                      // Draw Interval
    private float yRotation = 0.0f;                                     // Rotation
    private GLDisplay glDisplay;
    private GLU glu = new GLU();

    private boolean resolutionChanged = false;
    private boolean usageChanged = false;
    private boolean VBOChanged = false;
    private boolean dataTypeChanged = false;
    private boolean renderModeChanged = false;
    private boolean reloadFirst = true;
    private boolean enableLighting = false;

    private float[] lightAmbient = {1.0f, 1.0f, 1.0f, 1.0f};
    //private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightDiffuse = {0.6f, 0.6f, 0.6f, 1.0f};
    private float[] lightPosition = {0.0f, -50.0f * MESH_HEIGHTSCALE, 0.0f, 1.0f};

    public VBORenderer(GLDisplay glDisplay)
    {
        this.glDisplay = glDisplay;
    }

    public static void main(String[] args)
    {
        GLDisplay gldisplay = GLDisplay.createGLDisplay();
        VBORenderer renderer = new VBORenderer(gldisplay);
        gldisplay.addGLEventListener(renderer);
        gldisplay.addControlPanelActionListener(renderer);
        gldisplay.addControlPanelChangeListener(renderer);
        gldisplay.start();
    }

    public void actionPerformed(ActionEvent e)
    {

        String action = e.getActionCommand();
        // Buffer Type events
        if (action.equalsIgnoreCase("VBO"))
        {
            if (!glDisplay.getUseVBOs())
                VBOChanged = true;
        }
        else if (action.equalsIgnoreCase("VA"))
        {
            if (glDisplay.getUseVBOs())
                VBOChanged = true;
        }
        // Data type events
        if (action.equalsIgnoreCase("Float"))
        {
            if (!glDisplay.getUseFloats())
            {
                //glDisplay.setUseFloats(true);
                dataTypeChanged = true;
            }
        }
        else if (action.equalsIgnoreCase("Double"))
        {
            if (glDisplay.getUseFloats())
            {
                //glDisplay.setUseFloats(false);
                dataTypeChanged = true;
            }
        }
        // Render Mode events
        if (action.equalsIgnoreCase("GL_TRIANGLES"))
        {
            if (glDisplay.getUseTriangleStrip())
            {
                glDisplay.setUseTriangleStrip(false);
                renderModeChanged = true;
            }
        }
        else if (action.equalsIgnoreCase("GL_TRIANGLE_STRIP"))
        {
            if (!glDisplay.getUseTriangleStrip())
            {
                glDisplay.setUseTriangleStrip(true);
                renderModeChanged = true;
            }
        }

        // Buffer usage events
        if (action.equalsIgnoreCase("Static"))
        {
            if (glDisplay.getBufferDataUsage() != GL.GL_STATIC_DRAW_ARB)
            {
                glDisplay.setBufferDataUsage(GL.GL_STATIC_DRAW_ARB);
                usageChanged = true;
            }
        }
        else if (action.equalsIgnoreCase("Dynamic"))
        {
            if (glDisplay.getBufferDataUsage() != GL.GL_DYNAMIC_DRAW_ARB)
            {
                glDisplay.setBufferDataUsage(GL.GL_DYNAMIC_DRAW_ARB);
                usageChanged = true;
            }
        }
        else if (action.equalsIgnoreCase("Stream"))
        {
            if (glDisplay.getBufferDataUsage() != GL.GL_STREAM_DRAW_ARB)
            {
                glDisplay.setBufferDataUsage(GL.GL_STREAM_DRAW_ARB);
                usageChanged = true;
            }
        }
    }

    public void stateChanged(ChangeEvent e)
    {
        JSlider slider = (JSlider) e.getSource();
        if (!slider.getValueIsAdjusting())
        {
            String name = slider.getName();
            int value = slider.getValue();
            if (name.equalsIgnoreCase("Reload"))
            {
                glDisplay.setLoadFrequency((float) Math.pow(2.0, (double) value));
            }
            else if (name.equalsIgnoreCase("Resolution"))
            {
                glDisplay.setMeshResolution((float) Math.pow(2.0, (double) value));
                resolutionChanged = true;
            }
        }
    }

    public void init(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();

        // Check For VBO support
        final boolean VBOsupported = gl.isFunctionAvailable("glGenBuffersARB") &&
            gl.isFunctionAvailable("glBindBufferARB") &&
            gl.isFunctionAvailable("glBufferDataARB") &&
            gl.isFunctionAvailable("glDeleteBuffersARB");

        // Load The Mesh Data
        mesh = new Mesh();                                        // Instantiate the Mesh
        try
        {
            if (glDisplay.getUseFloats())
            {
                mesh.loadHeightmap(gl, "testData/Final.jpg",   // Load the Heightmap, using floats
                    MESH_HEIGHTSCALE,
                    glDisplay.getMeshResolution(),
                    VBOsupported,
                    BufferUtil.SIZEOF_FLOAT);
            }
            else
            {
                mesh.loadHeightmap(gl, "testData/Final.jpg",   // Load the Heightmap using doubles
                    (double) MESH_HEIGHTSCALE,
                    (double) glDisplay.getMeshResolution(),
                    VBOsupported,
                    BufferUtil.SIZEOF_DOUBLE);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                String title = glDisplay.getAppTitle() + " - " + glDisplay.getTriangleCount() + " triangles";
                if (VBOsupported && glDisplay.getUseVBOs())
                {
                    title += ", using VBO";
                }
                else
                {
                    title += ", using Vertex Array";
                }
                if (glDisplay.getUseFloats())
                {
                    title += ", using Floats";
                }
                else
                {
                    title += ", using Doubles";
                }
                glDisplay.setTitle(title);
            }
        });

        // Setup GL States
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);                         // Black Background
        gl.glClearDepth(1.0f);                                           // Depth Buffer Setup
        gl.glDepthFunc(GL.GL_LEQUAL);                                    // The Type Of Depth Testing (Less Or Equal)
        gl.glEnable(GL.GL_DEPTH_TEST);                                   // Enable Depth Testing
        gl.glShadeModel(GL.GL_SMOOTH);                                   // Select Smooth Shading
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT,
            GL.GL_NICEST);        // Set Perspective Calculations To Most Accurate
        gl.glEnable(GL.GL_TEXTURE_2D);                                    // Enable Textures
        gl.glColor4f(0.9f, 0.9f, 1.0f, 1.0f);                             // Set The Color To Pale Blue

        // Set up lighting
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, this.lightAmbient, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, this.lightDiffuse, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, this.lightPosition, 0);
        gl.glEnable(GL.GL_LIGHT1);
        //gl.glEnable(GL.GL_LIGHTING);
    }

    private void update(long milliseconds)
    {                                // Perform Motion Updates Here
        yRotation += (float) (milliseconds) / 1000.0f * 25.0f;                // Consistantly Rotate The Scenery
    }

    public void display(final GLAutoDrawable drawable)
    {
        if (VBOChanged)
        {
            VBOChanged = false;
            reloadFirst = true;                // release/rebind VBO as appropriate in mesh.render()
            boolean vbos = glDisplay.getUseVBOs();
            glDisplay.setUseVBOs(!vbos);
            init(drawable);                    // reset the mesh (not really necessary)
        }
        if (dataTypeChanged)
        {
            dataTypeChanged = false;
            reloadFirst = false;
            boolean usefloats = glDisplay.getUseFloats();
            glDisplay.setUseFloats(!usefloats);
            init(drawable);                    // reset the mesh using new data type
        }
        if (renderModeChanged)
        {
            renderModeChanged = false;
            reloadFirst = false;
            init(drawable);                    // reset the mesh using new data type
        }
        if (resolutionChanged)
        {
            resolutionChanged = false;
            reloadFirst = true;                // release/rebind VBO as appropriate in mesh.render()            
            init(drawable);                    // reset the mesh at the new resolution
        }
        if (usageChanged)
        {
            usageChanged = false;
            init(drawable);                    // reset the mesh with new usage setting
        }

        // compute elapsed time since last hit display()
        currentTime = System.currentTimeMillis();
        update(currentTime - previousTime);
        elapsedTime += currentTime - previousTime;
        previousTime = currentTime;

        // begin timing rendering code
        renderStartTime = System.nanoTime();
        GL gl = drawable.getGL();

        float viewAngle = 60;
        float viewHeight = -1400 * MESH_HEIGHTSCALE;

        int iterationCount = 20; // draw this many frames within each timing bracket
        for (int i = 0; i < iterationCount; i++)
        {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);    // Clear Screen And Depth Buffer
            gl.glLoadIdentity();                                            // Reset The Modelview Matrix

            if (glDisplay.getUseFloats())    // float version
            {
                // Move The Camera
                gl.glTranslatef(0, 0, viewHeight);             // Move above the terrain
                gl.glRotatef(viewAngle, 0.0f, 0.0f, 0.0f);                        // Look Down
                gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f);                    // Rotate The Camera

                // Render the mesh
                mesh.render(gl, GL.GL_FLOAT, BufferUtil.SIZEOF_FLOAT, mesh.vertices, mesh.texCoords);
            }
            else                            // double version
            {
                // Move The Camera
                gl.glTranslatef(0, 0, viewHeight);             // Move above the terrain
                gl.glRotatef(viewAngle, 0.0f, 0.0f, 0.0f);                        // Look Down
                gl.glRotated((double) yRotation, 0.0d, 1.0d, 0.0d);            // Rotate The Camera

                // Render the mesh
                mesh.render(gl, GL.GL_DOUBLE, BufferUtil.SIZEOF_DOUBLE, mesh.verticesDbl, mesh.texCoordsDbl);
            }
        }
        gl.glFinish(); // cause OpenGL to wait until it renders everything

        // compute time taken to render this frame
        drawInterval = (System.nanoTime() - renderStartTime);
        totalInterval += drawInterval;
        frameCount += iterationCount;

        if (elapsedTime > UPDATE_TIME)                          // update framerate in the display
        {
            double intervalInSeconds = totalInterval / 1e9;
            fps = frameCount / intervalInSeconds;
            glDisplay.updateFPS(fps);
            frameCount = 0;
            totalInterval = 0;
            elapsedTime = 0;
        }

        // This interleaves the repaint with the UI events in the event queue.
        SwingUtilities.invokeLater(
            new Runnable()
            {
                public void run()
                {
                    drawable.repaint();
                }
            }
        );
    }

    public void reshape(GLAutoDrawable drawable,
        int xstart,
        int ystart,
        int width,
        int height)
    {
        GL gl = drawable.getGL();

        height = (height == 0) ? 1 : height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(65, (float) width / height, 1, 2000);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable,
        boolean modeChanged,
        boolean deviceChanged)
    {
    }

    private class Mesh
    {
        // Mesh Data
        private int vertexCount;                                    // Vertex Count
        private int verticesAlongWidth;
        private int verticesAlongHeight;
        private int indexCount;                                     // Index Count

        private FloatBuffer vertices;                               // Vertex Data (Float)
        private FloatBuffer texCoords;                              // Texture Coordinates (Float)
        private DoubleBuffer verticesDbl;                           // Vertex Data (Double)
        private DoubleBuffer texCoordsDbl;                          // Texture Coordinates (Double)
        private IntBuffer indices;                                  // Indices (for TRIANGLE_STRIP)

        // Vertex Buffer Object Names
        private int[] VBOVertices = new int[1];                     // Vertex VBO Name
        private int[] VBOTexCoords = new int[1];                    // Texture Coordinate VBO Name
        private int[] VBOIndices = new int[1];                      // Index VBO Name

        private int[] textureId = new int[1];                       // Texture ID

        public int getVertexCount()
        {
            return vertexCount;
        }

        public boolean loadHeightmap(GL gl, String szPath, float flHeightScale, float flResolution,
            boolean VBOsupport, int bufferTypeSize) throws IOException
        {
            TextureReader.Texture texture = null;
            texture = TextureReader.readTexture(szPath);

            // Generate Vertex Field
            if (glDisplay.getUseTriangleStrip())           // GL_TRIANGLE_STRIP
            {
                verticesAlongWidth = (int) (texture.getWidth() / flResolution) + 1;
                verticesAlongHeight = (int) (texture.getHeight() / flResolution) + 1;
                vertexCount = (verticesAlongWidth * verticesAlongHeight);
            }
            else                                          // GL_TRIANGLES
                vertexCount = (int) (texture.getWidth() * texture.getHeight() * 6 / (flResolution * flResolution));

            vertices = BufferUtil.newFloatBuffer(vertexCount * 3);                      // Allocate Vertex Data
            texCoords = BufferUtil.newFloatBuffer(vertexCount * 2);                     // Allocate Tex Coord Data

            for (int nZ = 0; nZ <= texture.getHeight(); nZ += (int) flResolution)
            {
                for (int nX = 0; nX <= texture.getWidth(); nX += (int) flResolution)
                {
                    if (glDisplay.getUseTriangleStrip())     // create vertices in GL_TRIANGLE_STRIP format
                    {
                        // Set The Data, Using PtHeight To Obtain The Y Value
                        vertices.put((float) nX - (texture.getWidth() / 2f));               // X coord
                        vertices.put(pointHeightFl(texture, nX, nZ) * flHeightScale);       // Y coord
                        vertices.put((float) nZ - (texture.getHeight() / 2f));              // Z coord

                        // Stretch The Texture Across The Entire Mesh
                        texCoords.put((float) nX / texture.getWidth());
                        texCoords.put((float) nZ / texture.getHeight());
                    }
                    else       // create vertices for rendering as GL_TRIANGLES
                    {
                        // skip last row and/or column when using GL_TRIANGLES
                        if (nZ == texture.getHeight() || nX == texture.getWidth())
                            break;

                        for (int nTri = 0; nTri < 6; nTri++)
                        {
                            // Using This Quick Hack, Figure The X,Z Position Of The Point
                            float flX = (float) nX + ((nTri == 1 || nTri == 2 || nTri == 5) ? flResolution : 0.0f);
                            float flZ = (float) nZ + ((nTri == 2 || nTri == 4 || nTri == 5) ? flResolution : 0.0f);

                            // Set The Data, Using PtHeight To Obtain The Y Value
                            vertices.put(flX - (texture.getWidth() / 2f));
                            vertices.put(pointHeightFl(texture, (int) flX, (int) flZ) * flHeightScale);
                            vertices.put(flZ - (texture.getHeight() / 2f));

                            // Stretch The Texture Across The Entire Mesh
                            texCoords.put(flX / texture.getWidth());
                            texCoords.put(flZ / texture.getHeight());
                        }
                    }
                }
            }
            vertices.rewind();
            texCoords.rewind();

            if (glDisplay.getUseTriangleStrip())       // using GL_TRIANGLE_STRIP  mode ( 2 * width * height triangles )
            {
                indexCount = generateIndices(verticesAlongWidth, verticesAlongHeight);
                glDisplay.setTriangleCount((verticesAlongWidth - 1) * (verticesAlongHeight - 1) * 2);
            }
            else            // using GL_TRIANGLES render mode (3 vertices / triangle )
                glDisplay.setTriangleCount(this.getVertexCount() / 3);

            loadTexture(gl, texture);

            if (VBOsupport && glDisplay.getUseVBOs())
            {
                // Load Vertex Data Into The Graphics Card Memory
                buildVBOs(gl, bufferTypeSize, vertices, texCoords);         // Build The VBOs (Float version)
            }

            return true;
        }

        public boolean loadHeightmap(GL gl, String szPath, double dblHeightScale, double dblResolution,
            boolean VBOsupport, int bufferTypeSize) throws IOException
        {
            TextureReader.Texture texture = null;
            texture = TextureReader.readTexture(szPath);

            // Generate Vertex Field
            if (glDisplay.getUseTriangleStrip())           // GL_TRIANGLE_STRIP
            {
                verticesAlongWidth = (int) (texture.getWidth() / dblResolution) + 1;
                verticesAlongHeight = (int) (texture.getHeight() / dblResolution) + 1;
                vertexCount = (int) (verticesAlongWidth * verticesAlongHeight);
            }
            else                                          // GL_TRIANGLES
                vertexCount = (int) (texture.getWidth() * texture.getHeight() * 6 / (dblResolution * dblResolution));

            verticesDbl = BufferUtil.newDoubleBuffer(vertexCount * 3);                      // Allocate Vertex Data
            texCoordsDbl = BufferUtil.newDoubleBuffer(vertexCount * 2);                     // Allocate Tex Coord Data
            for (int nZ = 0; nZ <= texture.getHeight(); nZ += (int) dblResolution)
            {
                for (int nX = 0; nX <= texture.getWidth(); nX += (int) dblResolution)
                {
                    if (glDisplay.getUseTriangleStrip())     // create vertices in GL_TRIANGLE_STRIP format
                    {
                        // Set The Data, Using PtHeight To Obtain The Y Value
                        verticesDbl.put((double) nX - (texture.getWidth() / 2d));               // X coord
                        verticesDbl.put(pointHeightDbl(texture, nX, nZ) * dblHeightScale);      // Y coord
                        verticesDbl.put((double) nZ - (texture.getHeight() / 2d));              // Z coord

                        // Stretch The Texture Across The Entire Mesh
                        texCoordsDbl.put((double) nX / texture.getWidth());
                        texCoordsDbl.put((double) nZ / texture.getHeight());
                    }
                    else       // create vertices for rendering as GL_TRIANGLES
                    {
                        // skip last row and/or column when using GL_TRIANGLES
                        if (nZ == texture.getHeight() || nX == texture.getWidth())
                            break;

                        for (int nTri = 0; nTri < 6; nTri++)
                        {
                            // Using This Quick Hack, Figure The X,Z Position Of The Point
                            double dblX = (double) nX + ((nTri == 1 || nTri == 2 || nTri == 5) ? dblResolution : 0.0d);
                            double dblZ = (double) nZ + ((nTri == 2 || nTri == 4 || nTri == 5) ? dblResolution : 0.0d);

                            // Set The Data, Using PtHeight To Obtain The Y Value
                            verticesDbl.put(dblX - (texture.getWidth() / 2d));
                            verticesDbl.put(pointHeightDbl(texture, (int) dblX, (int) dblZ) * dblHeightScale);
                            verticesDbl.put(dblZ - (texture.getHeight() / 2d));

                            // Stretch The Texture Across The Entire Mesh
                            texCoordsDbl.put(dblX / texture.getWidth());
                            texCoordsDbl.put(dblZ / texture.getHeight());
                        }
                    }
                }
            }
            verticesDbl.rewind();
            texCoordsDbl.rewind();

            if (glDisplay.getUseTriangleStrip())       // using GL_TRIANGLE_STRIP  mode ( 2 * width * height triangles )
            {
                indexCount = generateIndices(verticesAlongWidth, verticesAlongHeight);
                glDisplay.setTriangleCount((verticesAlongWidth - 1) * (verticesAlongHeight - 1) * 2);
            }
            else            // using GL_TRIANGLES render mode (3 vertices / triangle )
                glDisplay.setTriangleCount(this.getVertexCount() / 3);

            loadTexture(gl, texture);

            if (VBOsupport && glDisplay.getUseVBOs())
            {
                // Load Vertex Data Into The Graphics Card Memory
                buildVBOs(gl, bufferTypeSize, verticesDbl, texCoordsDbl);         // Build The VBOs (Float version)
            }

            return true;
        }

        public boolean loadTexture(GL gl, TextureReader.Texture texture)
        {
            // Load The Texture Into OpenGL
            gl.glGenTextures(1, textureId, 0);                            // Get An Open ID
            gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);             // Bind The Texture
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE, texture.getPixels());
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            return true;
        }

        // Fills index buffer, returns the number of indices

        private int generateIndices(int verticesAlongWidth, int verticesAlongLength)
        {
            int sideSize = verticesAlongLength - 1;

            int numIndices = 2 * sideSize * sideSize + 4 * sideSize - 2;
            indices = BufferUtil.newIntBuffer(numIndices);
            int k = 0;
            for (int i = 0; i < sideSize; i++)
            {
                indices.put(k);
                if (i > 0)
                {
                    indices.put(++k);
                    indices.put(k);
                }

                if (i % 2 == 0) // even
                {
                    indices.put(++k);
                    for (int j = 0; j < sideSize; j++)
                    {
                        k += sideSize;
                        indices.put(k);
                        indices.put(++k);
                    }
                }
                else // odd
                {
                    indices.put(--k);
                    for (int j = 0; j < sideSize; j++)
                    {
                        k -= sideSize;
                        indices.put(k);
                        indices.put(--k);
                    }
                }
            }

            indices.flip();

            return numIndices;
        }

        public void render(GL gl, int glDataType, int bufferTypeSize, Buffer vertexBuffer, Buffer textureBuffer)
        {
            // Enable Pointers
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);                     // Enable Vertex Arrays
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);              // Enable Texture Coord Arrays

            // Set Pointers To Our Data
            if (glDisplay.getUseVBOs())
            {
                if (reloadFirst)
                {   // rebind buffer if have just switched back to VBO's
                    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, mesh.VBOVertices[0]);
                    reloadFirst = false;
                }

                gl.glVertexPointer(3, glDataType, 0, 0);        // Set The Vertex Pointer To The Vertex Buffer
                if (glDisplay.getReloadNow())
                {
                    if (glDisplay.getRebindVBO())     // rebind only if we are rebinding at every reload
                        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, mesh.VBOVertices[0]);
                    // copy vertices starting from 0 offset
                    gl.glBufferSubDataARB(GL.GL_ARRAY_BUFFER_ARB, 0, vertexCount * 3 * bufferTypeSize, vertexBuffer);
                }

                // Set The TexCoord Pointer To The TexCoord portion of the Buffer
                gl.glTexCoordPointer(2, glDataType, 0, vertexCount * 3 * bufferTypeSize);
                if (glDisplay.getReloadNow())
                {   // copy textureCoords after vertices
                    gl.glBufferSubDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 3 * bufferTypeSize,
                        vertexCount * 2 * bufferTypeSize, textureBuffer);
                    glDisplay.setReloadNow(false);
                }

                // If using GL_TRIANGLE_STRIP mode, set the Index pointer to the index buffer
                if (glDisplay.getUseTriangleStrip())
                {
                    gl.glBindBufferARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, mesh.VBOIndices[0]);
                    // gl.glIndexPointer(GL.GL_INT, 0, mesh.indices);
                    reloadFirst = true;         // rebind vertex buffer at each render, because using 2 buffers
                }

                // Render
                if (glDisplay.getUseTriangleStrip())       // draw using GL_TRIANGLE_STRIP
                    gl.glDrawElements(GL.GL_TRIANGLE_STRIP, mesh.indices.limit(), GL.GL_UNSIGNED_INT, 0);
                else                                     // draw using GL_TRIANGLES
                    gl.glDrawArrays(GL.GL_TRIANGLES, 0, mesh.vertexCount);    // Draw All Of The Triangles At Once
            }
            else
            {
                if (reloadFirst)
                {   // Release VBOs with ID 0 to reactivate normal Vertex Arrays
                    gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
                    gl.glBindBufferARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
                    reloadFirst = false;
                }

                gl.glVertexPointer(3, glDataType, 0, vertexBuffer);      // Set The Vertex Pointer To Our Vertex Data
                gl.glTexCoordPointer(2, glDataType, 0, textureBuffer);   // Set The Vertex Pointer To Our TexCoord Data

                // Render
                if (glDisplay.getUseTriangleStrip())       // draw using GL_TRIANGLE_STRIP
                    gl.glDrawElements(GL.GL_TRIANGLE_STRIP, mesh.indices.limit(), GL.GL_UNSIGNED_INT, mesh.indices);
                else                                     // draw using GL_TRIANGLES
                    gl.glDrawArrays(GL.GL_TRIANGLES, 0, mesh.vertexCount);    // Draw All Of The Triangles At Once
            }

            // Disable Pointers
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);                    // Disable Vertex Arrays
            gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);                // Disable Texture Coord Arrays
        }

        private float pointHeightFl(TextureReader.Texture texture, int nX, int nY)
        {
            // Calculate The Position In The Texture, Careful Not To Overflow
            int nPos = ((nX % texture.getWidth()) + ((nY % texture.getHeight()) * texture.getWidth())) * 3;
            float flR = unsignedByteToInt(texture.getPixels().get(nPos));            // Get The Red Component
            float flG = unsignedByteToInt(texture.getPixels().get(nPos + 1));        // Get The Green Component
            float flB = unsignedByteToInt(texture.getPixels().get(nPos + 2));        // Get The Blue Component
            return (0.299f * flR + 0.587f * flG
                + 0.114f * flB);        // Calculate The Height Using The Luminance Algorithm
        }

        private double pointHeightDbl(TextureReader.Texture texture, int nX, int nY)
        {
            // Calculate The Position In The Texture, Careful Not To Overflow
            int nPos = ((nX % texture.getWidth()) + ((nY % texture.getHeight()) * texture.getWidth())) * 3;
            double dblR = unsignedByteToInt(texture.getPixels().get(nPos));            // Get The Red Component
            double dblG = unsignedByteToInt(texture.getPixels().get(nPos + 1));        // Get The Green Component
            double dblB = unsignedByteToInt(texture.getPixels().get(nPos + 2));        // Get The Blue Component
            return (0.299d * dblR + 0.587d * dblG
                + 0.114d * dblB);        // Calculate The Height Using The Luminance Algorithm
        }

        private int unsignedByteToInt(byte b)
        {
            return (int) b & 0xFF;
        }

        // new version, using one VBO, partitioned into two parts: vertices, then texCoords

        private void buildVBOs(GL gl, int bufferTypeSize, Buffer vertexBuffer, Buffer textureBuffer)
        {
            // TODO: delete previously allocated buffer, if one exists
            gl.glDeleteBuffersARB(1, VBOVertices, 0);
            gl.glDeleteBuffersARB(1, VBOIndices, 0);

            // Generate And Bind The Vertex Buffer
            gl.glGenBuffersARB(1, VBOVertices, 0);                              // Get A Valid Name
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOVertices[0]);         // Bind The Buffer
            gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, (vertexCount * 5) * bufferTypeSize,
                null, glDisplay.getBufferDataUsage());

            // copy vertices starting from 0 offset
            gl.glBufferSubDataARB(GL.GL_ARRAY_BUFFER_ARB, 0, vertexCount * 3 * bufferTypeSize, vertexBuffer);
            // copy textureCoords after vertices
            gl.glBufferSubDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 3 * bufferTypeSize,
                vertexCount * 2 * bufferTypeSize, textureBuffer);

            // create and bind index VBO if using TRIANGLE_STRIP
            if (glDisplay.getUseTriangleStrip())
            {
                // Generate And Bind The Index Buffer
                gl.glGenBuffersARB(1, VBOIndices, 0);                                       // Get A Valid Name
                gl.glBindBufferARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, VBOIndices[0]);          // Bind The Buffer
                gl.glBufferDataARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, indexCount * BufferUtil.SIZEOF_INT,
                    mesh.indices, glDisplay.getBufferDataUsage());
            }
        }

/*      // original version, using two separate VBOs
        private void buildVBOs(GL gl)
        {
            // Generate And Bind The Vertex Buffer
            gl.glGenBuffersARB(1, VBOVertices, 0);                              // Get A Valid Name
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOVertices[0]);         // Bind The Buffer
            // Load The Data
            gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 3 * BufferUtil.SIZEOF_FLOAT, vertices,
                glDisplay.getBufferDataUsage());

            // Generate And Bind The Texture Coordinate Buffer
            gl.glGenBuffersARB(1, VBOTexCoords, 0);                             // Get A Valid Name
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOTexCoords[0]);        // Bind The Buffer
            // Load The Data
            gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 2 * BufferUtil.SIZEOF_FLOAT, texCoords,
                glDisplay.getBufferDataUsage());

            // Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
            //vertices = null;
            //texCoords = null;
        }
  */
    }
}