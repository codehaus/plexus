package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2004, Codehaus.org
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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

/**
 * @goal test-runtime
 *
 * @requiresDependencyResolution
 *
 * @description Generates a complete runtime with the application and it's requires services.
 *
 * @parameter name="basedir"
 * type="String"
 * required="true"
 * validator=""
 * expression="#basedir"
 * description=""
 *
 * @parameter name="finalName"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#maven.final.name"
 * description=""
 *
 * @parameter name="artifacts"
 * type="java.util.Set"
 * required="true"
 * validator=""
 * expression="#project.artifacts"
 * description=""
 *
 * @parameter name="testRuntimeConfiguration"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#plexus.runtime.configuration"
 * description=""
 *
 * @parameter name="runtimeBuilder"
 * type="org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
 * required="true"
 * validator=""
 * expression="#component.org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
 * description=""
 *
 * @parameter name="localRepository"
 * type="org.apache.maven.artifact.ArtifactRepository"
 * required="true"
 * validator=""
 * expression="#localRepository"
 * description=""
 *
 * @parameter name="remoteRepositories"
 * type="java.util.List"
 * required="true"
 * validator=""
 * expression="#project.remoteArtifactRepositories"
 * description=""
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusTestRuntimeGenerator
    extends AbstractPlugin
{
    public void execute( PluginExecutionRequest request, PluginExecutionResponse response )
        throws Exception
    {
        String basedir = (String) request.getParameter( "basedir" );

        String finalName = (String) request.getParameter( "finalName" );

        Set projectArtifacts = (Set) request.getParameter( "artifacts" );

        String testRuntimeConfiguration = (String) request.getParameter( "testRuntimeConfiguration" );

        PlexusRuntimeBuilder runtimeBuilder = (PlexusRuntimeBuilder) request.getParameter( "runtimeBuilder" );

        ArtifactRepository localRepository = (ArtifactRepository) request.getParameter( "localRepository" );

        List remoteRepositories = (List) request.getParameter( "remoteRepositories" );

        // ----------------------------------------------------------------------
        // Build the runtime
        // ----------------------------------------------------------------------

        File runtimeRoot = new File( basedir, "plexus-test-runtime" );

        runtimeBuilder.build( runtimeRoot,
                              remoteRepositories, localRepository, projectArtifacts,
                              new File( testRuntimeConfiguration ), null );

        // ----------------------------------------------------------------------
        // Copy the application
        // ----------------------------------------------------------------------

        File applicationJarFile = PlexusBundleApplicationMojo.getApplicationJarFile( basedir, finalName );

        if ( !applicationJarFile.canRead() )
        {
            throw new Exception( "Can't read Plexus application artifact '" + applicationJarFile.getAbsolutePath() + "'." );
        }

        runtimeBuilder.addPlexusApplication( applicationJarFile, runtimeRoot );

        // ----------------------------------------------------------------------
        // Copy any services
        // ----------------------------------------------------------------------

        for ( Iterator it = projectArtifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            if( artifact.getType().equals( "plexus-service" ) )
            {
                runtimeBuilder.addPlexusService( artifact.getFile(), runtimeRoot );
            }
        }
    }
}
