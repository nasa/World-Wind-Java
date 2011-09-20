/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * A collection of static methods use for opening, reading and otherwise working with XML files.
 *
 * @author dcollins
 * @version $Id$
 */
public class WWXML
{
    /**
     * Create a DOM builder.
     *
     * @param isNamespaceAware true if the builder is to be namespace aware, otherwise false.
     *
     * @return a {@link javax.xml.parsers.DocumentBuilder}.
     *
     * @throws WWRuntimeException if an error occurs.
     */
    public static DocumentBuilder createDocumentBuilder(boolean isNamespaceAware)
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        docBuilderFactory.setNamespaceAware(isNamespaceAware);

        //if (Configuration.getJavaVersion() >= 1.6)
        //{
        //    try
        //    {
        //        docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
        //            false);
        //    }
        //    catch (ParserConfigurationException e)
        //    {   // Note it and continue on. Some Java5 parsers don't support the feature.
        //        Logging.logger().finest(Logging.getMessage("XML.NonvalidatingNotSupported"));
        //    }
        //}

        try
        {
            return docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            String msg = Logging.getMessage("XML.ParserConfigurationException");
            Logging.verbose(msg);
            throw new WWRuntimeException(msg, e);
        }
    }

    /**
     * Open and XML document from a general source. The source type may be one of the following: <ul> <li>a {@link
     * java.net.URL}</li> <li>an {@link java.io.InputStream}</li> <li>a {@link java.io.File}</li> <li>a {@link String}
     * containing a valid URL description or a file or resource name available on the classpath.</li> </ul>
     *
     * @param source the source of the XML document.
     *
     * @return the source document as a {@link org.w3c.dom.Document}, or null if the source object is a string that does
     *         not identify a URL, a file or a resource available on the classpath.
     */
    public static Document openDocument(Object source)
    {
        if (source == null || WWUtil.isEmpty(source))
        {
            throw new IllegalArgumentException(Logging.getMessage("nullValue.SourceIsNull"));
        }

        if (source instanceof URL)
        {
            return openDocumentURL((URL) source);
        }
        else if (source instanceof InputStream)
        {
            return openDocumentStream((InputStream) source);
        }
        else if (source instanceof File)
        {
            return openDocumentFile(((File) source).getPath(), null);
        }
        else if (!(source instanceof String))
        {
            throw new IllegalArgumentException(Logging.getMessage("generic.SourceTypeUnrecognized", source));
        }

        String sourceName = (String) source;

        URL url = WWIO.makeURL(sourceName);
        if (url != null)
            return openDocumentURL(url);

        return openDocumentFile(sourceName, null);
    }

    /**
     * Opens an XML file given the file's location in the file system or on the classpath.
     *
     * @param path the path to the file. Must be an absolute path or a path relative to a location in the classpath.
     * @param c    the class that is used to find a path relative to the classpath.
     *
     * @return a DOM for the file, or null if the specified cannot be found.
     *
     * @throws IllegalArgumentException if the file path is null.
     * @throws WWRuntimeException       if an exception or error occurs while opening and parsing the file. The causing
     *                                  exception is included in this exception's {@link Throwable#initCause(Throwable)}
     *                                  .
     */
    public static Document openDocumentFile(String path, Class c)
    {
        if (path == null)
        {
            throw new IllegalArgumentException(Logging.getMessage("nullValue.PathIsNull"));
        }

        InputStream inputStream = WWIO.openFileOrResourceStream(path, c);

        return inputStream != null ? openDocumentStream(inputStream) : null;
    }

    /**
     * Opens an XML document given a generic {@link java.net.URL} reference.
     *
     * @param url the URL to the document.
     *
     * @return a DOM for the URL.
     *
     * @throws IllegalArgumentException if the url is null.
     * @throws WWRuntimeException       if an exception or error occurs while opening and parsing the url. The causing
     *                                  exception is included in this exception's {@link Throwable#initCause(Throwable)}
     *                                  .
     */
    public static Document openDocumentURL(URL url)
    {
        if (url == null)
        {
            throw new IllegalArgumentException(Logging.getMessage("nullValue.UrlIsNull"));
        }

        try
        {
            InputStream inputStream = url.openStream();
            return openDocumentStream(inputStream);
        }
        catch (IOException e)
        {
            throw new WWRuntimeException(Logging.getMessage("XML.ExceptionParsingXml", url), e);
        }
    }

    /**
     * Opens an XML document given an input stream.
     *
     * @param inputStream the document as an input stream.
     *
     * @return a DOM for the stream content.
     *
     * @throws IllegalArgumentException if the input stream is null.
     * @throws WWRuntimeException       if an exception or error occurs while parsing the stream. The causing exception
     *                                  is included in this exception's {@link Throwable#initCause(Throwable)}
     */
    public static Document openDocumentStream(InputStream inputStream)
    {
        return openDocumentStream(inputStream, true);
    }

    public static Document openDocumentStream(InputStream inputStream, boolean isNamespaceAware)
    {
        if (inputStream == null)
        {
            throw new IllegalArgumentException(Logging.getMessage("nullValue.InputStreamIsNull"));
        }

        try
        {
            return WWXML.createDocumentBuilder(isNamespaceAware).parse(inputStream);
        }
        catch (SAXException e)
        {
            throw new WWRuntimeException(Logging.getMessage("XML.ExceptionParsingXml", inputStream), e);
        }
        catch (IOException e)
        {
            throw new WWRuntimeException(Logging.getMessage("XML.ExceptionParsingXml", inputStream), e);
        }
    }

    /**
     * Shortcut method to create an {@link javax.xml.xpath.XPath}.
     *
     * @return a new XPath.
     */
    public static XPath makeXPath()
    {
        XPathFactory xpFactory = XPathFactory.newInstance();
        return xpFactory.newXPath();
    }

    /**
     * Returns the element node's unqualified name. If the element is qualified with a namespace, this returns the local
     * part of the qualified name. Otherwise, this returns the element's unqualified tag name.
     *
     * @param context the element who's unqualified name is returned.
     *
     * @return the unqualified tag name of the specified element.
     *
     * @throws IllegalArgumentException if the context is null.
     */
    public static String getUnqualifiedName(Element context)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return (context.getLocalName() != null) ? context.getLocalName() : context.getTagName();
    }

    /**
     * Returns the element identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return the element matching the XPath expression, or null if no element matches.
     *
     * @throws IllegalArgumentException if the context or XPath expression are null.
     */
    public static Element getElement(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (path == null)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (xpath == null)
            xpath = makeXPath();

        try
        {
            Node node = (Node) xpath.evaluate(path, context, XPathConstants.NODE);
            if (node == null)
                return null;

            return node instanceof Element ? (Element) node : null;
        }
        catch (XPathExpressionException e)
        {
            Logging.warning(Logging.getMessage("XML.InvalidXPathExpression", "internal expression"));
            return null;
        }
    }

    /**
     * Returns all elements identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return an array containing the elements matching the XPath expression.
     *
     * @throws IllegalArgumentException if the context or XPath expression are null.
     */
    public static List<Element> getElements(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (path == null)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (xpath == null)
            xpath = makeXPath();

        try
        {
            NodeList nodes = (NodeList) xpath.evaluate(path, context, XPathConstants.NODESET);
            if (nodes == null || nodes.getLength() == 0)
                return null;

            ArrayList<Element> elements = new ArrayList<Element>();
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                if (node instanceof Element)
                    elements.add((Element) node);
            }

            return elements;
        }
        catch (XPathExpressionException e)
        {
            Logging.warning(Logging.getMessage("XML.InvalidXPathExpression", "internal expression"), e);
            return null;
        }
    }

    /**
     * Returns the text of the element identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return the text of an element matching the XPath expression, or null if no match is found.
     *
     * @throws IllegalArgumentException if the context or XPath expression are null.
     */
    public static String getText(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (path == null)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (xpath == null)
            xpath = makeXPath();

        try
        {
            return xpath.evaluate(path, context);
        }
        catch (XPathExpressionException e)
        {
            return null;
        }
    }

    /**
     * Returns the {@link Integer} value of an element identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return the value of an element matching the XPath expression, or null if no match is found or the match does not
     *         contain a {@link Integer}.
     *
     * @throws IllegalArgumentException if the context or XPath expression are null.
     */
    public static Integer getInteger(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (path == null)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        String s = null;

        try
        {
            s = getText(context, path, xpath);
            if (WWUtil.isEmpty(s))
                return null;

            return Integer.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            Logging.error(Logging.getMessage("generic.ConversionError", s));
            return null;
        }
    }

    /**
     * Returns the {@link Double} value of an element identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return the value of an element matching the XPath expression, or null if no match is found or the match does not
     *         contain a {@link Double}.
     *
     * @throws IllegalArgumentException if the context or XPath expression are null.
     */
    public static Double getDouble(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (path == null)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        String s = null;

        try
        {
            s = getText(context, path, xpath);
            if (WWUtil.isEmpty(s))
                return null;

            return Double.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            Logging.error(Logging.getMessage("generic.ConversionError", s));
            return null;
        }
    }

    /**
     * Returns the {@link Boolean} value of an element identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return the value of an element matching the XPath expression, or null if no match is found or the match does not
     *         contain a {@link Boolean}.
     *
     * @throws IllegalArgumentException if the context or XPath expression are null.
     */
    public static Boolean getBoolean(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (path == null)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        String s = null;

        try
        {
            s = getText(context, path, xpath);
            if (WWUtil.isEmpty(s))
                return null;

            return Boolean.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            Logging.error(Logging.getMessage("generic.ConversionError", s));
            return null;
        }
    }

    /**
     * Returns the {@link gov.nasa.worldwind.geom.LatLon} value of an element identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression. If null, indicates that the context is the LatLon element itself. If
     *                non-null, the context is searched for a LatLon element using the expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return the value of an element matching the XPath expression, or null if no match is found or the match does not
     *         contain a {@link gov.nasa.worldwind.geom.LatLon}.
     *
     * @throws IllegalArgumentException if the context is null.
     */
    public static LatLon getLatLon(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            Element el = path == null ? context : getElement(context, path, xpath);
            if (el == null)
                return null;

            String units = getText(el, "@units", xpath);
            Double lat = getDouble(el, "@latitude", xpath);
            Double lon = getDouble(el, "@longitude", xpath);

            if (lat == null || lon == null)
                return null;

            if (units == null || units.equals("degrees"))
                return LatLon.fromDegrees(lat, lon);

            if (units.equals("radians"))
                return LatLon.fromRadians(lat, lon);

            // Warn that units are not recognized
            Logging.warning(Logging.getMessage("generic.UnitsUnrecognized", units));

            return null;
        }
        catch (NumberFormatException e)
        {
            Logging.warning(Logging.getMessage("generic.ConversionError", path));
            return null;
        }
    }

    /**
     * Returns the {@link gov.nasa.worldwind.geom.Sector} value of an element identified by an XPath expression.
     *
     * @param context the context from which to start the XPath search.
     * @param path    the XPath expression. If null, indicates that the context is the Sector element itself. If
     *                non-null, the context is searched for a Sector element using the expression.
     * @param xpath   an {@link XPath} object to use for the search. This allows the caller to re-use XPath objects when
     *                performing multiple searches. May be null.
     *
     * @return the value of an element matching the XPath expression, or null if no match is found or the match does not
     *         contain a {@link gov.nasa.worldwind.geom.Sector}.
     *
     * @throws IllegalArgumentException if the context is null.
     */
    public static Sector getSector(Element context, String path, XPath xpath)
    {
        if (context == null)
        {
            String msg = Logging.getMessage("nullValue.ContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Element el = path == null ? context : getElement(context, path, xpath);
        if (el == null)
            return null;

        LatLon sw = getLatLon(el, "SouthWest/LatLon", xpath);
        LatLon ne = getLatLon(el, "NorthEast/LatLon", xpath);

        if (sw == null || ne == null)
            return null;

        return new Sector(sw.latitude, ne.latitude, sw.longitude, ne.longitude);
    }

    /**
     * Uses reflection to invoke property methods on an object, with the properties specified in an XML document. For
     * each element named "Property" in the document, the corresponding <i>set</i> method is called on the specified
     * object, if such a method exists.
     *
     * @param parent     the object on which to set the properties.
     * @param domElement the XML document containing the properties.
     *
     * @throws IllegalArgumentException if the specified object or XML document element is null.
     * @see WWUtil#invokePropertyMethod(Object, String, String)
     */
    public static void invokePropertySetters(Object parent, Element domElement)
    {
        if (parent == null)
        {
            String msg = Logging.getMessage("nullValue.ParentIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (domElement == null)
        {
            String msg = Logging.getMessage("nullValue.ElementIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        List<Element> elements = getElements(domElement, "Property", null);
        if (elements == null || elements.size() == 0)
            return;

        for (Element element : elements)
        {
            String propertyName = element.getAttribute("name");
            if (WWUtil.isEmpty(propertyName))
                continue;

            String propertyValue = element.getAttribute("value");

            try
            {
                WWUtil.invokePropertyMethod(parent, propertyName, propertyValue);
            }
            catch (NoSuchMethodException e)
            {
                // No property method, so just add the property to the object's AVList if it has one.
                if (parent instanceof AVList)
                    ((AVList) parent).setValue(propertyName, propertyValue);
                // This is a benign exception; not all properties have set methods.
            }
            catch (InvocationTargetException e)
            {
                Logging.warning(Logging.getMessage("generic.ExceptionInvokingPropertySetter", propertyName), e);
            }
            catch (IllegalAccessException e)
            {
                Logging.warning(Logging.getMessage("generic.ExceptionInvokingPropertySetter", propertyName), e);
            }
        }
    }
}
