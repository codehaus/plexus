package org.codehaus.plexus.builder;

/*
 * LICENSE
 */

import org.apache.maven.artifact.Artifact;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MissingArtifactException
    extends PlexusRuntimeBuilderException
{
    public MissingArtifactException( Artifact artifact )
    {
        super( "Missing artifact: " + artifact.getId() + ", path: " + artifact.getPath() );
    }
}
