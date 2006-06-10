package org.codehaus.plexus.xfire.type;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.aegis.type.TypeMapping;
import org.codehaus.plexus.xfire.type.TypeMappingRegistry;
import org.codehaus.plexus.soap.SoapConstants;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 9, 2004
 */
public class TypeMappingRegistryTest 
    extends PlexusTestCase
{
    public void testRegistry() throws Exception
    {
        TypeMappingRegistry reg = (TypeMappingRegistry) lookup( TypeMappingRegistry.ROLE );
        
        assertNotNull(reg);
        
        TypeMapping tm = reg.getTypeMapping(SoapConstants.XSD);
        
        assertNotNull(tm);
        
        assertNotNull(tm.getType(String.class));
        assertNotNull(tm.getType(Integer.class));
        assertNotNull(tm.getType(int.class));
    }
}
