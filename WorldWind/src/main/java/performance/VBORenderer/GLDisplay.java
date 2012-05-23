package performance.VBORenderer;

import com.sun.opengl.util.FPSAnimator;

import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import javax.media.opengl.GL;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.*;
import java.util.ArrayList;

public class GLDisplay
{
    private static final int DEFAULT_WIDTH = 780;
    private static final int DEFAULT_HEIGHT = 780;

    private static final int DONT_CARE = -1;
    private static final String appTitle = "Vertex buffer objects";

    private JFrame frame;
    private GLCanvas glCanvas;
    private boolean fullscreen;
    private int width;
    private int height;
    private GraphicsDevice usedDevice;

    private boolean useVBOs = true;                         // use Vertex Buffer Objects (not Vertex Arrays)
    private boolean useFloats = true;
    private boolean useTriangleStrip = false;               // use GL_TRIANGLE_STRIP mode (not GL_TRIANGLES mode)
    private boolean reloadVBO = false;
    private boolean rebindVBO = false;                      // reloading of data (in general) flag
    private boolean reloadNow = false;                      // reload this frame
    private float meshResolution = 8.0f;                    // pixels per vertex, must be >= 1
    private float loadFrequency = 16.0f;
    private int bufferDataUsage = GL.GL_STATIC_DRAW_ARB;    // by default, set VBO usage to STATIC_DRAW
    private int triangleCount = 0;

    private double fps = 0.0d;                                   // most recent framerate

    private ControlPanel controlPanel;
    private MyGLEventListener myGLEventListener = new MyGLEventListener();
    private MyExceptionHandler exceptionHandler = new MyExceptionHandler();

    public static GLDisplay createGLDisplay()
    {
        return createGLDisplay(appTitle, new GLCapabilities());
    }

    public static GLDisplay createGLDisplay(String title, GLCapabilities caps)
    {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        boolean fullscreen = false;
//        if ( device.isFullScreenSupported() ) {
//            int selectedOption = JOptionPane.showOptionDialog(
//                    null,
//                    "How would you like to run this demo?",
//                    title,
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE,
//                    null,
//                    new Object[]{"Fullscreen", "Windowed"},
//                    "Windowed" );
//            fullscreen = selectedOption == 0;
//        }
        return new GLDisplay(title, DEFAULT_WIDTH, DEFAULT_HEIGHT, fullscreen, caps);
    }

    private GLDisplay(String title, int width, int height, boolean fullscreen, GLCapabilities caps)
    {

        this.fullscreen = fullscreen;
        this.width = width;
        this.height = height;

        StatsPanel statsPanel = new StatsPanel();
        statsPanel.createStatsPanel(width, height);
        addGLEventListener(statsPanel);

        controlPanel = new ControlPanel();
        controlPanel.createControlPanel(width, height);
        addGLEventListener(controlPanel);

        glCanvas = new GLCanvas(caps);
        glCanvas.setSize(width * 3 / 4, height);
        glCanvas.setPreferredSize(new Dimension(width * 3 / 4, height));
        glCanvas.setIgnoreRepaint(false);
        glCanvas.addGLEventListener(myGLEventListener);

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        outerPanel.add(statsPanel, BorderLayout.PAGE_START);
        outerPanel.add(controlPanel, BorderLayout.CENTER);

        frame = new JFrame(title);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(outerPanel, BorderLayout.WEST);
        frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

        addKeyListener(new MyKeyAdapter());

        //animator = new com.sun.opengl.util.Animator(glCanvas);
        //animator.setRunAsFastAsPossible(true);
    }

    public void start()
    {
        try
        {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setUndecorated(fullscreen);

            frame.addWindowListener(new MyWindowAdapter());

            if (fullscreen)
            {
                usedDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                usedDevice.setFullScreenWindow(frame);
                usedDevice.setDisplayMode(
                    findDisplayMode(
                        usedDevice.getDisplayModes(),
                        width, height,
                        usedDevice.getDisplayMode().getBitDepth(),
                        usedDevice.getDisplayMode().getRefreshRate()
                    )
                );
            }
            else
            {
                frame.setSize(frame.getContentPane().getPreferredSize());
                frame.setLocation(
                    (screenSize.width - frame.getWidth()) / 2,
                    (screenSize.height - frame.getHeight()) / 2
                );
                frame.setVisible(true);
            }

            glCanvas.requestFocus();

            glCanvas.repaint();
        }
        catch (Exception e)
        {
            exceptionHandler.handleException(e);
        }
    }

    public void stop()
    {
        try
        {
            if (fullscreen)
            {
                usedDevice.setFullScreenWindow(null);
                usedDevice = null;
            }
            frame.dispose();
        }
        catch (Exception e)
        {
            exceptionHandler.handleException(e);
        }
        finally
        {
            System.exit(0);
        }
    }

    private DisplayMode findDisplayMode(DisplayMode[] displayModes, int requestedWidth, int requestedHeight,
        int requestedDepth, int requestedRefreshRate)
    {
        // Try to find an exact match
        DisplayMode displayMode = findDisplayModeInternal(displayModes, requestedWidth, requestedHeight, requestedDepth,
            requestedRefreshRate);

        // Try again, ignoring the requested bit depth
        if (displayMode == null)
            displayMode = findDisplayModeInternal(displayModes, requestedWidth, requestedHeight, DONT_CARE, DONT_CARE);

        // Try again, and again ignoring the requested bit depth and height
        if (displayMode == null)
            displayMode = findDisplayModeInternal(displayModes, requestedWidth, DONT_CARE, DONT_CARE, DONT_CARE);

        // If all else fails try to get any display mode
        if (displayMode == null)
            displayMode = findDisplayModeInternal(displayModes, DONT_CARE, DONT_CARE, DONT_CARE, DONT_CARE);

        return displayMode;
    }

    private DisplayMode findDisplayModeInternal(DisplayMode[] displayModes, int requestedWidth, int requestedHeight,
        int requestedDepth, int requestedRefreshRate)
    {
        DisplayMode displayModeToUse = null;
        for (DisplayMode displayMode : displayModes)
        {
            if ((requestedWidth == DONT_CARE || displayMode.getWidth() == requestedWidth) &&
                (requestedHeight == DONT_CARE || displayMode.getHeight() == requestedHeight) &&
                (requestedHeight == DONT_CARE || displayMode.getRefreshRate() == requestedRefreshRate) &&
                (requestedDepth == DONT_CARE || displayMode.getBitDepth() == requestedDepth))
                displayModeToUse = displayMode;
        }

        return displayModeToUse;
    }

    public void addGLEventListener(GLEventListener glEventListener)
    {
        this.myGLEventListener.addGLEventListener(glEventListener);
    }

    public void removeGLEventListener(GLEventListener glEventListener)
    {
        this.myGLEventListener.removeGLEventListener(glEventListener);
    }

    public void addControlPanelActionListener(ActionListener actionListener)
    {
        this.controlPanel.addActionListener(actionListener);
    }

    public void addControlPanelChangeListener(ChangeListener changeListener)
    {
        this.controlPanel.addChangeListener(changeListener);
    }

    public void addKeyListener(KeyListener l)
    {
        glCanvas.addKeyListener(l);
    }

    public void removeKeyListener(KeyListener l)
    {
        glCanvas.removeKeyListener(l);
    }

    public String getAppTitle()
    {
        return appTitle;
    }

    public String getTitle()
    {
        return frame.getTitle();
    }

    public void setTitle(String title)
    {
        frame.setTitle(title);
    }

    public boolean getUseVBOs()
    {
        return this.useVBOs;
    }

    public void setUseVBOs(boolean useVBOs)
    {
        this.useVBOs = useVBOs;
    }

    public boolean getUseFloats()
    {
        return this.useFloats;
    }

    public void setUseFloats(boolean usefloats)
    {
        this.useFloats = usefloats;
    }

    public boolean getUseTriangleStrip()
    {
        return this.useTriangleStrip;
    }

    public void setUseTriangleStrip(boolean useTriangleStrip)
    {
        this.useTriangleStrip = useTriangleStrip;
    }

    public int getTriangleCount()
    {
        return triangleCount;
    }

    public void setTriangleCount(int triangleCount)
    {
        this.triangleCount = triangleCount;
    }

    public void setMeshResolution(float resolution)
    {
        if (resolution >= 0.0f && resolution <= 256.0f)
            this.meshResolution = resolution;
    }

    public float getMeshResolution()
    {
        return this.meshResolution;
    }

    public void setLoadFrequency(float frames)
    {
        if (frames >= 1.0f && frames <= 1024.0f)
            this.loadFrequency = frames;
    }

    public float getLoadFrequency()
    {
        return this.loadFrequency;
    }

    public void setBufferDataUsage(int usage)
    {
        this.bufferDataUsage = usage;
    }

    public int getBufferDataUsage()
    {
        return this.bufferDataUsage;
    }

    // reload VBO this frame?

    public void setReloadNow(boolean reload)
    {
        reloadNow = reload;
    }

    public boolean getReloadNow()
    {
        return reloadNow;
    }

    // reload VBO's turned on?

    public void setReloadVBO(boolean reload)
    {
        reloadVBO = reload;
    }

    public boolean getReloadVBO()
    {
        return reloadVBO;
    }

    public void setRebindVBO(boolean rebind)
    {
        rebindVBO = rebind;
    }

    public boolean getRebindVBO()
    {
        return rebindVBO;
    }

    public void updateFPS(double fps)
    {
        this.fps = fps;
    }

    private class MyKeyAdapter extends KeyAdapter
    {

        public void keyReleased(KeyEvent e)
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_ESCAPE:
                    stop();
                    break;
                case KeyEvent.VK_F1:
                    break;
            }
        }
    }

    private class MyWindowAdapter extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            stop();
        }
    }

    private class MyExceptionHandler
    {
        public void handleException(final Exception e)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    e.printStackTrace(printWriter);
                    JOptionPane.showMessageDialog(frame, stringWriter.toString(), "Exception occurred",
                        JOptionPane.ERROR_MESSAGE);
                    stop();
                }
            });
        }
    }

    private class MyGLEventListener implements GLEventListener
    {
        private java.util.List<GLEventListener> eventListeners = new ArrayList<GLEventListener>();

        public void addGLEventListener(GLEventListener glEventListener)
        {
            eventListeners.add(glEventListener);
        }

        public void removeGLEventListener(GLEventListener glEventListener)
        {
            eventListeners.remove(glEventListener);
        }

        public void display(GLAutoDrawable glDrawable)
        {
            for (int i = 0; i < eventListeners.size(); i++)
            {
                ((GLEventListener) eventListeners.get(i)).display(glDrawable);
            }
        }

        public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1)
        {
            for (int i = 0; i < eventListeners.size(); i++)
            {
                ((GLEventListener) eventListeners.get(i)).displayChanged(glDrawable, b, b1);
            }
        }

        public void init(GLAutoDrawable glDrawable)
        {
            for (int i = 0; i < eventListeners.size(); i++)
            {
                ((GLEventListener) eventListeners.get(i)).init(glDrawable);
            }
        }

        public void reshape(GLAutoDrawable glDrawable, int i0, int i1, int i2, int i3)
        {
            for (int i = 0; i < eventListeners.size(); i++)
            {
                ((GLEventListener) eventListeners.get(i)).reshape(glDrawable, i0, i1, i2, i3);
            }
        }
    }

    private class StatsPanel extends JPanel implements GLEventListener
    {
        private long timestamp;
        private int loadcount;

        private JTextArea sysProperties;
        private JTextArea fpsTextArea;
        private JTextArea reloadArea;
        private JTextArea trianglesArea;
        private JTextArea triangleStripArea;
        private JTextArea throughputArea;

        private boolean updateStatsPanel;

        public StatsPanel createStatsPanel()
        {
            return createStatsPanel(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }

        public StatsPanel createStatsPanel(int width, int height)
        {
            StatsPanel panel = new StatsPanel();
            setSize(width / 4, height * 3 / 10);
            setPreferredSize(new Dimension(width / 4, height * 3 / 10));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createTitledBorder(" Scene Statistics "));

            return panel;
        }

        public void init(GLAutoDrawable glDrawable)
        {
            timestamp = System.currentTimeMillis();
            loadcount = 0;
            fps = 0;
            updateStatsPanel = true;

            GL gl = glDrawable.getGL();

            StringBuilder sb = new StringBuilder();
            //sb.append("  System Properties:\n");
            sb.append("  OS name: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n");
            //sb.append("  OS version: " + System.getProperty("os.version") + "\n");
            sb.append("  Architecture: " + System.getProperty("os.arch") + "\n");
            sb.append("  Processors: " + Runtime.getRuntime().availableProcessors() + "\n");
            sb.append("  Free memory: " + Runtime.getRuntime().freeMemory() / 1000 + " kb\n");
            sb.append("  Max memory: " + Runtime.getRuntime().maxMemory() / 1000 + " kb\n");
            sb.append("  Total memory: " + Runtime.getRuntime().totalMemory() / 1000 + " kb\n");
            sb.append("  Video card vender: " + gl.glGetString(GL.GL_VENDOR) + "\n");
            sb.append("  Renderer: " + gl.glGetString(GL.GL_RENDERER));
            sysProperties = new JTextArea(sb.toString());
            sysProperties.setBackground(new Color(238, 238, 238));
            sysProperties.setEnabled(true);
            sysProperties.setEditable(true);
            add(sysProperties);

            trianglesArea = new JTextArea("  Triangles:  " + triangleCount);
            trianglesArea.setBackground(new Color(238, 238, 238));
            add(trianglesArea);

            triangleStripArea = new JTextArea("  Render mode:  " +
                (getUseTriangleStrip() ? "TRIANGLE_STRIP" : "TRIANGLES"));
            triangleStripArea.setBackground(new Color(238, 238, 238));
            add(triangleStripArea);

            reloadArea = new JTextArea("  Reload frequency:  N/A");
            reloadArea.setBackground(new Color(238, 238, 238));
            add(reloadArea);

            throughputArea = new JTextArea("  Throughput:  " + fps * triangleCount);
            throughputArea.setBackground(new Color(238, 238, 238));
            add(throughputArea);

            fpsTextArea = new JTextArea("  FPS:  " + fps);
            fpsTextArea.setBackground(new Color(238, 238, 238));
            add(fpsTextArea);
        }

        public void display(GLAutoDrawable glDrawable)
        {
            if (getUseVBOs() && getReloadVBO())
                loadcount++;

            // reload data if we are reloading VBO data AND enough frames have elapsed
            if (getUseVBOs() && getReloadVBO() && loadcount >= loadFrequency)
            {
                setReloadNow(true);
                loadcount = 0;
            }

            if (updateStatsPanel)
            {
                NumberFormat numFormat = NumberFormat.getInstance();
                numFormat.setMaximumFractionDigits(0);

                trianglesArea.removeAll();
                trianglesArea.setText("  Triangles:  " + numFormat.format(triangleCount));

                triangleStripArea.removeAll();
                triangleStripArea.setText("  Render mode:  " +
                    (getUseTriangleStrip() ? "TRIANGLE_STRIP" : "TRIANGLES"));

                reloadArea.removeAll();
                if (getUseVBOs() && getReloadVBO())
                    reloadArea.setText("  Reload every " + (int) loadFrequency + " frame(s)");
                else
                    reloadArea.setText("  Reload frequency:  N/A ");

                throughputArea.removeAll();
                throughputArea.setText("  Throughput:  " + numFormat.format(fps * triangleCount)
                    + "  (tris/sec)");

                fpsTextArea.removeAll();
                fpsTextArea.setText("  FPS:  " + numFormat.format(fps));

                revalidate();
                repaint();
            }
        }

        public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1)
        {
        }

        public void reshape(GLAutoDrawable glDrawable, int i0, int i1, int i2, int i3)
        {
        }
    }

    private class ControlPanel extends JPanel implements ActionListener, ItemListener, GLEventListener
    {

        JRadioButton buttonVBO;
        JRadioButton buttonVA;
        JRadioButton buttonTriangle;
        JRadioButton buttonTristrip;
        JRadioButton buttonFloat;
        JRadioButton buttonDouble;
        JRadioButton buttonStatic;
        JRadioButton buttonDynamic;
        JRadioButton buttonStream;

        JCheckBox reloadBox;
        JCheckBox rebindBox;

        JSlider meshSlider;
        JSlider reloadSlider;

        JLabel resolutionLabel;
        JLabel reloadLabel;

        public ControlPanel createControlPanel()
        {
            return createControlPanel(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }

        public ControlPanel createControlPanel(int width, int height)
        {

            ControlPanel panel = new ControlPanel();
            panel.setSize(width / 4, height * 7 / 10);
            panel.setPreferredSize(new Dimension(width / 4, height * 7 / 10));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createTitledBorder(" Demo Parameters "));

            // add resolution slider, using logarithmic scale
            meshSlider = new JSlider(0, 5, 3);
            meshSlider.setMajorTickSpacing(1);
            meshSlider.setPaintTicks(true);
            //slider.setPaintLabels(true);
            //meshSlider.setSize(5, 5);
            meshSlider.setSnapToTicks(true);
            meshSlider.setInverted(true);
            meshSlider.setName("Resolution");

            resolutionLabel = new JLabel("Mesh Resolution: ");

            add(new JLabel("   "));
            add(resolutionLabel);
            add(meshSlider);

            // add Buffer Type radio buttons
            buttonVBO = new JRadioButton("VBO");
            buttonVBO.setActionCommand("VBO");
            buttonVBO.addActionListener(this);
            if (useVBOs)
                buttonVBO.setSelected(true);

            buttonVA = new JRadioButton("Vertex Arrays");
            buttonVA.setActionCommand("VA");
            buttonVA.addActionListener(this);
            if (!useVBOs)
                buttonVA.setSelected(true);

            ButtonGroup VBOButtonGroup = new ButtonGroup();
            VBOButtonGroup.add(buttonVBO);
            VBOButtonGroup.add(buttonVA);

            // add buffer labels/buttons to panel
            add(new JLabel("   "));
            add(new JLabel("Buffer Type:"));
            add(buttonVBO);
            add(buttonVA);

            //create/add Data Type radio buttons
            buttonFloat = new JRadioButton("Float");
            buttonFloat.setActionCommand("Float");
            buttonFloat.addActionListener(this);
            if (useFloats)
                buttonFloat.setSelected(true);

            buttonDouble = new JRadioButton("Double");
            buttonDouble.setActionCommand("Double");
            buttonDouble.addActionListener(this);
            if (!useFloats)
                buttonDouble.setSelected(true);

            ButtonGroup DataTypeButtonGroup = new ButtonGroup();
            DataTypeButtonGroup.add(buttonFloat);
            DataTypeButtonGroup.add(buttonDouble);

            // add Data labels/buttons to panel
            add(new JLabel("   "));
            add(new JLabel("Data Type:"));
            add(buttonFloat);
            add(buttonDouble);

            // add Render Mode radio buttons
            buttonTriangle = new JRadioButton("GL_TRIANGLES");
            buttonTriangle.setActionCommand("GL_TRIANGLES");
            buttonTriangle.addActionListener(this);
            if (!useTriangleStrip)
                buttonTriangle.setSelected(true);

            buttonTristrip = new JRadioButton("GL_TRIANGLE_STRIP");
            buttonTristrip.setActionCommand("GL_TRIANGLE_STRIP");
            buttonTristrip.addActionListener(this);
            if (useTriangleStrip)
                buttonTristrip.setSelected(true);

            ButtonGroup RenderModeButtonGroup = new ButtonGroup();
            RenderModeButtonGroup.add(buttonTriangle);
            RenderModeButtonGroup.add(buttonTristrip);

            // add buffer labels/buttons to panel
            add(new JLabel("   "));
            add(new JLabel("Render Mode:"));
            add(buttonTriangle);
            add(buttonTristrip);

            //create/add Buffer Usage radio buttons
            buttonStatic = new JRadioButton("STATIC_DRAW");
            buttonStatic.setActionCommand("Static");
            buttonStatic.addActionListener(this);
            if (bufferDataUsage == GL.GL_STATIC_DRAW_ARB)
                buttonStatic.setSelected(true);

            buttonDynamic = new JRadioButton("DYNAMIC_DRAW");
            buttonDynamic.setActionCommand("Dynamic");
            buttonDynamic.addActionListener(this);
            if (bufferDataUsage == GL.GL_DYNAMIC_DRAW_ARB)
                buttonDynamic.setSelected(true);

            buttonStream = new JRadioButton("STREAM_DRAW");
            buttonStream.setActionCommand("Stream");
            buttonStream.addActionListener(this);
            if (bufferDataUsage == GL.GL_STREAM_DRAW_ARB)
                buttonStream.setSelected(true);

            ButtonGroup BufferUsageButtonGroup = new ButtonGroup();
            BufferUsageButtonGroup.add(buttonStatic);
            BufferUsageButtonGroup.add(buttonDynamic);
            BufferUsageButtonGroup.add(buttonStream);

            // add all labels/buttons to panel
            add(new JLabel("   "));
            add(new JLabel("Buffer Usage:"));
            add(buttonStatic);
            add(buttonDynamic);
            add(buttonStream);

            // create reload/rebind check boxes
            reloadBox = new JCheckBox("Reload data");
            reloadBox.setSelected(reloadVBO);
            reloadBox.addItemListener(this);

            rebindBox = new JCheckBox("Rebind at each Reload");
            rebindBox.setSelected(rebindVBO);
            rebindBox.setEnabled(false);
            rebindBox.addItemListener(this);

            // add load frequency slider, using logarithmic scale
            reloadSlider = new JSlider(0, 10, 4);
            reloadSlider.setMajorTickSpacing(1);
            reloadSlider.setPaintTicks(true);
            //reloadSlider.setPaintLabels(true);
            //reloadSlider.setSize(5, 5);
            reloadSlider.setSnapToTicks(true);
            reloadSlider.setInverted(true);
            reloadSlider.setName("Reload");
            reloadSlider.setEnabled(false);

            reloadLabel = new JLabel("   ");
            reloadLabel.setEnabled(false);

            add(new JLabel("   "));
            add(reloadBox);
            add(rebindBox);
            add(reloadLabel);
            add(reloadSlider);

            return panel;
        }

        public void addActionListener(ActionListener actionListener)
        {
            buttonVBO.addActionListener(actionListener);
            buttonVA.addActionListener(actionListener);
            buttonFloat.addActionListener(actionListener);
            buttonDouble.addActionListener(actionListener);
            buttonTriangle.addActionListener(actionListener);
            buttonTristrip.addActionListener(actionListener);
            buttonStatic.addActionListener(actionListener);
            buttonDynamic.addActionListener(actionListener);
            buttonStream.addActionListener(actionListener);
        }

        public void addChangeListener(ChangeListener changeListener)
        {
            meshSlider.addChangeListener(changeListener);
            reloadSlider.addChangeListener(changeListener);
        }

        /** Listens to the radio buttons. */
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();
            // Buffer Type events
            if (action.equalsIgnoreCase("VBO"))
            {
                buttonStatic.setEnabled(true);
                buttonDynamic.setEnabled(true);
                buttonStream.setEnabled(true);

                reloadBox.setEnabled(true);
                if (reloadBox.isSelected())
                {
                    rebindBox.setEnabled(true);
                    reloadSlider.setEnabled(true);
                }
            }
            else if (action.equalsIgnoreCase("VA"))
            {
                buttonStatic.setEnabled(false);
                buttonDynamic.setEnabled(false);
                buttonStream.setEnabled(false);

                reloadSlider.setEnabled(false);
                reloadBox.setEnabled(false);
                rebindBox.setEnabled(false);
            }
        }

        public void itemStateChanged(ItemEvent e)
        {

            Object source = e.getItemSelectable();

            if (e.getStateChange() == ItemEvent.DESELECTED)
            {
                if (source == reloadBox)
                {
                    setReloadVBO(false);
                    rebindBox.setEnabled(false);
                    reloadLabel.setEnabled(false);
                    reloadSlider.setEnabled(false);
                }
                else if (source == rebindBox)
                {
                    setRebindVBO(false);
                }
            }
            else if (e.getStateChange() == ItemEvent.SELECTED)
            {
                if (source == reloadBox)
                {
                    setReloadVBO(true);
                    rebindBox.setEnabled(true);
                    reloadLabel.setEnabled(true);
                    reloadSlider.setEnabled(true);
                }
                else if (source == rebindBox)
                {
                    setRebindVBO(true);
                }
            }
        }

        public void init(GLAutoDrawable glDrawable)
        {
        }

        public void display(GLAutoDrawable glDrawable)
        {
            resolutionLabel.removeAll();
            resolutionLabel.setText("Mesh Resolution:   "
                + NumberFormat.getInstance().format(triangleCount));

            reloadLabel.removeAll();
            reloadLabel.setText("  Reload every " + (int) loadFrequency + " frame(s)");

            revalidate();
            repaint();
        }

        public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1)
        {
        }

        public void reshape(GLAutoDrawable glDrawable, int i0, int i1, int i2, int i3)
        {
        }
    }
}
