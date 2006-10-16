package org.codehaus.plexus.compiler.groovyc;

/**
 * The MIT License
 *
 * Copyright (c) 2005, The Codehaus
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

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.messages.WarningMessage;

/**
 * A CompilerConfiguration that initialise itself from a plexus.compiler.CompilerConfiguration
 * 
 * @author <a href="mailto:eburghar@free.fr">Eric BURGHARD</a>
 */
public class GroovycConfiguration extends CompilerConfiguration
{	
	GroovycConfiguration( org.codehaus.plexus.compiler.CompilerConfiguration config )
	{
	   	// Classpath
        List classpath = config.getClasspathEntries();

        if ( classpath != null )
        {
            String path = "";
 
            for ( Iterator i = classpath.iterator(); i.hasNext(); )
            {
            	String cp = (String) i.next();

            	if ( path.equals( "" ) )
            	{
            		path.concat( cp );
            	}
            	else
            	{
            		path.concat( File.pathSeparator + cp );
            	}
            }

            this.setClasspath( path );
        }
        
        // Build directory
        this.setTargetDirectory( config.getOutputLocation() );

        // Sources encoding
        String encoding = config.getSourceEncoding();
 
        if (encoding != null)
        {
            this.setSourceEncoding( encoding );
        }
        
        // Debug: This break compilation
        //this.setDebug( config.isDebug() );
        
        // Verbose
        this.setVerbose( config.isVerbose() );
        
        // Warnings
        if ( config.isShowWarnings() )
        {
        	this.setWarningLevel( WarningMessage.LIKELY_ERRORS );
        }
        else
        {
        	this.setWarningLevel( WarningMessage.NONE );
        }
	}
}