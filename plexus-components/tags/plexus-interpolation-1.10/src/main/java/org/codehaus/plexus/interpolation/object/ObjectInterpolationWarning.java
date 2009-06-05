package org.codehaus.plexus.interpolation.object;

/*
 * Copyright 2001-2008 Codehaus Foundation.
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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Represents a warning that occurred while interpolating an object graph. These
 * warnings may not have a serious effect, so they don't cause an exception to be
 * thrown. Each warning contains the path through the object graph from the point
 * of entry to the place where the warning occurred, along with a message containing
 * the actual warning and possibly a {@link Throwable} cause.
 * 
 * @author jdcasey
 */
public class ObjectInterpolationWarning
{
    
    private final String message;
    private Throwable cause;
    private final String path;

    public ObjectInterpolationWarning( String path, String message )
    {
        this.path = path;
        this.message = message;
    }

    public ObjectInterpolationWarning( String path, String message, Throwable cause )
    {
        this.path = path;
        this.message = message;
        this.cause = cause;
    }
    
    public String getPath()
    {
        return path;
    }

    public String getMessage()
    {
        return message;
    }

    public Throwable getCause()
    {
        return cause;
    }
    
    public String toString()
    {
        if ( cause == null )
        {
            return path + ": " + message;
        }
        else
        {
            StringWriter w = new StringWriter();
            PrintWriter pw = new PrintWriter( w );
            
            pw.print( path );
            pw.print( ": " );
            pw.println( message );
            pw.println( "Cause: " );
            cause.printStackTrace( pw );
            
            return w.toString();
        }
    }

}
