package org.codehaus.plexus;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/** This component implements all the start and stop phases:
 *
 *  LogEnabled
 *  Contexualize
 *  Serviceable
 *  Configurable
 *  Initializable
 *  Startable
 *
 *  Disposable
 *
 */
public class DefaultConfigureService
    extends AbstractLogEnabled
    implements ConfigureService, Configurable
{

    boolean configure = false;
    
    boolean blah = false;
    
    boolean blahHasChildren = false;
    
    boolean blehHasAttribute = false;

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        configure = true;
        
        Configuration blahConfig = configuration.getChild("blah");
        
        blah = true;
        
        Configuration[] children = blahConfig.getChildren("child");
        
        if ( children.length == 2 )
            blahHasChildren = true;
        
        String bleh = configuration.getChild("bleh").getAttribute("name");
        
        if ( bleh.equals("bleh") )
            blehHasAttribute = true;
    }
}
