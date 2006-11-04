/* Created on Aug 6, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.net.URL;

/**
 * Component factory for reading marmalade scripts off of the classpath (realm)
 * and executing them. The root of the script should be a tag which implements
 * PlexusComponentTag, and the result of the script execution is the component.
 * Scripts are cached in the caching parser.
 * 
 * @author jdcasey
 */
public class MarmaladeClasspathComponentFactory
    extends AbstractMarmaladeComponentFactory
{

    protected URL getScriptLocation( ComponentDescriptor componentDescriptor, ClassRealm classRealm )
    {
        String impl = componentDescriptor.getImplementation();
        if ( !impl.startsWith( "/" ) )
        {
            impl = "/" + impl;
        }

        URL scriptLocation = classRealm.getResource( impl );

        if(scriptLocation == null)
        {
            System.out.println("Cannot find: " + impl + " in classpath:");
            for ( int i = 0; i < classRealm.getConstituents().length; i++ )
            {
                URL constituent = classRealm.getConstituents()[i];
                System.out.println("[" + i + "]  " + constituent);
            }
        }

        return scriptLocation;
    }

}