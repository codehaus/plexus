/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.cli.InvocationTemplate;
import org.codehaus.plexus.cling.model.DefaultLegalUsage;

/**
 * @author jdcasey
 */
public class LegalUsageTag
    extends AbstractMarmaladeTag
{
    
    private DefaultLegalUsage usage = new DefaultLegalUsage();
    
    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        processChildren(context);
        
        AppTag parent = (AppTag)requireParent(AppTag.class);
        parent.setApplicationLegalUsage(usage);
    }
    
    public void addOptionSet(InvocationTemplate template) {
        usage.addInvocationTemplate(template);
    }

}
