/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.configuration;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class DefaultCLIngConfigurationTest
    extends MockObjectTestCase
{
    
    public void testShouldConstructWithNoArgs() throws MalformedURLException {
        DefaultCLIngConfiguration config = new DefaultCLIngConfiguration();
    }
    
    public void testShouldHaveNonNullLocalRepo() throws MalformedURLException {
        DefaultCLIngConfiguration config = new DefaultCLIngConfiguration();
        assertNotNull(config.getLocalRepository());
    }

    public void testShouldHaveRemoteRepoSetOfSizeOne() throws MalformedURLException {
        DefaultCLIngConfiguration config = new DefaultCLIngConfiguration();
        assertEquals(1, config.getRemoteRepositories().size());
    }
    
}
