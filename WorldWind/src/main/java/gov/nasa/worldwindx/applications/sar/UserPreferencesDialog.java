/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.applications.sar;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class UserPreferencesDialog extends JDialog
{
    protected AVList preferences;
    protected AVList newPreferences;
    protected boolean ignoreActionEvents;
    protected String[] autoSaveIntervalChoices = new String[] {
        Long.toString((long) WWMath.convertSecondsToMillis(1)),  // 1  second
        Long.toString((long) WWMath.convertSecondsToMillis(5)),  // 5  seconds
        Long.toString((long) WWMath.convertSecondsToMillis(10)), // 10 seconds
        Long.toString((long) WWMath.convertSecondsToMillis(20)), // 20 seconds
        Long.toString((long) WWMath.convertSecondsToMillis(30)), // 30 seconds
        Long.toString((long) WWMath.convertMinutesToMillis(1)),  // 1  minute
        Long.toString((long) WWMath.convertMinutesToMillis(5)),  // 5  minutes
        Long.toString((long) WWMath.convertMinutesToMillis(10)), // 10 minutes
        Long.toString((long) WWMath.convertMinutesToMillis(20)), // 20 minutes
        Long.toString((long) WWMath.convertMinutesToMillis(30)), // 30 minutes
    };

    @SuppressWarnings({"FieldCanBeLocal"})
    private WindowAdapter windowListener = new WindowAdapter()
    {
        public void windowClosing(WindowEvent windowEvent)
        {
            lowerDialog();
        }
    };

    public UserPreferencesDialog(Frame parent, boolean modal) throws HeadlessException
    {
        super(parent, "Preferences", modal);

        this.layoutComponents();
        this.setResizable(true);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this.windowListener);

        // Invoke the cancel action when the user presses the "esc" or "Escape" key.
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CancelAction.ACTION_COMMAND);
        this.getRootPane().getActionMap().put(CancelAction.ACTION_COMMAND, new CancelAction());
    }

    public void raiseDialog(AVList preferences)
    {
        this.setPreferences(preferences);

        WWUtil.alignComponent(this.getParent(), this, AVKey.CENTER);
        this.setVisible(true);
    }

    public void lowerDialog()
    {
        this.setPreferences(null);

        setVisible(false);
    }

    public String[] getAutoSaveIntervalChoices()
    {
        String[] copy = new String[this.autoSaveIntervalChoices.length];
        System.arraycopy(this.autoSaveIntervalChoices, 0, copy, 0, this.autoSaveIntervalChoices.length);
        return copy;
    }

    public void setAutoSaveIntervalChoices(String[] array)
    {
        if (array == null)
        {
            String message = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.autoSaveIntervalChoices = new String[array.length];
        System.arraycopy(array, 0, this.autoSaveIntervalChoices, 0, array.length);
    }

    protected void setPreferences(AVList preferences)
    {
        this.preferences = preferences;
        this.newPreferences = (this.preferences != null) ? this.preferences.copy() : null;

        this.firePropertyChange("Preferences", null, this.preferences);
    }

    protected void applyChanges()
    {
        if (this.preferences == null || this.newPreferences == null)
            return;

        this.preferences.setValues(this.newPreferences);
    }

    protected Object getProperty(String key)
    {
        return (this.newPreferences != null) ? this.newPreferences.getValue(key) : null;
    }

    protected boolean getBooleanProperty(String key)
    {
        return (this.newPreferences != null) && UserPreferenceUtils.getBooleanValue(this.newPreferences, key);
    }

    protected void setProperty(String key, Object value)
    {
        if (this.newPreferences == null)
            return;

        this.newPreferences.setValue(key, value);
        this.firePropertyChange(key, null, value);
    }

    protected void layoutComponents()
    {
        this.getContentPane().setLayout(new BorderLayout(0, 0)); // hgap, vgap
        this.getContentPane().add(this.createControls(), BorderLayout.CENTER);
        this.getContentPane().add(this.createOkayCancelPanel(), BorderLayout.SOUTH);
        this.validate();
        this.pack();
    }

    protected JComponent createControls()
    {
        return this.createBasicPreferenceControls();
    }

    protected JComponent createBasicPreferenceControls()
    {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 0)); // nrows, ncols, hgap, vgap
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10)); // top, left, bottom, right
        panel.add(this.createAutoSavePanel());

        JPanel northPanel = new JPanel(new BorderLayout(0, 0)); // hgap, vgap
        northPanel.add(panel, BorderLayout.NORTH);

        JPanel westPanel = new JPanel(new BorderLayout(0, 0)); // hgap, vgap
        westPanel.add(northPanel, BorderLayout.WEST);

        westPanel.validate();
        westPanel.setPreferredSize(new Dimension(400, 100));
        westPanel.setMinimumSize(westPanel.getPreferredSize());

        return westPanel;
    }

    protected JComponent createAutoSavePanel()
    {
        Box box = Box.createHorizontalBox();

        JCheckBox checkBox = new JCheckBox("Automatically save tracks every");
        this.bindButtonStateToParam(checkBox, SARKey.AUTO_SAVE_TRACKS);
        box.add(checkBox);

        box.add(Box.createHorizontalStrut(10));

        JComboBox comboBox = new JComboBox();
        this.bindComboBoxToParam(comboBox, SARKey.AUTO_SAVE_TRACKS, SARKey.AUTO_SAVE_TRACKS_INTERVAL,
            this.autoSaveIntervalChoices, new AutoSaveChoiceComparator());
        comboBox.setRenderer(new TimeIntervalRenderer(comboBox.getRenderer()));
        box.add(comboBox);

        box.add(Box.createHorizontalGlue());

        return box;
    }

    protected JPanel createOkayCancelPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 0)); // hgap, vgap
        panel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);

        Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // top, left, bottom, right
        box.add(Box.createHorizontalGlue());

        JButton button = new JButton(new OkayAction());
        this.getRootPane().setDefaultButton(button);
        box.add(button);

        box.add(Box.createHorizontalStrut(10));

        button = new JButton(new CancelAction());
        box.add(button);

        panel.add(box, BorderLayout.EAST);

        return panel;
    }

    protected void bindButtonStateToParam(final AbstractButton button, final String paramName)
    {
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (ignoreActionEvents)
                    return;

                setProperty(paramName, button.isSelected());
            }
        });

        this.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent)
            {
                ignoreActionEvents = true;
                try
                {
                    boolean b = getBooleanProperty(paramName);
                    button.setSelected(b);
                }
                finally
                {
                    ignoreActionEvents = false;
                }
            }
        });
    }

    protected void bindComboBoxToParam(final JComboBox box, final String enableParamName, final String setParamName,
        final Object[] defaultItems, final Comparator<Object> c)
    {
        box.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (ignoreActionEvents)
                    return;

                setProperty(setParamName, box.getSelectedItem());
            }
        });

        this.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent)
            {
                ignoreActionEvents = true;
                try
                {
                    Object selectedItem = getProperty(setParamName);

                    box.removeAllItems();
                    Object[] itemArray = createComboBoxItems(defaultItems, selectedItem, c);
                    for (Object item : itemArray)
                    {
                        box.addItem(item);
                    }

                    if (selectedItem != null)
                        box.setSelectedItem(selectedItem);

                    boolean b = getBooleanProperty(enableParamName);
                    box.setEnabled(b);
                }
                finally
                {
                    ignoreActionEvents = false;
                }
            }
        });
    }

    private Object[] createComboBoxItems(Object[] defaultItems, Object selectedItem, Comparator<Object> c)
    {
        Set<Object> set = new HashSet<Object>();
        set.addAll(Arrays.asList(defaultItems));
        if (selectedItem != null)
            set.add(selectedItem);

        ArrayList<Object> list = new ArrayList<Object>();
        list.addAll(set);
        Collections.sort(list, c);

        Object[] array = new Object[list.size()];
        list.toArray(array);
        return array;
    }

    private class OkayAction extends AbstractAction
    {
        public OkayAction()
        {
            super("OK");
        }

        public void actionPerformed(ActionEvent e)
        {
            applyChanges();
            lowerDialog();
        }
    }

    private class CancelAction extends AbstractAction
    {
        public static final String ACTION_COMMAND = "CancelAction";

        public CancelAction()
        {
            super("Cancel");
        }

        public void actionPerformed(ActionEvent e)
        {
            lowerDialog();
        }
    }

    private class TimeIntervalRenderer implements ListCellRenderer
    {
        private ListCellRenderer delegate;

        private TimeIntervalRenderer(ListCellRenderer delegate)
        {
            this.delegate = delegate;
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
        {
            if (value != null)
            {
                String s = value.toString();
                if (s != null)
                {
                    s = this.timeIntervalToString(s);
                    if (s != null)
                        value = s;
                }
            }
            else
            {
                String message = Logging.getMessage("generic.ConversionError", value);
                Logging.logger().warning(message);
            }

            return this.delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

        protected String timeIntervalToString(String time)
        {
            Double d = null;
            try
            {
                d = Double.parseDouble(time);
            }
            catch (NumberFormatException e)
            {
                String message = Logging.getMessage("generic.ConversionError", time);
                Logging.logger().warning(message);
            }

            if (d == null)
                return null;

            long value = (long) WWMath.convertMillisToHours(d);
            if (value > 0)
                return String.format("%d hour%s", value, this.pluralString(value));

            value = (long) WWMath.convertMillisToMinutes(d);
            if (value > 0)
                return String.format("%d minute%s", value, this.pluralString(value));

            value = (long) WWMath.convertMillisToSeconds(d);
            if (value > 0)
                return String.format("%d second%s", value, this.pluralString(value));

            value = d.longValue();
            return String.format("%d millisecond%s", value, this.pluralString(value));
        }

        protected String pluralString(long value)
        {
            return (value > 1) ? "s" : "";
        }
    }

    private class AutoSaveChoiceComparator implements Comparator<Object>
    {
        public int compare(Object o1, Object o2)
        {
            Double d1 = this.asDouble(o1);
            Double d2 = this.asDouble(o2);
            if (d1 == null || d2 == null)
                return 0;

            return Double.compare(d1, d2);
        }

        private Double asDouble(Object o)
        {
            try
            {
                return Double.parseDouble(o.toString());
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
    }
}
