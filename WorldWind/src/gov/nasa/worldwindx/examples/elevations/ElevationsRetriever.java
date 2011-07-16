/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.examples.elevations;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.retrieve.*;
import gov.nasa.worldwind.terrain.*;
import gov.nasa.worldwind.util.*;

import java.net.*;
import java.nio.*;
import java.util.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class ElevationsRetriever
{
    private Retriever retriever = null;
    private GetElevationsPostProcessor callBack = null;


    public ElevationsRetriever( WMSBasicElevationModel model, ArrayList<LatLon> locations, int connectTimeout, int readTimeout, GetElevationsPostProcessor callBack)
    {
        if( null == model )
        {
            String message = Logging.getMessage( "nullValue.ElevationModelIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException( message );
        }

        if( null == locations || 0 == locations.size() )
        {
            String message = Logging.getMessage( "nullValue.LatLonListIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException( message );
        }

        if( null == callBack )
        {
            String message = Logging.getMessage( "nullValue.PostProcessorIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException( message );
        }

        Level level = model.getLevels().getLastLevel();

        String svc = level.getService();

        StringBuffer reqURL = new StringBuffer( svc );

        if( !svc.endsWith( "?") )
            reqURL.append("?");

        reqURL.append( "REQUEST=GetElevations" );
        reqURL.append( "&WW_MAX_THREADS=2" );

        reqURL.append( "&LAYERS=" ).append( level.getDataset() );

        // String urlWMS = "http://localhost:8080/elev?request=GetElevations&Layers=mergedSrtm" ;

        reqURL.append( "&LOCATIONS=" );

        for (LatLon ll : locations)
        {
            reqURL.append(ll.getLongitude().degrees).append(",").append(ll.getLatitude().degrees).append(";");
        }
        reqURL.deleteCharAt(reqURL.lastIndexOf(";"));

        this.callBack = callBack;

        try
        {
            URL url = new URL(reqURL.toString());

            this.retriever = new HTTPRetriever(url, new ElevationsRetrieverPostProcessor(callBack) );

            this.retriever.setConnectTimeout( connectTimeout );
            this.retriever.setReadTimeout( readTimeout );
        }
        catch(Exception e)
        {
            Logging.logger().severe( e.getMessage() );
            callBack.onError( e.getMessage() );
        }
    }

    public void start()
    {
        try
        {
            this.retriever.call();
        }
        catch(Exception e)
        {
            Logging.logger().severe( e.getMessage() );
            this.callBack.onError( e.getMessage() );
        }
    }


    protected static class ElevationsRetrieverPostProcessor implements RetrievalPostProcessor
    {
        private GetElevationsPostProcessor callBack = null;

        public ElevationsRetrieverPostProcessor(GetElevationsPostProcessor callBack)
        {
            this.callBack = callBack;
        }

        public ByteBuffer run(Retriever retriever)
        {
            if (retriever == null)
            {
                String msg = Logging.getMessage("nullValue.RetrieverIsNull");
                Logging.logger().severe(msg);
                this.callBack.onError( msg );
//                throw new IllegalArgumentException(msg);
                return null;
            }

            if (!retriever.getState().equals(Retriever.RETRIEVER_STATE_SUCCESSFUL))
            {
                this.callBack.onError( retriever.getState() );
                return null;
            }

            if (retriever instanceof HTTPRetriever)
            {
                HTTPRetriever htr = (HTTPRetriever) retriever;
                if (htr.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    this.callBack.onError( "" + htr.getResponseCode() );
                    return null;
                }
            }

            URLRetriever r = (URLRetriever) retriever;
            ByteBuffer buffer = r.getBuffer();

            if (buffer != null)
            {
//              String contentType = r.getContentType();

                buffer.rewind();
                FloatBuffer bil32 = buffer.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();

                bil32.rewind();
                int size = bil32.capacity() / 3;

                Position[] positions = new Position[ size ];
                for (int i = 0; i < size; i++)
                {
                    double lat = bil32.get();
                    double lon = bil32.get();
                    double elev = bil32.get();
                    positions[i] = Position.fromDegrees(lat, lon, elev);
                }

                this.callBack.onSuccess( positions );

                return buffer;
            }

            this.callBack.onError( "Unknown error" );
            return null;
        }
    }


}
