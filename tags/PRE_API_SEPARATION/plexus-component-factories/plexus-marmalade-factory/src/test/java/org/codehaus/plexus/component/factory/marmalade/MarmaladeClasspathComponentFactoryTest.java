/* Created on Aug 9, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DefaultClassRealm;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class MarmaladeClasspathComponentFactoryTest extends TestCase {
    
    private static final String TEST_SCRIPT = "<?xml version=\"1.0\"?>" +
        "<testScript xmlns=\"marmalade:org.codehaus.plexus.component.factory.marmalade.TestTaglib\"/>";
    
    public void testShouldLoadComponentFromScript() throws IOException, ComponentInstantiationException {
        File file = new File("test.mmld");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(TEST_SCRIPT);
            out.flush();
            out.close();
            
            MarmaladeClasspathComponentFactory factory = new MarmaladeClasspathComponentFactory();
            
            DefaultPlexusContainer container = new DefaultPlexusContainer();
            ClassRealm realm = new DefaultClassRealm(new ClassWorld(), "test");
            realm.addConstituent(new File(".").toURL());
            
            ComponentDescriptor descriptor = new ComponentDescriptor();
            descriptor.setImplementation("test");
            
            Object component = factory.newInstance(descriptor, realm, container);
            
            assertNotNull(component);
            assertEquals("TestComponent", component);
        }
        finally {
            if(file.exists()) {
                file.delete();
            }
        }
        
        
    }

}
