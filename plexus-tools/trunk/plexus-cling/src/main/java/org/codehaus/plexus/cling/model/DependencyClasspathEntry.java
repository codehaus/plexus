/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.plexus.cling.configuration.CLIngConfiguration;

/**
 * @author jdcasey
 */
public class DependencyClasspathEntry
    implements ClasspathEntry, ResolvedClasspathEntry
{
    private transient URL artifactUrl;
    private final String version;
    private final String artifactId;
    private final String groupId;
    
    public DependencyClasspathEntry(String groupId, String artifactId, String version)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
    
    public void resolve(ArtifactResolver resolver, CLIngConfiguration config) 
    throws ArtifactResolutionException, MalformedURLException 
    {
        Artifact dep = new DefaultArtifact(groupId, artifactId, version, "jar");
        dep = resolver.resolve(dep, config.getRemoteRepositories(), config.getLocalRepository());
        
        this.artifactUrl = dep.getFile().toURL();
    }

    public URL getURL()
    {
        if(artifactUrl == null) {
            throw new IllegalStateException("dependency has not yet been resolved");
        }
        else {
            return artifactUrl;
        }
    }

}
