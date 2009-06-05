package org.codehaus.plexus.interpolation;

/*
 * Copyright 2007 The Codehaus Foundation.
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

import org.codehaus.plexus.interpolation.os.OperatingSystemUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * {@link ValueSource} which resolves expressions against the environment variables
 * available from the underlying operating system (and possibly, the shell environment
 * that created the present Java process). If the expression starts with 'env.',
 * this prefix is trimmed before resolving the rest as an environment variable name.
 *
 * @version $Id$
 */
public class EnvarBasedValueSource
    extends AbstractValueSource
{

    private static Properties envarsCaseSensitive;
    private static Properties envarsCaseInsensitive;

    private final Properties envars;
    private final boolean caseSensitive;

    /**
     * Create a new value source for interpolation based on shell environment variables. In this
     * case, envar keys ARE CASE SENSITIVE.
     *
     * @throws IOException
     */
    public EnvarBasedValueSource() throws IOException
    {
        this( true );
    }

    /**
     * Create a new value source for interpolation based on shell environment variables.
     *
     * @param caseSensitive Whether the environment variable key should be treated in a
     *                      case-sensitive manner for lookups
     * @throws IOException
     */
    public EnvarBasedValueSource( boolean caseSensitive ) throws IOException
    {
        super( false );
        this.caseSensitive = caseSensitive;
        this.envars = getEnvars( caseSensitive );
    }

    private static synchronized Properties getEnvars( boolean caseSensitive )
        throws IOException
    {
        if ( caseSensitive )
        {
            if ( envarsCaseSensitive == null )
            {
                envarsCaseSensitive = OperatingSystemUtils.getSystemEnvVars( caseSensitive );
            }
            return envarsCaseSensitive;
        }
        else
        {
            if ( envarsCaseInsensitive == null )
            {
                envarsCaseInsensitive = OperatingSystemUtils.getSystemEnvVars( caseSensitive );
            }
            return envarsCaseInsensitive;
        }
    }

    /**
     * If the expression starts with 'env.' then trim this prefix. Next, resolve
     * the (possibly trimmed) expression as an environment variable name against
     * the collection of environment variables that were read from the operating
     * system when this {@link ValueSource} instance was created.
     *
     * @param expression envar expression, like 'HOME' or 'env.HOME'
     * @return the environment variable value for the given expression
     */
    public Object getValue( String expression )
    {
        String expr = expression;

        if ( expr.startsWith( "env." ) )
        {
            expr = expr.substring( "env.".length() );
        }

        if ( !caseSensitive )
        {
            expr = expr.toUpperCase();
        }

        return envars.getProperty( expr );
    }

}
