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

import java.util.Map;

import org.apache.commons.fileupload.FileItem;

/**
 * Provides numerous convienence methods which operate on a map of
 * parameters contained within this object.  The parameters represent
 * the parameters associated with an HTTP request.  These parameters
 * not only include simple form items, but also contain any uploaded
 * files associated with the request.
 *
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Revision$
 * @todo Add the file upload convienence methods.
 */
public class RequestParameters extends BaseParameterConverter
{
    /**
     * Constructor that takes a map of parameters and uses the default
     * character encoding, "US-ASCII", when converting to byte arrays.
     * The map of parameters must contain name/value pairs.  Each value
     * <b>must</b> be an array of <tt>String</tt>s or <tt>FileItem</tt>s
     * even if there is only a single value for the parameter.
     *
     * @param parameters A map of name/value pairs representing parameters.
     * @throws NullPointerException If the parameters map is null.
     */
    public RequestParameters( Map parameters )
    {
        super( parameters );
    }

    /**
     * Constructor that takes a map of parameters and a character encoding
     * that is used when converting to byte arrays.  The map of parameters
     * must contain name/value pairs.  Each value <b>must</b> be an array
     * of <tt>String</tt>s or <tt>FileItem</tt>s even if there is only a
     * single value for the parameter.
     *
     * @param parameters        A map of name/value pairs representing parameters.
     * @param characterEncoding The character encoding used to convert byte arrays.
     * @throws NullPointerException If the parameters map is null, or if
     *                              specified character encoding is null.
     */
    public RequestParameters( Map parameters, String characterEncoding )
    {
        super( parameters, characterEncoding );
    }

    /**
     * Get the <tt>FileItem</tt>, which represents a file that was uploaded as
     * part of an HTTP request, associated with the specified parameter name.
     *
     * @param name The name of the parameter containing the file uploaded.
     * @return FileItem A file item representing the uploaded file.  If the
     *         <tt>name</tt> does not exist, or if the parameter does not represent
     *         an uploaded file, <tt>null</tt> is returned.
     */
    public FileItem getFileItem( String name )
    {
        try
        {
            FileItem value = null;
            Object object = parameters.get( name );
            if ( object != null )
            {
                value = ( (FileItem[]) object )[0];
            }
            return value;
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /**
     * Get the <tt>FileItem[]</tt>s, which represents one or more files that
     * were uploaded as part of an HTTP request, associated with the specified
     * parameter name.
     *
     * @param name The name of the parameter containing the file(s) uploaded.
     * @return FileItem[] An array of file items containing the files uploaded.
     *         If the <tt>name</tt> does not exist, or if the parameter does not
     *         represent an uploaded file, <tt>null</tt> is returned.
     */
    public FileItem[] getFileItems( String name )
    {
        try
        {
            return (FileItem[]) parameters.get( name );
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }
}
