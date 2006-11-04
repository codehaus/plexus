/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.plexus.cling.configuration.CLIngConfiguration;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class DependencyClasspathEntryTest
    extends MockObjectTestCase
{

    public void testShouldConstructWithGroupIdArtifactIdAndVersion() {
        DependencyClasspathEntry entry = new DependencyClasspathEntry("group", "artifact", "version");
    }
    
    public void testShouldFailToReturnURLBeforeResolved() {
        DependencyClasspathEntry entry = new DependencyClasspathEntry("group", "artifact", "version");
        
        try
        {
            entry.getURL();
            fail("Should fail because entry has not been resolved");
        }
        catch ( IllegalStateException e )
        {
            // should fail because the entry is not resolved.
        }
    }
    
    public void testShouldReturnURLAfterResolved() throws MalformedURLException, ArtifactResolutionException {
        DependencyClasspathEntry entry = new DependencyClasspathEntry("group", "artifact", "version");
        
        File artifactFile = new File("/tmp/artifact.jar");
        URL url = artifactFile.toURL();
        
        DefaultArtifact artifact = new DefaultArtifact("group", "artifact", "version", "jar");
        artifact.setPath(artifactFile.getPath());
        
        Mock resolverMock = mock(ArtifactResolver.class);
        resolverMock.expects(once()).method("resolve").with(isA(Artifact.class), isA(Set.class), isA(ArtifactRepository.class)).will(returnValue(artifact));
        
        ArtifactResolver resolver = (ArtifactResolver)resolverMock.proxy();
        
        Mock configMock = mock(CLIngConfiguration.class);
        configMock.expects(once()).method("getRemoteRepositories").withNoArguments().will(returnValue(Collections.EMPTY_SET));
        configMock.expects(once()).method("getLocalRepository").withNoArguments().will(returnValue(new ArtifactRepository("local", "file:/tmp")));
        
        CLIngConfiguration config = (CLIngConfiguration)configMock.proxy();
        
        entry.resolve(resolver, config);
        
        assertEquals(url, entry.getURL());
    }
    
}
