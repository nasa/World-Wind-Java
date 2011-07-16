/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.wss;

import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.cache.FileStoreFilter;
import gov.nasa.worldwind.database.DatabaseConnectionPool;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.util.*;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dcollins
 * @version $Id$
 */
public class KMZFeatureType implements WSSFeatureType
{
    protected String name;
    protected FileStore fileStore;
    protected WebShapeService wss;

//    protected Map<String, String> featureMap = new HashMap<String, String>();
//    protected FileStoreFilter kmzFileStoreFilter = new FileStoreFilter()
//        {
//            public boolean accept(FileStore fileStore, String fileName)
//            {
//                String suffix = WWIO.getSuffix(fileName);
//                return !WWUtil.isEmpty(suffix) && suffix.toLowerCase().startsWith("kmz");
//            }
//        };

    public KMZFeatureType(String name, FileStore fileStore, WebShapeService wss)
    {
        this.name = name;
        this.fileStore = fileStore;
        this.wss = wss;
//        this.loadFeatureResources();
    }

    public String getName()
    {
        return this.name;
    }

    public Iterable<String> getOutputFormats()
    {
        return Arrays.asList(KMLConstants.KMZ_MIME_TYPE);
    }

    public Iterable<String> getFeatureResourceIDs()
    {
        return this.wss.queryGetAllFeatureNames();
    }

    public URL getFeatureResource(String resourceID)
    {
        String path = this.wss.queryGetFeatureURL(resourceID);
        if (WWUtil.isEmpty(path))
            return null;

        return fileStore.findFile(path, false);
    }

//    public Iterable<String> getFeatureResourceIDs()
//    {
//        return this.featureMap.keySet();
//    }
//
//    public URL getFeatureResource(String resourceID)
//    {
//        String path = this.featureMap.get(resourceID);
//        if (WWUtil.isEmpty(path))
//            return null;
//
//        return fileStore.findFile(path, false);
//    }
//
//    protected void loadFeatureResources()
//    {
//        this.featureMap.clear();
//
//        String[] featurePaths = this.fileStore.listAllFileNames(null, this.kmzFileStoreFilter);
//        if (featurePaths == null)
//            return;
//
//        for (String path : featurePaths)
//        {
//            if (!WWUtil.isEmpty(path))
//                this.addFeatureResource(path);
//        }
//    }
//
//    protected void addFeatureResource(String path)
//    {
//      // TODO: extract the feature's name from the KMZ document.
//        String name = WWIO.getFilename(path);
//
//        if (WWUtil.isEmpty(name))
//            name = path;
//
//        if (!WWUtil.isEmpty(name))
//            name = WWIO.replaceSuffix(name, "");
//
//        this.featureMap.put(name, path);
//    }
}
