package org.codehaus.plexus.summit.parameters;

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

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * Default implementation of a <tt>RequestParameterParser</tt>.  This
 * implementation uses the standard servlet method of parsing query info
 * and post data to assemble a parameter map. In addition, any file uploads
 * are processed and are made available via the returned <tt>RequestParameters</tt>
 * object.
 * <p/>
 * This component is thread-safe.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 */
public class BaseRequestParameterParser
    extends AbstractLogEnabled
    implements RequestParameterParser, Initializable
{
    /**
     * A file upload object used to parse uploaded files
     */
    private DiskFileUpload fileUpload = new DiskFileUpload();

    /**
     * @plexus.configuration
     *  default-value="1048576"
     */
    private int maxUploadSize;

    /**
     * @plexus.configuration
     *  default-value="10240"
     */
    private int maxMemorySize;

    /**
     * @plexus.configuration
     *  default-value="${basedir}/temp"
     */
    private String tempDirectory;

    /**
     * @plexus.configuration
     *  default-value="US-ASCII"
     */
    private String defaultEncoding;

    public void initialize()
        throws InitializationException
    {
        fileUpload.setSizeMax( maxUploadSize );

        fileUpload.setSizeThreshold( maxMemorySize );

        fileUpload.setRepositoryPath( tempDirectory );
    }

    /**
     * Parses the query info for the parameters associated with this
     * request as well as any uploaded files sent with the HTTP request.
     * Note: This parser does not inspect the path info for name/value
     * pairs as the Turbine does.  If you want the path info parsed for
     * parameters, use the {@link SummitRequestParameterParser} instead.
     * <p/>
     * Implementation note: This method is a template method and provides
     * a hook that implementors may use for additional parsing of the
     * request.  This method does the following:
     * <ul>
     * <li> Processes any file upload requests.
     * <li> Parses the query info for parameters.
     * <li> Invokes <tt>doParse</tt> which may be implemented by
     * subclasses to do additional processing on the request.
     * <li> Returns the parameter map wrapped in a RequestParameter
     * object.
     * </ul>
     *
     * @param request The HTTP request to parse for parameters.
     * @return RequestParameters The requested parameters wrapped in
     *         a RequestParameters object for easy access to the parameters.
     * @see org.codehaus.plexus.summit.parameters.RequestParameterParser#parse(javax.servlet.http.HttpServletRequest)
     */
    public RequestParameters parse( HttpServletRequest request )
    {
        Map parameterMap = new HashMap();
        String encoding =
            request.getCharacterEncoding() == null
            ? defaultEncoding
            : request.getCharacterEncoding();

        if ( FileUpload.isMultipartContent( request ) )
        {
            processFileUploadItems( request, parameterMap, encoding );
        }

        processQueryInfo( request, parameterMap );
        doParse( request, parameterMap );

        return new RequestParameters( parameterMap, encoding );
    }

    /**
     * Invoked as part of the <tt>parse</tt> template method to provide a
     * hook for additional parsing of parameters.  The HTTP request is
     * passed as an argument as well as a map which is acting as a collector
     * argument.  The map contains name/value pairs where each name is a
     * <tt>String</tt> representing the parameter name, and the value is an
     * array of either <tt>String</tt>s or <tt>FileItem</tt>s representing
     * the value of the parameter.  Note: the value <b>must</b> be an array
     * even if there is only a single value.
     *
     * @param request      The HTTP request to parse for parameters.
     * @param parameterMap A map of the collected parameters which should
     *                     be used to store additional parameters in the format described above.
     *                     A helper method has been provided to ensure the values are stored
     *                     as arrays (see <tt>addParameter</tt>).
     */
    protected void doParse( HttpServletRequest request, Map parameterMap )
    {
    }

    /**
     * Process the specified HTTP request for uploaded files sent via a mime
     * multipart request.  Each file is parsed and stored in the specified
     * parameter map as an array of <tt>FileItem</tt>s.  Form fields part of
     * the multipart request are also decoded and stored in the parameter
     * map as arrays of <tt>String</tt>s.
     *
     * @param request      The HTTP request to be parsed.
     * @param parameterMap The parameter map which is populated with the
     *                     uploaded files and/or form fields.
     * @param encoding     The encoding to use when decoding a form field
     *                     parameter.
     */
    private void processFileUploadItems( HttpServletRequest request,
                                         Map parameterMap,
                                         String encoding )
    {
        try
        {
            List items = fileUpload.parseRequest( request );
            for ( Iterator i = items.iterator(); i.hasNext(); )
            {
                FileItem item = (FileItem) i.next();

                if ( item.isFormField() )
                {
                    addParameter( parameterMap,
                                  item.getFieldName(),
                                  getFormField( item, encoding ) );
                }
                else
                {
                    addFileItem( parameterMap, item.getFieldName(), item );
                }
            }
        }
        catch ( FileUploadException e )
        {
            getLogger().error( "FileUpload failed", e );
        }
    }

    /**
     * Process the specified HTTP request for name/value pairs encoded within
     * the query info.  Each parsed pair is stored in the supplied parameter
     * map.
     *
     * @param request      The HTTP request to be parsed.
     * @param parameterMap The parameter map which is populated with the
     *                     parsed name/value pairs.
     */
    private void processQueryInfo( HttpServletRequest request, Map parameterMap )
    {
        for ( Enumeration e = request.getParameterNames(); e.hasMoreElements(); )
        {
            String name = (String) e.nextElement();
            parameterMap.put( name, request.getParameterValues( name ) );
        }
    }

    /**
     * Add a <tt>FileItem</tt> to the specified parameter map ensuring that
     * the value is stored as an array of <tt>FileItem</tt>s even if there
     * is only a single parameter.
     *
     * @param parameterMap The map which the parameter should be added.
     * @param name         The name of the parameter.
     * @param item         The file item associated with the parameter.
     */
    protected void addFileItem( Map parameterMap, String name, FileItem item )
    {
        FileItem[] items = (FileItem[]) parameterMap.get( name );
        if ( items == null )
        {
            items = new FileItem[1];
            items[0] = item;
            parameterMap.put( name, items );
        }
        else
        {
            FileItem[] newItems = new FileItem[items.length + 1];
            System.arraycopy( items, 0, newItems, 0, items.length );
            newItems[items.length] = item;
            parameterMap.put( name, newItems );
        }
    }

    /**
     * Add a parameter to the specified parameter map ensuring that the value
     * is stored as an array of <tt>String</tt>s even if there is only a
     * single parameter.
     *
     * @param parameterMap The map which the parameter should be added.
     * @param name         The name of the parameter.
     * @param value        The value of the parameter.
     */
    protected void addParameter( Map parameterMap, String name, String value )
    {
        String[] values = (String[]) parameterMap.get( name );
        if ( values == null )
        {
            values = new String[1];
            values[0] = value;
            parameterMap.put( name, values );
        }
        else
        {
            String[] newItems = new String[values.length + 1];
            System.arraycopy( values, 0, newItems, 0, values.length );
            newItems[values.length] = value;
            parameterMap.put( name, newItems );
        }
    }

    /**
     * Get the value of a form field that is part of a <tt>FileItem</tt> using
     * the specified encoding.
     *
     * @param item     The file item which contains a form value instead of a file upload.
     * @param encoding The encoding to use when getting the form value.
     * @return String The form value encoded in the specified encoding.
     */
    private String getFormField( FileItem item, String encoding )
    {
        String value = null;
        try
        {
            value = item.getString( encoding );
        }
        catch ( UnsupportedEncodingException e )
        {
            value = item.getString();
            getLogger().error( "Unsupported encoding, used default" );
        }
        return value;
    }

}
