/* Created on Aug 6, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.marmalade.launch.MarmaladeLaunchException;
import org.codehaus.marmalade.launch.MarmaladeLauncher;
import org.codehaus.marmalade.model.MarmaladeScript;
import org.codehaus.marmalade.model.MarmaladeTag;
import org.codehaus.marmalade.monitor.log.CommonLogLevels;
import org.codehaus.marmalade.monitor.log.DefaultLog;
import org.codehaus.marmalade.monitor.log.MarmaladeLog;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        ClassRealm componentRealm = container.getComponentRealm( componentDescriptor.getComponentKey() );
        ClassRealm thisRealm = container.getComponentRealm(ComponentFactory.ROLE + "marmalade");

        URL scriptLocation = getScriptLocation( componentDescriptor, componentRealm );

        Object result = null;

        try
        {
            result = parseComponent( scriptLocation, componentRealm, classRealm, thisRealm );
        }
        catch ( Exception e )
        {
            componentRealm.display();

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

    public Object parseComponent( URL scriptLocation, ClassRealm realm, ClassRealm parameterRealm, ClassRealm thisRealm )
        throws ComponentInstantiationException
    {
        Object result = null;

        try
        {
//            URL it0015Resource2 = realm.getResource( "META-INF/marmalade/it0015.def" );
//            marmaladeLog.log(CommonLogLevels.INFO, "it0015 taglib definition resource from component realm is: " + it0015Resource2);
            
            List entries = new ArrayList();
            entries.add("Realm loaders from each level starting at component level and moving away: ");
            
            ClassRealm cr = realm;
            
            int idx = 0;
            while(cr != null)
            {
                entries.add("\n  ");
                entries.add("[");
                entries.add(String.valueOf(idx++));
                entries.add(": id=");
                entries.add(cr.getId());
                entries.add("] ");
                entries.add(cr.getClassLoader());
                
                cr = cr.getParent();
            }
            
            entries.add("\n\nRealm loaders from each level starting at parameter-realm level and moving away: ");
            
            cr = parameterRealm;
            
            idx = 0;
            while(cr != null)
            {
                entries.add("\n  ");
                entries.add("[");
                entries.add(String.valueOf(idx++));
                entries.add(": id=");
                entries.add(cr.getId());
                entries.add("] ");
                entries.add(cr.getClassLoader());
                
                cr = cr.getParent();
            }
            
            entries.add("\n\nRealm loaders from each level starting at this-realm level and moving away: ");
            
            cr = thisRealm;
            
            idx = 0;
            while(cr != null)
            {
                entries.add("\n  ");
                entries.add("[");
                entries.add(String.valueOf(idx++));
                entries.add(": id=");
                entries.add(cr.getId());
                entries.add("] ");
                entries.add(cr.getClassLoader());
                
                cr = cr.getParent();
            }
            
            entries.add("\n\nRealm loader used to load this factory is: ");
            entries.add(getClass().getClassLoader());
            
            marmaladeLog.log(CommonLogLevels.INFO, entries);
            
            MarmaladeLauncher launcher = new MarmaladeLauncher();
            
            launcher.withLog( marmaladeLog );
            launcher.withInputURL(scriptLocation);
            
            launcher.withDefaultTagLibrary(null);

            MarmaladeScript script = launcher.buildScript();

            MarmaladeTag rootTag = script.getRoot();
            
            marmaladeLog.log(CommonLogLevels.INFO, "Root tag type: " + rootTag);
            
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