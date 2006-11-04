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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Implementation of a password keystore that uses a Java KeyStore.
 *
 * @author Brett Porter
 * @version $Id$
 * @plexus.component
 */
public class JksPasswordStore
    implements PasswordStore, Initializable
{
    private KeyStore keystore;

    private File keystoreLocation;

    private String masterPassword;

    public static final String KEYSTORE_TYPE = "JCEKS";

    public static final String ALGORITHM = "raw";

    private long lastModified = 0L;

    public JksPasswordStore()
    {
    }

    JksPasswordStore( File keystoreLocation, String masterPassword )
    {
        this.keystoreLocation = keystoreLocation;
        this.masterPassword = masterPassword;
    }

    public String getPassword( String id )
        throws UnsupportedOperationException, PasswordStoreException
    {
        try
        {
            synchronized ( keystore )
            {
                reloadKeyStore();
                if ( !keystore.isKeyEntry( id ) )
                {
                    return null;
                }

                Key key = keystore.getKey( id, masterPassword.toCharArray() );

                return new String( key.getEncoded() );
            }
        }
        catch ( KeyStoreException e )
        {
            throw new PasswordStoreException( "Error retrieving password", e );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new PasswordStoreException( "Error retrieving password", e );
        }
        catch ( UnrecoverableKeyException e )
        {
            throw new PasswordStoreException( "Error retrieving password", e );
        }
    }

    public boolean checkPassword( String id, String attempt )
        throws PasswordStoreException
    {
        return attempt.equals( getPassword( id ) );
    }

    public void setPassword( String id, String oldPassword, String newPassword )
        throws PasswordStoreException
    {
        if ( !checkPassword( id, oldPassword ) )
        {
            throw new PasswordStoreException( "The password specified was not correct for id " + id );
        }

        synchronized ( keystore )
        {
            removePassword( id );
            setPassword( id, newPassword );
        }
    }

    public void setPassword( String id, String newPassword )
        throws PasswordStoreException
    {
        try
        {
            synchronized ( keystore )
            {
                reloadKeyStore();
                if ( keystore.isKeyEntry( id ) )
                {
                    throw new PasswordStoreException( "Password under key " + id + " already exists in the store" );
                }

                Key key = new SecretKeySpec( newPassword.getBytes(), ALGORITHM );
                keystore.setKeyEntry( id, key, masterPassword.toCharArray(), null );
                saveKeyStore();
            }
        }
        catch ( KeyStoreException e )
        {
            throw new PasswordStoreException( "Error setting password", e );
        }
    }

    public void removePassword( String id )
        throws PasswordStoreException
    {
        try
        {
            synchronized ( keystore )
            {
                reloadKeyStore();
                if ( !keystore.isKeyEntry( id ) )
                {
                    throw new PasswordStoreException( "Password under key " + id + " not found in the store" );
                }

                keystore.deleteEntry( id );
                saveKeyStore();
            }
        }
        catch ( KeyStoreException e )
        {
            throw new PasswordStoreException( "Error deleting password", e );
        }
    }

    private void saveKeyStore()
        throws PasswordStoreException
    {
        if ( keystoreLocation != null )
        {
            try
            {
                synchronized ( keystore )
                {
                    FileOutputStream stream = new FileOutputStream( keystoreLocation );
                    try
                    {
                        keystore.store( stream, masterPassword.toCharArray() );
                    }
                    finally
                    {
                        stream.close();
                    }
                    this.lastModified = keystoreLocation.lastModified();
                }
            }
            catch ( KeyStoreException e )
            {
                throw new PasswordStoreException( "Unable to write store", e );
            }
            catch ( IOException e )
            {
                throw new PasswordStoreException( "Unable to write store", e );
            }
            catch ( NoSuchAlgorithmException e )
            {
                throw new PasswordStoreException( "Unable to write store", e );
            }
            catch ( CertificateException e )
            {
                throw new PasswordStoreException( "Unable to write store", e );
            }
        }
    }

    public void unlock( String masterPassword )
    {
        // TODO: maybe this is preferred to initializable?
    }

    public void unlock()
    {
        // TOOD: implement?
    }

    public void lock()
    {
        // TOOD: implement?
    }

    public void initialize()
        throws InitializationException
    {
        try
        {
            keystore = KeyStore.getInstance( KEYSTORE_TYPE );

            if ( masterPassword == null )
            {
                masterPassword = "";
            }

            if ( !reloadKeyStore() )
            {
                keystore.load( null, masterPassword.toCharArray() );

                saveKeyStore();
            }
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Problem initializing keystore: ", e );
        }
    }

    private boolean reloadKeyStore()
        throws PasswordStoreException
    {
        boolean loaded = false;

        if ( keystoreLocation != null )
        {
            try
            {
                loaded = readKeyStore( masterPassword.toCharArray() );
            }
            catch ( IOException e )
            {
                throw new PasswordStoreException( "Unable to read back store ", e );
            }
            catch ( NoSuchAlgorithmException e )
            {
                throw new PasswordStoreException( "Unable to read back store ", e );
            }
            catch ( CertificateException e )
            {
                throw new PasswordStoreException( "Unable to read back store ", e );
            }
        }

        return loaded;
    }

    private boolean readKeyStore( char[] password )
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
        boolean result = false;

        if ( keystoreLocation.exists() )
        {
            synchronized ( keystore )
            {
                long lastModified = keystoreLocation.lastModified();
                if ( this.lastModified < lastModified )
                {
                    FileInputStream stream = new FileInputStream( keystoreLocation );
                    try
                    {
                        keystore.load( stream, password );
                        this.lastModified = lastModified;
                        result = true;
                    }
                    finally
                    {
                        stream.close();
                    }
                }
            }
        }
        return result;
    }
}
