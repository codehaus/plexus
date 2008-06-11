package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ContainerBasedMarmaladeClasspathComponentFactoryTest
    extends PlexusTestCase
{
    
    public void testShouldFindComponentFromScript() throws Exception
    {
        assertNotNull( this.lookup( "org.codehaus.marmalade.monitor.log.MarmaladeLog" ) );
        
        ComponentDescriptor descriptor = new ComponentDescriptor();
        
        descriptor.setComponentFactory("marmalade");
        descriptor.setRole("myrole");
        descriptor.setRoleHint("myhint");
        descriptor.setImplementation("containerTest.mmld");
        
        getContainer().addComponentDescriptor(descriptor);
        
        Object component = lookup("myrole", "myhint");
        
        assertNotNull(component);
    }

}
