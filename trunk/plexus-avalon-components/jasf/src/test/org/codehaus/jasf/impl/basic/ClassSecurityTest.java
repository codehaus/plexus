package org.codehaus.jasf.impl.basic;

import java.lang.reflect.Method;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.commons.attributes.Attributes;
import org.codehaus.jasf.Authenticator;
import org.codehaus.jasf.ResourceController;
import org.codehaus.jasf.resources.ClassMethodResource;
import org.codehaus.jasf.resources.Credential;
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
        
        assertTrue( Attributes.hasAttributeType(restrictedMethod, Credential.class) );
        
        Collection attributes = Attributes.getAttributes( restrictedMethod );
        
        Credential cred = (Credential) attributes.iterator().next();
        
        assertTrue( cred.getName().equals("employee_read") );
        
        assertTrue( !cred.getName().equals("blah") );
        
        ResourceController controller =
            (ResourceController) resSelector.select(ClassMethodResource.RESOURCE_TYPE);
        
        assertFalse( ((ClassAccessController)controller).getDefaultAuthorization() );
    
        assertTrue( controller.isAuthorized(entity, restrictedMethod) );
    }

    public void testBadCredentials() throws Exception
    {
        Method restrictedMethod = 
            ClassSecurityTest.class.getDeclaredMethod("myBadRestrictedMethod", new Class[0]);
        
        ResourceController controller =
            (ResourceController) resSelector.select(ClassMethodResource.RESOURCE_TYPE);
        
        assertFalse( controller.isAuthorized(entity, restrictedMethod) );
    }
    
    /**
     * Pretends to do something that needs security.
     * 
     * @@Credential("employee_read")
     */
    public void myRestrictedMethod()
    {
    }
    
    /**
     * Pretends to do something that needs security.
     * 
     * @@Credential("wontwork")
     */
    public void myBadRestrictedMethod()
    {
    }
}
