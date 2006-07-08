package org.codehaus.plexus.component.repository.io;

import junit.framework.TestCase;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentConfigurationDescriptor;
import org.codehaus.plexus.component.repository.ComponentConfigurationFieldDescriptor;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusToolsTest
    extends TestCase
{
    public void testConfigurationDescription()
        throws Exception
    {
        String configuration = IOUtil.toString( getClass().getResourceAsStream( "PlexusToolsTest-1.xml" ) );

        ComponentDescriptor cd = PlexusTools.buildComponentDescriptor( configuration );

        assertNotNull( cd );

        ComponentConfigurationDescriptor ccd = cd.getConfigurationDescriptor();

        assertNotNull( ccd );

        assertNotNull( ccd.getFields() );
        assertEquals( 3, ccd.getFields().size() );

        ComponentConfigurationFieldDescriptor field1 = (ComponentConfigurationFieldDescriptor) ccd.getFields().get( 0 );
        assertEquals( "name-1", field1.getName() );
        assertEquals( "type-1", field1.getType() );
        assertEquals( "private", field1.getInjectionMethod() );
        assertEquals( "description-1", field1.getDescription() );
        assertEquals( "expression-1", field1.getExpression() );
        assertEquals( false, field1.isRequired() );
        assertEquals( null, field1.getSince() );

        ComponentConfigurationFieldDescriptor field2 = (ComponentConfigurationFieldDescriptor) ccd.getFields().get( 1 );
        assertEquals( "name-2", field2.getName() );
        assertEquals( "type-2", field2.getType() );
        assertEquals( "setter", field2.getInjectionMethod() );
        assertEquals( "description-2", field2.getDescription() );
        assertEquals( "expression-2", field2.getExpression() );
        assertEquals( true, field2.isRequired() );
        assertEquals( "1.2", field2.getSince() );

        ComponentConfigurationFieldDescriptor field3 = (ComponentConfigurationFieldDescriptor) ccd.getFields().get( 2 );
        assertEquals( "name-3", field3.getName() );
        assertEquals( "type-3", field3.getType() );
        assertNull( field3.getInjectionMethod() );
        assertEquals( "description-3", field3.getDescription() );
        assertEquals( "expression-3", field3.getExpression() );
        assertEquals( false, field3.isRequired() );
        assertEquals( "1.2", field3.getSince() );
    }

    public void testComponentWithoutConfigurationDescription()
        throws Exception
    {
        String configuration = IOUtil.toString( getClass().getResourceAsStream( "PlexusToolsTest-2.xml" ) );

        ComponentDescriptor cd = PlexusTools.buildComponentDescriptor( configuration );

        assertNotNull( cd );

        ComponentConfigurationDescriptor ccd = cd.getConfigurationDescriptor();

        assertNull( ccd );
    }
}
