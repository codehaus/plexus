package org.codehaus.plexus.summit;

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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.servlet.PlexusServlet;
import org.codehaus.plexus.summit.exception.ExceptionHandler;
import org.codehaus.plexus.summit.pipeline.Pipeline;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.util.ExceptionUtils;

public class Summit
    extends PlexusServlet
{
    public final void doGet( HttpServletRequest req, HttpServletResponse res )
        throws IOException, ServletException
    {
        RunData data = null;

        try
        {
            data = (RunData) lookup( RunData.ROLE );

            data.setRequest( req );

            data.setResponse( res );

            data.setServletConfig( getServletConfig() );

            Pipeline pipeline = null;

            pipeline = (Pipeline) lookup( Pipeline.ROLE );

            pipeline.invoke( data );
        }
        catch ( Throwable t )
        {
            t.printStackTrace();

            try
            {
                ExceptionHandler handler = (ExceptionHandler) lookup( ExceptionHandler.ROLE );

                // Not quite sure what to do here, we may not have a
                // RunData object as the result of a ServiceException,
                // perhaps we should change the interface of
                // ExceptionHandler to take the request, response, and
                // application applicationView instead of RunData?? --pete

                handler.handleException( data, t );
            }
            catch ( Exception e )
            {
                //ok do it the hard coded way
                res.setContentType( "text/html" );

                PrintWriter out = res.getWriter();

                res.setHeader( "Expires", "Mon, 01 Jan 1990 00:00:00 GMT" );

                out.println( "<!doctype html public \"-//w3c//dtd html 3.2//en\">" );

                out.println( "<html><head><head><body>" );

                out.println( "<p><b>Error displaying template</b>. Exception:</p><pre>" );

                out.println( ExceptionUtils.getFullStackTrace( t ) );

                out.println( "</pre>" );

                out.println( "</body></html>" );

                out.close();
            }
        }
        finally
        {
            if ( data != null )
            {
                release( data );
            }
        }
    }

    public final void doPost( HttpServletRequest req, HttpServletResponse res )
        throws IOException, ServletException
    {
        doGet( req, res );
    }

    public final String getServletInfo()
    {
        return "Summit Servlet";
    }
}
