package org.codehaus.plexus.builder;

/*
 * LICENSE
 */

import org.apache.maven.artifact.MavenArtifact;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MissingArtifactException
    extends PlexusRuntimeBuilderException
{
    public MissingArtifactException( MavenArtifact artifact )
    {
        super( "Missing artifact: " + artifact.getDependency().getId() + ", path: " + artifact.getPath() );
    }
}
