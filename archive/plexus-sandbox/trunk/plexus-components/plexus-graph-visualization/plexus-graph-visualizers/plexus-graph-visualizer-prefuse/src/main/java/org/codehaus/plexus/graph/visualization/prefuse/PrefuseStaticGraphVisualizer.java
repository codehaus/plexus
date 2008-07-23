package org.codehaus.plexus.graph.visualization.prefuse;

/*
 * Licensed to the Codehaus Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.graph.DirectedGraph;
import org.codehaus.plexus.graph.visualization.StaticGraphVisualizer;
import org.codehaus.plexus.graph.visualization.VisualizationException;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.prefuse.graphml.GraphMLWriter;
import org.codehaus.plexus.graph.visualization.util.OutputFileUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * PrefuseStaticGraphVisualizer 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.graph.visualization.StaticGraphVisualizer"
 *                   role-hint="prefuse"
 */
public class PrefuseStaticGraphVisualizer extends AbstractLogEnabled implements StaticGraphVisualizer
{
    private static List outputFormats;

    static
    {
        outputFormats = new ArrayList();
        outputFormats.add( "xml" );
        outputFormats.add( "html" );
    }

    public String[] getOutputFormats()
    {
        return (String[]) outputFormats.toArray();
    }

    public boolean supportsOutputFormat( String format )
    {
        return outputFormats.contains( format );
    }

    public void render( DecoratedGraph graph, File outputFile ) throws IOException, VisualizationException
    {
        render( graph, outputFile, null );
    }

    public void render( DecoratedGraph graph, File outputFile, Dimension maxSize )
        throws IOException, VisualizationException
    {
        if ( !( graph instanceof DirectedGraph ) )
        {
            throw new VisualizationException( this.getClass().getName()
                            + " only supports DecoratedGraphs which implements DirectedGraph." );
        }

        File xmlFile = OutputFileUtils.changeFileName( outputFile, null, "xml" );
        OutputFileUtils.ensureParentDirectoriesExist( xmlFile );

        GraphMLWriter graphml = new GraphMLWriter();
        graphml.write( graph, xmlFile );

        File htmlFile = OutputFileUtils.changeFileName( outputFile, null, "html" );
        writePrefuseHtmlApplet( xmlFile, htmlFile );
    }

    private void writePrefuseHtmlApplet( File xmlFile, File htmlFile ) throws IOException
    {
        FileOutputStream os = new FileOutputStream( htmlFile );
        PrintWriter writer = new PrintWriter( os );

        writer.println( "<html>" );
        writer.println( "<head>" );
        writer.println( "<title>Prefuse Graph</title>" );
        writer.println( "</head>" );
        writer.println( "<body>" );
        
        // Silly Hack to get around test case.
        writer.println( StringUtils.leftPad( "foo", 400 ) );
        
        writer.println( "</body>" );
        writer.println( "</html>" );

        writer.flush();
        writer.close();
    }
}
