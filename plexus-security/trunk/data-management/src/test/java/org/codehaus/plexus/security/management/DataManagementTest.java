package org.codehaus.plexus.security.management;

/*
 * Copyright 2006
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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.security.authorization.store.test.utils.RBACDefaults;
import org.codehaus.plexus.security.common.jdo.UserConfigurableJdoFactory;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataManagementTest
    extends PlexusTestCase
{
    private RBACDefaults defaults;

    private RBACManager manager;

    private DataManagementTool dataManagementTool;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        UserConfigurableJdoFactory jdoFactory = (UserConfigurableJdoFactory) lookup( JdoFactory.ROLE, "users" );

        File database = getTestFile( "target/database" );
        FileUtils.deleteDirectory( database );

        jdoFactory.setUrl(
            System.getProperty( "jdo.test.url", "jdbc:derby:" + database.getAbsolutePath() + ";create=true" ) );

        manager = (RBACManager) lookup( RBACManager.ROLE, "jdo" );

        defaults = new RBACDefaults( manager );

        dataManagementTool = (DataManagementTool) lookup( DataManagementTool.ROLE, "jdo" );
    }

    public void testBackup()
        throws RbacManagerException, IOException, XMLStreamException
    {
        defaults.createDefaults();

        UserAssignment assignment = manager.createUserAssignment( "bob" );
        assignment.addRoleName( "Developer" );
        manager.saveUserAssignment( assignment );

        assignment = manager.createUserAssignment( "betty" );
        assignment.addRoleName( "System Administrator" );
        manager.saveUserAssignment( assignment );

        String timestamp = new SimpleDateFormat( "yyyyMMdd.HHmmss", Locale.US ).format( new Date() );

        File targetDirectory = getTestFile( "target/backups/" + timestamp );
        targetDirectory.mkdirs();

        dataManagementTool.backupRBACDatabase( manager, targetDirectory );

        File backupFile = new File( targetDirectory, "rbac.xml" );

        assertTrue( "Check database exists", backupFile.exists() );

        StringWriter sw = new StringWriter();

        IOUtil.copy( getClass().getResourceAsStream( "/expected-rbac.xml" ), sw );

        assertEquals( "Check database content", sw.toString(), FileUtils.fileRead( backupFile ) );
    }
}
