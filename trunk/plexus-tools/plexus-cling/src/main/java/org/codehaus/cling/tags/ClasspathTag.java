/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.cling.model.Classpath;
import org.codehaus.cling.model.DefaultClasspath;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class ClasspathTag
    extends AbstractMarmaladeTag
{
    
    private Classpath classpath = new DefaultClasspath();
    
    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        processChildren(context);
        
        AppTag parent = (AppTag)requireParent(AppTag.class);
        parent.setClasspath(classpath);
    }

    public void addClassEntry(URL entry) {
        classpath.addEntry(entry);
    }

}
