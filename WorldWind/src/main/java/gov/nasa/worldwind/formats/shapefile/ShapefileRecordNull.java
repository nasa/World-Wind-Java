/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.formats.shapefile;

import java.nio.ByteBuffer;

/**
 * Represents a Shapefile record with a <strong>null</strong> shape type. Null shape records may have attributes but
 * have no geometric data, and are therefore typically used as placeholders.
 *
 * @author tag
 * @version $Id$
 */
public class ShapefileRecordNull extends ShapefileRecord
{
    /** {@inheritDoc} */
    public ShapefileRecordNull(Shapefile shapeFile, ByteBuffer buffer)
    {
        super(shapeFile, buffer);
    }

    @Override
    protected void doReadFromBuffer(Shapefile shapefile, ByteBuffer buffer)
    {
        this.numberOfParts = 0;
        this.numberOfPoints = 0;
    }
}
