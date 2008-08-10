/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.cdc;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;
import org.codehaus.plexus.component.repository.cdc.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Test for the {@link DefaultComponentDescriptorCreator} class.
 *
 * @version $Rev$ $Date$
 */
public class DefaultComponentDescriptorCreatorTest
    extends PlexusTestCase
{
    private DefaultComponentDescriptorCreator descriptorCreator;

    // @Override
    protected void setUp() throws Exception {
        super.setUp();

        descriptorCreator = (DefaultComponentDescriptorCreator) lookup(ComponentDescriptorCreator.class);
        assertNotNull(descriptorCreator);
    }

    // @Override
    protected void tearDown() throws Exception {
        descriptorCreator = null;

        super.tearDown();
    }

    public void testBasic() throws Exception {
        File descriptor = File.createTempFile( "plexus", "tmp" );
        try {
            descriptorCreator.processSources( new File[] { getTestFile( "src/test/sources" ) }, descriptor );

            // TODO: assert component
        } finally {
            descriptor.delete();
        }
    }
}
