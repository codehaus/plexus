/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class OptionLongNameTag
    extends AbstractMarmaladeTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String longName = (String)getBody(context, String.class);
        
        if(longName == null || longName.length() < 1) {
            throw new MarmaladeExecutionException("option long name cannot be empty");
        }
        
        OptionTag parent = (OptionTag)requireParent(OptionTag.class);
        parent.setLongName(longName);
    }
}
