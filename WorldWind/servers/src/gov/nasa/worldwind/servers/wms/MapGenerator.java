/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.servers.wms.formats.*;

import java.io.*;
import java.util.*;

/**
 * <p>Concrete implementations of <code>MapGenerator</code> are the means by which geospatial data are introduced and
 * served by the WMS. A <code>MapGenerator</code> embodies all the knowledge necessary to read a particular class of
 * geospatial data. This includes interpretation of the file formats, along with awareness of any spatial indexing
 * scheme or other organization of files on the disk. It also includes the ability to extract subregions of the data and
 * likely mosaic pieces together in response to arbitrary WMS GetMap requests.</p>
 * <p/>
 * <p> Instances of <code>MapGenerator</code> classes further embody the state needed to manage a particular set of
 * data. Multiple instances of the same class might therefore be used to manage differing collections of the same data
 * type that might be organized by region, scale, or other arbitrary organization. The <code>initialize()</code> method
 * is called once at startup time to give the <code>MapGenerator</code> instance a chance to perform any processing
 * required before GetMap requests can be satisfied.</p>
 * <p/>
 * <p>Per-request state is managed by instances of <code>ServiceInstance</code>. As the WMS identifies a particular
 * <code>MapGenerator</code> to field a GetMap request, it obtains an instance of <code>ServiceInstance</code> from the
 * <code>MapGenerator</code> via a call to <code>getServiceInstance()</code>.  The WMS then invokes the
 * <code>serviceRequest()</code> method of the <code>ServiceInstance</code>, in which the <code>MapGenerator</code>
 * attempts to generate the requested imagery. If successful, an instance of <code>ImageFormatter</code> is returned,
 * which the WMS uses to deliver the map in the requested image format. Finally, the WMS calls the
 * <code>freeResources()</code> on the <code>ServiceInstance</code>, which gives the <code>MapGenerator</code> a chance
 * to reclaim any resources, such as temporary files, etc.  If the <code>MapGenerator</code> is unable to satisfy the
 * GetMap request, it should throw a <code>WMSServiceException</code> with appropriate descriptive text as to why the
 * request failed.</p>
 * <p/>
 * <p><code>MapGenerators</code> are configured into the WMS via <code>&lt;MapSource&gt;</code> elements in the
 * <code>WEB-INF/config.xml</code> file. See the javadoc Overview for details.</p>
 *
 * @author brownrigg
 * @version $Id$
 */
public interface MapGenerator
{

    /**
     * Encapsulates per-request state needed to generate a response to a GetMap request.
     */
    public interface ServiceInstance
    {
        /**
         * Generates the imagery requested by a WMS-GetMap request. The WMS ensures that the WMSGetMapRequest represents
         * a well-formed WMS GetMap request; i.e., all required parameters are present and well-formed.
         *
         * @param req
         * @return
         * @throws IOException
         * @throws WMSServiceException
         */
        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException;

        /**
         * An extension to the WMS messaging system.  Unsupported for external use at present.
         *
         * @param req
         * @return
         * @throws IOException
         * @throws WMSServiceException
         */
        public List<File> serviceRequest(WMSGetImageryListRequest req) throws IOException, WMSServiceException;

        /**
         * Called by the WMS after the GetMap response has been delivered. This method allows a MapGenerator to reclaim
         * any resources, such as temporary files, etc., that were allocated during the generation of the map request.
         * This method can be implemented as a no-op if no resources need to be reclaimed.
         */
        public void freeResources();
    }

    /**
     * Gives the MapGenerator implementation an opportunity to perform any needed initialization, prior to fielding
     * GetMap requests. This typically includes extraction of any MapGenerator-specific properties that were given in
     * the MapSource configuration.  Called once at WMS start-up time.
     *
     * @param mapSource
     * @return
     * @throws IOException
     * @throws WMSServiceException
     */
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException;

    public boolean isInitialized();
    public  void markAsInitialized();

    public void setMapSource(MapSource mapSource);

    /**
     * Called by the WMS to return an instance of ServiceInstance to field a GetMap request.
     *
     * @return
     */
    public ServiceInstance getServiceInstance();

    /**
     * Returns the bounding-box for the geospatial dataset managed by the MapGenerator instance.
     *
     * @return
     */
    public Sector getBBox();

    /**
     * Returns the coordinate-reference systems (CRS) that the MapGenerator is capable of using to generate map
     * requests.
     *
     * @return
     */
    public String[] getCRS();


    /**
     * Returns 'true' if the MapSource returns imagery data, and 'false' for elevation data
     *
     * @return
     */
    public String getDataType();

    public double getPixelSize();

    public MapSource getMapSource();

    /**
     * Returns 'true' if the MapSource has imagery or elevation available for a given sector.
     * Technically you may compare your request sector with the Bounding Box, however USGS NED or USGS Urban
     * do not have a continuous coverage within the bounding box.
     *
     * @param sector
     * @return
     */
    public boolean hasCoverage(Sector sector);

    public WMSServerApplication getApplicationContext();
    public void setApplicationContext( WMSServerApplication wmsApp );
}
