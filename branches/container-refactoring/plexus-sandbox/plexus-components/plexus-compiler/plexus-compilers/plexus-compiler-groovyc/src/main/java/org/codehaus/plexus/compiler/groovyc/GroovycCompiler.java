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

import org.codehaus.plexus.compiler.AbstractCompiler;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerError;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.CompilerOutputStyle;

import groovy.lang.GroovyClassLoader;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.messages.Message;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Add .groovy support to plexus-compilers.
 * 
 * @author <a href="mailto:eburghar@free.fr">Eric BURGHARD</a>
 */
public class GroovycCompiler
    extends AbstractCompiler
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public GroovycCompiler()
    {
        super( CompilerOutputStyle.ONE_OUTPUT_FILE_PER_INPUT_FILE,
        		".groovy",
        		".class",
               null );
    }

    // ----------------------------------------------------------------------
    // Compiler Implementation
    // ----------------------------------------------------------------------

    public List compile( CompilerConfiguration config )
        throws CompilerException
    {	
    	ErrorCollector collector = null;
 
    	LinkedList messages = new LinkedList();

        try
        {   
            String[] sources = getSourceFiles( config );
            
            if (sources.length == 0)
            {
            	getLogger().info( "Nothing to compile - all classes are up to date" );

            }
            else
            {
            	String srcstr = ( sources.length == 1 ) ? "1 source" : sources.length + " sources";
            	
            	getLogger().info( "Compiling " + srcstr + " to " + config.getOutputLocation() );
            	
            	GroovycConfiguration configuration = new GroovycConfiguration( config );
            	
	            GroovyClassLoader classloader = new GroovyClassLoader( this.getClass().getClassLoader(), configuration );
	            
	            CompilationUnit unit = new CompilationUnit( configuration, null, classloader );

	            collector = unit.getErrorCollector();
	            
	            unit.addSources( sources );

	            unit.compile();
            }
        }
        catch (CompilationFailedException e)
        {
        	// why does e.getUnit() always return null ?
        	if (collector != null)
        	{
        		List errors = collector.getErrors();
        		
	        	Iterator i = errors.iterator();

	        	while ( i.hasNext() )
	        	{
	        		Message message = (Message) i.next();
	        		
	        		StringWriter msg = new StringWriter();
	        		
	        		PrintWriter writer = new PrintWriter( msg );
	        		
	        		message.write( writer );
	        		
	        		messages.add( new CompilerError( msg.toString(), true) );
	        	}
        	}
        	else
        	{
        		messages.add( new CompilerError( e.getMessage(), true) );
        	}
        }
        catch (Exception e)
        {
        	messages.add( new CompilerError( e.getMessage(), true) );
        }
        return messages;
    }

    public String[] createCommandLine( CompilerConfiguration config )
            throws CompilerException
    {
        return null;
    }
}