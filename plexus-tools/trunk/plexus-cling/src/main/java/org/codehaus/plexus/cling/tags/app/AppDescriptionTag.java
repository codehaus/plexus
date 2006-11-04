/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class AppDescriptionTag
    extends AbstractBodyValueTag
{

    protected void doExecute( MarmaladeExecutionContext context ) throws MarmaladeExecutionException
    {
        String description = (String)getBody(context, String.class);
        if(description == null || description.length() < 1) {
            throw new MarmaladeExecutionException("App description requires a value");
        }
        
        AppTag parent = (AppTag)requireParent(AppTag.class);
        parent.setApplicationDescription(description);
    }
}
