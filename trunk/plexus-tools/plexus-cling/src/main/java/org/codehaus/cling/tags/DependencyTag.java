/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags;

import java.net.MalformedURLException;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.cling.CLIngConstants;
import org.codehaus.cling.model.AppModel;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.PlexusContainer;
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
        
        PlexusContainer container = (PlexusContainer) context.getVariable(
            CLIngConstants.PLEXUS_CONTAINER_CONTEXT_KEY, getExpressionEvaluator());
        
        ArtifactResolver resolver = null;
        try
        {
            resolver = (ArtifactResolver) container.lookup(ArtifactResolver.ROLE);
        }
        catch ( ComponentLookupException e )
        {
            throw new MarmaladeExecutionException("cannot retrieve artifact resolver", e);
        }
        
        Set remoteRepos = (Set)context.getVariable(CLIngConstants.REMOTE_REPOSITORIES_CONTEXT_KEY, getExpressionEvaluator());
        ArtifactRepository localRepo = (ArtifactRepository)context.getVariable(CLIngConstants.LOCAL_REPOSITORY_CONTEXT_KEY, getExpressionEvaluator());
        
        Artifact artifact = new DefaultArtifact(groupId, artifactId, version, "jar", "jar");
        
        try
        {
            artifact = resolver.resolve(artifact, remoteRepos, localRepo);
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MarmaladeExecutionException("cannot resolve application dependency", e);
        }
        
        ClasspathTag parent = (ClasspathTag)requireParent(ClasspathTag.class);
        try
        {
            parent.addClassEntry(artifact.getFile().toURL());
        }
        catch ( MalformedURLException e )
        {
            throw new MarmaladeExecutionException("cannot convert artifact file to URL", e);
        }
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
