/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.model.Classpath;
import org.codehaus.plexus.cling.model.ClasspathEntry;
import org.codehaus.plexus.cling.model.DefaultClasspath;

/**
 * @author jdcasey
 */
public class ClasspathTag
    extends AbstractMarmaladeTag
{
    
    private DefaultClasspath classpath = new DefaultClasspath();
    
    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        processChildren(context);
        
        AppTag parent = (AppTag)requireParent(AppTag.class);
        parent.setClasspath(classpath);
    }

    public void addClasspathEntry(ClasspathEntry entry) {
        classpath.addEntry(entry);
    }

}
