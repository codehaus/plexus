package org.codehaus.plexus.maven.plugin.webapp;

/*
 * Licensed to The Codehaus ( www.codehaus.org ) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Codehaus licenses this file
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.artifact.Artifact;

import java.util.Iterator;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @author Andrew Williams
 * @version $Id$
 * @goal add-webapps
 * @requiresDependencyResolution
 * @phase package
 * @description Adds all web applications in the dependencies to the Plexus runtime. This is used when
 * you are generating a Plexus runtime which houses several web applications.
 */
public class WebApplicationsRuntimePopulatorMojo
    extends AbstractWebApplicationRuntimePopulatorMojo
{
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            /* don't look for wars in transitive deps (plexus-applications will likely have some) */
            for ( Iterator it = project.getDependencyArtifacts().iterator(); it.hasNext(); )
            {
                Artifact artifact = (Artifact) it.next();

                if ( artifact.getType().equals( "war" ) )
                {
                    String context = webappMappings.getProperty( artifact.getGroupId() + ":" + artifact.getArtifactId(),
                                                          "/" + artifact.getArtifactId() );

                    File plexusApplication = wrapWebApplication( artifact.getFile(), context, webappPort );
                    runtimeBuilder.addPlexusApplication( plexusApplication, runtimePath );
                }
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error while building test runtimePath.", e );
        }
    }
}
