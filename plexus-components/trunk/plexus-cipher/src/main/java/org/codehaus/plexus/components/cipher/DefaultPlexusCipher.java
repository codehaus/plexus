package org.codehaus.plexus.components.cipher;

/*
 * Copyright 2001-2007 Codehaus Foundation.
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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.plaf.SliderUI;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

import sun.misc.BASE64Encoder;

/**
 * @plexus.component
 * 
 * @author <a href="oleg@codehaus.org">Oleg Gusakov</a>
 * 
 */
public class DefaultPlexusCipher
    extends AbstractLogEnabled
    implements PlexusCipher, Initializable
{
	private static final String SECURITY_PROVIDER = "BC";
	private static final int SALT_SIZE = 8;
	
	/**
     * Encryption algorithm to use by this instance. Needs protected scope for tests
     * 
     * @plexus.configuration default-value="PBEWithSHAAnd128BitRC4"
     */
    protected String algorithm = "PBEWithSHAAnd128BitRC4";

    /**
     * Number of iterations when generationg the key
     * 
     * @plexus.configuration default-value="23"
     */
    protected int iterationCount = 23;

//    /**
//     * Salt to init this cypher
//     * 
//     * @plexus.configuration default-value="maven.rules.in.this"
//     */
//    protected String salt = "maven.rules.in.this";
//    protected byte [] saltData = new byte[8];
    //---------------------------------------------------------------
    public void initialize()
	throws InitializationException
	{
    	Security.addProvider(new BouncyCastleProvider());

//    	if( StringUtils.isEmpty(salt) && salt.length() > 7 )
//    		System.arraycopy( salt.getBytes(), 0, saltData, 0, 8 );
	}
    //---------------------------------------------------------------
    private Cipher init( String passPhrase, byte [] salt, boolean encrypt )
	throws PlexusCipherException
	{
    	int mode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE; 
        try {
            KeySpec keySpec = new PBEKeySpec( passPhrase.toCharArray() );
            SecretKey key = SecretKeyFactory.getInstance(algorithm, SECURITY_PROVIDER).generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance( algorithm );

            PBEParameterSpec paramSpec = new PBEParameterSpec( salt, iterationCount );

            cipher.init( mode, key, paramSpec);
            return cipher;

        } catch( Exception e ) {
        	throw new PlexusCipherException(e);
        }	
	}
    //---------------------------------------------------------------
    private byte [] getSalt( int saltSize )
    throws NoSuchAlgorithmException, NoSuchProviderException
    {
//    	SecureRandom sr = SecureRandom.getInstance( SECURE_RANDOM_ALGORITHM, SECURITY_PROVIDER );
    	SecureRandom sr = new SecureRandom();
    	sr.setSeed( System.currentTimeMillis() );
    	return sr.generateSeed( saltSize );
    }
    //---------------------------------------------------------------
	public String encrypt( String str, String passPhrase )
	throws PlexusCipherException
	{
        try {
        	byte [] salt = getSalt( SALT_SIZE );
    		Cipher cipher = init( passPhrase, salt, true );
    		
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt it
            byte[] enc = cipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            BASE64Encoder b64 = new BASE64Encoder();
            byte saltLen = (byte)(salt.length & 0x00ff);
            int encLen = enc.length;
            byte [] res = new byte [ salt.length + encLen + 1 ];
            res[0] = saltLen;
            System.arraycopy(salt, 0, res, 1, saltLen );
            System.arraycopy( enc, 0, res, saltLen+1, encLen );

            return b64.encode( res );
        } catch( Exception e ) {
        	throw new PlexusCipherException(e);
        }	
	}
    //---------------------------------------------------------------
	public String encryptAndDecorate( String str, String passPhrase )
	throws PlexusCipherException
	{
		return decorate( encrypt( str, passPhrase ) );
	}
    //---------------------------------------------------------------
	public String decrypt( String str, String passPhrase )
	throws PlexusCipherException
	{
		if( StringUtils.isEmpty(str) )
			return str;

        try {
            // Decode base64 to get bytes
            byte[] res = new sun.misc.BASE64Decoder().decodeBuffer(str);
            int saltLen = res[0] & 0x00ff;
            if( saltLen != SALT_SIZE )
            	throw new Exception("default.plexus.cipher.encryptedStringCorruptedStructure");

            if( res.length < (saltLen + 2) )
            	throw new Exception("default.plexus.cipher.encryptedStringCorruptedLength");
            
            byte [] salt = new byte[ saltLen ];
            System.arraycopy( res, 1, salt, 0, saltLen );
            
            int decLen = res.length - saltLen - 1;
            if( decLen < 1 )
            	throw new Exception("default.plexus.cipher.encryptedStringCorruptedSize");

            byte [] dec = new byte[ decLen ];
            System.arraycopy( res, saltLen+1, dec, 0, decLen );

            // Decrypt
    		Cipher cipher = init( passPhrase, salt, false );
            byte [] utf8 = cipher.doFinal(dec);

            // Decode using utf-8
            return new String( utf8, "UTF8" );

        } catch( Exception e ) {
        	throw new PlexusCipherException(e);
        }	
	}
    //---------------------------------------------------------------
	public String decryptDecorated( String str, String passPhrase )
	throws PlexusCipherException
	{
		if( StringUtils.isEmpty(str) )
			return str;
		
		if( isEncryptedString(str) )
			return decrypt( unDecorate(str), passPhrase );
		
		return decrypt( str, passPhrase );
	}
	//-----------------------------------------------------------------------------------------------
	public boolean isEncryptedString( String str )
	{
		if( StringUtils.isEmpty(str) )
			return false;

		int start = str.indexOf( ENCRYPTED_STRING_DECORATION_START );
		int stop  = str.indexOf( ENCRYPTED_STRING_DECORATION_STOP  );
		if( start != -1 && stop != -1 && stop > start+1  )
			return true;
		return false;
	}
	//-----------------------------------------------------------------------------------------------
	public String unDecorate( String str )
	throws PlexusCipherException
	{
		if( !isEncryptedString(str) )
			throw new PlexusCipherException( "default.plexus.cipher.badEncryptedPassword");

		int start = str.indexOf( ENCRYPTED_STRING_DECORATION_START );
		int stop  = str.indexOf( ENCRYPTED_STRING_DECORATION_STOP  );
		return str.substring( start+1, stop );
	}
	//-----------------------------------------------------------------------------------------------
	public String decorate( String str )
	{
		return    ENCRYPTED_STRING_DECORATION_START
				+ (str == null ? "" : str)
				+ ENCRYPTED_STRING_DECORATION_STOP
		;
	}
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    //***************************************************************
	/**
	 * Exploratory part.
	 * This method returns all available services types
	 */
    public static String[] getServiceTypes()
    {
        Set result = new HashSet();
    
        // All all providers
        Provider[] providers = Security.getProviders();
        for (int i=0; i<providers.length; i++) {
            // Get services provided by each provider
            Set keys = providers[i].keySet();
            for( Iterator it = keys.iterator(); it.hasNext(); ) {
                String key = (String)it.next();
                key = key.split(" ")[0];
    
                if (key.startsWith("Alg.Alias.")) {
                    // Strip the alias
                    key = key.substring(10);
                }
                int ix = key.indexOf('.');
                result.add(key.substring(0, ix));
            }
        }
        return (String[])result.toArray(new String[result.size()]);
    }
    
    /**
     *  This method returns the available implementations for a service type 
     */
    public static String[] getCryptoImpls(String serviceType)
    {
        Set<String> result = new HashSet<String>();
    
        // All all providers
        Provider[] providers = Security.getProviders();
        for (int i=0; i<providers.length; i++) {
            // Get services provided by each provider
            Set keys = providers[i].keySet();
            for (Iterator<String> it=keys.iterator(); it.hasNext(); ) {
                String key = it.next();
                key = key.split(" ")[0];
    
                if (key.startsWith(serviceType+".")) {
                    result.add(key.substring(serviceType.length()+1));
                } else if (key.startsWith("Alg.Alias."+serviceType+".")) {
                    // This is an alias
                    result.add(key.substring(serviceType.length()+11));
                }
            }
        }
        return (String[])result.toArray(new String[result.size()]);
    }	
	//---------------------------------------------------------------
    public static void main(String[] args)
	{
    	Security.addProvider(new BouncyCastleProvider());

		String [] serviceTypes = getServiceTypes();
		if( serviceTypes != null )
			for( String serviceType : serviceTypes ) {
				String [] serviceProviders = getCryptoImpls( serviceType );
				if( serviceProviders != null ) {
					System.out.println(serviceType+": provider list");
					for( String provider : serviceProviders )
						System.out.println("        "+provider);
				} else {
					System.out.println(serviceType+": does not have any providers in this environment");
				}
			}
	}
	//---------------------------------------------------------------
}
