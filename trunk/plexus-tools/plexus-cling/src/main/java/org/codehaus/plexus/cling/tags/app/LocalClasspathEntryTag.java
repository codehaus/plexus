/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.tags.app;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.marmalade.el.ExpressionEvaluationException;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.CLIngConstants;
import org.codehaus.plexus.cling.model.LocalClasspathEntry;
import org.codehaus.plexus.cling.tags.AbstractBodyValueTag;

/**
 * @author jdcasey
 */
public class LocalClasspathEntryTag
    extends AbstractBodyValueTag
{
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String location = (String)getBody(context, String.class);
        if(location == null || location.length() < 1) {
            throw new MarmaladeExecutionException("local classpath entry location not specified");
        }
        
        if(!location.endsWith(FILE_SEPARATOR)) {
            location += FILE_SEPARATOR;
        }
        
        File locationFile = new File(location);

        try
        {
            locationFile = locationFile.getCanonicalFile();
        }
        catch ( IOException e )
        {
            throw new MarmaladeExecutionException("local classpath entry cannot be canonicalized");
        }

        String basedir = System.getProperty(CLIngConstants.APPDIR_SYSPROP);
        
        if(!locationFile.getAbsolutePath().startsWith(basedir)) {
            throw new MarmaladeExecutionException("local classpath entry must be within application basedir");
        }
        
        ClasspathTag parent = (ClasspathTag)requireParent(ClasspathTag.class);
        try
        {
            URL localUrl = locationFile.toURL();
            parent.addClasspathEntry(new LocalClasspathEntry(localUrl));
        }
        catch ( MalformedURLException e )
        {
            throw new MarmaladeExecutionException("cannot create local location URL", e);
        }
    }
    
}
