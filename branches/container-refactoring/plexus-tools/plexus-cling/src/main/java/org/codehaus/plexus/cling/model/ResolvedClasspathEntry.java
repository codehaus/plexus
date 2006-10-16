/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.model;

import java.net.MalformedURLException;

import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.plexus.cling.configuration.CLIngConfiguration;

/**
 * @author jdcasey
 */
public interface ResolvedClasspathEntry
{
    
    public void resolve(ArtifactResolver resolver, CLIngConfiguration config)
    throws ArtifactResolutionException, MalformedURLException;

}
