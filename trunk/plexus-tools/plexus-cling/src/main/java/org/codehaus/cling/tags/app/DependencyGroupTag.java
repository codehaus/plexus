/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class DependencyGroupTag
    extends AbstractMarmaladeTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String groupId = (String)getBody(context, String.class);
        
        if(groupId == null || groupId.length() < 1) {
            throw new MarmaladeExecutionException("dependency groupId cannot be empty");
        }
        
        DependencyTag parent = (DependencyTag)requireParent(DependencyTag.class);
        parent.setGroupId(groupId);
    }
}
