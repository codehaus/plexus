/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class DependencyVersionTag
    extends AbstractBodyValueTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String version = (String)getBody(context, String.class);
        
        if(version == null || version.length() < 1) {
            throw new MarmaladeExecutionException("dependency version cannot be empty");
        }
        
        DependencyTag parent = (DependencyTag)requireParent(DependencyTag.class);
        parent.setVersion(version);
    }
}
