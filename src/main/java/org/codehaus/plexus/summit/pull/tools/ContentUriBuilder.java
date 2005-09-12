package org.codehaus.plexus.summit.pull.tools;

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

import java.lang.reflect.Method;

import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.util.UriBuilder;

/**
 * Utility class to allow the easy inclusion of
 * images in templates: &lt;img src="$content.getURI("image.jpg")">
 *
 * @author <a href="mailto:criley@ekmail.com">Cameron Riley</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class ContentUriBuilder
    extends UriBuilder
    implements RequestTool
{
    /**
     * stores the context path for servlet 2.1+ compliant containers
     */
    private String contextPath;

    /**
     * Default constructor
     */
    public ContentUriBuilder()
    {
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Initialize this object using the given RunData object
     *
     * @param data a RunData instance
     */
    public void setRunData( RunData data )
    {
        init( data );
        try
        {
            Class RunDataClass = RunData.class;
            Method meth = RunDataClass.getDeclaredMethod( "getContextPath", null );
            contextPath = (String) meth.invoke( data, null );
        }
        catch ( Exception e )
        {
            /*
             * Ignore a NoSuchMethodException because it means we are
             * using Servlet API 2.0.  Make sure scriptName is not
             * null.
             */
            contextPath = "";
        }
    }

    /**
     * Returns a URI pointing to the given content (where content is a
     * path relative to the webapp root.
     *
     * @param pathToContent a path relative to the webapp root
     */
    public String getURI( String pathToContent )
    {
        StringBuffer sb = new StringBuffer();
        sb.append( getServerScheme() ); //http
        sb.append( "://" );
        sb.append( getServerName() ); //www.foo.com
        sb.append( ':' );
        sb.append( getServerPort() ); //port webserver running on (8080 for TDK)
        //the context for tomcat adds a / so no need to add another
        sb.append( contextPath ); //the tomcat context
        sb.append( '/' );
        sb.append( pathToContent );
        return ( sb.toString() );
    }
}
