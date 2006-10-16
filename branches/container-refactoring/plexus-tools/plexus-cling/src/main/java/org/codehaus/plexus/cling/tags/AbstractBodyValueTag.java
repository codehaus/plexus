/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.tags;

import org.codehaus.marmalade.el.ExpressionEvaluationException;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;

/**
 * @author jdcasey
 */
public abstract class AbstractBodyValueTag
    extends AbstractMarmaladeTag
{

    protected boolean alwaysProcessChildren()
    {
        return false;
    }
    
    protected boolean preserveBodyWhitespace( MarmaladeExecutionContext arg0 ) throws ExpressionEvaluationException
    {
        return false;
    }
}
