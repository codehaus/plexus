/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.configuration;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class SupplementaryCLIngConfigurationTest
    extends TestCase
{

    public void testShouldConstructWithNoArgs() {
        SupplementaryCLIngConfiguration config = new SupplementaryCLIngConfiguration();
    }
    
    public void testShouldSetAndRetrieveNewLocalRepo() {
        SupplementaryCLIngConfiguration config = new SupplementaryCLIngConfiguration();
        
        ArtifactRepository newLocal = new ArtifactRepository("local-new", "file:/tmp");
        config.setLocalRepository(newLocal);
        
        assertEquals(newLocal.getId(), config.getLocalRepository().getId());
    }
    
    public void testShouldSetAndRetrieveNewRemoteRepoSet() {
        SupplementaryCLIngConfiguration config = new SupplementaryCLIngConfiguration();
        
        Set newRemotes = new HashSet();
        newRemotes.add(new ArtifactRepository("remote1", "file:/tmp"));
        newRemotes.add(new ArtifactRepository("remote2", "file:/tmp"));
        
        config.setRemoteRepositories(newRemotes);
        
        assertEquals(2, config.getRemoteRepositories().size());
    }
    
}
