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

import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.util.UriBuilder;

/**
 * A customized version of the RelativeDynamicUriBuilder to be used in Templates.
 * Here's an example of its Velocity/WebMacro use:
 * <p/>
 * <p><code>
 * $link.setPage("index.wm").addPathInfo("hello","world")
 * This would return: /myapp/servlet/myapp/target/index.wm/hello/world
 * </code>
 *
 * @author <a href="jmcnally@collab.net">John D. McNally</a>
 * @author see the authors of TemplateLink
 * @version $Id$
 */
public class RelativeTemplateLink extends UriBuilder implements RequestTool
{
    /**
     * the pathinfo key stored in the UriBuilder
     */
    private static final String TEMPLATE_KEY = "template";
    /**
     * cache of the target name for getPage()
     */
    private String target = null;

    /**
     * Default constructor
     * <p/>
     * The init method must be called before use.
     */
    public RelativeTemplateLink()
    {
        setRelative( true );
    }

    /**
     * This will turn off the execution of res.encodeURL()
     * by making res == null. This is a hack for cases
     * where you don't want to see the session information
     */
    public RelativeTemplateLink setEncodeURLOff()
    {
        this.res = null;
        return this;
    }

    /**
     * Sets the target variable used by the Template Service.
     *
     * @param t A String with the target name.
     * @return A TemplateLink.
     */
    public RelativeTemplateLink setPage( String t )
    {
        target = t;
        addPathInfo( TEMPLATE_KEY, t );
        return this;
    }

    /**
     * Gets the target variable used by the Template Service.
     * It is only available after setPage() has been called.
     *
     * @return The target name.
     */
    public String getPage()
    {
        return target;
    }

    /**
     * Returns the URI. After rendering the URI, it clears the
     * pathInfo and QueryString portions of the UriBuilder.
     *
     * @return A String with the URI in the form
     *         http://foo.com/Turbine/target/index.wm/hello/world
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
     *         http://foo.com/Turbine/target/index.wm/hello/world
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
     * @see org.codehaus.plexus.summit.pull.ApplicationTool#refresh()
     */
    public void refresh()
    {
        // This was added to allow multilple $link variables in one
        // template.
        removePathInfo();
        removeQueryData();
    }
}
