/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.Logging;

/**
 * @author pabercrombie
 * @version $Id$
 */
public abstract class MilStd2525TacticalGraphic extends AVListImpl implements TacticalGraphic
{
    protected String standardIdentity;
    protected String echelon;
    protected String category;
    protected String status;
    // TODO: add country code, etc.

    protected String text;

    protected boolean visible;
    protected TacticalGraphicAttributes attributes;

    public abstract String getFunctionId();

    public String getIdentifier()
    {
        SymbolCode symCode = new SymbolCode();
        symCode.setValue(SymbolCode.STANDARD_IDENTITY, this.standardIdentity);
        symCode.setValue(SymbolCode.ECHELON, this.echelon);
        symCode.setValue(SymbolCode.CATEGORY, this.category);
        symCode.setValue(SymbolCode.FUNCTION_ID, this.getFunctionId());

        return symCode.toString();
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public String getStandardIdentity()
    {
        return this.standardIdentity;
    }

    public void setStandardIdentity(String standardIdentity)
    {
        if (standardIdentity == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.standardIdentity = standardIdentity;
    }

    public String getCategory()
    {
        return this.category;
    }

    public void setCategory(String category)
    {
        if (category == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.category = category;
    }

    public String getEchelon()
    {
        return this.echelon;
    }

    public void setEchelon(String echelon)
    {
        if (echelon == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.echelon = echelon;
    }

    public String getStatus()
    {
        return this.status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * Indicates a string of descriptive text for this graphic.
     *
     * @return Descriptive text for this graphic.
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Specifies a string of descriptive text for this graphic.
     *
     * @param text Descriptive text for this graphic.
     */
    public void setText(String text)
    {
        this.text = text;
    }

    public TacticalGraphicAttributes getAttributes()
    {
        return this.attributes;
    }

    public void setAttributes(TacticalGraphicAttributes attributes)
    {
        this.attributes = attributes;
    }

    public Object setValue(String key, Object value)
    {
        if (AVKey.TEXT.equals(key) && (value instanceof String))
        {
            this.setText((String) value);
            return null;
        }
        else
        {
            return super.setValue(key, value);
        }
    }
}
