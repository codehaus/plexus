/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class OptionShortNameTag
    extends AbstractMarmaladeTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String shortNameStr = (String)getBody(context, String.class);
        
        if(shortNameStr == null || shortNameStr.length() < 1) {
            throw new MarmaladeExecutionException("option short name cannot be empty");
        }
        
        OptionTag parent = (OptionTag)requireParent(OptionTag.class);
        parent.setShortName(new Character(shortNameStr.charAt(0)));
    }
}
