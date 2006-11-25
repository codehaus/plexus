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
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataManagementTest
    extends PlexusTestCase
{
    private DataManagementTool dataManagementTool;

    private File targetDirectory;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        UserConfigurableJdoFactory jdoFactory = (UserConfigurableJdoFactory) lookup( JdoFactory.ROLE, "users" );

        File database = getTestFile( "target/database" );
        FileUtils.deleteDirectory( database );

        jdoFactory.setUrl(
            System.getProperty( "jdo.test.url", "jdbc:derby:" + database.getAbsolutePath() + ";create=true" ) );

        dataManagementTool = (DataManagementTool) lookup( DataManagementTool.ROLE, "jdo" );

        targetDirectory = createBackupDirectory();
    }

    public void testBackupRbac()
        throws Exception
    {
        RBACManager manager = (RBACManager) lookup( RBACManager.ROLE, "jdo" );

        RBACDefaults defaults = new RBACDefaults( manager );

        defaults.createDefaults();

        UserAssignment assignment = manager.createUserAssignment( "bob" );
        assignment.addRoleName( "Developer" );
        manager.saveUserAssignment( assignment );

        assignment = manager.createUserAssignment( "betty" );
        assignment.addRoleName( "System Administrator" );
        manager.saveUserAssignment( assignment );

        dataManagementTool.backupRBACDatabase( manager, targetDirectory );

        File backupFile = new File( targetDirectory, "rbac.xml" );

        assertTrue( "Check database exists", backupFile.exists() );

        StringWriter sw = new StringWriter();

        IOUtil.copy( getClass().getResourceAsStream( "/expected-rbac.xml" ), sw );

        assertEquals( "Check database content", sw.toString(), FileUtils.fileRead( backupFile ) );
    }

    public void testBackupUsers()
        throws Exception
    {
        UserManager manager = (UserManager) lookup( UserManager.ROLE, "jdo" );

        User user = manager.createUser( "smcqueen", "Steve McQueen", "the cooler king" );
        user.setPassword( "abc123" );
        manager.addUser( user );

        user = manager.createUser( "bob", "Sideshow Bob", "bob_862@hotmail.com" );
        user.setPassword( "bobby862" );
        manager.addUser( user );

        user = manager.createUser( "betty", "Betty", "betty@aol.com" );
        user.setPassword( "rover2" );
        manager.addUser( user );

        dataManagementTool.backupUserDatabase( manager, targetDirectory );

        File backupFile = new File( targetDirectory, "users.xml" );

        assertTrue( "Check database exists", backupFile.exists() );

        StringWriter sw = new StringWriter();

        IOUtil.copy( getClass().getResourceAsStream( "/expected-users.xml" ), sw );

        String actual = FileUtils.fileRead( backupFile );
        String expected = sw.toString();
        assertEquals( "Check database content", removeTimestampVariance( expected ),
                      removeTimestampVariance( actual ) );
    }

    private static String removeTimestampVariance( String content )
    {
        return content.replaceAll( "<lastPasswordChange>.*</lastPasswordChange>",
                                   "<lastPasswordChange></lastPasswordChange>" );
    }

    private static File createBackupDirectory()
    {
        String timestamp = new SimpleDateFormat( "yyyyMMdd.HHmmss", Locale.US ).format( new Date() );

        File targetDirectory = getTestFile( "target/backups/" + timestamp );
        targetDirectory.mkdirs();

        return targetDirectory;
    }
}
