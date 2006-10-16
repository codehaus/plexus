package org.codehaus.plexus.commandline;

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

import java.util.List;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import java.io.File;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultExecutableResolver
    extends AbstractLogEnabled
    implements ExecutableResolver, Initializable
{
    /** @plexus.configuration */
    private String defaultPath;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private List defaultPathList;

    // ----------------------------------------------------------------------
    // Component lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        if ( defaultPath == null )
        {
            defaultPath = System.getProperty( "plexus.system.path" );
        }

        if ( defaultPath == null )
        {
            defaultPathList = Collections.EMPTY_LIST;
        }
        else
        {
            String separator = System.getProperty( "path.separator" );

            StringTokenizer tokenizer = new StringTokenizer( defaultPath, separator );

            defaultPathList = new LinkedList();

            while ( tokenizer.hasMoreElements() )
            {
                String element = (String) tokenizer.nextElement();

                defaultPathList.add( element );
            }
        }
    }

    // ----------------------------------------------------------------------
    // ExecutableResolver Implementation
    // ----------------------------------------------------------------------

    public List getDefaultPath()
    {
        return defaultPathList;
    }

    public File findExecutable( String executable )
    {
        return findExecutable( executable, getDefaultPath() );
    }

    public File findExecutable( String executable, List path )
    {
        if ( StringUtils.isEmpty( executable ) )
        {
            throw new NullPointerException( "executable cannot be null" );
        }

        if ( path == null )
        {
            throw new NullPointerException( "path cannot be null" );
        }

        File f = new File( executable );

        if ( f.isAbsolute() && f.isFile() )
        {
            return f;
        }

        if ( path == null )
        {
            return null;
        }

        // TODO: Need to resolve it with defaults extension of system
        // ie. if executable is 'mvn', we must search 'mvn.bat'
        for ( Iterator it = path.iterator(); it.hasNext(); )
        {
            String s = (String) it.next();

            f = new File( s, executable );

            if ( f.isFile() )
            {
                return f;
            }
        }

        return null;
    }

    public boolean hasExecutable( String executable )
    {
        return hasExecutable( executable, getDefaultPath() );
    }

    public boolean hasExecutable( String executable, List path )
    {
        return findExecutable( executable, path ) != null;
    }
}
