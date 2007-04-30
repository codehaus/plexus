package org.codehaus.plexus.graph.visualization.graphviz;

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
import org.codehaus.plexus.graph.visualization.graphviz.writer.GraphvizDirectedGraphWriter;
import org.codehaus.plexus.graph.visualization.graphviz.writer.GraphvizDotWriter;
import org.codehaus.plexus.graph.visualization.graphviz.writer.GraphvizUndirectedWriter;
import org.codehaus.plexus.graph.visualization.util.OutputFileUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GraphvizStaticGraphVisualizer 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.graph.visualization.StaticGraphVisualizer"
 *                   role-hint="graphviz"
 */
public class GraphvizStaticGraphVisualizer extends AbstractLogEnabled implements StaticGraphVisualizer
{
    private static List outputFormats;

    static
    {
        outputFormats = new ArrayList();
        // Postscript
        outputFormats.add( "ps" );
        // Scalable Vector Graphics
        outputFormats.add( "svg" );
        outputFormats.add( "svgz" );
        // XFig graphics
        outputFormats.add( "fig" );
        // FrameMaker graphics
        outputFormats.add( "mif" );
        // HP Pen Plotters
        outputFormats.add( "hpgl" );
        // Laserjet Printers
        outputFormats.add( "pcl" );
        // Pixel Graphics
        outputFormats.add( "jpg" );
        outputFormats.add( "png" );
        outputFormats.add( "gif" );
        outputFormats.add( "jpeg" );
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
        GraphvizDotWriter dotWriter = null;
        if ( graph instanceof DirectedGraph )
        {
            dotWriter = new GraphvizDirectedGraphWriter();
        }
        else
        {
            dotWriter = new GraphvizUndirectedWriter();
        }
        
        File dotFile = OutputFileUtils.changeFileName( outputFile, null, "dot" );
        OutputFileUtils.ensureParentDirectoriesExist( dotFile );

        dotWriter.writeDot( graph, dotFile );

        String workdir = outputFile.getParent();
        if ( StringUtils.isEmpty( workdir ) )
        {
            workdir = ".";
        }
        String extension = FileUtils.extension( outputFile.getName() );

        Commandline cmdline = new Commandline();

        if ( graph instanceof DirectedGraph )
        {
            cmdline.setExecutable( "dot" );
        }
        else
        {
            cmdline.setExecutable( "neato" );
        }
        cmdline.setWorkingDirectory( workdir );

        try
        {
            cmdline.addSystemEnvironment();
        }
        catch ( Exception e )
        {
            throw new VisualizationException( "Unable to add system environment to graphviz commandline.", e );
        }

        cmdline.createArgument().setValue( "-T" + extension );
        cmdline.createArgument().setValue( dotFile.getName() );
        cmdline.createArgument().setValue( "-o" );
        cmdline.createArgument().setValue( outputFile.getName() );

        StreamConsumer stdOut = new TeeConsumer( System.out );
        StreamConsumer stdErr = new TeeConsumer( System.err );

        try
        {
            getLogger().info( "Executing: " + cmdline.toString() );

            int result = CommandLineUtils.executeCommandLine( cmdline, stdOut, stdErr );

            if ( result != 0 )
            {
                throw new VisualizationException( "Graphviz execution failed, exit code: \'" + result + "\'" );
            }
        }
        catch ( CommandLineException e )
        {
            throw new VisualizationException( "Can't run graphviz: " + cmdline.toString(), e );
        }
    }
}
