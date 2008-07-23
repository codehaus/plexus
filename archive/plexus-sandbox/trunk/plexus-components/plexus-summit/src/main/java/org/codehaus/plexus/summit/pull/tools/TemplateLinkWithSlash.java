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

/**
 * This class allows one to specify paths in the setPage method
 * using '/' slash as opposed to the ',' used in TemplateLink.
 * It is less efficient as the '/' are converted to ',' to avoid
 * problems parsing the pathinfo after conversion in a web server.
 * <p/>
 * It is recommended that projects standardize on using the ','
 * separator and use TemplateLink.  But this class is available for
 * those who do not mind the inefficiency.
 *
 * @author <a href="jmcnally@collab.net">John D. McNally</a>
 * @version $Id$
 */
public class TemplateLinkWithSlash
    extends TemplateLink
{
    /**
     * Default constructor
     * <p/>
     * The init method must be called before use.
     */
    public TemplateLinkWithSlash()
    {
    }

    /**
     * Sets the template variable used by the Template Service.
     * This method allows slashes '/' as the path separator.
     *
     * @param t A String with the template name.
     * @return A TemplateLink.
     */
    public TemplateLink setPage( String t )
    {
        super.setPage( t.replace( '/', ',' ) );
        return this;
    }
}


