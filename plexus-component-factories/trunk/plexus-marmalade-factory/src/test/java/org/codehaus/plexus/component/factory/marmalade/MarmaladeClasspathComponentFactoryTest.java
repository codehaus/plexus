/* Created on Aug 9, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.realm.ClassRealm;
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
            
            Class.forName("org.codehaus.plexus.component.factory.marmalade.TestTaglib");
            
            ClassRealm realm = world.newRealm("plexus.core", getClass().getClassLoader());
            realm.addURL(file.getAbsoluteFile().getParentFile().toURL());
            
            String role="role";
            String hint = "hint";
            
            container.start(world);
            
            ComponentDescriptor descriptor = new ComponentDescriptor();
            descriptor.setRole(role);
            descriptor.setRoleHint(hint);
            descriptor.setImplementation(file.getPath());
            
            Object component = factory.newInstance(descriptor, realm, container.getContainer());
            
            assertNotNull(component);
            assertTrue(component instanceof TestTag);
        }
        finally {
            if(file.exists()) {
                file.delete();
            }
        }
        
        
    }

}
