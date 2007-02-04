package org.codehaus.plexus.maven.plugin;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * @author Jason van Zyl
 */
public abstract class AbstractAppServerMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${basedir}"
     * @required
     */
    protected File basedir;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File target;

    /**
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    protected String finalName;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     */
    protected List remoteRepositories;

    /**
     * @parameter expression="${localRepository}"
     * @required
     */
    protected ArtifactRepository localRepository;

    /**
     * @parameter expression="${project.artifacts}"
     * @readonly
     * @required
     */
    protected Set projectArtifacts;

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder}"
     * @required
     * @readonly
     */
    protected PlexusRuntimeBuilder runtimeBuilder;

    /**
     * @parameter expression="${project.build.directory}/plexus-test-runtime"
     * @required
     */
    protected File runtimePath;
}
