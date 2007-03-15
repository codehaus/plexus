package org.codehaus.plexus.appserver.manager.web.action.applications;

import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.appserver.manager.web.action.AbstractManagerAction;

/*
 * Copyright 2007 The Codehaus Foundation.
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
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 12 mars 07
 * @version $Id$
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="manageApplication"
 */
public class ManageApplication
    extends AbstractManagerAction
{
    private String name;

    public String delete()
        throws Exception
    {
        try
        {
            this.getLogger().info( "deleting application " + this.getName() );
            this.getApplicationServer().deleteApplication( this.getName() );
        }
        catch ( ApplicationServerException e )
        {
            getLogger().error( e.getMessage(), e );
            this.addActionError( getText( "page.deleteApplication.delete.error", new String[] {
                this.getName(),
                e.getMessage() } ) );
            return INPUT;
        }
        return SUCCESS;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
}
