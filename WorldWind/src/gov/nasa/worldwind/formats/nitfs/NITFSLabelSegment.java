/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.formats.nitfs;
/**
 * @author Lado Garakanidze
 * @version $Id: NitfsLabelSegment Mar 31, 2007 12:57:41 AM
 */
class NITFSLabelSegment extends NITFSSegment
{
    public NITFSLabelSegment(java.nio.ByteBuffer buffer, int headerStartOffset, int headerLength, int dataStartOffset, int dataLength)
    {
        super(NITFSSegmentType.LABEL_SEGMENT, buffer, headerStartOffset, headerLength, dataStartOffset, dataLength);

        this.restoreBufferPosition();
    }
}
