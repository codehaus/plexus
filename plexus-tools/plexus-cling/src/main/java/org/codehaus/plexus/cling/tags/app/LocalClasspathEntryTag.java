/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import java.io.File;
import java.net.MalformedURLException;

import org.codehaus.marmalade.el.ExpressionEvaluationException;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.model.LocalClasspathEntry;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class LocalClasspathEntryTag
    extends AbstractBodyValueTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String location = (String)getBody(context, String.class);
        if(location == null || location.length() < 1) {
            throw new MarmaladeExecutionException("local classpath entry location not specified");
        }
        
        File locationFile = new File(location);
        
        ClasspathTag parent = (ClasspathTag)requireParent(ClasspathTag.class);
        try
        {
            parent.addClasspathEntry(new LocalClasspathEntry(locationFile.toURL()));
        }
        catch ( MalformedURLException e )
        {
            throw new MarmaladeExecutionException("cannot create local location URL", e);
        }
    }
    
}
