package org.codehaus.plexus.formica.util;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.logging.Logger;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This https implementation ignores certificates and processes urls of the format
 * 
 * https://[username:password@]host.com/
 * 
 * @author <a href="mailto:jesse.mcconnell@gmail.com">Jesse McConnell</a>
 * @version $Id$
 */
public class MungedHttpsURL
{
    // ----------------------------------------------------------------------
    // urlString to make the Https connection to
    // ----------------------------------------------------------------------

    private String urlString;

    private String username;

    private String password;

    private Logger logger;

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    public MungedHttpsURL( String mungedUrl )
        throws MalformedURLException
    {
       username = scrapeUsername( mungedUrl );

       password = scrapePassword( mungedUrl );

       urlString = scrapeUrl( mungedUrl );

       if ( urlString == null )
       {
           throw new MalformedURLException( "Unable to generate clean url from url string: " + mungedUrl );
       }
       
    }

    public MungedHttpsURL( String urlString, String username, String password )
        throws MalformedURLException
    {
       this.username = username;

       this.password = password;

       this.urlString = urlString;

       if ( !isValid() )
       {
           throw new MalformedURLException( "Unable to validate URL" );
       }
    }

    public void setLogger( Logger logger )
    {
        this.logger = logger;
    }
    
    // ----------------------------------------------------------------------
    // HttpsURL Implementation
    // ----------------------------------------------------------------------

    /**
     * this bit attempts to ignore certificates that might need to be accepted and also tries to 
     * conform to the format https://[<username>:<password>@]host
     *  
     * adapted from code snippets from http://javaalmanac.com/egs/javax.net.ssl/TrustAll.html
     */
    public boolean isValid()
    {
        try
        {
            HttpURLConnection urlc = getURLConnection();

            urlc.getResponseCode();
        }
        catch ( IOException e )
        {
            if ( logger != null )
            {
                logger.info( "URL is not valid: " + urlString, e );
            }

            return false;
        }

        return true;
    }
    
    public HttpURLConnection getURLConnection()
        throws MalformedURLException
    {
        try
        {
            if ( urlString.startsWith( "https") )
            {
                return getHttpsUrlConnection();
            }
            else
            {
                return (HttpURLConnection) getHttpUrl().openConnection();
            }
        }
        catch ( IOException e )
        {
            throw new MalformedURLException( "unable to create munged http url connection" );
        }
    }

    public URL getURL()
        throws MalformedURLException
    {
        try
        {
            if ( urlString.startsWith( "https" ) )
            {
                return getHttpsUrl();
            }
            else
            {
                return getHttpUrl();
            }

        }
        catch ( Exception e )
        {
            throw new MalformedURLException( "unable to create munged url" );
        }
    }

    private URL getHttpUrl() throws MalformedURLException
    {
        if ( username != null && password != null )
        {
            Authenticator.setDefault( new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    PasswordAuthentication passwordAuthentication = new PasswordAuthentication( username, password.toCharArray() );

                    return passwordAuthentication;
                }
            } );
        }

        return new URL( urlString );
    }

    private HttpURLConnection getHttpsUrlConnection()
        throws IOException 
    {
        ignoreCertificates();

        String authString = username + ":" + password;

        URL url = new URL( urlString );

        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

        urlc.setDoInput( true );

        urlc.setUseCaches( false );

        urlc.setRequestProperty( "Content-Type", "application/octet-stream" );

        if ( username != null && password != null )
        {
            urlc.setRequestProperty( "Authorization", "Basic " +
                    new sun.misc.BASE64Encoder().encode( authString.getBytes() ) );
        }

        return urlc;
    }
    
    private URL getHttpsUrl()
            throws Exception
    {
        ignoreCertificates();

        String authString = username + ":" + password;

        URL url = new URL( urlString );

        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

        urlc.setDoInput( true );

        urlc.setUseCaches( false );

        urlc.setRequestProperty( "Content-Type", "application/octet-stream" );

        if ( username != null && password != null )
        {
            urlc.setRequestProperty( "Authorization", "Basic " +
                new sun.misc.BASE64Encoder().encode( authString.getBytes() ) );
        }

        return url;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUrlString()
    {
        return urlString;
    }

    // ----------------------------------------------------------------------
    // helper methods
    // ----------------------------------------------------------------------

    /**
     * setup the environment to ignore all certificates for the connection
     */
    private void ignoreCertificates()
        throws IOException
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

        HostnameVerifier hostnameVerifier = new HostnameVerifier()
        {
            public boolean verify( String urlHostName, SSLSession session )
            {
                System.out.println( "Warning: URL Host: " + urlHostName + " vs." + session.getPeerHost() );
                return true;
            }
        };

        // Install the all-trusting trust manager

        try
        {
            SSLContext sslContext = SSLContext.getInstance( "SSL" );

            sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );

            HttpsURLConnection.setDefaultSSLSocketFactory( sslContext.getSocketFactory() );

            HttpsURLConnection.setDefaultHostnameVerifier( hostnameVerifier );
        }
        catch ( KeyManagementException e )
        {
            throw new RuntimeException( e );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * return the username from https://<name>:<password>@<url>
     */
    private String scrapeUsername( String url )
    {
        String t = scrapeHost( url );

        if ( t == null || t.indexOf( "@" ) < 0 || ( t.indexOf( ":" ) < 0 && t.indexOf( "@" ) < 0 ) )
        {
            return null;
        }

        return t.substring( 0, t.indexOf( ":" ) );
    }

    /**
     * return to password from https://<name>:<password>@<url>
     */
    private String scrapePassword( String url )
    {
        String t = scrapeHost( url );

        if ( t == null || t.indexOf( "@" ) < 0 || ( t.indexOf( ":" ) < 0 && t.indexOf( "@" ) < 0 ) )
        {
            return null;
        }

       return t.substring( t.indexOf( ":" ) + 1, t.indexOf( "@" ) );
    }

    /**
     * return to host from https://<name>:<password>@<url>
     */
    private String scrapeHost( String url )
    {
        int hostStart = url.indexOf( "//" );
        
        if ( hostStart == -1 )
        {
            return null;
        }

        int hostEnd = url.indexOf( '/', hostStart + 2 );
        
        if ( hostEnd == -1 )
        {
            return url.substring( hostStart + 2 );
        }

        return url.substring( hostStart + 2, hostEnd );
    }

    /**
     * return the url from https://<name>:<password>@<url>
     */
    private String scrapeUrl( String url )
    {
        if ( url.indexOf( "@" ) != -1 )
        {
            String s = url.substring( url.indexOf( "@" ) + 1, url.length() );
            if ( url.startsWith( "http://" ) )
            {
                return "http://" + s;
            }
            else
            {
                return "https://" + s;
            }
        }
        else
        {
            return url;
        }
    }
}
