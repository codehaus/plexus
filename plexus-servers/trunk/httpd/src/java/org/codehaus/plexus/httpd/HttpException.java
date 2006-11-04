/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * HttpException is emitted when an error parsing an HTTP request appears
 *
 * @author <a href="mailto:tibu@users.sourceforge.net">Carlos Quiroz</a>
 * @version $Revision$
 */
public class HttpException extends IOException
{
    /** Error code */
    protected int code;

    /**
     * Constructor for the HttpException object
     *
     * @param code    Error code
     * @param description  Description
     */
    public HttpException( int code, String description )
    {
        super( description );
        this.code = code;
    }

    /**
     * Return the exception code
     */
    public int getCode()
    {
        return code;
    }

    public Document getResponseDoc()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement( "HttpException" );
            root.setAttribute( "code", Integer.toString( code ) );
            root.setAttribute( "description", getMessage() );
            document.appendChild( root );
            return document;
        }
        catch ( ParserConfigurationException e )
        {
            return null;
        }
    }
}
