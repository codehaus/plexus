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

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.codehaus.plexus.formica.FormicaException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * @plexus.component
 *  role-hint="pattern-digits"
 *
 * An implementation of the Validator interface which validates
 * FormElements based on a Perl 5 regular expression.
 *
 * @author Anthony Eden
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class REValidator
    extends AbstractValidator
    implements Initializable
{
    /**
     * @plexus.configuration
     *  default-value="[0-9]+"
     */
    private String pattern;

    private Perl5Compiler compiler;

    private Perl5Matcher matcher;

    Pattern compiledPattern;

    /**
     * Construct a new REFormValidator.  This constructor will
     * initialize the Perl5Compiler and matcher.
     */

    public REValidator()
    {
        compiler = new Perl5Compiler();

        matcher = new Perl5Matcher();
    }

    public boolean validate( String data )
        throws FormicaException
    {

        if ( !matcher.matches( data, compiledPattern ) )
        {
            return false;
        }

        return true;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern( String pattern )
    {
        this.pattern = pattern;
    }

    public void initialize()
        throws InitializationException
    {
        // compile pattern only once
        if ( pattern != null )
        {
            try
            {
                compiledPattern = compiler.compile( pattern );
            }
            catch ( MalformedPatternException e )
            {
                throw new InitializationException( "Regular expression pattern is malformed.", e );
            }
        }
        else
        {
            throw new InitializationException( "Regular expression cannot be null." );
        }
    }
}
