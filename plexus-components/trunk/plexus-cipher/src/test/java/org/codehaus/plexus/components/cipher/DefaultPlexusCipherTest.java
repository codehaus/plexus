package org.codehaus.plexus.components.cipher;

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
import org.codehaus.plexus.util.StringUtils;

import java.io.File;

/**
 * Test the Plexus Cipher container
 *
 * @author Oleg Gusakov
 * @version $Id$
 */
public class DefaultPlexusCipherTest
    extends TestCase
{
    private String passPhrase = "foofoo";
    DefaultPlexusCipher pc;
    String str = "my testing phrase";
    String encStr =  "CEBn9v94nTa6aTGJoMQ6MEAT/KEXl2qtgBw=";
    
    //-------------------------------------------------------------
    public void setUp()
        throws Exception
    {
        super.setUp();

        pc = new DefaultPlexusCipher();
        pc.initialize();
    }
    //-------------------------------------------------------------
    public void testDefaultAlgorithmExists()
    throws Exception
    {
    	if( StringUtils.isEmpty(pc.algorithm) )
    		throw new Exception("No default algoritm found in DefaultPlexusCipher");
    	
    	String [] res = DefaultPlexusCipher.getCryptoImpls("Cipher");
    	assertNotNull("No Cipher providers found in the current environment", res );
    	
    	for( String provider : res )
    		if( pc.algorithm.equals(provider) )
    			return;
    	
    	throw new Exception("Cannot find default algorithm "+pc.algorithm+" in the current environment.");
    }
    //-------------------------------------------------------------
    public void stestFindDefaultAlgorithm()
    throws Exception
    {
    	String [] res = DefaultPlexusCipher.getServiceTypes();
    	assertNotNull("No Cipher providers found in the current environment", res );
	    	
    	for( String provider : DefaultPlexusCipher.getCryptoImpls("Cipher") ) 
    		try {
    			System.out.print( provider );
        		pc.algorithm = provider;
        		pc.encrypt(str, passPhrase);
    			System.out.println( "------------------> Success !!!!!!" );
    		} catch( Exception e ) {
    			System.out.println( e.getMessage() );
    		}
    }
    //-------------------------------------------------------------
    public void testDecrypt()
    throws Exception
    {
    	String res = pc.decrypt(encStr, passPhrase);
    	assertEquals("Decryption did not produce desired result", str, res );
    }
    //-------------------------------------------------------------
    public void testEncrypt()
    throws Exception
    {
    	String xRes = pc.encrypt(str, passPhrase);
    	String res = pc.decrypt(xRes, passPhrase);
    	assertEquals("Encryption/Decryption did not produce desired result", str, res );
    }
    //-------------------------------------------------------------
    public void testDecorate()
    throws Exception
    {
    	String res = pc.decorate("aaa");
    	assertEquals("Decoration failed"
    			, PlexusCipher.ENCRYPTED_STRING_DECORATION_START
    			+ "aaa"
    			+ PlexusCipher.ENCRYPTED_STRING_DECORATION_STOP
    			, res 
    				);
    }
    //-------------------------------------------------------------
    public void testUnDecorate()
    throws Exception
    {
    	String res = pc.unDecorate(
    			PlexusCipher.ENCRYPTED_STRING_DECORATION_START
    			+ "aaa"
    			+ PlexusCipher.ENCRYPTED_STRING_DECORATION_STOP
    			);
    	assertEquals("Decoration failed", "aaa", res );
    }
    //-------------------------------------------------------------
    //-------------------------------------------------------------
}
