/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * HttpInputStream processes an HTTP request
 *
 * @author <a href="mailto:tibu@users.sourceforge.net">Carlos Quiroz</a>
 * @version $Revision$
 */
public class HttpInputStream extends BufferedInputStream
{

    /** Http method. only GET, POST implemented */
    private String method;

    /** Path of the request */
    private String path;

    /** Query string */
    private String queryString;

    /** Request version */
    private float version;

    /** Current headers */
    private Map headers = new HashMap();

    private Map variables = new HashMap();

    /**
     * Constructs a new HttpInputStream
     *
     * @param in  InputStream
     */
    public HttpInputStream( InputStream in )
    {
        super( in );
    }


    /**
     * Returns the method of the request
     *
     * @return   the method of the request GET/POST
     */
    public String getMethod()
    {
        return method;
    }


    /**
     * Returns the path of the request
     *
     * @return   the path of the request
     */
    public String getPath()
    {
        return path;
    }


    /**
     * Returns the query string
     *
     * @return   The queryString value
     */
    public String getQueryString()
    {
        return queryString;
    }


    /**
     * Returns the version of the request
     *
     * @return   The version value 1.0/1.1
     */
    public float getVersion()
    {
        return version;
    }


    /**
     * Returns a given header by name, assumes lower case
     *
     * @param name  Name of the header
     * @return      The header value
     */
    public String getHeader( String name )
    {
        return (String) headers.get( name );
    }

    /**
     * Returns a given header by name, assumes lower case
     *
     * @return      The header value
     */
    public Map getHeaders()
    {
        return headers;
    }


    /**
     * Reads the request parsing the headers
     *
     * @exception IOException  Description of Exception
     */
    public void readRequest() throws IOException
    {
        String request = readLine();
        if ( request == null )
        {
            throw new HttpException( HttpConstants.STATUS_BAD_REQUEST, "Null query" );
        }
        // Parses the request
        StringTokenizer parts = new StringTokenizer( request );
        try
        {
            parseMethod( parts.nextToken() );
            parseRequest( parts.nextToken() );
        }
        catch ( NoSuchElementException ex )
        {
            throw new HttpException( HttpConstants.STATUS_BAD_REQUEST, request );
        }
        if ( parts.hasMoreTokens() )
        {
            parseVersion( parts.nextToken() );
        }
        else
        {
            version = 0.9f;
        }
        if ( version >= 1.0f )
        {
            readHeaders();
            parseVariables();
        }
    }


    /**
     * Reads an HTTP line
     *
     * @return A read line
     * @exception IOException  Emmited in case of errors reading the stream
     */
    public String readLine() throws IOException
    {
        StringBuffer line = new StringBuffer( 64 );
        line.delete( 0, line.length() );
        int c;
        while ( ( ( c = read() ) != -1 ) && ( c != '\n' ) && ( c != '\r' ) )
        {
            line.append( (char) c );
        }
        if ( ( c == '\r' ) && ( ( c = read() ) != '\n' ) && ( c != -1 ) )
        {
            --pos;
        }
        if ( ( c == -1 ) && ( line.length() == 0 ) )
        {
            return null;
        }
        else
        {
            return line.toString();
        }
    }

    /**
     * Returns a map with the variables passed in the request.
     *
     * @return A map containing variables/value pairs. If a variable is
     * present only once in the request the value will be a String.
     * If it is present many times the variable will be a String[]
     */
    public Map getVariables()
    {
        return variables;
    }

    /**
     * Returns one variable value. If the variable is present many times, the first
     * instance will be returned
     *
     * @return A String with the variable value
     */
    public String getVariable( String name )
    {
        if ( variables.containsKey( name ) )
        {
            Object variable = variables.get( name );
            if ( variable instanceof String )
            {
                return (String) variable;
            }
            else if ( variable instanceof String[] )
            {
                return ( (String[]) variable )[0];
            }
        }
        return null;
    }

    /**
     * Returns one variable values.
     *
     * @return A String array with all variable instances
     */
    public String[] getVariableValues( String name )
    {
        if ( variables.containsKey( name ) )
        {
            Object variable = variables.get( name );
            if ( variable instanceof String[] )
            {
                return (String[]) variable;
            }
            else if ( variable instanceof String )
            {
                String[] result = new String[1];
                result[0] = (String) variable;
                return result;
            }
        }
        return null;
    }

    /**
     * Parses the request parameters
     */
    protected void parseVariables() throws HttpException
    {
        try
        {
            // get request variables
            StringTokenizer parser = new StringTokenizer( getQueryString(), "&" );
            while ( parser.hasMoreTokens() )
            {
                String command = parser.nextToken();
                int equalIndex = command.indexOf( '=' );
                if ( equalIndex > 0 )
                {
                    String variableName = URLDecoder.decode( command.substring( 0, equalIndex ) );
                    String variableValue = URLDecoder.decode( command.substring( equalIndex + 1, command.length() ) );
                    variableValue = new String( variableValue.getBytes(), "UTF-8" );
                    if ( variables.get( variableName ) != null )
                    {
                        Object value = variables.get( variableName );
                        String[] newValue = null;
                        if ( value instanceof String )
                        {
                            newValue = new String[2];
                            newValue[0] = variableValue;
                            newValue[1] = (String) value;
                        }
                        else
                        {
                            String[] oldValue = (String[]) value;
                            newValue = new String[oldValue.length + 1];
                            System.arraycopy( oldValue, 0, newValue, 1, oldValue.length );
                            newValue[0] = variableValue;
                        }
                        variables.put( variableName, newValue );
                    }
                    else
                    {
                        variables.put( variableName, variableValue );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            throw new HttpException( HttpConstants.STATUS_BAD_REQUEST, getQueryString() );
        }
    }


    /**
     * Parses the connection method. GET/POST are allowed
     *
     * @param method             Description of Parameter
     * @exception HttpException  Description of Exception
     */
    protected void parseMethod( String method ) throws HttpException
    {
        if ( method.equals( HttpConstants.METHOD_GET ) )
        {
            this.method = HttpConstants.METHOD_GET;
        }
        else if ( method.equals( HttpConstants.METHOD_POST ) )
        {
            this.method = HttpConstants.METHOD_POST;
        }
        else
        {
            throw new HttpException( HttpConstants.STATUS_NOT_IMPLEMENTED, method );
        }
    }


    /**
     * Parses the request
     *
     * @param request Request string
     * @exception HttpException  Thrown if an error ocurr
     */
    protected void parseRequest( String request ) throws HttpException
    {
        if ( !request.startsWith( "/" ) )
        {
            throw new HttpException( HttpConstants.STATUS_BAD_REQUEST, request );
        }
        int queryIdx = request.indexOf( '?' );
        if ( queryIdx == -1 )
        {
            path = HttpUtil.canonicalizePath( request );
            queryString = "";
        }
        else
        {
            path = HttpUtil.canonicalizePath( request.substring( 0, queryIdx ) );
            queryString = request.substring( queryIdx + 1 );
        }
    }


    /**
     * Parses the request HttpConstants version
     *
     * @param verStr String containing the HTTP version
     * @exception HttpException
     */
    protected void parseVersion( String verStr ) throws HttpException
    {
        if ( !verStr.startsWith( "HTTP/" ) )
        {
            throw new HttpException( HttpConstants.STATUS_BAD_REQUEST, verStr );
        }
        try
        {
            version = Float.valueOf( verStr.substring( 5 ) ).floatValue();
        }
        catch ( NumberFormatException ex )
        {
            throw new HttpException( HttpConstants.STATUS_BAD_REQUEST, verStr );
        }
    }


    /**
     * Reads the headers
     *
     * @exception IOException
     */
    protected void readHeaders() throws IOException
    {
        String header;
        while ( ( ( header = readLine() ) != null ) && !header.equals( "" ) )
        {
            int colonIdx = header.indexOf( ':' );
            if ( colonIdx != -1 )
            {
                String name = header.substring( 0, colonIdx );
                String value = header.substring( colonIdx + 1 );
                headers.put( name.toLowerCase(), value.trim() );
            }
        }
    }
}
