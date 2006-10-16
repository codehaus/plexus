/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class MainClassTag
    extends AbstractBodyValueTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String mainClass = (String)getBody(context, String.class);
        
        if(mainClass == null || mainClass.length() < 1) {
            throw new MarmaladeExecutionException("main class cannot be empty");
        }
        
        MainTag parent = (MainTag)requireParent(MainTag.class);
        parent.setMainClass(mainClass);
    }
}
