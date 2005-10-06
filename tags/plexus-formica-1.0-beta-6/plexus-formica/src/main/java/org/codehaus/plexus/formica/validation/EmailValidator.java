package org.codehaus.plexus.formica.validation;

/*
 * Copyright (c) 2004, Codehaus.org
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

import org.apache.oro.text.perl.Perl5Util;

/**
 * @plexus.component
 *  role-hint="email"
 *
 * <p>Perform email validations.</p>
 * <p/>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * <p/>
 * Based on a script by <a href="mailto:stamhankar@hotmail.com">Sandeep V. Tamhankar</a>
 * http://javascript.internet.com
 * </p>
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @version $Revision$ $Date$
 */
public class EmailValidator
    extends AbstractValidator
{
    private static String SPECIAL_CHARS = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
    private static String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static String QUOTED_USER = "(\"[^\"]*\")";
    private static String ATOM = VALID_CHARS + '+';
    private static String WORD = "(" + ATOM + "|" + QUOTED_USER + ")";

    // Each pattern must be surrounded by /
    private static String LEGAL_ASCII_PATTERN = "/^[\\000-\\177]+$/";
    private static String EMAIL_PATTERN = "/^(.+)@(.+)$/";
    private static String IP_DOMAIN_PATTERN =
        "/^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$/";

    private static String USER_PATTERN = "/^" + WORD + "(\\." + WORD + ")*$/";
    private static String DOMAIN_PATTERN = "/^" + ATOM + "(\\." + ATOM + ")*$/";
    private static String ATOM_PATTERN = "/(" + ATOM + ")/";

    /**
     * Protected constructor for subclasses to use.
     */
    public EmailValidator()
    {
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A <code>null</code>
     */
    public boolean validate( String email )
    {
        if ( email == null )
        {
            return false;
        }

        Perl5Util matchAsciiPat = new Perl5Util();
        if ( !matchAsciiPat.match( LEGAL_ASCII_PATTERN, email ) )
        {
            return false;
        }

        // Check the whole email address structure
        Perl5Util emailMatcher = new Perl5Util();
        if ( !emailMatcher.match( EMAIL_PATTERN, email ) )
        {
            return false;
        }

        if ( email.endsWith( "." ) )
        {
            return false;
        }

        if ( !isValidUser( emailMatcher.group( 1 ) ) )
        {
            return false;
        }

        if ( !isValidDomain( emailMatcher.group( 2 ) ) )
        {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the domain component of an email address is valid.
     */
    protected boolean isValidDomain( String domain )
    {
        boolean symbolic = false;
        Perl5Util ipAddressMatcher = new Perl5Util();

        if ( ipAddressMatcher.match( IP_DOMAIN_PATTERN, domain ) )
        {
            if ( !isValidIpAddress( ipAddressMatcher ) )
            {
                return false;
            }
        }
        else
        {
            // Domain is symbolic name
            Perl5Util domainMatcher = new Perl5Util();
            symbolic = domainMatcher.match( DOMAIN_PATTERN, domain );
        }

        if ( symbolic )
        {
            if ( !isValidSymbolicDomain( domain ) )
            {
                return false;
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the user component of an email address is valid.
     */
    protected boolean isValidUser( String user )
    {
        Perl5Util userMatcher = new Perl5Util();
        return userMatcher.match( USER_PATTERN, user );
    }

    /**
     * Validates an IP address. Returns true if valid.
     */
    protected boolean isValidIpAddress( Perl5Util ipAddressMatcher )
    {
        for ( int i = 1; i <= 4; i++ )
        {
            String ipSegment = ipAddressMatcher.group( i );
            if ( ipSegment == null || ipSegment.length() <= 0 )
            {
                return false;
            }

            int iIpSegment;

            try
            {
                iIpSegment = Integer.parseInt( ipSegment );
            }
            catch ( NumberFormatException e )
            {
                return false;
            }

            if ( iIpSegment > 255 )
            {
                return false;
            }

        }
        return true;
    }

    /**
     * Validates a symbolic domain name.  Returns true if it's valid.
     */
    protected boolean isValidSymbolicDomain( String domain )
    {
        String[] domainSegment = new String[10];
        boolean match = true;
        int i = 0;
        Perl5Util atomMatcher = new Perl5Util();

        while ( match )
        {
            match = atomMatcher.match( ATOM_PATTERN, domain );
            if ( match )
            {
                domainSegment[i] = atomMatcher.group( 1 );
                int l = domainSegment[i].length() + 1;
                domain =
                    ( l >= domain.length() )
                    ? ""
                    : domain.substring( l );

                i++;
            }
        }

        int len = i;
        if ( domainSegment[len - 1].length() < 2
            || domainSegment[len - 1].length() > 4 )
        {

            return false;
        }

        // Make sure there's a host name preceding the domain.
        if ( len < 2 )
        {
            return false;
        }

        return true;
    }

}
