package org.codehaus.plexus.summit.util;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.parameters.RequestParameters;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * This creates a Dynamic URI for use within the Turbine system
 * <p/>
 * <p>If you use this class to generate all of your href tags as well
 * as all of your URI's, then you will not need to worry about having
 * session data setup for you or using HttpServletRequest.encodeUrl()
 * since this class does everything for you.
 * <p/>
 * <code><pre>
 * UriBuilder dui = new UriBuilder (data, "UserScreen" );
 * dui.setName("Click Here").addPathInfo("user","jon");
 * dui.getA();
 * </pre></code>
 * <p/>
 * The above call to getA() would return the String:
 * <p/>
 * &lt;A HREF="http://www.server.com:80/servlets/Turbine/screen=UserScreen&amp;amp;user=jon"&gt;Click Here&lt;/A&gt;
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 */
public class UriBuilder
{
    public static final String ROLE = UriBuilder.class.getName();

    /**
     * HTTP protocol.
     */
    public static final String HTTP = "http";

    /**
     * HTTPS protocol.
     */
    public static final String HTTPS = "https";

    /**
     * Length of static part of an A tag
     */
    public static final int ANCHOR_STATIC_PART_LENGTH
        = "<a href=\"\"></a>".length();

    /**
     * The RunData object.
     */
    protected RunData data = null;

    /**
     * Servlet response interface.
     */
    public HttpServletResponse res = null;

    /**
     * A Vector that contains all the path info if any.
     */
    protected ArrayList pathInfo = new ArrayList();

    /**
     * A Vectory that contains all the query data if any.
     */
    protected ArrayList queryData = new ArrayList();

    /**
     * Whether we want to redirect or not.
     */
    protected boolean redirect = false;

    /**
     * P = 0 for path info.
     */
    protected static final int PATH_INFO = 0;

    /**
     * Q = 1 for query data.
     */
    protected static final int QUERY_DATA = 1;

    /**
     * true=relative url's; false=absolute url's
     */
    private boolean isRelative = false;

    /**
     * true=url will be rewritten with session_id if needed; false=url will
     * not be encoded
     */
    private boolean encodeUrl = true;

    /**
     * Default constructor - the init() method must be called befroe use.
     */
    public UriBuilder()
    {
    }

    /**
     * Constructor that takes the <tt>RunData</tt> as an argument.
     *
     * @param data The <tt>RunData</tt> associated with this builder.
     */
    public UriBuilder( RunData data )
    {
        init( data );
    }

    /**
     * Initializes the builder for use by clearing any previous state.
     */
    public void init()
    {
        this.pathInfo.clear();
        this.queryData.clear();
    }

    /**
     * Initialize with a RunData object
     *
     * @param data
     */
    public void init( RunData data )
    {
        this.data = data;
        this.res = this.data.getResponse();
        init();
    }

    /**
     * <p>If the type is P (0), then add name/value to the pathInfo
     * hashtable.
     * <p/>
     * <p>If the type is Q (1), then add name/value to the queryData
     * hashtable.
     *
     * @param type  Type (P or Q) of insertion.
     * @param name  A String with the name to add.
     * @param value A String with the value to add.
     */
    protected void add( int type,
                        String name,
                        String value )
    {
        Object[] tmp = new Object[]{name, value};

        if ( type == PATH_INFO )
        {
            this.pathInfo.add( tmp );
        }
        else if ( type == QUERY_DATA )
        {
            this.queryData.add( tmp );
        }
    }

    /**
     * Add a key value pair (in the form of a 2 object array) to the provided
     * list
     *
     * @param list  List to add to.
     * @param name  A String with the name to add.
     * @param value A String with the value to add.
     */
    protected void addPair( ArrayList list,
                            String name,
                            String value )
    {
        Object[] tmp = new Object[]{name, value};

        list.add( tmp );
    }

    /**
     * Method for a quick way to add all the parameters in a
     * RequestParameters to a given List
     *
     * @param list The list of pairs to add to
     * @param pp   A RequestParameters.
     */
    protected void add( ArrayList list,
                        RequestParameters pp )
    {
        Iterator itr = pp.keys();
        while ( itr.hasNext() )
        {
            String key = (String) itr.next();

            if ( !key.equalsIgnoreCase( SummitConstants.ACTION ) &&
                !key.equalsIgnoreCase( SummitConstants.TARGET ) )
            {
                String[] values = pp.getStrings( key );
                for ( int i = 0; i < values.length; i++ )
                {
                    addPair( list, key, values[i] );
                }
            }
        }
    }

    /**
     * Method for a quick way to add all the parameters in a
     * RequestParameters.
     * <p/>
     * <p>If the type is P (0), then add name/value to the pathInfo
     * hashtable.
     * <p/>
     * <p>If the type is Q (1), then add name/value to the queryData
     * hashtable.
     *
     * @param type Type (P or Q) of insertion.
     * @param pp   A RequestParameters.
     */
    protected void add( int type,
                        RequestParameters pp )
    {
        if ( type == PATH_INFO )
        {
            add( pathInfo, pp );
        }
        else if ( type == QUERY_DATA )
        {
            add( queryData, pp );
        }
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name  A String with the name to add.
     * @param value An Object with the value to add.
     */
    public UriBuilder addPathInfo( String name, Object value )
    {
        addPathInfo( name, value.toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name  A String with the name to add.
     * @param value A String with the value to add.
     */
    public UriBuilder addPathInfo( String name, String value )
    {
        addPair( pathInfo, name, value );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name  A String with the name to add.
     * @param value A double with the value to add.
     */
    public UriBuilder addPathInfo( String name, double value )
    {
        addPathInfo( name, Double.toString( value ) );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name  A String with the name to add.
     * @param value An int with the value to add.
     */
    public UriBuilder addPathInfo( String name, int value )
    {
        addPathInfo( name, new Integer( value ).toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name  A String with the name to add.
     * @param value A long with the value to add.
     */
    public UriBuilder addPathInfo( String name, long value )
    {
        addPathInfo( name, new Long( value ).toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name  A String with the name to add.
     * @param value A double with the value to add.
     */
    public UriBuilder addPathInfo( String name, boolean value )
    {
        addPathInfo( name, ( value ? "true" : "false" ) );
        return this;
    }

    /**
     * Adds a name=value pair for every entry in a RequestParameters
     * object to the path_info string.
     *
     * @param pp A RequestParameters.
     */
    public UriBuilder addPathInfo( RequestParameters pp )
    {
        add( pathInfo, pp );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name  A String with the name to add.
     * @param value An Object with the value to add.
     */
    public UriBuilder addQueryData( String name, Object value )
    {
        addQueryData( name, value.toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name  A String with the name to add.
     * @param value A String with the value to add.
     */
    public UriBuilder addQueryData( String name, String value )
    {
        addPair( queryData, name, value );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name  A String with the name to add.
     * @param value A double with the value to add.
     */
    public UriBuilder addQueryData( String name, double value )
    {
        addQueryData( name, Double.toString( value ) );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name  A String with the name to add.
     * @param value An int with the value to add.
     */
    public UriBuilder addQueryData( String name, int value )
    {
        addQueryData( name, new Integer( value ).toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name  A String with the name to add.
     * @param value A long with the value to add.
     */
    public UriBuilder addQueryData( String name, long value )
    {
        addQueryData( name, new Long( value ).toString() );
        return this;
    }

    /**
     * Adds a name=value pair for every entry in a RequestParameters
     * object to the query string.
     *
     * @param pp A RequestParameters.
     */
    public UriBuilder addQueryData( RequestParameters pp )
    {
        add( queryData, pp );
        return this;
    }

    /**
     * Create an anchor object.  This call to getA():
     * <p/>
     * <code><pre>
     * UriBuilder dui = new UriBuilder (data, "UserScreen" );
     * dui.setName("Click Here").addPathInfo("user","jon");
     * dui.getA();
     * </pre></code>
     * <p/>
     * would return the String:
     * <p/>
     * <p>&lt;A HREF="http://www.server.com:80/servlets/Turbine/screen=UserScreen&amp;amp;user=jon"&gt;Click Here&lt;/A&gt;
     *
     * @param name A String with the name for the anchor.
     * @return The anchor as a &lt;A HREF=""&gt;name&lt;/A&gt;.
     */
    public String getA( String name )
    {
        final String s = this.toString();

        // I'm being a pit picky about size here to avoid useless
        // StringBuffer reallocation

        final int size = ANCHOR_STATIC_PART_LENGTH + s.length() + name.length();

        return new StringBuffer( size )
            .append( "<a href=\"" )
            .append( s )
            .append( "\">" )
            .append( name )
            .append( "</a>" )
            .toString();
    }

    /**
     * Gets the script name (/servlets/Turbine).
     *
     * @return A String with the script name.
     */
    public String getScriptName()
    {
        return data.getScriptName();
    }

    /**
     * Gets the server name.
     *
     * @return A String with the server name.
     */
    public String getServerName()
    {
        return data.getServerName();
    }

    /**
     * Gets the server port.
     *
     * @return A String with the server port.
     */
    public int getServerPort()
    {
        return data.getServerPort();
    }

    /**
     * Gets the server scheme (HTTP or HTTPS).
     *
     * @return A String with the server scheme.
     */
    public String getServerScheme()
    {
        return data.getServerScheme();
    }

    /**
     * <p>If the type is P (0), then remove name/value from the
     * pathInfo hashtable.
     * <p/>
     * <p>If the type is Q (1), then remove name/value from the
     * queryData hashtable.
     *
     * @param type Type (P or Q) of removal.
     * @param name A String with the name to be removed.
     */
    protected void remove( int type,
                           String name )
    {
        try
        {
            if ( type == PATH_INFO )
            {
                removePathInfo( name );
            }
            else if ( type == QUERY_DATA )
            {
                removeQueryData( name );
            }
        }
        catch ( Exception e )
        {
        }
    }

    /**
     * Helper method to remove one or more pairs by its name (ie key).
     * It is intended to be used with <tt>queryData</tt> and <tt>pathInfo</tt>.
     *
     * @param pairs the list of pairs to look over for removal.
     * @param name  the name of the pair(s) to remove.
     */
    protected void removePairByName( ArrayList pairs, String name )
    {
        // CAUTION: the dynamic evaluation of the size is on purpose because
        // elements may be removed on the fly.
        for ( int i = 0; i < pairs.size(); i++ )
        {
            Object[] pair = (Object[]) pairs.get( i );
            if ( name.equals( (String) pair[0] ) )
            {
                pairs.remove( i );
            }
        }
    }

    /**
     * Removes all the path info elements.
     */
    public void removePathInfo()
    {
        this.pathInfo.clear();
    }

    /**
     * Removes a name=value pair from the path info.
     *
     * @param name A String with the name to be removed.
     */
    public void removePathInfo( String name )
    {
        removePairByName( pathInfo, name );
        ;
    }

    /**
     * Removes all the query string elements.
     */
    public void removeQueryData()
    {
        this.queryData.clear();
    }

    /**
     * Removes a name=value pair from the query string.
     *
     * @param name A String with the name to be removed.
     */
    public void removeQueryData( String name )
    {
        removePairByName( queryData, name );
    }

    /**
     * This method takes a Vector of key/value arrays and converts it
     * into a URL encoded querystring format.
     *
     * @param data A Vector of key/value arrays.
     * @return A String with the URL encoded data.
     * @deprecated Prefer the ArrayList / StringBuffer form.
     */
    protected String renderPathInfo( Vector data )
    {
        StringBuffer out = new StringBuffer();

        renderPathInfo( new ArrayList( data ), out );

        return out.toString();
    }

    /**
     * This method takes a Vector of key/value arrays and writes it to the
     * supplied StringBuffer as encoded path info.
     *
     * @param pairs A Vector of key/value arrays.
     * @param out   Buffer to which encoded path info is written
     */
    protected void renderPathInfo( ArrayList pairs, StringBuffer out )
    {
        renderPairs( pairs, out, '/', '/' );
    }

    /**
     * This method takes a Vector of key/value arrays and converts it
     * into a URL encoded querystring format.
     *
     * @param data A Vector of key/value arrays.
     * @return A String with the URL encoded data.
     * @deprecated Prefer the ArrayList / StringBuffer form.
     */
    protected String renderQueryString( Vector data )
    {
        StringBuffer out = new StringBuffer();

        renderQueryString( new ArrayList( data ), out );

        return out.toString();
    }

    /**
     * This method takes a List of key/value arrays and writes it to the
     * provided StringBuffer in encoded query string format.
     *
     * @param data A Vector of key/value arrays.
     * @param out  Buffer to which encoded query string is written.
     */
    protected void renderQueryString( ArrayList data, StringBuffer out )
    {
        renderPairs( data, out, '&', '=' );
    }

    /**
     * This method takes a List of key/value arrays and converts it
     * into a URL encoded key/value pair format with the appropriate
     * separator.
     *
     * @param out       the buffer to write the pairs to.
     * @param pairs     A List of key/value arrays.
     * @param pairSep   the character to use as a separator between pairs.
     *                  For example for a query-like rendering it would be '&'.
     * @param keyValSep the character to use as a separator between
     *                  key and value. For example for a query-like rendering, it would be '='.
     */
    protected void renderPairs( ArrayList pairs, StringBuffer out,
                                char pairSep, char keyValSep )
    {
        boolean first = true;

        final int count = pairs.size();
        for ( int i = 0; i < count; i++ )
        {
            Object[] pair = (Object[]) pairs.get( i );

            if ( first )
            {
                first = false;
            }
            else
            {
                out.append( pairSep );
            }

            writeFastEncoded( (String) pair[0], out );
            out.append( keyValSep );
            writeFastEncoded( (String) pair[1], out );
        }
    }

    /**
     * Sets the action= value for this URL.
     * <p/>
     * <p>By default it adds the information to the path_info instead
     * of the query data.
     *
     * @param action A String with the action value.
     * @return A UriBuilder (self).
     */
    public UriBuilder setAction( String action )
    {
        addPathInfo( SummitConstants.ACTION, action );
        return this;
    }

    /**
     * Sets the target = value for this URL.
     * <p/>
     * <p>By default it adds the information to the path_info instead
     * of the query data.
     *
     * @param template A String with the template value.
     * @return A UriBuilder (self).
     */
    public UriBuilder setTarget( String template )
    {
        addPathInfo( SummitConstants.TARGET, template );
        return this;
    }

    /**
     * Sets whether we want to redirect or not.
     *
     * @param doRedirect True if it should redirect.
     * @return A UriBuilder (self).
     */
    public UriBuilder setRedirect( boolean doRedirect )
    {
        this.redirect = doRedirect;
        return this;
    }

    /**
     * Method to specify that a URI should use SSL.  Whether or not it
     * does is determined from TurbineResources.properties.  Port
     * number is 443.
     *
     * @return A UriBuilder (self).
     * /
     public UriBuilder setSecure()
     {
     return setSecure(443);
     }*/

    /**
     * Method to specify that a URI should use SSL.  Whether or not it
     * does is determined from TurbineResources.properties.
     *
     * @param port An int with the port number.
     * @return A UriBuilder (self).
     * /
     public UriBuilder setSecure(int port)
     {
     boolean isSSL = Turbine.getConfiguration()
     .getBoolean(Turbine.USE_SSL, true);
     if (isSSL)
     {
     data.setServerScheme(UriBuilder.HTTPS);
     data.setServerPort(port);
     }
     return this;
     }*/

    /**
     * if true, the scheme, domain, and port will not be included in the
     * String representation of this uri..
     *
     * @param b a <code>boolean</code>
     * @return a <code>UriBuilder</code> (self)
     */
    public UriBuilder setRelative( boolean b )
    {
        isRelative = b;
        return this;
    }

    /**
     * Will a call to toString() generate a relative url?
     * where relative means no scheme, domain, and port info
     *
     * @return a <code>boolean</code>
     */
    public boolean isRelative()
    {
        return isRelative;
    }

    /**
     * Can be used to disable url rewriting.
     *
     * @param b a <code>boolean</code>
     * @return a <code>UriBuilder</code> (self)
     */
    public UriBuilder setEncodeUrl( boolean b )
    {
        encodeUrl = b;
        return this;
    }

    /**
     * Will a call to toString() add session info if needed to maintain
     * a session.  Does not determine whether the url will be rewritten,
     * only that it might be.
     *
     * @return a <code>boolean</code>
     */
    public boolean isEncodeUrl()
    {
        return encodeUrl;
    }

    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl().
     * <p/>
     * <p/>
     * <code><pre>
     * UriBuilder dui = new UriBuilder (data, "UserScreen" );
     * dui.addPathInfo("user","jon");
     * dui.toString();
     * </pre></code>
     * <p/>
     * The above call to toString() would return the String:
     * <p/>
     * <p/>
     * http://www.server.com/servlets/Turbine/screen/UserScreen/user/jon
     *
     * @return A String with the built URL.
     */
    public String toString()
    {
        StringBuffer output = new StringBuffer();
        if ( !isRelative() )
        {
            output.append( data.getServerScheme() );
            output.append( "://" );
            output.append( data.getServerName() );
            if ( ( data.getServerScheme().equals( HTTP ) &&
                data.getServerPort() != 80 ) ||
                ( data.getServerScheme().equals( HTTPS ) &&
                data.getServerPort() != 443 ) )
            {
                output.append( ':' );
                output.append( data.getServerPort() );
            }
        }

        output.append( data.getContextPath() );
        output.append( data.getScriptName() );

        if ( this.hasPathInfo() )
        {
            output.append( '/' );
            renderPathInfo( this.pathInfo, output );
        }
        if ( this.hasQueryData() )
        {
            output.append( '?' );
            renderQueryString( this.queryData, output );
        }

        // There seems to be a bug in Apache JServ 1.0 where the
        // session id is not appended to the end of the url when a
        // cookie has not been set.
        if ( this.res != null && isEncodeUrl() )
        {
            if ( this.redirect )
            {
                return res.encodeRedirectURL( output.toString() );
            }
            else
            {
                return res.encodeURL( output.toString() );
            }
        }
        else
        {
            return output.toString();
        }
    }

    /**
     * Given a RunData object, get a URI for the request.  This is
     * necessary sometimes when you want the exact URL and don't want
     * UriBuilder to be too smart and remove actions, screens, etc.
     * This also returns the Query Data where UriBuilder normally
     * would not.  This method always returns absolute url's.
     *
     * @param data A Turbine RunData object.
     * @return A String with the URL representing the RunData.
     */
    public static String toString( RunData data )
    {
        return toString( data, true );
    }

    /**
     * Given a RunData object, get a URI for the request.  This is
     * necessary sometimes when you want the exact URL and don't want
     * UriBuilder to be too smart and remove actions, screens, etc.
     * This also returns the Query Data where UriBuilder normally
     * would not.
     *
     * @param data       A Turbine RunData object.
     * @param isAbsolute to determine absolute vs. relative links.
     * @return A String with the URL representing the RunData.
     */
    public static String toString( RunData data, boolean isAbsolute )
    {
        StringBuffer output = new StringBuffer( 128 );
        HttpServletRequest request = data.getRequest();

        if ( isAbsolute )
        {
            output.append( data.getServerScheme() );
            output.append( "://" );
            output.append( data.getServerName() );

            if ( ( data.getServerScheme().equals( HTTP ) &&
                data.getServerPort() != 80 ) ||
                ( data.getServerScheme().equals( HTTPS ) &&
                data.getServerPort() != 443 ) )
            {
                output.append( ':' );
                output.append( data.getServerPort() );
            }
        }

        output.append( data.getContextPath() );
        output.append( data.getScriptName() );

        if ( request.getPathInfo() != null )
        {
            output.append( request.getPathInfo() );
        }

        if ( request.getQueryString() != null )
        {
            output.append( '?' );
            output.append( request.getQueryString() );
        }
        return output.toString();
    }

    /**
     * URL encodes <code>in</code> and writes it to <code>out</code>. If the
     * string is null, 'null' will be written.
     * This method is faster if
     * the string does not contain any characters needing encoding.  It
     * adds some penalty for strings which actually need to be encoded.
     * for short strings ~20 characters the upside is a 75% decrease.  while
     * the penalty is a 10% increase.  As many query parameters do not need
     * encoding even in i18n applications it should be much better to
     * delay the byte conversion.
     *
     * @param in  String to write.
     * @param out Buffer to write to.
     */
    protected static final void writeFastEncoded( String in, StringBuffer out )
    {
        if ( in == null || in.length() == 0 )
        {
            out.append( "null" );
            return;
        }

        char[] chars = in.toCharArray();

        for ( int i = 0; i < chars.length; i++ )
        {
            char c = chars[i];

            if ( c < 128 && safe[c] )
            {
                out.append( c );
            }
            else if ( c == ' ' )
            {
                out.append( '+' );
            }
            else
            {
                // since we need to encode we will give up on
                // doing it the fast way and convert to bytes.
                writeEncoded( new String( chars, i, chars.length - i ), out );
                break;
            }
        }
    }

    /**
     * URL encodes <code>in</code> and writes it to <code>out</code>. If the
     * string is null, 'null' will be written.
     *
     * @param in  String to write.
     * @param out Buffer to write to.
     */
    protected static final void writeEncoded( String in, StringBuffer out )
    {
        if ( in == null || in.length() == 0 )
        {
            out.append( "null" );
            return;
        }

        // This is the most expensive operation:

        byte[] bytes = in.getBytes();

        for ( int i = 0; i < bytes.length; i++ )
        {
            char c = (char) bytes[i];

            if ( c < 128 && safe[c] )
            {
                out.append( c );
            }
            else if ( c == ' ' )
            {
                out.append( '+' );
            }
            else
            {
                byte toEscape = bytes[i];
                out.append( '%' );
                int low = (int) ( toEscape & 0x0f );
                int high = (int) ( ( toEscape & 0xf0 ) >> 4 );
                out.append( hexadecimal[high] );
                out.append( hexadecimal[low] );
            }
        }
    }

    /**
     * Does this URI have path info.
     */
    public boolean hasPathInfo()
    {
        return !pathInfo.isEmpty();
    }

    /**
     * Does this URI have query data.
     */
    public boolean hasQueryData()
    {
        return !queryData.isEmpty();
    }

    // ------------------------------------- private constants for url encoding

    /**
     * Array mapping hexadecimal values to the corresponding ASCII characters.
     */
    private static final char[] hexadecimal =
        {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'
        };

    /**
     * Characters that need not be encoded. This is much faster than using a
     * BitSet, and for such a small array the space cost seems justified.
     */
    private static boolean[] safe = new boolean[128];

    /** Static initializer for {@link #safe} */
    static
    {
        for ( int i = 'a'; i <= 'z'; i++ )
        {
            safe[i] = true;
        }
        for ( int i = 'A'; i <= 'Z'; i++ )
        {
            safe[i] = true;
        }
        for ( int i = '0'; i <= '9'; i++ )
        {
            safe[i] = true;
        }

        safe['-'] = true;
        safe['_'] = true;
        safe['.'] = true;
        safe['!'] = true;
        safe['~'] = true;
        safe['*'] = true;
        safe['\''] = true;
        safe['('] = true;
        safe[')'] = true;
    }
}
