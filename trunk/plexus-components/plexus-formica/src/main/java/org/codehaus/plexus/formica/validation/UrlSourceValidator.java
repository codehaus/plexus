/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.formica.validation;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.codehaus.plexus.formica.FormicaException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * @plexus.component
 *  role-hint="url-source"
 *
 * An implementation of the Validator interface which validates
 * FormElements based on a Perl 5 regular expression.
 *
 * @author Anthony Eden
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class UrlSourceValidator
    extends AbstractValidator
{
    /**
     * <p>Checks if a field has a valid url address.</p>
     *
     * @param value The value validation is being performed on.  A <code>null</code>
     * value is considered valid.
     * @return true if the url exists.
     */
    public boolean validate( String urlString )
    {
        if ( StringUtils.isEmpty( urlString ) )
        {
            return true;
        }

        try
        {
            // ----------------------------------------------------------------------
            // We are assuming that this url is valid as the UrlValidator has been
            // used to make sure that can create a valid URL. Here we simply
            // want to make sure the URL corresponds to an existing resource.
            // ----------------------------------------------------------------------

        	// if it is a https connection then we have a bit more work to do
            if ( urlString != null && urlString.startsWith("https") )
            {
            	return validateSecureURL( urlString );
            }
            else
            {
                URL url = new URL( urlString );

                InputStream is = url.openStream();

                is.close();
            }
        }
        catch ( Exception e )
        {
            return false;
        }

        return true;
    }

    /**
     * This bit attempts to ignore certificates that might need to be accepted and also tries to 
     * conform to the format https://<username>:<password>@host
     *  
     * adapted from code snippets from http://javaalmanac.com/egs/javax.net.ssl/TrustAll.html *
     */
    public boolean validateSecureURL( String urlString )
    {
        try
        {
    		// Create a trust manager that does not validate certificate
    		// chains
    	    TrustManager[] trustAllCerts = new TrustManager[]
    	    {
    	         new X509TrustManager()
    	         {
		              public java.security.cert.X509Certificate[] getAcceptedIssuers()
		              {
		                 return null;
		              }

		              public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType )
		              {
		              }

		              public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType )
		              {
		              }
    		     }
    		};

    		// Install the all-trusting trust manager

    	    SSLContext sslContext = SSLContext.getInstance( "SSL" );

    		sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );

    		HttpsURLConnection.setDefaultSSLSocketFactory( sslContext.getSocketFactory() );

            String username = scrapeUsername( urlString );

            String password = scrapePassword( urlString );

            String authString = username + ":" + password;

            String cleanUrl = scrapeUrl( urlString );

            URL url = new URL( cleanUrl );

            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

            urlc.setDoInput( true );

            urlc.setUseCaches( false );

            urlc.setRequestProperty( "Content-Type", "application/octet-stream" );

            if ( username != null && password != null )
            {
                urlc.setRequestProperty( "Authorization", "Basic " + new sun.misc.BASE64Encoder().encode( authString.getBytes() ) );
            }

            InputStream is = urlc.getInputStream();

            is.close();
          
        }
        catch ( Exception e )
        {
           return false;
        }

        return true;
    }

    /**
     * return the username from https://<name>:<password>@<url>
     */
    private String scrapeUsername( String url )
    {
        String t = url.substring(8, url.length());

        return t.substring(0, t.indexOf(":"));
    }

    /**
     * return to password from https://<name>:<password>@<url>
     */
    private String scrapePassword( String url )
    {
       String t = url.substring( 8, url.length() );

       return t.substring( t.indexOf( ":" ) + 1, t.indexOf( "@" ) );
    }

     /**
      * return the url from https://<name>:<password>@<url>
      */
     private String scrapeUrl( String url )
     {
    	 if ( url.indexOf( "@" ) != -1 )
    	 {
    		 return "https://" + url.substring(url.indexOf( "@" ) + 1, url.length() );
    	 }
    	 else
    	 {
    		 return url;
    	 }
     }
}
