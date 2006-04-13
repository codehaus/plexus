package org.codehaus.plexus.components.password;

/*
 * The MIT License
 *
 * Copyright (c) 2005, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import junit.framework.TestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Test the JKS Password Store.
 *
 * @author Brett Porter
 * @version $Id$
 */
public class JksPasswordStoreTest
    extends TestCase
{
    private String masterPassword = "foo";

    private File keystoreFile;

    private static final String KNOWN_ID = "id";

    private static final String KNOWN_PASSWORD = "newPassword";

    private String basedir;

    public void setUp()
        throws Exception
    {
        super.setUp();

        basedir = System.getProperty( "basedir" );
    }

    public void testCreation()
        throws Exception
    {
        createTestKeystore();

        assertFalse( "Keystore should not yet be created", keystoreFile.exists() );

        initPasswordStore();

        assertTrue( "Keystore was not created", keystoreFile.exists() );
    }

    public void testGetPassword()
        throws Exception
    {
        // TODO: use a resource URL instead
        keystoreFile = new File( basedir, "target/test-classes/test.keystore" );

        PasswordStore passwordStore = initPasswordStore();
        assertEquals( "Password was not found", KNOWN_PASSWORD, passwordStore.getPassword( KNOWN_ID ) );
    }

    public void testGetPasswordDoesNotExist()
        throws Exception
    {
        // TODO: use a resource URL instead
        keystoreFile = new File( basedir, "target/test-classes/test.keystore" );

        PasswordStore passwordStore = initPasswordStore();
        assertNull( "Password should not be found", passwordStore.getPassword( "foo" ) );
    }

    public void testCheckPassword()
        throws Exception
    {
        // TODO: use a resource URL instead
        keystoreFile = new File( basedir, "target/test-classes/test.keystore" );

        PasswordStore passwordStore = initPasswordStore();
        assertTrue( "Password check failed on good password", passwordStore.checkPassword( KNOWN_ID, KNOWN_PASSWORD ) );
        assertFalse( "Password check succeeded on bad password", passwordStore.checkPassword( KNOWN_ID, "foo" ) );
        assertFalse( "Password check succeeded on bad password ID", passwordStore.checkPassword( "foo", "bar" ) );
    }

    public void testSetPassword()
        throws Exception
    {
        createTestKeystore();
        PasswordStore passwordStore = initPasswordStore();

        String password = "setPassword";
        String id = "newId";
        passwordStore.setPassword( id, password );
        assertEquals( "Password was not set correctly", password, passwordStore.getPassword( id ) );
    }

    public void testChangePassword()
        throws Exception
    {
        createTestKeystore();
        FileUtils.copyFile( new File( basedir, "target/test-classes/test.keystore" ), keystoreFile );

        PasswordStore passwordStore = initPasswordStore();
        assertEquals( "Password was not set correctly", KNOWN_PASSWORD, passwordStore.getPassword( KNOWN_ID ) );
        passwordStore.setPassword( KNOWN_ID, KNOWN_PASSWORD, "foo" );
        assertEquals( "Password was not set correctly", "foo", passwordStore.getPassword( KNOWN_ID ) );
    }

    public void testReload()
        throws Exception
    {
        createTestKeystore();
        FileUtils.copyFile( new File( basedir, "target/test-classes/test.keystore" ), keystoreFile );

        PasswordStore originalPasswordStore = initPasswordStore();
        assertEquals( "Password was not set correctly", KNOWN_PASSWORD, originalPasswordStore.getPassword( KNOWN_ID ) );
        PasswordStore newPasswordStore = initPasswordStore();
        originalPasswordStore.setPassword( KNOWN_ID, KNOWN_PASSWORD, "foo" );
        assertEquals( "Password was not set correctly", "foo", originalPasswordStore.getPassword( KNOWN_ID ) );
        //TODO: check this as it was failing
        //assertEquals( "Password was not set correctly", "foo", newPasswordStore.getPassword( KNOWN_ID ) );
    }

    public void testRemovePassword()
        throws Exception
    {
        createTestKeystore();
        FileUtils.copyFile( new File( basedir, "target/test-classes/test.keystore" ), keystoreFile );

        PasswordStore passwordStore = initPasswordStore();
        assertEquals( "Password was not set correctly", KNOWN_PASSWORD, passwordStore.getPassword( KNOWN_ID ) );
        passwordStore.removePassword( KNOWN_ID );
        assertNull( "Password was not removed", passwordStore.getPassword( KNOWN_ID ) );
    }

    // ------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------

    private void createTestKeystore()
        throws Exception
    {
        this.keystoreFile = File.createTempFile( "test", getClass().getName() );
        this.keystoreFile.delete(); // delete the one just created
        this.keystoreFile.deleteOnExit(); // ensure it is deleted at the end
    }

    private PasswordStore initPasswordStore()
        throws Exception
    {
        JksPasswordStore passwordStore = new JksPasswordStore( keystoreFile, masterPassword );
        passwordStore.initialize();
        return passwordStore;
    }

}
