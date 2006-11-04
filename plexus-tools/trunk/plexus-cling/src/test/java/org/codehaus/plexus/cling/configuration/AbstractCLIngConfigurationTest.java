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
public class AbstractCLIngConfigurationTest
    extends MockObjectTestCase
{

        public void testShouldOverrideWithRemoteRepoOfSizeTwo() throws MalformedURLException{
            TestCLIngConfiguration config = new TestCLIngConfiguration();
            
            Mock configMock = mock(CLIngConfiguration.class);
            
            Set repoSet = new HashSet();
            repoSet.add(new ArtifactRepository("1", "file:/tmp"));
            repoSet.add(new ArtifactRepository("2", "file:/tmp"));
            
            configMock.expects(once()).method("getLocalRepository").withNoArguments().will(returnValue(null));
            configMock.expects(once()).method("getRemoteRepositories").withNoArguments().will(returnValue(repoSet));
            
            config.overrideWith((CLIngConfiguration)configMock.proxy());
            
            assertEquals(2, config.getRemoteRepositories().size());
        }

        public void testShouldNotOverrideWithRemoteRepoOfSizeZero() throws MalformedURLException{
            TestCLIngConfiguration config = new TestCLIngConfiguration();
            
            Mock configMock = mock(CLIngConfiguration.class);
            
            Set repoSet = new HashSet();
            
            configMock.expects(once()).method("getLocalRepository").withNoArguments().will(returnValue(null));
            configMock.expects(once()).method("getRemoteRepositories").withNoArguments().will(returnValue(repoSet));
            
            config.overrideWith((CLIngConfiguration)configMock.proxy());
            
            assertEquals(1, config.getRemoteRepositories().size());
        }

        public void testShouldOverrideWithNewLocalRepo() throws MalformedURLException{
            TestCLIngConfiguration config = new TestCLIngConfiguration();
            
            Mock configMock = mock(CLIngConfiguration.class);
            
            ArtifactRepository repo = new ArtifactRepository("new", "file:/tmp");
            configMock.expects(once()).method("getLocalRepository").withNoArguments().will(returnValue(repo));
            configMock.expects(once()).method("getRemoteRepositories").withNoArguments().will(returnValue(null));
            
            config.overrideWith((CLIngConfiguration)configMock.proxy());
            
            assertEquals(repo.getId(), config.getLocalRepository().getId());
        }

        private static final class TestCLIngConfiguration extends AbstractCLIngConfiguration
        {
            TestCLIngConfiguration(){
                Set remotes = new HashSet();
                remotes.add(new ArtifactRepository("test-remote", "file:/tmp"));
                
                setRemoteRepositories(remotes);
            }
        }
}
