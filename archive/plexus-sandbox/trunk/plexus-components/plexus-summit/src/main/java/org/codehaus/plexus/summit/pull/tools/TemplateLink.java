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

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.util.UriBuilder;

/**
 * A customized version of the UriBuilder to be used in Templates.
 * This is automatically inserted into the template context by the
 * appropriate templating service so page authors can create links
 * in templates.  Here's an example of its Velocity/WebMacro use:
 * <p/>
 * <p><code>
 * $link.setPage("index.wm").addPathInfo("hello","world")
 * This would return: http://foo.com/Turbine/template/index.wm/hello/world
 * </code>
 *
 * @plexus.component
 *  role="linktool"
 *  instantiation-strategy="per-lookup"
 *
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TemplateLink
    extends UriBuilder
    implements RequestTool
{
    /**
     * cache of the template name for getPage()
     */
    private String target = null;

    /**
     * Default constructor
     * <p/>
     * The init method must be called before use.
     */
    public TemplateLink()
    {
    }

    /**
     * This will turn off the execution of res.encodeURL(). This is useful
     * for cases where you don't want to see the session information
     */
    public TemplateLink setEncodeURLOff()
    {
        setEncodeUrl( false );
        return this;
    }

    /**
     * Sets the template variable used by the Template Service.
     *
     * @param t A String with the template name.
     * @return A TemplateLink.
     */
    public TemplateLink setPage( String t )
    {
        target = t;
        addPathInfo( SummitConstants.TARGET, t );
        return this;
    }

    /**
     * Gets the template variable used by the Template Service.
     * It is only available after setPage() has been called.
     *
     * @return The template name.
     */
    public String getPage()
    {
        return target;
    }

    /**
     * Set to false to skip the scheme, host, and port sections of the url.
     * The default is to return absolute url's from the toString method.
     *
     * @param b a <code>boolean</code> value
     * @return a <code>TemplateLink</code> value
     */
    public TemplateLink setAbsolute( boolean b )
    {
        setRelative( !b );
        return this;
    }

    /**
     * Returns the URI. After rendering the URI, it clears the
     * pathInfo and QueryString portions of the UriBuilder.
     *
     * @return A String with the URI in the form
     *         http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String toString()
    {
        String output = super.toString();

        refresh();

        return output;
    }

    /**
     * Returns the URI leaving the source intact. Wraps directly to the
     * <code>UriBuilder.toString</code> method of the superclass
     * (avoiding the local toString implementation).
     *
     * @return A String with the URI in the form
     *         http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String getURI()
    {
        return super.toString();
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.pull.ApplicationTool#setData(java.lang.Object)
     */
    public void setRunData( RunData data )
    {
        init( data );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.summit.util.UriBuilder#refresh()
     */
    public void refresh()
    {
        // This was added to allow multilple $link variables in one
        // template.
        removePathInfo();
        removeQueryData();
        setEncodeUrl( true );
        setAbsolute( true );
    }
}
