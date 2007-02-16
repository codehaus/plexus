/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.formica.validation;

import org.codehaus.plexus.formica.util.MungedHttpsURL;
import org.codehaus.plexus.util.StringUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

            // if it is a https connection then we have a bit more work to do, currently 
            // only accepting the munged https url that is resolved in MungedHttpsURL
            if ( urlString != null && urlString.startsWith( "http" ) )
            {
                MungedHttpsURL url = new MungedHttpsURL( urlString );

                url.setLogger( getLogger() );

                return url.isValid();
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
            getLogger().debug( "An exception is occurred.", e );

            return false;
        }

        return true;
    }
}