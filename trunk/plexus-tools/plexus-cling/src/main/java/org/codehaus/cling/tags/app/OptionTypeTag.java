/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class OptionTypeTag
    extends AbstractMarmaladeTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String type = (String)getBody(context, String.class);
        
        if(type == null || type.length() < 1) {
            throw new MarmaladeExecutionException("option type cannot be empty");
        }
        
        OptionTag parent = (OptionTag)requireParent(OptionTag.class);
        parent.setType(type);
    }
}
