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
public class LocalRepositoryTag
    extends AbstractBodyValueTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String location = (String)getBody(context, String.class);
        
        if(location == null || location.length() < 1) {
            throw new MarmaladeExecutionException("Invalid local artifact repository");
        }
        
        ArtifactRepository localRepo = new ArtifactRepository("local", location);
        
        ConfigTag parent = (ConfigTag)requireParent(ConfigTag.class);
        parent.getConfiguration().setLocalRepository(localRepo);
    }
}
