/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class MainMethodTag
    extends AbstractBodyValueTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String mainMethod = (String)getBody(context, String.class);
        
        if(mainMethod == null || mainMethod.length() < 1) {
            throw new MarmaladeExecutionException("main method cannot be empty");
        }
        
        MainTag parent = (MainTag)requireParent(MainTag.class);
        parent.setMainMethod(mainMethod);
    }
}
