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
        URL scriptLocation = classRealm.getResource( componentDescriptor.getImplementation() );

        return scriptLocation;
    }

}