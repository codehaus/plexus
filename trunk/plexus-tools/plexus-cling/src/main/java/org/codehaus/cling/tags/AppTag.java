/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags;

import java.util.Properties;

import org.codehaus.cling.model.AppModel;
import org.codehaus.cling.model.Classpath;
import org.codehaus.cling.model.DefaultApplication;
import org.codehaus.cling.model.DefaultLegalUsage;
import org.codehaus.cling.model.LegalUsage;
import org.codehaus.cling.model.Main;
import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class AppTag
    extends AbstractMarmaladeTag
{
    
    private AppModel app = new DefaultApplication();
    
    protected void doExecute( MarmaladeExecutionContext context ) 
    throws MarmaladeExecutionException
    {
    }
    
    public AppModel getAppModel()
    {
        return app;
    }

    public void setApplicationMain( Main main )
    {
        app.setMain(main);
    }

    public void setApplicationEnvironment( Properties environment )
    {
        app.setEnvironment(environment);
    }

    public void setClasspath( Classpath classpath )
    {
        app.setClasspath(classpath);
    }

    public void setApplicationLegalUsage( LegalUsage usage )
    {
        app.setLegalUsage(usage);
    }
    
}
