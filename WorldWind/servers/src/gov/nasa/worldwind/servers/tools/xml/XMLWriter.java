/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.tools.xml;

import gov.nasa.worldwind.util.Logging;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

/**
 * @author garakl
 * @version $Id$
 */

public class XMLWriter
{
    private Writer writer;
    private Stack<String> stack;
    private boolean closed;

    public XMLWriter(Writer writer)
    {
        this.writer = writer;
        this.closed = true;
        this.stack = new Stack<String>();
    }


    public XMLWriter addXmlHeader() throws IOException
    {
        this.writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        return this;
    }

    public XMLWriter addElement(String name, double d) throws IllegalArgumentException, IOException
    {
        return this.addElement(name, Double.toString(d));
    }

    public XMLWriter addElement(String name, String value) throws IllegalArgumentException, IOException
    {
        if ((null == name || 0 == name.length()))
        {
            String message = Logging.getMessage("nullValue.ElementNameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!this.stack.empty() && !this.closed)
        {
            this.closeOpeningTag();
        }

        this.writer.write("<");
        this.writer.write(name);
        if ((null == value || 0 == value.length()))
        {
            this.writer.write(" />");
        }
        else
        {
            this.writer.write(">");
            this.writer.write(value);
            this.writer.write("</");
            this.writer.write(this.escapeXml(name));
            this.writer.write(">");
        }
        return this;
    }


    public XMLWriter openElement(String elementName) throws IOException
    {
        if ((null == elementName || 0 == elementName.length()))
        {
            String message = Logging.getMessage("nullValue.ElementNameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!this.stack.empty() && !this.closed)
        {
            this.closeOpeningTag();
        }

        this.writer.write("<");
        this.writer.write(elementName);
        stack.add(elementName);
        this.closed = false;

        return this;
    }

    public XMLWriter addAttribute(String attrName, int attrValue) throws IOException
    {
        return this.addAttribute(attrName, Integer.toString(attrValue));
    }

    public XMLWriter addAttribute(String attrName, double attrValue) throws IOException
    {
        return this.addAttribute(attrName, Double.toString(attrValue));
    }

    public XMLWriter addAttribute(String attrName, String attrValue)
            throws IOException
    {
        if (null == attrName
            || 0 == attrName.length()
            || null == attrValue
            || 0 == attrValue.length())
        {
            // do not throw exception here, just log it
            Logging.logger().finest("An empty attribute is ignored - " + ((null != attrName) ? attrName : "null"));
            return this;
        }

        if (this.stack.empty())
        {
            throw new IOException("An XML Element must be open filrst.");
        }
        if (this.closed)
        {
            throw new IOException("Cannot add an attribute to already closed XML Element.");
        }

        this.writer.write(" ");
        this.writer.write(attrName);
        this.writer.write("=\"");
        this.writer.write(this.escapeXml(attrValue));
        this.writer.write("\"");

        return this;
    }

    public XMLWriter addValue(String value)
            throws IOException
    {
        if (this.stack.empty())
        {
            throw new IOException("An XML Element must be open filrst.");
        }
        if (this.closed)
        {
            throw new IOException("Cannot add an attribute to already closed XML Element.");
        }

        this.closeOpeningTag();
        this.writer.write(value);

        return this;
    }

    public XMLWriter closeElement(String elementName)
            throws IOException, IllegalArgumentException
    {
        if ((null == elementName || 0 == elementName.length()))
        {
            String message = Logging.getMessage("nullValue.ElementNameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.stack.empty())
        {
            throw new IOException("An XML Element must be open filrst.");
        }

        if (!elementName.equals(this.stack.peek()))
        {
            throw new IOException("Cannot close the element `" + elementName
                                  + "`, expected element is `" + this.stack.peek() + "`");
        }

        if (!this.closed)
        {
            this.closeOpeningTag();
        }

        this.writer.write("</");
        this.writer.write((String) this.stack.pop());
        this.writer.write(">");

        return this;
    }


    public XMLWriter addXML(String xml) throws IOException
    {
        if (this.stack.empty())
        {
            throw new IOException("An XML Element must be open filrst.");
        }
        this.closeOpeningTag();

        if (null != xml && xml.length() > 0)
        {
            this.writer.write(xml);
        }
        return this;
    }

    private void closeOpeningTag() throws IOException
    {
        if (!this.stack.empty() && !this.closed)
        {
            this.closed = true;
            this.writer.write(">");
        }
    }

    private String escapeXml(String s)
    {
        StringBuffer sb = new StringBuffer();
        int n = (null != s) ? s.length() : 0;
        for (int i = 0; i < n; i++)
        {
            char c = s.charAt(i);
            switch (c)
            {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#039;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}

