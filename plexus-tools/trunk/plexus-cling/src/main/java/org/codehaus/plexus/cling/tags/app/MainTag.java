/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.model.DefaultMain;
import org.codehaus.plexus.cling.model.Main;

/**
 * @author jdcasey
 */
public class MainTag
    extends AbstractMarmaladeTag
{
    
    private DefaultMain main = new DefaultMain();
    
    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        processChildren(context);
        
        AppTag parent = (AppTag)requireParent(AppTag.class);
        parent.setApplicationMain(main);
    }
    
    public void setMainClass(String mainClass) 
    {
        main.setMainClass(mainClass);
    }
    
    public void setMainMethod(String mainMethod)
    {
        main.setMainMethod(mainMethod);
    }
}
