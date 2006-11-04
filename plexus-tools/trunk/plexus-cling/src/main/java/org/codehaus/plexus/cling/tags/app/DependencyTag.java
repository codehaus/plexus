/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import java.net.MalformedURLException;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.cling.CLIngConstants;
import org.codehaus.plexus.cling.model.AppModel;
import org.codehaus.plexus.cling.model.DependencyClasspathEntry;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author jdcasey
 */
public class DependencyTag
    extends AbstractMarmaladeTag
{

    private String groupId;
    private String artifactId;
    private String version;

    protected void doExecute( MarmaladeExecutionContext context ) throws MarmaladeExecutionException
    {
        processChildren( context );
        
        
        ClasspathTag parent = (ClasspathTag)requireParent(ClasspathTag.class);
        parent.addClasspathEntry(new DependencyClasspathEntry(groupId, artifactId, version));
    }
    
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }
    
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

}
