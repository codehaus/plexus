/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags;

import org.codehaus.cling.model.DefaultMain;
import org.codehaus.cling.model.Main;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class MainTag
    extends AbstractMarmaladeTag
{
    
    private String mainClass;
    private String mainMethod;
    
    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        processChildren(context);
        
        Main main = new DefaultMain(mainClass, mainMethod);
        
        AppTag parent = (AppTag)requireParent(AppTag.class);
        parent.setApplicationMain(main);
    }
    
    public void setMainClass(String mainClass) 
    {
        this.mainClass = mainClass;
    }
    
    public void setMainMethod(String mainMethod)
    {
        this.mainMethod = mainMethod;
    }
}
