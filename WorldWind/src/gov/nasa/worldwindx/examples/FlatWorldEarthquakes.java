/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.view.orbit.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.media.opengl.GL;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.xml.parsers.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.text.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Using the EarthFlat and FlatOrbitView to display USGS latest earthquakes rss feed.
 *
 * @author Patrick Murris
 * @version $Id$
 */
public class FlatWorldEarthquakes extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        private RenderableLayer eqLayer;
        private EqAnnotation mouseEq, latestEq;
        private GlobeAnnotation tooltipAnnotation;
        private JButton downloadButton;
        private JLabel statusLabel, latestLabel;
        private Date lastUpdate;
        private Blinker blinker;
        private Timer updater;
        private Date lastUpdaterEvent;
        private JComboBox magnitudeCombo;

        public AppFrame()
        {
            super(true, true, false);

            // Change atmosphere SkyGradientLayer for SkyColorLayer
            // and set worldmap and compass max active altitude
            LayerList layers = this.getWwd().getModel().getLayers();
            for(int i = 0; i < layers.size(); i++)
            {
                if(layers.get(i) instanceof SkyGradientLayer)
                    layers.set(i, new SkyColorLayer());
                else if(layers.get(i) instanceof WorldMapLayer)
                    (layers.get(i)).setMaxActiveAltitude(20e6);
                else if(layers.get(i) instanceof CompassLayer)
                    (layers.get(i)).setMaxActiveAltitude(20e6);
            }

            // Init tooltip annotation
            this.tooltipAnnotation = new GlobeAnnotation("", Position.fromDegrees(0, 0, 0));
            Font font = Font.decode("Arial-Plain-16");
            this.tooltipAnnotation.getAttributes().setFont(font);
            this.tooltipAnnotation.getAttributes().setSize(new Dimension(270, 0));
            this.tooltipAnnotation.getAttributes().setDistanceMinScale(1);
            this.tooltipAnnotation.getAttributes().setDistanceMaxScale(1);
            this.tooltipAnnotation.getAttributes().setVisible(false);
            this.tooltipAnnotation.setAlwaysOnTop(true);

            // Add control panels
            JPanel controls = new JPanel();
            controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
            // Add earthquakes view control panel
            controls.add(makeEarthquakesPanel());
            // Add flat world projection control panel
            controls.add(new FlatWorldPanel(this.getWwd()));
            this.getLayerPanel().add(controls,  BorderLayout.SOUTH);

            // Add select listener for earthquake picking
            this.getWwd().addSelectListener(new SelectListener(){
                public void selected(SelectEvent event){
                    if (event.getEventAction().equals(SelectEvent.ROLLOVER))
                        highlight(event.getTopObject());
                }});

            // Add click-and-go select listener for earthquakes
            this.getWwd().addSelectListener(new ClickAndGoSelectListener(
                    this.getWwd(), EqAnnotation.class, 1000e3));

            // Add updater timer
            this.updater = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    if (lastUpdaterEvent == null)
                        lastUpdaterEvent = new Date();
                    Date now = new Date();
                    long delay = javax.management.timer.Timer.ONE_MINUTE * 5;
                    long elapsed = now.getTime() - lastUpdaterEvent.getTime();
                    if (elapsed >= delay)
                    {
                        // Auto download every 5 minutes
                        lastUpdaterEvent = new Date();
                        downloadButton.setText("Update");
                        startEarthquakeDownload();
                    }
                    else
                    {
                        // Display remaining time in button text
                        long remain = delay - elapsed;
                        int min = (int)Math.floor((double)remain / javax.management.timer.Timer.ONE_MINUTE);
                        int sec = (int)((remain - min * javax.management.timer.Timer.ONE_MINUTE) / javax.management.timer.Timer.ONE_SECOND);
                        downloadButton.setText(String.format("Update (in %1$02d:%2$02d)", min, sec));
                    }
                }
            });
            this.updater.start();
            
            // Download earthquakes
            startEarthquakeDownload();
        }

        private void highlight(Object o)
        {
            if (this.mouseEq == o)
                return; // same thing selected

            if (this.mouseEq != null)
            {
                this.mouseEq.getAttributes().setHighlighted(false);
                this.mouseEq = null;
                this.tooltipAnnotation.getAttributes().setVisible(false);
            }

            if (o != null && o instanceof EqAnnotation)
            {
                this.mouseEq = (EqAnnotation) o;
                this.mouseEq.getAttributes().setHighlighted(true);
                this.tooltipAnnotation.setText("<p><b>" + this.mouseEq.earthquake.title + "</b></p>" + composeElapsedString(this.mouseEq) + "<br />" + this.mouseEq.earthquake.summary);
                this.tooltipAnnotation.setPosition(this.mouseEq.earthquake.position);
                this.tooltipAnnotation.getAttributes().setVisible(true);
                this.getWwd().redraw();
            }
        }

        private void setBlinker(EqAnnotation ea)
        {
            if (this.blinker != null)
            {
                this.blinker.stop();
                this.getWwd().redraw();
            }

            if (ea == null)
                return;

            this.blinker = new Blinker(ea);
        }

        private void setLatestLabel(EqAnnotation ea)
        {
            this.latestLabel.setText("");
            if (ea != null)
            {
                String htmlText = "<html>" + composeElapsedString(ea) + "<p><b>" + ea.earthquake.title + "</b></p>" + ea.earthquake.summary + "</html>";
                htmlText = htmlText.replaceAll("(?i)<img\\s?.*?>", "\n");  // Remove <img> tags
                this.latestLabel.setText(htmlText);
            }
        }

        private String composeElapsedString(EqAnnotation ea)
        {
            String s = "";
            if (ea.earthquake.date != null)
            {
                Date now = new Date();
                long elapsed = now.getTime() - ea.earthquake.date.getTime();
                long days = elapsed / javax.management.timer.Timer.ONE_DAY;
                elapsed -= days * javax.management.timer.Timer.ONE_DAY;
                long hours = elapsed / javax.management.timer.Timer.ONE_HOUR;
                elapsed -= hours * javax.management.timer.Timer.ONE_HOUR;
                long minutes = elapsed / javax.management.timer.Timer.ONE_MINUTE;
                if(days > 0)
                {
                    s = days + (days > 1 ? " days" : " day") + (hours > 0 ? " and " + hours + (hours > 1 ? " hours" : " hour") : "");
                }
                else
                {
                    if (hours > 0)
                        s = hours + (hours > 1 ? " hours" : " hour") + (hours < 12 ? " and " + minutes + (minutes > 1 ? " minutes" : " minute") : "");
                    else
                        s = minutes + (minutes > 1 ? " minutes" : " minute");
                }
                s += " ago";
            }
            return s;
        }

        private JPanel makeEarthquakesPanel()
        {
            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

            // Zoom on latest button
            JPanel zoomPanel = new JPanel(new GridLayout(0, 1, 0, 0));
            zoomPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            JButton btZoom = new JButton("Zoom on latest");
            btZoom.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    if (latestEq != null)
                    {
                        Position targetPos = latestEq.earthquake.position;
                        BasicOrbitView view = (BasicOrbitView) getWwd().getView();
                        view.addPanToAnimator(
                                // The elevation component of 'targetPos' here is not the surface elevation,
                                // so we ignore it when specifying the view center position.
                                new Position(targetPos, 0),
                                Angle.ZERO, Angle.ZERO, 1000e3);
                    }

                }
            });
            zoomPanel.add(btZoom);
            controlPanel.add(zoomPanel);

            // View reset button
            JPanel viewPanel = new JPanel(new GridLayout(0, 1, 0, 0));
            viewPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            JButton btReset = new JButton("Reset Global View");
            btReset.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    Double lat = Configuration.getDoubleValue(AVKey.INITIAL_LATITUDE);
                    Double lon = Configuration.getDoubleValue(AVKey.INITIAL_LONGITUDE);
                    Double elevation = Configuration.getDoubleValue(AVKey.INITIAL_ALTITUDE);
                    Position targetPos = Position.fromDegrees(lat, lon, 0);
                    BasicOrbitView view = (BasicOrbitView) getWwd().getView();
                    view.addPanToAnimator(
                        // The elevation component of 'targetPos' here is not the surface elevation,
                        // so we ignore it when specifying the view center position.
                        new Position(targetPos, 0),
                        Angle.ZERO, Angle.ZERO, elevation);

                }
            });
            viewPanel.add(btReset);
            controlPanel.add(viewPanel);

            // Update button
            JPanel downloadPanel = new JPanel(new GridLayout(0, 1, 0, 0));
            downloadPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            this.downloadButton = new JButton("Update");
            this.downloadButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    startEarthquakeDownload();
                }
            });
            this.downloadButton.setEnabled(false);
            downloadPanel.add(this.downloadButton);
            controlPanel.add(downloadPanel);

            // Status label
            JPanel statusPanel = new JPanel(new GridLayout(0, 1, 0, 0));
            statusPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            this.statusLabel = new JLabel();
            this.statusLabel.setPreferredSize(new Dimension(200, 20));
            this.statusLabel.setVerticalAlignment(SwingConstants.CENTER);
            statusPanel.add(this.statusLabel);
            controlPanel.add(statusPanel);

            // Magnitude filter combo
            JPanel magnitudePanel = new JPanel(new GridLayout(0, 2, 0, 0));
            magnitudePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            magnitudePanel.add(new JLabel("Min Magnitude:"));
            magnitudeCombo = new JComboBox(new String[] {"2.5", "3", "4", "5", "6", "7" });
            magnitudeCombo.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    applyFilter(Double.parseDouble((String) magnitudeCombo.getSelectedItem()));
                }
            });
            magnitudePanel.add(magnitudeCombo);
            controlPanel.add(magnitudePanel);

            // Blink latest checkbox
            JPanel blinkPanel = new JPanel(new GridLayout(0, 2, 0, 0));
            blinkPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            blinkPanel.add(new JLabel("Latest:"));
            final JCheckBox jcb = new JCheckBox("Animate");
            jcb.setSelected(true);
            jcb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    if (jcb.isSelected())
                    {
                        setBlinker(latestEq);
                    }
                    else
                    {
                        setBlinker(null);
                    }
                }
            });
            blinkPanel.add(jcb);
            controlPanel.add(blinkPanel);

            // Latest label
            JPanel latestPanel = new JPanel(new GridLayout(0, 1, 0, 0));
            latestPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            this.latestLabel = new JLabel();
            this.latestLabel.setPreferredSize(new Dimension(200, 140));
            this.latestLabel.setVerticalAlignment(SwingConstants.TOP);
            latestPanel.add(this.latestLabel);
            controlPanel.add(latestPanel);

            controlPanel.setBorder(
                new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Earthquakes")));
            controlPanel.setToolTipText("Earthquakes controls.");
            return controlPanel;
        }

        // Earthquake layer ------------------------------------------------------------------

        private void startEarthquakeDownload()
        {
            new Thread(new Runnable() {
                public void run()
                {
                    downloadEarthquakes();
                }
            }, "Earthquakes download").start();
        }

        private void downloadEarthquakes()
        {
            // Disable download button and update status label
            if (this.downloadButton != null)
                this.downloadButton.setEnabled(false);
            if (this.statusLabel != null)
                this.statusLabel.setText("Downloading earthquakes...");
            // Reset updater last event date
            lastUpdaterEvent = new Date();
            // Download and parse
            RenderableLayer newLayer = (RenderableLayer)buildEarthquakeLayer();
            // Update layer list and status
            if (newLayer.getRenderables().iterator().hasNext())
            {
                LayerList layers = this.getWwd().getModel().getLayers();
                if (this.eqLayer != null)
                    layers.remove(this.eqLayer);
                this.eqLayer = newLayer;
                this.eqLayer.addRenderable(this.tooltipAnnotation);
                insertBeforePlacenames(this.getWwd(), this.eqLayer);
                this.getLayerPanel().update(this.getWwd());
                this.lastUpdate = new Date();
                if (this.statusLabel != null)
                    this.statusLabel.setText(this.lastUpdate.toString());
                applyFilter(Double.parseDouble((String) magnitudeCombo.getSelectedItem()));
            }
            else
            {
                if (this.statusLabel != null)
                    this.statusLabel.setText("Download failed!");
            }
            if (this.downloadButton != null)
                this.downloadButton.setEnabled(true);
        }

        private Layer buildEarthquakeLayer()
        {
            final String USGS_EARTHQUAKES_M25_7DAYS = "http://earthquake.usgs.gov/eqcenter/catalogs/7day-M2.5.xml";
            RenderableLayer layer = new RenderableLayer();
            layer.setName("Earthquakes");
            try
            {
                // Get rss feed
                URL url = new URL(USGS_EARTHQUAKES_M25_7DAYS);
                ByteBuffer bb = WWIO.readURLContentToBuffer(url);
                // Parse feed and add renderables to layer
                parseFile(layer, WWIO.saveBufferToTempFile(bb, ".xml"));
            }
            catch (Exception e)
            {
                String message = Logging.getMessage("generic.ExceptionWhileReading", e);
                Logging.logger().severe(message);
            }

            return layer;
        }

        private void parseFile(RenderableLayer layer, File file)
        {
            if (file == null)
            {
                String message = Logging.getMessage("nullValue.FileIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            try
            {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                docBuilderFactory.setNamespaceAware(false);
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(file);

                parseDoc(layer, doc);

            }
            catch (ParserConfigurationException e)
            {
                String message = Logging.getMessage("GeoRSS.ParserConfigurationException");
                Logging.logger().log(Level.SEVERE, message, e);
                throw new WWRuntimeException(message, e);
            }
            catch (IOException e)
            {
                String message = Logging.getMessage("GeoRSS.IOExceptionParsing", file.getPath());
                Logging.logger().log(Level.SEVERE, message, e);
                throw new WWRuntimeException(message, e);
            }
            catch (SAXException e)
            {
                String message = Logging.getMessage("GeoRSS.IOExceptionParsing", file.getPath());
                Logging.logger().log(Level.SEVERE, message, e);
                throw new WWRuntimeException(message, e);
            }
        }

        private void parseDoc(RenderableLayer layer, Document xmlDoc)
        {
            if (xmlDoc == null)
            {
                String message = Logging.getMessage("nullValue.DocumentIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            // Entries
            NodeList nodes = xmlDoc.getElementsByTagName("entry");
            if (nodes != null && nodes.getLength() > 0)
            {
                this.latestEq = null;
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    Node entry = nodes.item(i);
                    Earthquake eq = new Earthquake(entry);
                    addEarthquake(layer, eq);
                }
            }
        }

        private AnnotationAttributes eqAttributes;
        private BufferedImage eqIcons[] =
        {
                PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, Color.RED),
                PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, Color.ORANGE),
                PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, Color.YELLOW),
                PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, Color.GREEN),
                PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, Color.BLUE),
                PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, Color.GRAY),
                PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, Color.BLACK),
        };
        private Color eqColors[] =
        {
                Color.RED,
                Color.ORANGE,
                Color.YELLOW,
                Color.GREEN,
                Color.BLUE,
                Color.GRAY,
                Color.BLACK,
        };

        private void addEarthquake(RenderableLayer layer, Earthquake earthquake)
        {
            if (eqAttributes == null)
            {
                // Init default attributes for all eq
                eqAttributes = new AnnotationAttributes();
                eqAttributes.setLeader(AVKey.SHAPE_NONE);
                eqAttributes.setDrawOffset(new Point(0, -16));
                eqAttributes.setSize(new Dimension(32, 32));
                eqAttributes.setBorderWidth(0);
                eqAttributes.setCornerRadius(0);
                eqAttributes.setBackgroundColor(new Color(0, 0, 0, 0));
            }
            EqAnnotation ea = new EqAnnotation(earthquake, eqAttributes);
            int days = 6;
            if (earthquake.date != null) {
                // Compute days since
                Date now = new Date();
                days = (int) ((now.getTime() - earthquake.date.getTime()) / javax.management.timer.Timer.ONE_DAY);
                // Update latestEq
                if (this.latestEq != null)
                {
                    if (this.latestEq.earthquake.date.getTime() < earthquake.date.getTime())
                        this.latestEq = ea;
                }
                else
                    this.latestEq = ea;
            }
            ea.getAttributes().setImageSource(eqIcons[Math.min(days, eqIcons.length - 1)]);
            ea.getAttributes().setTextColor(eqColors[Math.min(days, eqColors.length - 1)]);
            ea.getAttributes().setScale(earthquake.magnitude / 10);
            layer.addRenderable(ea);
        }

        private static Node findChildByName(Node parent, String localName)
        {
            NodeList children = parent.getChildNodes();
            if (children == null || children.getLength() < 1)
                return null;
            for (int i = 0; i < children.getLength(); i++)
            {
                String ln = children.item(i).getNodeName();
                if (ln != null && ln.equals(localName))
                    return children.item(i);
            }

            return null;
        }

        private void applyFilter(double minMagnitude)
        {
            this.latestEq = null;
            setBlinker(null);
            setLatestLabel(null);
            Iterable<Renderable> renderables = eqLayer.getRenderables();
            for (Renderable r : renderables)
            {
                if (r instanceof EqAnnotation)
                {
                    EqAnnotation ea = (EqAnnotation)r;
                    ea.getAttributes().setVisible(ea.earthquake.magnitude >= minMagnitude);
                    if (ea.getAttributes().isVisible())
                    {
                        if (this.latestEq != null)
                        {
                            if (this.latestEq.earthquake.date != null && ea.earthquake.date != null)
                                if (this.latestEq.earthquake.date.getTime() < ea.earthquake.date.getTime())
                                    this.latestEq = ea;
                        }
                        else
                            this.latestEq = ea;
                    }
                }
            }
            setBlinker(this.latestEq);
            setLatestLabel(this.latestEq);
            this.getWwd().redraw();
        }

        private class Earthquake
        {
            public String title;
            public String summary;
            public Position position;
            public double elevation;
            public Date date;
            public double magnitude;
            public String link;

            public Earthquake(Node entry)
            {
                Node node =  findChildByName(entry, "title");
                if(node != null)
                {
                    this.title = node.getTextContent();
                    this.magnitude = Double.parseDouble(title.split(",")[0].substring(2));
                }
                node = findChildByName(entry, "georss:point");
                if (node != null)
                {
                    String pointString = node.getTextContent();
                    String[] coord = pointString.split(" ");
                    this.position = Position.fromDegrees(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), 0);
                }
                node = findChildByName(entry, "georss:elev");
                if (node != null)
                    this.elevation = Double.parseDouble(node.getTextContent());
                node = findChildByName(entry, "summary");
                if (node != null)
                    this.summary = node.getTextContent();
                node = findChildByName(entry, "link");
                if (node != null)
                    this.link = node.getAttributes().getNamedItem("href").getTextContent();
                node = findChildByName(entry, "updated");
                if (node != null)
                {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                    try
                    {
                        this.date = df.parse(node.getTextContent().replaceAll("[TZ]", " ").trim());
                    }
                    catch (Exception e)
                    {
                        String message = Logging.getMessage("generic.CannotParse", e);
                        Logging.logger().severe(message);
                    }
                }
            }
        }

        private class EqAnnotation extends GlobeAnnotation
        {
            public Earthquake earthquake;
            public EqAnnotation(Earthquake earthquake, AnnotationAttributes defaults)
            {
                super ("", earthquake.position, defaults);
                this.earthquake = earthquake;
            }

            protected void applyScreenTransform(DrawContext dc, int x, int y, int width, int height, double scale)
            {
                double finalScale = scale * this.computeScale(dc);

                GL gl = dc.getGL();
                gl.glTranslated(x, y, 0);
                gl.glScaled(finalScale, finalScale, 1);
            }

            // Override annotation drawing for a simple circle
            private DoubleBuffer shapeBuffer;
            protected void doDraw(DrawContext dc, int width, int height, double opacity, Position pickPosition)
            {
                // Draw colored circle around screen point - use annotation's text color
                if (dc.isPickingMode())
                {
                    this.bindPickableObject(dc, pickPosition);
                }

                this.applyColor(dc, this.getAttributes().getTextColor(), 0.6 * opacity, true);

                // Draw 32x32 shape from its bottom left corner
                int size = 32;
                if (this.shapeBuffer == null)
                    this.shapeBuffer = FrameFactory.createShapeBuffer(AVKey.SHAPE_ELLIPSE, size, size, 0, null);
                dc.getGL().glTranslated(-size/2, -size/2, 0);
                FrameFactory.drawBuffer(dc, GL.GL_TRIANGLE_FAN, this.shapeBuffer);
            }
        }

        private class Blinker
        {
            private EqAnnotation annotation;
            private double initialScale, initialOpacity;
            private int steps = 10;
            private int step = 0;
            private int delay = 100;
            private Timer timer;

            private Blinker(EqAnnotation ea)
            {
                this.annotation = ea;
                this.initialScale = this.annotation.getAttributes().getScale();
                this.initialOpacity = this.annotation.getAttributes().getOpacity();
                this.timer = new Timer(delay, new ActionListener() {
                    public void actionPerformed(ActionEvent event)
                    {
                        annotation.getAttributes().setScale(initialScale * (1f + 7f * ((float)step / (float)steps)));
                        annotation.getAttributes().setOpacity(initialOpacity * (1f - ((float)step / (float)steps)));
                        step = step == steps ? 0 : step + 1;
                        getWwd().redraw();
                    }
                });
                start();
            }

            private void stop()
            {
                timer.stop();
                step = 0;
                this.annotation.getAttributes().setScale(initialScale);
                this.annotation.getAttributes().setOpacity(initialOpacity);
            }

            private void start()
            {
                timer.start();
            }
        }


    } // End AppFrame

    // --- Main -------------------------------------------------------------------------
    public static void main(String[] args)
    {
        // Adjust configuration values before instantiation
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 0);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, 0);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 50e6);
        Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
        Configuration.setValue(AVKey.VIEW_CLASS_NAME, FlatOrbitView.class.getName());
        ApplicationTemplate.start("World Wind USGS Earthquakes M 2.5+ - 7 days", AppFrame.class);
    }
}
