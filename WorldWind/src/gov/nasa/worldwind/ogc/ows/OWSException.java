/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.util.xml.*;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.util.*;
import java.util.logging.Level;

/**
 * Parses an OGC Web Service Common (OWS) ExceptionType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsExceptionReport.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSException extends AbstractXMLEventParser
{
    protected QName EXCEPTION_TEXT;

    protected List<String> exceptionText = new ArrayList<String>();

    /**
     * Creates a new <code>OWSException</code> with the default namespace URI <code>OGCConstants.OWS_NAMESPACE</code>,
     * an empty list of exception text, no exception code, and no locator.
     */
    public OWSException()
    {
        super(OWSConstants.OWS_1dot1_NAMESPACE);

        this.initialize();
    }

    /**
     * Creates a new <code>OWSException</code> with the specified <code>namespaceURI</code>, an empty list of exception
     * text, no exception code, and no locator.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public OWSException(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        EXCEPTION_TEXT = new QName(this.getNamespaceURI(), "ExceptionText");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, EXCEPTION_TEXT))
        {
            this.addExceptionText((String) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    public String getExceptionCode()
    {
        return (String) this.getField("exceptionCode");
    }

    public void setExceptionCode(String exceptionCode)
    {
        this.setField("exceptionCode", exceptionCode);
    }

    public String getLocator()
    {
        return (String) this.getField("locator");
    }

    public void setLocator(String locator)
    {
        this.setField("locator", locator);
    }

    public List<String> getExceptionText()
    {
        return this.exceptionText;
    }

    public void setExceptionText(List<String> exceptionText)
    {
        this.exceptionText = exceptionText;
    }

    public void addExceptionText(String text)
    {
        if (this.getExceptionText() != null && !WWUtil.isEmpty(text))
            this.getExceptionText().add(text);
    }

    /**
     * Writes this exception and its exception text list to the specified <code>output</code> as an OGC Web Service
     * Common (OWS) <code>Exception</code> XML element.
     * <p/>
     * The exported element is formatted according to the OGC Web Service Common (OWS) specification, and is compatible
     * with OWS version <code>1.0.0</code>, <code>1.1.0</code>, and <code>2.0.0</code>.
     *
     * @param output the XML output stream. May be a <code>{@link java.io.OutputStream}</code>, a <code>{@link
     *               java.io.Writer}</code>, or any type allowed by <code>{@link gov.nasa.worldwind.util.WWXML#openStreamWriter(Object)}</code>.
     *
     * @throws IllegalArgumentException if <code>output</code> is <code>null</code>, or if <code>output</code> is not
     *                                  one of the recognized types.
     * @throws XMLStreamException       if an exception occurs while attempting to export the XML event stream.
     */
    public void export(Object output) throws XMLStreamException
    {
        if (output == null)
        {
            String message = Logging.getMessage("nullValue.OutputIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        XMLStreamWriter writer;
        boolean closeWriterWhenFinished = false;

        if (output instanceof XMLStreamWriter)
        {
            writer = (XMLStreamWriter) output;
        }
        else
        {
            writer = WWXML.openStreamWriter(output);
            closeWriterWhenFinished = true;
        }

        if (writer == null)
        {
            String message = Logging.getMessage("Export.UnsupportedOutputObject", output);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        writer.setDefaultNamespace(this.getNamespaceURI());
        writer.writeStartElement("Exception");

        if (!WWUtil.isEmpty(this.getExceptionCode()))
            writer.writeAttribute("exceptionCode", this.getExceptionCode());

        if (!WWUtil.isEmpty(this.getLocator()))
            writer.writeAttribute("locator", this.getLocator());

        if (this.getExceptionText() != null)
        {
            for (String text : this.getExceptionText())
            {
                try
                {
                    writer.writeStartElement("ExceptionText");
                    writer.writeCharacters(text);
                    writer.writeEndElement();
                }
                catch (Exception e)
                {
                    Logging.logger().log(Level.WARNING, Logging.getMessage("Export.UnableToExportObject", text), e);
                }
            }
        }

        writer.writeEndElement();
        writer.flush();

        if (closeWriterWhenFinished)
            writer.close();
    }
}
