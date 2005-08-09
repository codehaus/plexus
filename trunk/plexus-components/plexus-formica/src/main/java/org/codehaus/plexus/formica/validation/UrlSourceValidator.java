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

import java.net.URL;
import java.net.MalformedURLException;
import java.io.InputStream;

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
    public boolean validate( String urlString )
    {
        try
        {
            // ----------------------------------------------------------------------
            // We are assuming that this url is valid as the UrlValidator has been
            // used to make sure that can create a valid URL. Here we simply
            // want to make sure the URL corresponds to an existing resource.
            // ----------------------------------------------------------------------

            //TODO: we need to support authentication (CONTINUUM-258)

            URL url = new URL( urlString );

            InputStream is = url.openStream();

            is.close();
        }
        catch ( Exception e )
        {
            return false;
        }

        return true;
    }
}
