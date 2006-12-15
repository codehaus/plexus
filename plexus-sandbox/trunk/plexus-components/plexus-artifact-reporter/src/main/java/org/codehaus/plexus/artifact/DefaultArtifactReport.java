/**
 *
 * Copyright 2006
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

package org.codehaus.plexus.artifact;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.velocity.VelocityComponent;
import org.codehaus.plexus.velocity.DefaultVelocityComponent;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;

import java.io.StringWriter;

/**
 * @author John Tolentino
 */
public class DefaultArtifactReport
    extends AbstractLogEnabled
    implements ArtifactReport, Startable
{
    private String artifactName;

    private String artifactGroupId;

    private String artifactId;

    private String artifactVersion;

    private String artifactRepository;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
    {
        getLogger().info( "Starting DefaultArtifactReport component." );
    }

    public void stop()
    {
        getLogger().info( "Stopping DefaultArtifactReport component." );
    }

    // ----------------------------------------------------------------------
    // ArtifactReport Implementation
    // ----------------------------------------------------------------------

    public void generate( VelocityComponent velocityComponent )
        throws Exception
    {
        VelocityContext context = new VelocityContext();

        context.put( "artifactName", artifactName );

        context.put( "artifactGroupId", artifactGroupId );

        context.put( "artifactId", artifactId );

        context.put( "artifactVersion", artifactVersion );

        context.put( "artifactRepository", artifactRepository );

        Template template = velocityComponent.getEngine().getTemplate( "target/classes/org/codehaus/plexus/artifact/ArtifactReport.vm" );

        StringWriter writer = new StringWriter();

        template.merge( context, writer );

        System.out.println( writer.toString() );

    }

}
