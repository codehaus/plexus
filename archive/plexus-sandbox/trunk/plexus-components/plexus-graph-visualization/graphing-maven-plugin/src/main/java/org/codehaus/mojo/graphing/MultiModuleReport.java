package org.codehaus.mojo.graphing;

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

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.StringUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * MultiModuleDumpMojo 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @goal multimodule
 * @aggregator
 */
public class MultiModuleReport
    extends AbstractMavenReport
    implements Contextualizable
{
    /**
     * The projects in the current build. Each of these is subject to refreshing.
     * 
     * @parameter default-value="${reactorProjects}"
     * @required
     * @readonly
     */
    private List projects;

    /**
     * @parameter expression="${graphing.implementation}" default-value="graphviz"
     */
    private String graphingImpl;

    /**
     * @parameter expression="${graphing.ignoreVersions}" default-value="true"
     */
    private boolean ignoreVersions;

    /**
     * @parameter expression="${graphing.filterTests}" default-value="true"
     */
    private boolean filterTests;

    private PlexusContainer container;

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Found " + projects.size() + " Project(s)" );

        GraphRenderer graphRenderer;

        try
        {
            graphRenderer = (GraphRenderer) container.lookup( GraphRenderer.ROLE, graphingImpl );
        }
        catch ( ComponentLookupException e )
        {
            graphRenderer = null;
            getLog().error( "GraphRenderer implementation [" + graphingImpl + "] does not exist.", e );
        }

        Graph graph = new Graph();

        try
        {
            Iterator it = projects.iterator();
            while ( it.hasNext() )
            {
                MavenProject project = (MavenProject) it.next();
                List deps = project.getDependencies();

                if ( !StringUtils.equals( "pom", project.getPackaging() ) )
                {
                    Node currentNode = graph.addNode( toNode( project ) );
                    getLog().info( "   Project: " + project.getId() + "  - " + deps.size() + " dep(s)" );

                    addDependenciesToGraph( graph, currentNode, deps );
                }
            }

            if ( graph.getDecorator() == null )
            {
                graph.setDecorator( new GraphDecorator() );
            }

            graph.getDecorator().setTitle( "Module Relationship" );
            graph.getDecorator().setOrientation( GraphDecorator.LEFT_TO_RIGHT );

            graphRenderer.render( graph, new File( "target/graph-multimodule.png" ) );
        }
        catch ( GraphConstraintException e )
        {
            getLog().error( "Unable to generate graph." );
        }
        catch ( IOException e )
        {
            getLog().error( "Unable to generate graph.", e );
        }
        catch ( GraphingException e )
        {
            getLog().error( "Unable to generate graph.", e );
        }
    }

    private void addDependenciesToGraph( Graph graph, Node currentNode, List deps )
        throws GraphConstraintException
    {
        Iterator it = deps.iterator();
        while ( it.hasNext() )
        {
            Dependency dep = (Dependency) it.next();

            boolean isModule = isMultiModuleDependency( dep );

            if ( filterTests && isTestDep( dep ) )
            {
                // Skip
                continue;
            }

            if ( isModule )
            {
                Node depNode = graph.addNode( toNode( dep ) );
                Edge edge = graph.addEdge( currentNode, depNode );

                if ( isTestDep( dep ) )
                {
                    if ( edge.getDecorator() == null )
                    {
                        edge.setDecorator( new EdgeDecorator() );
                    }
                    edge.getDecorator().setLineColor( Color.blue );

                    Color testColor = new Color( 200, 200, 255 );

                    if ( depNode.getDecorator() == null )
                    {
                        depNode.setDecorator( new NodeDecorator() );
                    }

                    depNode.getDecorator().setBackgroundColor( testColor );
                    depNode.getDecorator().setBorderColor( testColor );

                    graph.addNode( depNode );
                }
            }

            getLog().info( "     " + ( isModule ? "* " : "  " ) + dep );
        }
    }

    private boolean isMultiModuleDependency( Dependency dep )
    {
        boolean ret = false;

        Iterator it = projects.iterator();
        while ( it.hasNext() )
        {
            MavenProject project = (MavenProject) it.next();
            if ( StringUtils.equals( project.getGroupId(), dep.getGroupId() )
                && StringUtils.equals( project.getArtifactId(), dep.getArtifactId() )
                && StringUtils.equals( project.getPackaging(), dep.getType() ) )
            {
                // Found dep that matches on groupId / artifactId / type only.
                if ( ignoreVersions )
                {
                    // No test of version.
                    ret = true;
                    break;
                }
                else if ( StringUtils.equals( project.getVersion(), dep.getVersion() ) )
                {
                    // Found dep that matches on version too.
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    private boolean isTestDep( Dependency dep )
    {
        return StringUtils.equals( "test", dep.getScope() );
    }

    private Node toNode( Dependency dep )
    {
        return toNode( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType() );
    }

    private Node toNode( MavenProject project )
    {
        return toNode( project.getGroupId(), project.getArtifactId(), project.getVersion(), project.getPackaging() );
    }

    private Node toNode( String groupId, String artifactId, String version, String type )
    {
        StringBuffer label = new StringBuffer();
        label.append( groupId ).append( "\n" );
        label.append( artifactId ).append( "\n" );

        if ( !ignoreVersions )
        {
            label.append( version ).append( "\n" );
        }

        label.append( type );

        return new Node( label.toString() );
    }

    protected void executeReport( Locale locale ) throws MavenReportException
    {
        // TODO Auto-generated method stub
        
    }

    protected String getOutputDirectory()
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected MavenProject getProject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected Renderer getSiteRenderer()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescription( Locale locale )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName( Locale locale )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getOutputName()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
