/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * @author garakl
 * @version $Id$
 */

public class Stats {
    private static final String LAYER_ID = "--Layer.Id---";
    private static final String TOTAL_TILES = "Total.Tiles";
    private static final String TOTAL_TIME_MSEC = "Total.Time.In.mSec";

    public static final Stats INSTANCE = new Stats();
    public static final Map<String, AVList> m = Collections.synchronizedMap(new HashMap<String, AVList>());

    private Stats() {}

    public static void report( String layerId, long numOfTiles, long procTime_msec )
    {
        if( null != layerId && layerId.length() > 0 )
        {
            synchronized (m)
            {
                try
                {
                    AVList stats = null;
                    if(m.containsKey(layerId))
                    {
                        stats = m.get(layerId);
                        if(null != stats)
                        {
                            if(stats.hasKey(Stats.TOTAL_TILES))
                                numOfTiles += (Long)stats.getValue(Stats.TOTAL_TILES);
                            if(stats.hasKey(Stats.TOTAL_TIME_MSEC))
                                procTime_msec += (Long)stats.getValue(Stats.TOTAL_TIME_MSEC);
                        }
                    }
                    if( null == stats )
                    {
                        stats = new AVListImpl();
                        stats.setValue( Stats.LAYER_ID, layerId );
                        if( !m.containsKey(layerId))
                            m.put(layerId, stats);
                    }

                    stats.setValue(Stats.TOTAL_TILES, Long.valueOf(numOfTiles));
                    stats.setValue(Stats.TOTAL_TIME_MSEC, Long.valueOf(procTime_msec));
                }
                catch(Exception ignore) {}
            }
        }
    }

    public static String getStats( String layerId )
    {
        StringBuffer sb = new StringBuffer("STATS: for ");

        if( null != layerId && layerId.length() > 0 )
        {
            sb.append( layerId ).append( " { ");
            synchronized (m)
            {
                try
                {
                    AVList stats = null;
                    if(m.containsKey(layerId))
                    {
                        stats = m.get(layerId);
                        if(null != stats)
                        {
                            long totalTiles = (long)AVListImpl.getLongValue( stats,Stats.TOTAL_TILES, 0L );
                            long totalTIme_msec = (long)AVListImpl.getLongValue( stats,Stats.TOTAL_TIME_MSEC, 0L );

                            sb.append(Stats.TOTAL_TILES).append("=").append(totalTiles).append(", ");
                            sb.append(Stats.TOTAL_TIME_MSEC).append("=").append(totalTIme_msec);

                            if(totalTIme_msec > 0)
                            {
                                double speed = (((double)totalTiles) * 1000d)/ (double)(totalTIme_msec);
                                sb.append(". Overall processing speed: ").append( speed ).append(" tiles/sec" );
                            }
                        }
                    }
                }
                catch(Exception ignore) {}
            }
            sb.append( " }");
        }
        return sb.toString();
    }
}
