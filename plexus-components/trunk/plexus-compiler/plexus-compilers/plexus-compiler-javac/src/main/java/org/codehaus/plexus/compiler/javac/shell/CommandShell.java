package org.codehaus.plexus.compiler.javac.shell;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

/**
 * <p>
 * Class with patches copied from plexus-utils with fix for PLX-161,
 * as we can not upgrade plexus-utils until it's upgraded in core Maven
 * </p>
 * 
 * TODO deprecate when plexus-utils 1.2 can be used
 *
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 */
public class CommandShell
    extends Shell
{
    public CommandShell()
    {
        setShellCommand( "command.com" );
        setShellArgs( new String[]{"/C"} );
    }

}
