/* Created on Sep 13, 2004 */
package org.codehaus.cling.tags;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;

/**
 * @author jdcasey
 */
public class EnvironmentTag
    extends AbstractMarmaladeTag
{

    protected void doExecute( MarmaladeExecutionContext context )
    throws MarmaladeExecutionException
    {
        String propertiesSpec = (String)getBody(context, String.class);
        
        if(propertiesSpec != null && propertiesSpec.length() > 0) {
            ByteArrayInputStream bin = new ByteArrayInputStream(propertiesSpec.getBytes());
            Properties envars = new Properties();
            try
            {
                envars.load(bin);
            }
            catch ( IOException e )
            {
                throw new MarmaladeExecutionException("cannot read envars from inline properties", e);
            }
            
            Properties finalEnvars = new Properties(System.getProperties());
            finalEnvars.putAll(envars);
            
            AppTag parent = (AppTag)requireParent(AppTag.class);
            parent.setApplicationEnvironment(finalEnvars);
        }
    }
}
