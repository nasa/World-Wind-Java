/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.formats.vpf;

/**
 * @author dcollins
 * @version $Id$
 */
public interface VPFRecord
{
    int getId();

    boolean hasValue(String parameterName);

    Object getValue(String parameterName);
}
