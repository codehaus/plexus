package org.codehaus.plexus.personality.avalon;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.DefaultComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * A ComponentRepository for Avalon services that creates ServiceSelectors
 * for id'd components.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:jvanzyl@maven.org">Jason van Zyl</a>
 * @since May 10, 2003
 */
public class AvalonComponentRepository
    extends DefaultComponentRepository
{
	/**
	 * @see org.codehaus.plexus.component.repository.ComponentRepository#initialize()
	 */
	public void initialize() throws Exception
	{
		super.initialize();
	}
    
    /**
     * @see org.codehaus.plexus.component.repository.ComponentRepository#addComponentDescriptor(org.codehaus.plexus.component.repository.ComponentDescriptor)
     */
    public void addComponentDescriptor(ComponentDescriptor componentDescriptor)
        throws ComponentRepositoryException
    {
        super.addComponentDescriptor(componentDescriptor);
        
        String componentRole = componentDescriptor.getRole();
        String selectorRole = componentRole + "Selector";
        
        if ( componentDescriptor.getRoleHint() != null 
             &&
             !componentDescriptor.getRoleHint().equals("")
             &&
             !hasComponent( selectorRole ) )
        {
            ComponentDescriptor d = new ComponentDescriptor();

            d.setRole( selectorRole );

            d.setImplementation( "org.codehaus.plexus.personality.avalon.AvalonServiceSelector" );

            XmlPlexusConfiguration configuration = new XmlPlexusConfiguration( "configuration" );

            XmlPlexusConfiguration selectableRole = new XmlPlexusConfiguration( "selectable-role" );

            selectableRole.setValue( componentRole );

            configuration.addChild( selectableRole );

            d.setConfiguration( configuration );

            try
            {
                addComponentDescriptor( d );
            }
            catch ( ComponentRepositoryException e )
            {
                getLogger().info( "Could not add ComponentDescriptor.", e );
            }
        }
    }
}
