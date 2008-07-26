package org.codehaus.plexus.jdo;

import javax.jdo.PersistenceManager;

import org.jpox.PersistenceManagerFactoryImpl;

/*
 * Copyright 2006 The Apache Software Foundation.
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

/**
 * Test for {@link DefaultConfigurableJdoFactory}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class DefaultConfigurableJdoFactoryTest
    extends DefaultJdoFactoryTest
{

    public void testLoad()
        throws Exception
    {
        DefaultConfigurableJdoFactory jdoFactory = (DefaultConfigurableJdoFactory) lookup( JdoFactory.ROLE );

        String password = jdoFactory.getProperties().getProperty( "javax.jdo.option.ConnectionPassword" );
        assertNull( password );

        PersistenceManagerFactoryImpl pmf = (PersistenceManagerFactoryImpl) jdoFactory.getPersistenceManagerFactory();
        assertTrue( pmf.getAutoCreateTables() );
    }

}
