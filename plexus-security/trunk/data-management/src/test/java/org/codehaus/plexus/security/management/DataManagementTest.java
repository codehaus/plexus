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
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

        File database = getTestFile( "target/database/" + getName() );
        FileUtils.deleteDirectory( database );
        assertFalse( database.exists() );
        database.getParentFile().mkdirs();

        jdoFactory.setUrl(
            System.getProperty( "jdo.test.url", "jdbc:derby:" + database.getAbsolutePath() + ";create=true" ) );

        dataManagementTool = (DataManagementTool) lookup( DataManagementTool.ROLE, "jdo" );

        targetDirectory = createBackupDirectory();
    }

    public void testEraseRbac()
        throws Exception
    {
        RBACManager manager = (RBACManager) lookup( RBACManager.ROLE, "jdo" );

        createRbacDatabase( manager );

        dataManagementTool.eraseRBACDatabase( manager );

        assertEmpty( manager );
    }

    public void testEraseUsers()
        throws Exception
    {
        UserManager manager = (UserManager) lookup( UserManager.ROLE, "jdo" );

        createUserDatabase( manager );

        dataManagementTool.eraseUsersDatabase( manager );

        assertEmpty( manager );
    }

    public void testEraseKeys()
        throws Exception
    {
        KeyManager manager = (KeyManager) lookup( KeyManager.ROLE, "jdo" );

        createKeyDatabase( manager );

        dataManagementTool.eraseKeysDatabase( manager );

        assertEmpty( manager );
    }

    public void testBackupRbac()
        throws Exception
    {
        RBACManager manager = (RBACManager) lookup( RBACManager.ROLE, "jdo" );

        createRbacDatabase( manager );

        dataManagementTool.backupRBACDatabase( manager, targetDirectory );

        File backupFile = new File( targetDirectory, "rbac.xml" );

        assertTrue( "Check database exists", backupFile.exists() );

        StringWriter sw = new StringWriter();

        IOUtil.copy( getClass().getResourceAsStream( "/expected-rbac.xml" ), sw );

        assertEquals( "Check database content", sw.toString(), FileUtils.fileRead( backupFile ) );
    }

    private void createRbacDatabase( RBACManager manager )
        throws RbacManagerException
    {
        RBACDefaults defaults = new RBACDefaults( manager );

        defaults.createDefaults();

        UserAssignment assignment = manager.createUserAssignment( "bob" );
        assignment.addRoleName( "Developer" );
        manager.saveUserAssignment( assignment );

        assignment = manager.createUserAssignment( "betty" );
        assignment.addRoleName( "System Administrator" );
        manager.saveUserAssignment( assignment );
    }

    public void testBackupUsers()
        throws Exception
    {
        UserManager manager = (UserManager) lookup( UserManager.ROLE, "jdo" );

        createUserDatabase( manager );

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

    private void createUserDatabase( UserManager manager )
    {
        User user = manager.createUser( "smcqueen", "Steve McQueen", "the cooler king" );
        user.setPassword( "abc123" );
        manager.addUser( user );

        user = manager.createUser( "bob", "Sideshow Bob", "bob_862@hotmail.com" );
        user.setPassword( "bobby862" );
        manager.addUser( user );

        user = manager.createUser( "betty", "Betty", "betty@aol.com" );
        user.setPassword( "rover2" );
        manager.addUser( user );
    }

    public void testBackupKeys()
        throws Exception
    {
        KeyManager manager = (KeyManager) lookup( KeyManager.ROLE, "jdo" );

        createKeyDatabase( manager );

        dataManagementTool.backupKeyDatabase( manager, targetDirectory );

        File backupFile = new File( targetDirectory, "keys.xml" );

        assertTrue( "Check database exists", backupFile.exists() );

        StringWriter sw = new StringWriter();

        IOUtil.copy( getClass().getResourceAsStream( "/expected-keys.xml" ), sw );

        String actual = FileUtils.fileRead( backupFile );
        String expected = sw.toString();
        assertEquals( "Check database content", removeKeyAndTimestampVariance( expected ),
                      removeKeyAndTimestampVariance( actual ) );
    }

    private static void createKeyDatabase( KeyManager manager )
        throws KeyManagerException
    {
        manager.createKey( "bob", "Testing", 15 );
        manager.createKey( "betty", "Something", 25 );
        manager.createKey( "fred", "Else", 30 );
    }

    public void testRestoreRbac()
        throws Exception
    {
        RBACManager manager = (RBACManager) lookup( RBACManager.ROLE, "jdo" );

        assertEmpty( manager );

        File backupFile = new File( targetDirectory, "rbac.xml" );

        IOUtil.copy( getClass().getResourceAsStream( "/expected-rbac.xml" ), new FileWriter( backupFile ) );

        dataManagementTool.restoreRBACDatabase( manager, targetDirectory );

        List roles = manager.getAllRoles();
        List assignments = manager.getAllUserAssignments();
        assertEquals( 4, roles.size() );
        assertEquals( 2, assignments.size() );
        assertEquals( 6, manager.getAllOperations().size() );
        assertEquals( 1, manager.getAllResources().size() );
        assertEquals( 6, manager.getAllPermissions().size() );

        Role role = (Role) roles.get( 0 );
        assertEquals( "User Administrator", role.getName() );
        assertTrue( role.isAssignable() );
        assertEquals( 2, role.getPermissions().size() );
        assertPermission( (Permission) role.getPermissions().get( 0 ), "Edit All Users", "edit-all-users", "*" );
        assertPermission( (Permission) role.getPermissions().get( 1 ), "Remove Roles", "remove-roles", "*" );

        role = (Role) roles.get( 1 );
        assertEquals( "System Administrator", role.getName() );
        assertTrue( role.isAssignable() );
        assertEquals( 1, role.getChildRoleNames().size() );
        assertEquals( "User Administrator", role.getChildRoleNames().get( 0 ) );
        assertEquals( 4, role.getPermissions().size() );
        assertPermission( (Permission) role.getPermissions().get( 0 ), "Edit Configuration", "edit-configuration",
                          "*" );
        assertPermission( (Permission) role.getPermissions().get( 1 ), "Run Indexer", "run-indexer", "*" );
        assertPermission( (Permission) role.getPermissions().get( 2 ), "Add Repository", "add-repository", "*" );
        assertPermission( (Permission) role.getPermissions().get( 3 ), "Regenerate Index", "regenerate-index", "*" );

        role = (Role) roles.get( 2 );
        assertEquals( "Trusted Developer", role.getName() );
        assertTrue( role.isAssignable() );
        assertEquals( 1, role.getChildRoleNames().size() );
        assertEquals( "System Administrator", role.getChildRoleNames().get( 0 ) );
        assertEquals( 1, role.getPermissions().size() );
        assertPermission( (Permission) role.getPermissions().get( 0 ), "Run Indexer", "run-indexer", "*" );

        role = (Role) roles.get( 3 );
        assertEquals( "Developer", role.getName() );
        assertTrue( role.isAssignable() );
        assertEquals( 1, role.getChildRoleNames().size() );
        assertEquals( "Trusted Developer", role.getChildRoleNames().get( 0 ) );
        assertEquals( 1, role.getPermissions().size() );
        assertPermission( (Permission) role.getPermissions().get( 0 ), "Run Indexer", "run-indexer", "*" );

        UserAssignment assignment = (UserAssignment) assignments.get( 0 );
        assertEquals( "bob", assignment.getPrincipal() );
        assertEquals( 1, assignment.getRoleNames().size() );
        assertEquals( "Developer", assignment.getRoleNames().get( 0 ) );

        assignment = (UserAssignment) assignments.get( 1 );
        assertEquals( "betty", assignment.getPrincipal() );
        assertEquals( 1, assignment.getRoleNames().size() );
        assertEquals( "System Administrator", assignment.getRoleNames().get( 0 ) );
    }

    private void assertEmpty( RBACManager manager )
        throws RbacManagerException
    {
        assertEquals( 0, manager.getAllRoles().size() );
        assertEquals( 0, manager.getAllUserAssignments().size() );
        assertEquals( 0, manager.getAllOperations().size() );
        assertEquals( 0, manager.getAllResources().size() );
        assertEquals( 0, manager.getAllPermissions().size() );
    }

    public void testRestoreUsers()
        throws Exception
    {
        UserManager manager = (UserManager) lookup( UserManager.ROLE, "jdo" );

        assertEmpty( manager );

        File backupFile = new File( targetDirectory, "users.xml" );

        IOUtil.copy( getClass().getResourceAsStream( "/expected-users.xml" ), new FileWriter( backupFile ) );

        dataManagementTool.restoreUsersDatabase( manager, targetDirectory );

        List users = manager.getUsers();
        assertEquals( 3, users.size() );

        User user = (User) users.get( 0 );
        assertEquals( "smcqueen", user.getUsername() );
        assertEquals( "bKE9UspwyIPg8LsQHkJaiehiTeUdstI5JZOvaoQRgJA=", user.getEncodedPassword() );
        assertEquals( "Steve McQueen", user.getFullName() );
        assertEquals( "the cooler king", user.getEmail() );
        assertEquals( 1164424661686L, user.getLastPasswordChange().getTime() );
        assertEquals( Arrays.asList( new String[]{"bKE9UspwyIPg8LsQHkJaiehiTeUdstI5JZOvaoQRgJA="} ),
                      user.getPreviousEncodedPasswords() );

        user = (User) users.get( 1 );
        assertEquals( "bob", user.getUsername() );
        assertEquals( "A0MR+q0lm554bD6Uft60ztlYZ8N1pEqXhKNM9H7SlS8=", user.getEncodedPassword() );
        assertEquals( "Sideshow Bob", user.getFullName() );
        assertEquals( "bob_862@hotmail.com", user.getEmail() );
        assertEquals( 1164424669526L, user.getLastPasswordChange().getTime() );
        assertEquals( Arrays.asList( new String[]{"A0MR+q0lm554bD6Uft60ztlYZ8N1pEqXhKNM9H7SlS8="} ),
                      user.getPreviousEncodedPasswords() );

        user = (User) users.get( 2 );
        assertEquals( "betty", user.getUsername() );
        assertEquals( "L/mA/suWallwvYzw4wyRYkn5y8zWxAITuv4sLhJLN1E=", user.getEncodedPassword() );
        assertEquals( "Betty", user.getFullName() );
        assertEquals( "betty@aol.com", user.getEmail() );
        assertEquals( 1164424669536L, user.getLastPasswordChange().getTime() );
        assertEquals( Arrays.asList( new String[]{"L/mA/suWallwvYzw4wyRYkn5y8zWxAITuv4sLhJLN1E="} ),
                      user.getPreviousEncodedPasswords() );
    }

    private void assertEmpty( UserManager manager )
    {
        List users = manager.getUsers();
        assertEquals( 0, users.size() );
    }

    public void testRestoreKeys()
        throws Exception
    {
        KeyManager manager = (KeyManager) lookup( KeyManager.ROLE, "jdo" );

        assertEmpty( manager );

        File backupFile = new File( targetDirectory, "keys.xml" );

        IOUtil.copy( getClass().getResourceAsStream( "/expected-keys.xml" ), new FileWriter( backupFile ) );

        dataManagementTool.restoreKeysDatabase( manager, targetDirectory );

        List keys = manager.getAllKeys();
        assertEquals( 3, keys.size() );

        AuthenticationKey key = (AuthenticationKey) keys.get( 0 );
        assertEquals( "248df0fec5d54e3eb11339f5e81d8bd7", key.getKey() );
        assertEquals( "bob", key.getForPrincipal() );
        assertEquals( "Testing", key.getPurpose() );
        assertEquals( 1164426311921L, key.getDateCreated().getTime() );
        assertEquals( 1164427211921L, key.getDateExpires().getTime() );

        key = (AuthenticationKey) keys.get( 1 );
        assertEquals( "a98dddc2ae614a7c82f8afd3ba6e39fb", key.getKey() );
        assertEquals( "betty", key.getForPrincipal() );
        assertEquals( "Something", key.getPurpose() );
        assertEquals( 1164426315657L, key.getDateCreated().getTime() );
        assertEquals( 1164427815657L, key.getDateExpires().getTime() );

        key = (AuthenticationKey) keys.get( 2 );
        assertEquals( "1428d2ca3a0246f0a1d979504e351388", key.getKey() );
        assertEquals( "fred", key.getForPrincipal() );
        assertEquals( "Else", key.getPurpose() );
        assertEquals( 1164426315664L, key.getDateCreated().getTime() );
        assertEquals( 1164428115664L, key.getDateExpires().getTime() );
    }

    private void assertEmpty( KeyManager manager )
    {
        assertEquals( 0, manager.getAllKeys().size() );
    }

    private String removeKeyAndTimestampVariance( String content )
    {
        return removeTagContent( removeTagContent( removeTagContent( content, "dateCreated" ), "dateExpires" ), "key" );
    }

    private static String removeTimestampVariance( String content )
    {
        return removeTagContent( content, "lastPasswordChange" );
    }

    private static String removeTagContent( String content, String field )
    {
        return content.replaceAll( "<" + field + ">.*</" + field + ">", "<" + field + "></" + field + ">" );
    }

    private static void assertPermission( Permission permission, String name, String operation, String resource )
    {
        assertEquals( name, permission.getName() );
        assertEquals( operation, permission.getOperation().getName() );
        assertEquals( resource, permission.getResource().getIdentifier() );
    }

    private static File createBackupDirectory()
    {
        String timestamp = new SimpleDateFormat( "yyyyMMdd.HHmmss", Locale.US ).format( new Date() );

        File targetDirectory = getTestFile( "target/backups/" + timestamp );
        targetDirectory.mkdirs();

        return targetDirectory;
    }
}
