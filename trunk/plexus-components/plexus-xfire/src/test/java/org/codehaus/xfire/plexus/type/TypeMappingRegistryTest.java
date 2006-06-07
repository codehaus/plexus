package org.codehaus.xfire.plexus.type;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.plexus.type.TypeMappingRegistry;
import org.codehaus.xfire.soap.SoapConstants;

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
