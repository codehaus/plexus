package org.codehaus.plexus.servlet;

/**
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class MockServletContext
    implements ServletContext
{
    public ServletContext getContext( String arg0 )
    {
        throw new RuntimeException( "not implemented" );
    }

    public int getMajorVersion()
    {
        throw new RuntimeException( "not implemented" );
    }

    public int getMinorVersion()
    {
        throw new RuntimeException( "not implemented" );
    }

    public String getMimeType( String arg0 )
    {
        throw new RuntimeException( "not implemented" );
    }

    public Set getResourcePaths( String arg0 )
    {
        throw new RuntimeException( "not implemented" );
    }

    public URL getResource( String resourceName )
    {
        if ( resourceName.equals( PlexusServletUtils.DEFAULT_PLEXUS_CONFIG ) )
        {
            return MockServletContext.class.getResource( "plexus.xml" );
        }

        if ( resourceName.equals( PlexusServletUtils.DEFAULT_PLEXUS_PROPERTIES ) )
        {
            return MockServletContext.class.getResource( "plexus.properties" );
        }

        throw new RuntimeException( "not implemented" );
    }

    public InputStream getResourceAsStream( String resourceName )
    {
        throw new RuntimeException( "not implemented" );
    }

    public RequestDispatcher getRequestDispatcher( String arg0 )
    {
        throw new RuntimeException( "not implemented" );
    }

    public RequestDispatcher getNamedDispatcher( String arg0 )
    {
        throw new RuntimeException( "not implemented" );
    }

    /**
     * @deprecated
     */
    public Servlet getServlet( String arg0 )
        throws ServletException
    {
        throw new RuntimeException( "not implemented" );
    }

    /**
     * @deprecated
     */
    public Enumeration getServlets()
    {
        throw new RuntimeException( "not implemented" );
    }

    /**
     * @deprecated
     */
    public Enumeration getServletNames()
    {
        throw new RuntimeException( "not implemented" );
    }

    public void log( String arg0 )
    {
        System.out.println( arg0 );
    }

    /**
     * @deprecated
     */
    public void log( Exception arg0, String arg1 )
    {
        System.out.println( arg1 );
        arg0.printStackTrace();
    }

    public void log( String arg0, Throwable arg1 )
    {
        System.out.println( arg0 );
        arg1.printStackTrace();
    }

    public String getRealPath( String resourceName )
    {
        if ( resourceName.equals( "/WEB-INF" ) )
        {
            return resourceName;
        }

        if ( resourceName.equals( "/WEB-INF/plexus.xml" ) )
        {
            return MockServletContext.class.getResource( "plexus.xml" ).getPath();
        }

        return null;
    }

    public String getServerInfo()
    {
        throw new RuntimeException( "not implemented" );
    }

    public String getInitParameter( String arg0 )
    {
        return null;
    }

    private static final Vector EMPTY_VECTOR = new Vector();

    public Enumeration getInitParameterNames()
    {
        return EMPTY_VECTOR.elements();
    }

    private final Map attributes = new HashMap();

    public Object getAttribute( String arg0 )
    {
        return attributes.get( arg0 );
    }

    public Enumeration getAttributeNames()
    {
        Vector v = new Vector( attributes.keySet() );
        return v.elements();
    }

    public void setAttribute( String arg0, Object arg1 )
    {
        attributes.put( arg0, arg1 );
    }

    public void removeAttribute( String arg0 )
    {
        attributes.remove( arg0 );
    }

    public String getServletContextName()
    {
        throw new RuntimeException( "not implemented" );
    }
}
