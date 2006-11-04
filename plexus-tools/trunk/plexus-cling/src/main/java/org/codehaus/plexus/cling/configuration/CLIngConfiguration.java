/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.configuration;

import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * @author jdcasey
 */
public interface CLIngConfiguration
{
    
    public ArtifactRepository getLocalRepository();
    
    public Set getRemoteRepositories();
    
    public void overrideWith(CLIngConfiguration otherConfig);

}
