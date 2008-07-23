/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.tags.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class RemoteRepositorySetTag
    extends AbstractMarmaladeTag
{
    
    private Set remoteRepositories = new HashSet();
    
    protected void doExecute( MarmaladeExecutionContext context ) throws MarmaladeExecutionException
    {
        processChildren(context);
        
        ConfigTag parent = (ConfigTag)requireParent(ConfigTag.class);
        parent.getConfiguration().setRemoteRepositories(remoteRepositories);
    }
    
    public Set getRemoteRepositories()
    {
        return remoteRepositories;
    }
    
    public void addRemoteRepository(ArtifactRepository repository)
    {
        remoteRepositories.add(repository);
    }

}
