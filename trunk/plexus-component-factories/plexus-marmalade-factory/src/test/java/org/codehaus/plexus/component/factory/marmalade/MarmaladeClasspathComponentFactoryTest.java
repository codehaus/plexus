/* Created on Aug 9, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.embed.Embedder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class MarmaladeClasspathComponentFactoryTest extends TestCase {
    
    private static final String TEST_SCRIPT = "<?xml version=\"1.0\"?>" +
        "<testScript xmlns=\"marmalade:org.codehaus.plexus.component.factory.marmalade.TestTaglib\"/>";
    
    public void testShouldLoadComponentFromScript() throws Exception {
        File file = new File("test.mmld");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(TEST_SCRIPT);
            out.flush();
            out.close();
            
            MarmaladeClasspathComponentFactory factory = new MarmaladeClasspathComponentFactory();
            
            Embedder container = new Embedder();
            ClassWorld world = new ClassWorld();
            
            String role="role";
            String hint = "hint";
            
            String key = role + hint;
            
            container.start(world);
            
            ClassRealm realm = container.getContainer().getComponentRealm(key);
            realm.addConstituent(file.getAbsoluteFile().getParentFile().toURL());
            
            ComponentDescriptor descriptor = new ComponentDescriptor();
            descriptor.setRole(role);
            descriptor.setRoleHint(hint);
            descriptor.setImplementation(file.getPath());
            
            Object component = factory.newInstance(descriptor, realm, container.getContainer());
            
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
