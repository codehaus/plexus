/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.tags.config;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class RemoteRepositoryTag
    extends AbstractBodyValueTag
{
    private static int repoIdCounter = 0;

    protected void doExecute( MarmaladeExecutionContext context ) throws MarmaladeExecutionException
    {
        String location = (String)getBody(context, String.class);
        
        if(location == null || location.length() < 1) {
            throw new MarmaladeExecutionException("Invalid remote artifact repository");
        }
        
        ArtifactRepository remoteRepository = new ArtifactRepository("remote-" + repoIdCounter++, location);
        
        RemoteRepositorySetTag parent = (RemoteRepositorySetTag)requireParent(RemoteRepositorySetTag.class);
        parent.addRemoteRepository(remoteRepository);
    }
}
