/* Created on Sep 15, 2004 */
package org.codehaus.cling.configuration;

import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * @author jdcasey
 */
public class SupplementaryCLIngConfiguration
    implements CLIngConfiguration
{
    
    private ArtifactRepository localRepository;
    private Set remoteRepositories;
    
    public void setLocalRepository(ArtifactRepository localRepository)
    {
        this.localRepository = localRepository;
    }

    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }
    
    public void setRemoteRepositories(Set remoteRepositories)
    {
        this.remoteRepositories = remoteRepositories;
    }

    public Set getRemoteRepositories()
    {
        return remoteRepositories;
    }

    public void overrideWith( CLIngConfiguration otherConfig )
    {
        ArtifactRepository otherLocal = otherConfig.getLocalRepository();
        if(otherLocal != null) {
            this.localRepository = otherLocal;
        }
        
        Set otherRemoteRepos = otherConfig.getRemoteRepositories();
        if(otherRemoteRepos != null) {
            this.remoteRepositories = otherRemoteRepos;
        }
    }

}
