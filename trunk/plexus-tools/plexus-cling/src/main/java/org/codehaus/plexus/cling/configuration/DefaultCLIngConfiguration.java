/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;

/**
 * @author jdcasey
 */
public class DefaultCLIngConfiguration extends AbstractLogEnabled
    implements CLIngConfiguration
{
    
    private ArtifactRepository localRepository;
    private Set remoteRepositories;

    public DefaultCLIngConfiguration() throws MalformedURLException
    {
        String userHome = System.getProperty("user.home");
        File homeDir = new File(userHome);
        
        File repoLocation = new File(homeDir, ".CLIng/repository");
        
        this.localRepository = new ArtifactRepository("local", repoLocation.toURL().toExternalForm());
        
        this.remoteRepositories = new HashSet();
        
        remoteRepositories.add(new ArtifactRepository("remote-0", "http://repository.codehaus.org"));
    }

    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
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
