package org.codehaus.plexus.graphing.graphviz;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.graphing.GraphRenderer;
import org.codehaus.plexus.graphing.GraphingException;
import org.codehaus.plexus.graphing.decorators.EdgeDecorator;
import org.codehaus.plexus.graphing.decorators.GraphDecorator;
import org.codehaus.plexus.graphing.decorators.NodeDecorator;
import org.codehaus.plexus.graphing.model.Edge;
import org.codehaus.plexus.graphing.model.Graph;
import org.codehaus.plexus.graphing.model.Node;
import org.codehaus.plexus.graphing.util.ColorUtil;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * GraphvizRenderer 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.graphing.GraphRenderer"
 *                   role-hint="graphviz"
 */
public class GraphvizRenderer
    extends AbstractLogEnabled
    implements GraphRenderer
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

    public void render( Graph graphModel, File outputFile )
        throws IOException, GraphingException
    {
        File dotFile = prepareDot( graphModel, outputFile );

        String workdir = outputFile.getParent();
        if ( StringUtils.isEmpty( workdir ) )
        {
            workdir = ".";
        }
        String extension = FileUtils.extension( outputFile.getName() );

        Commandline cmdline = new Commandline();
        cmdline.setExecutable( "dot" );
        cmdline.setWorkingDirectory( workdir );

        try
        {
            cmdline.addSystemEnvironment();
        }
        catch ( Exception e )
        {
            throw new GraphingException( "Unable to add system environment to graphviz commandline.", e );
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
                throw new GraphingException( "Graphviz execution failed, exit code: \'" + result + "\'" );
            }
        }
        catch ( CommandLineException e )
        {
            throw new GraphingException( "Can't run graphviz: " + cmdline.toString(), e );
        }
    }

    private File prepareDot( Graph graphModel, File outputFile )
        throws IOException, GraphingException
    {
        String dotFilename = FileUtils.removeExtension( outputFile.getName() ) + ".dot";
        File dotFile = new File( dotFilename );
        if ( outputFile.getParentFile() != null )
        {
            dotFile = new File( outputFile.getParentFile(), dotFilename );
        }

        if ( !dotFile.getParentFile().exists() )
        {
            dotFile.getParentFile().mkdirs();
        }

        FileWriter fwriter = new FileWriter( dotFile );
        PrintWriter dot = new PrintWriter( fwriter );

        GraphDecorator graphDecorator = graphModel.getDecorator();

        String graphId = "gid"; // default graph id.

        if ( StringUtils.isNotEmpty( graphDecorator.getTitle() ) )
        {
            graphId = toVizId( graphId );
        }

        dot.println( "// Auto generated dot file from plexus-graphing-graphviz." );

        dot.println( "digraph " + graphId + " {" );

        dot.println( "" );

        prepareDefaults( graphModel, dot );

        Iterator it;

        it = graphModel.getNodes().iterator();
        while ( it.hasNext() )
        {
            Node node = (Node) it.next();

            writeNode( dot, node );
        }

        it = graphModel.getEdges().iterator();
        while ( it.hasNext() )
        {
            Edge edge = (Edge) it.next();

            writeEdge( dot, edge );
        }

        dot.println( "}" );

        dot.flush();
        fwriter.flush();
        return dotFile;
    }

    private void writeNode( PrintWriter dot, Node node )
        throws GraphingException
    {
        if ( StringUtils.isEmpty( node.getLabel() ) )
        {
            throw new GraphingException( "Node.label must be provided." );
        }

        dot.println( "" );
        dot.println( "  // Node" );
        dot.println( "  \"" + toVizId( node ) + "\" [" );
        if ( StringUtils.isNotEmpty( node.getLabel() ) )
        {
            dot.println( "    label=\"" + StringUtils.escape( node.getLabel() ) + "\"," );
        }
        
        NodeDecorator decorator = node.getDecorator();

        if(decorator != null)
        {
            if ( decorator.getBorderColor() != null )
            {
                dot.println( "    color=\"" + ColorUtil.toCssDeclaration( decorator.getBorderColor() ) + "\"" );
            }
            if ( decorator.getBackgroundColor() != null )
            {
                dot.println( "    style=filled," );
                dot.println( "    fillcolor=\"" + ColorUtil.toCssDeclaration( decorator.getBackgroundColor() ) + "\"" );
            }
            if ( decorator.getLabelColor() != null )
            {
                dot.println( "    fontcolor=\"" + ColorUtil.toCssDeclaration( decorator.getLabelColor() ) + "\"" );
            }
        }

        dot.println( "    shape=box" );
        dot.println( "  ];" );
    }

    private void writeEdge( PrintWriter dot, Edge edge )
    {
        dot.println( "" );
        dot.println( "  // Edge" );
        dot.println( "  \"" + toVizId( edge.getFrom() ) + "\" -> \"" + toVizId( edge.getTo() ) + "\" [" );

        EdgeDecorator decorator = edge.getDecorator();
        if ( decorator != null )
        {
            switch ( decorator.getStyle() )
            {
                case EdgeDecorator.BOLD:
                    dot.println( "    style=\"bold\"," );
                    break;
                case EdgeDecorator.DASHED:
                    dot.println( "    style=\"dotted\"," );
                    break;
            }

            if ( decorator.getLineColor() != null )
            {
                dot.println( "    color=\"" + ColorUtil.toCssDeclaration( decorator.getLineColor() ) + "\"," );
            }

            if ( StringUtils.isNotEmpty( decorator.getLineLabel() ) )
            {
                dot.println( "    label=\"" + StringUtils.escape( decorator.getLineLabel() ) + "\"," );
                dot.println( "    fontname=\"Helvetica\"," );
                dot.println( "    fontsize=\"8\"," );
            }

            dot.println( "    arrowtail=" + getLineEndingName( decorator.getLineTail() ) + "," );
            dot.println( "    arrowhead=" + getLineEndingName( decorator.getLineHead() ) );
        }

        dot.println( "  ];" );
    }

    private Map vizIdMap = new HashMap();

    private String toVizId( Node node )
    {
        if ( node == null )
        {
            return "";
        }

        if ( StringUtils.isEmpty( node.getLabel() ) )
        {
            return "";
        }

        String id = (String) vizIdMap.get( node.getLabel() );
        if ( id == null )
        {
            id = toVizId( node.getLabel() );
            vizIdMap.put( node.getLabel(), id );
        }

        return id;
    }

    private String toVizId( String raw )
    {
        StringBuffer id = new StringBuffer();

        for ( int i = 0; i < raw.length(); i++ )
        {
            char c = raw.charAt( i );
            if ( Character.isLetterOrDigit( c ) )
            {
                id.append( Character.toUpperCase( c ) );
            }
            else if ( ( c == '-' ) || ( c == '_' ) )
            {
                id.append( "_" );
            }
        }

        return id.toString();
    }

    private void prepareDefaults( Graph graphModel, PrintWriter dot )
    {
        // Graph Defaults.
        
        GraphDecorator decorator = graphModel.getDecorator();

        dot.println( "  // Graph Defaults" );
        dot.println( "  graph [" );

        if ( decorator.getBackgroundColor() != null )
        {
            dot.println( "    bgcolor=\"" + ColorUtil.toCssDeclaration( decorator.getBackgroundColor() ) + "\"," );
        }

        if ( StringUtils.isNotEmpty( decorator.getTitle() ) )
        {
            dot.println( "    fontname=\"Helvetica\"," );
            dot.println( "    fontsize=\"14\"," );
            dot.println( "    label=\"" + StringUtils.escape( decorator.getTitle() ) + "\"," );
            dot.println( "    labeljust=\"l\"" );
        }

        if ( decorator.getTitleColor() != null )
        {
            dot.println( "    fontcolor=\"" + ColorUtil.toCssDeclaration( decorator.getTitleColor() ) + "\"," );
        }

        switch ( decorator.getOrientation() )
        {
            case GraphDecorator.WIDE:
                dot.println( "    rankdir=\"LR\"" );
            case GraphDecorator.TALL:
            default:
                dot.println( "    rankdir=\"TB\"" );
                break;
        }

        dot.println( "  ];" );

        // Node Defaults.

        dot.println( "" );
        dot.println( "  // Node Defaults." );
        dot.println( "  node [" );
        dot.println( "    fontname=\"Helvetica\"," );
        dot.println( "    fontsize=\"11\"," );
        dot.println( "    shape=\"plaintext\"" );
        dot.println( "  ];" );

        // Edge Defaults.

        dot.println( "" );
        dot.println( "  // Edge Defaults." );
        dot.println( "  edge [" );
        dot.println( "    arrowsize=\"0.8\"" );
        dot.println( "  ];" );
    }

    private String getLineEndingName( int lineending )
    {
        switch ( lineending )
        {
            case EdgeDecorator.ARROW:
                return "normal";

            case EdgeDecorator.DOT:
                return "dot";

            case EdgeDecorator.HOLLOW_DOT:
                return "odot";

            case EdgeDecorator.INVERT_ARROW:
                return "inv";

            case EdgeDecorator.INVERT_ARROW_DOT:
                return "invdot";

            case EdgeDecorator.INVERT_ARROW_HOLLOW_DOT:
                return "invodot";

            case EdgeDecorator.NONE:
            default:
                return "none";
        }
    }
}
