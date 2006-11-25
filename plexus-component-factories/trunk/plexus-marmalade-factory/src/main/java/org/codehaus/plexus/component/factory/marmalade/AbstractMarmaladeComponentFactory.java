/* Created on Aug 6, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.marmalade.compat.ant.discovery.AntBasedDiscoveryStrategy;
import org.codehaus.marmalade.launch.MarmaladeLaunchException;
import org.codehaus.marmalade.launch.MarmaladeLauncher;
import org.codehaus.marmalade.metamodel.MarmaladeTaglibResolver;
import org.codehaus.marmalade.model.MarmaladeScript;
import org.codehaus.marmalade.model.MarmaladeTag;
import org.codehaus.marmalade.monitor.log.DefaultLog;
import org.codehaus.marmalade.monitor.log.MarmaladeLog;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.io.IOException;
import java.net.URL;

/**
 * Component factory for reading marmalade scripts and executing them to return
 * a component. The root of the script should be a tag which implements
 * PlexusComponentTag, and this root tag will return a component via the
 * getComponent() method. Scripts are cached in the caching parser.
 * 
 * @author jdcasey
 */
public abstract class AbstractMarmaladeComponentFactory
    implements ComponentFactory
{

    private MarmaladeLog marmaladeLog = new DefaultLog();

    private String id = "marmalade";

    protected AbstractMarmaladeComponentFactory()
    {
    }

    public String getId()
    {
        return id;
    }

    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        URL scriptLocation = getScriptLocation( componentDescriptor, container.getContainerRealm() );

        Object result = null;

        try
        {
            result = parseComponent( scriptLocation, container.getContainerRealm() );
        }
        catch ( Exception e )
        {
            container.getContainerRealm().display();

            if ( e instanceof ComponentInstantiationException )
            {
                throw (ComponentInstantiationException) e;
            }
            else
            {
                throw new ComponentInstantiationException( "Cannot build component for: "
                    + componentDescriptor.getComponentKey(), e );
            }
        }

        return result;
    }

    protected abstract URL getScriptLocation( ComponentDescriptor componentDescriptor, ClassRealm classRealm );

    public Object parseComponent( URL scriptLocation, ClassRealm realm )
        throws ComponentInstantiationException
    {
        Object result = null;

        try
        {
            MarmaladeLauncher launcher = new MarmaladeLauncher();
            
            launcher.withLog( marmaladeLog );
            
            launcher.withInputURL( scriptLocation );
            
            launcher.withAdditionalTaglibDefinitionStrategies( MarmaladeTaglibResolver.NO_PASSTHROUGH_STRATEGY_CHAIN );
            
            launcher.withAdditionalTaglibDefinitionStrategy( new AntBasedDiscoveryStrategy() );
            
            launcher.withClassLoader( new RealmDelegatingClassLoader( realm ) );
            
            MarmaladeScript script = launcher.buildScript();

            MarmaladeTag rootTag = script.getRoot();
            
            PlexusComponentTag componentTag = (PlexusComponentTag) rootTag;

            launcher.run();

            result = componentTag.getComponent();
        }
        catch ( IOException e )
        {
            throw new ComponentInstantiationException( "Cannot read component script: " + scriptLocation, e );
        }
        catch ( MarmaladeLaunchException e )
        {
            throw new ComponentInstantiationException( "Error parsing component from script: " + scriptLocation, e );
        }

        return result;
    }

}