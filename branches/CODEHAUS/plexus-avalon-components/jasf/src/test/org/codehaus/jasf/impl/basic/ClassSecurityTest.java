package org.codehaus.jasf.impl.basic;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.commons.attributes.Attribute;
import org.apache.commons.attributes.Attributes;
import org.codehaus.jasf.Authenticator;
import org.codehaus.jasf.ResourceController;
import org.codehaus.jasf.resources.ClassMethodResource;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Test the xml web security package
 * 
 * @author Dan Diephouse
 * @since Nov 23, 2002 
 */
public class ClassSecurityTest extends PlexusTestCase
{

    ServiceSelector authSelector;
    ServiceSelector resSelector;
    
    public ClassSecurityTest( String testName ) throws Exception
    {
        super( testName );
    }
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
      
    public static Test suite() {
        return new TestSuite(ClassSecurityTest.class);
    }
    
    BasicUser entity;
    
    public void setUp() throws Exception
    {   
        super.setUp();
        
        authSelector = (ServiceSelector) lookup(Authenticator.SELECTOR_ROLE);
        resSelector = (ServiceSelector) lookup(ResourceController.SELECTOR_ROLE);
        
        Authenticator controller =
            (Authenticator) authSelector.select(BasicUser.ENTITY_TYPE);
        entity = (BasicUser) controller.authenticate( "dan", "password" );	
    }
    
    public void testAttributes() throws Exception
    {
        Method restrictedMethod = 
            ClassSecurityTest.class.getDeclaredMethod("myRestrictedMethod", new Class[0]);
        
        assertTrue( Attributes.hasAttribute(restrictedMethod, "credential") );
        
        Attribute credential = Attributes.getAttribute( restrictedMethod, "credential" );
        
        assertTrue( credential.getValue().equals("employee_read") );
        
        assertTrue( !credential.getValue().equals("blah") );
        
        ResourceController controller =
            (ResourceController) resSelector.select(ClassMethodResource.RESOURCE_TYPE);
        
        assertTrue( controller.isAuthorized(entity, restrictedMethod) );
    }

    public void testBadCredentials() throws Exception
    {
        Method restrictedMethod = 
            ClassSecurityTest.class.getDeclaredMethod("myBadRestrictedMethod", new Class[0]);
        
        ResourceController controller =
            (ResourceController) resSelector.select(ClassMethodResource.RESOURCE_TYPE);
        
        assertTrue( !controller.isAuthorized(entity, restrictedMethod) );
    }
    
    /**
     * Pretends to do something that needs security.
     * 
     * @credential employee_read
     */
    public void myRestrictedMethod()
    {
    }
    
    /**
     * Pretends to do something that needs security.
     * 
     * @credential wontwork
     */
    public void myBadRestrictedMethod()
    {
    }
}
