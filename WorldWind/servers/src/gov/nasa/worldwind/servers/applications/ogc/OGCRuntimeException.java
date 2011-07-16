/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.ogc;

import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.ogc.ows.OWSExceptionReport;

import java.io.StringWriter;

/**
 * @author dcollins
 * @version $Id$
 */
public class OGCRuntimeException extends WWRuntimeException
{
    protected OWSExceptionReport exceptionReport = new OWSExceptionReport();

    public OGCRuntimeException()
    {
    }

    public OGCRuntimeException(String s)
    {
        super(s);
    }

    public OGCRuntimeException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public OGCRuntimeException(Throwable throwable)
    {
        super(throwable);
    }

    public OGCRuntimeException(String s, OWSExceptionReport exceptionReport)
    {
        super(s);
        
        this.exceptionReport = exceptionReport;
    }

    public OGCRuntimeException(OWSExceptionReport exceptionReport)
    {
        this.exceptionReport = exceptionReport;
    }

    public OWSExceptionReport getExceptionReport()
    {
        return this.exceptionReport;
    }

    @Override
    public String toString()
    {
        if (this.getExceptionReport() != null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());

            //noinspection EmptyCatchBlock
            try
            {
                StringWriter writer = new StringWriter();
                this.getExceptionReport().export(writer);
                sb.append("\n");
                sb.append(writer);
            }
            catch (Exception e)
            {
            }

            return sb.toString();
        }
        else
        {
            return super.toString();
        }
    }
}
