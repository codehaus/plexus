package org.codehaus.jasf.impl.basic;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.service.ServiceSelector;
import org.codehaus.jasf.Authenticator;
import org.codehaus.jasf.ResourceController;
import org.codehaus.jasf.resources.PageResource;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Test the xml web security package
 * 
 * @author Dan Diephouse
 * @since Nov 23, 2002 
 */
public class BasicSecurityTest extends PlexusTestCase
{

    ServiceSelector authSelector;
    ServiceSelector resSelector;
    
    public BasicSecurityTest( String testName ) throws Exception
    {
        super( testName );
    }
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
      
    public static Test suite() {
        return new TestSuite(BasicSecurityTest.class);
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
    
    public void testUserAuthentication() throws Exception
    {
        // Test Authentication
        if (entity == null)
            throw new Exception("XmlUser was null!");
            
        assertTrue( entity.getUserName() != null);
    }
    
    public void testPositiveAuthorization() throws Exception
    {
        
        // Test Authorization
        ResourceController controller = 
            (ResourceController) resSelector.select(PageResource.RESOURCE_TYPE);
        
        assertTrue( controller != null );
        
        assertTrue( controller.isAuthorized(entity, 
            new PageResource("formanagers/employees.html"))  );
             
        assertTrue( controller.isAuthorized(entity, 
            new PageResource("everyone.html")) );

        assertTrue( controller.isAuthorized(entity, 
            new PageResource("emptycred.html")) );
    }
    
    public void testNegativeAuthorization() throws Exception
    {       
        // Test Authorization
        ResourceController controller = 
            (ResourceController) resSelector.select(PageResource.RESOURCE_TYPE);
        
        assertTrue( controller != null );
        
        // Non existant directory
        assertTrue( !controller.isAuthorized(entity, 
            new PageResource("writedir" )) );                 

        // NOn existant credential
        assertTrue( !controller.isAuthorized(entity, 
            new PageResource("formanagers/badcred.html" )) );                 
    }
}
