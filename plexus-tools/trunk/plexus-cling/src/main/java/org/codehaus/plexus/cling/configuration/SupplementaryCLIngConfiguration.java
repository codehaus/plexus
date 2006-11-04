/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.configuration;

import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * @author jdcasey
 */
public class SupplementaryCLIngConfiguration extends AbstractCLIngConfiguration
    implements CLIngConfiguration
{
    
    public void setLocalRepository(ArtifactRepository localRepository)
    {
        super.setLocalRepository(localRepository);
    }

    public void setRemoteRepositories(Set remoteRepositories)
    {
        super.setRemoteRepositories(remoteRepositories);
    }

}
