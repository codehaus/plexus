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
public class DefaultCLIngConfiguration extends AbstractCLIngConfiguration
    implements CLIngConfiguration
{
    
    public DefaultCLIngConfiguration() throws MalformedURLException
    {
        String userHome = System.getProperty("user.home");
        File homeDir = new File(userHome);
        File repoLocation = new File(homeDir, ".CLIng/repository");
        
        setLocalRepository(new ArtifactRepository("local", "file://" + repoLocation.getAbsolutePath()));
        
        Set remoteRepositories = new HashSet();
        
        remoteRepositories.add(new ArtifactRepository("remote-0", "http://repository.codehaus.org"));
        
        setRemoteRepositories(remoteRepositories);
    }

}
