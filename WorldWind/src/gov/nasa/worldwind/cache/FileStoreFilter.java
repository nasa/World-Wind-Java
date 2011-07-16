/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.cache;

/**
 * A filter for named file paths in a {@link FileStore}.
 *
 * @author dcollins
 * @version $Id$
 */
public interface FileStoreFilter
{
    /**
     * Returns whether or not this filter accepts the file name under a specified FileStore.
     *
     * @param fileStore the FileStore containing the named file path.
     * @param fileName  the named file path in question.
     *
     * @return true if the file name should be accepted; false otherwise.
     */
    boolean accept(FileStore fileStore, String fileName);
}
