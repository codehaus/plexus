package org.codehaus.plexus.formica.validation;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.codehaus.plexus.formica.FormicaException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * An implementation of the Validator interface which validates
 * FormElements based on a Perl 5 regular expression.
 *
 * @author Anthony Eden
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class REValidator
    extends AbstractValidator implements Initializable
{
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

    /**
     * @throws FormicaException
     */
    public void initialize() throws FormicaException
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
                throw new FormicaException( "Regular expression pattern is malformed.", e );
            }
        }
        else
        {
            throw new FormicaException( "Regular expression cannot be null." );
        }
    }
}
