/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class OptionPropertyTag
    extends AbstractBodyValueTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String property = (String)getBody(context, String.class);
        
        if(property == null || property.length() < 1) {
            throw new MarmaladeExecutionException("option property cannot be empty");
        }
        
        OptionTag parent = (OptionTag)requireParent(OptionTag.class);
        parent.setObjectProperty(property);
    }
}
