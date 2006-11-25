package org.codehaus.plexus.security.management;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.authorization.rbac.jdo.RbacDatabase;
import org.codehaus.plexus.security.authorization.rbac.jdo.io.stax.RbacJdoModelStaxWriter;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.util.IOUtil;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * JDO implementation of the data management tool.
 *
 * @todo do we really need JDO specifics here?
 * @plexus.component role="org.codehaus.plexus.security.management.DataManagementTool" role-hint="jdo"
 */
public class JdoDataManagementTool
    implements DataManagementTool
{
    public void backupRBACDatabase( RBACManager rbacManager, File backupDirectory )
        throws RbacManagerException, IOException, XMLStreamException
    {
        RbacDatabase database = new RbacDatabase();
        database.setRoles( rbacManager.getAllRoles() );
        database.setUserAssignments( rbacManager.getAllUserAssignments() );

        RbacJdoModelStaxWriter writer = new RbacJdoModelStaxWriter();
        FileWriter fileWriter = new FileWriter( new File( backupDirectory, "rbac.xml" ) );
        try
        {
            writer.write( fileWriter, database );
        }
        finally
        {
            IOUtil.close( fileWriter );
        }
    }
}
