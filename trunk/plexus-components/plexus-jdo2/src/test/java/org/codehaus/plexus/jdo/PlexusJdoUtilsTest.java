package org.codehaus.plexus.jdo;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.PlexusTestCase;
import org.jpox.SchemaTool;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * Tests for PlexusJdoUtilsTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class PlexusJdoUtilsTest
    extends PlexusTestCase
{
    private PersistenceManagerFactory pmf;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        ConfigurableJdoFactory jdoFactory = (ConfigurableJdoFactory) lookup( JdoFactory.ROLE );
        assertEquals( DefaultConfigurableJdoFactory.class.getName(), jdoFactory.getClass().getName() );

        jdoFactory.setPersistenceManagerFactoryClass( "org.jpox.PersistenceManagerFactoryImpl" ); 

        jdoFactory.setDriverName( "org.hsqldb.jdbcDriver" ); 

        jdoFactory.setUrl( "jdbc:hsqldb:mem:" + getName() ); 

        jdoFactory.setUserName( "sa" ); 

        jdoFactory.setPassword( "" ); 

        jdoFactory.setProperty( "org.jpox.transactionIsolation", "READ_UNCOMMITTED" );  

        jdoFactory.setProperty( "org.jpox.poid.transactionIsolation", "READ_UNCOMMITTED" );  

        jdoFactory.setProperty( "org.jpox.autoCreateSchema", "true" );  

        Properties properties = jdoFactory.getProperties();

        for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            System.setProperty( (String) entry.getKey(), (String) entry.getValue() );
        }

        SchemaTool.createSchemaTables( new URL[] { getClass().getResource( "/META-INF/package.jdo" ) }, null, false ); 

        pmf = jdoFactory.getPersistenceManagerFactory();
        assertNotNull( pmf );

        PersistenceManager pmgr = pmf.getPersistenceManager();
        pmgr.close();
    }
    
    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.getFetchPlan().setMaxFetchDepth( -1 );

        return pm;
    }
    
    public void testAddGetUpdateBasic()
        throws Exception
    {
        Basic basic = new Basic();
        basic.setName( "Fry" );
        basic.setDescription( "Addicted to Slurm" );

        Basic added = (Basic) PlexusJdoUtils.addObject( getPersistenceManager(), basic );

        // Ensure that only 1 entry exists.
        assertEquals( 1, PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), Basic.class ).size() );

        Basic fetched = (Basic) PlexusJdoUtils.getObjectById( getPersistenceManager(), Basic.class, added.getId() );

        int id = fetched.getId();
        String BRAINSLUG = "Starved a Brain Slug";

        fetched.setDescription( BRAINSLUG );

        PlexusJdoUtils.updateObject( getPersistenceManager(), fetched );

        // Ensure that only 1 still entry exists.
        assertEquals( 1, PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), Basic.class ).size() );

        Basic actual = (Basic) PlexusJdoUtils.getObjectById( getPersistenceManager(), Basic.class, id );
        assertEquals( BRAINSLUG, actual.getDescription() );
    }
    
    public void testAddGetUpdateParentChild()
        throws Exception
    {
        Parent parent = new Parent();
        parent.setName( "Doctor Farnsworth" );
        parent.setDescription( "Great x30 Newphew of Fry");
        parent.setPrice( 30.00 );
        
        Child cubert = new Child();
        cubert.setName( "Cubert" );
        cubert.setDescription( "Clone of Farnsworth" );
        
        parent.addChildren( cubert );

        Parent added = (Parent) PlexusJdoUtils.addObject( getPersistenceManager(), parent );

        // Ensure that only 1 entry exists.
        assertEquals( 1, PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), Parent.class ).size() );

        Parent fetched = (Parent) PlexusJdoUtils.getObjectById( getPersistenceManager(), Parent.class, added.getId() );
        assertNotNull( fetched );
        assertNotNull( fetched.getChildren() );
        assertEquals( 1, fetched.getChildren().size() );

        int id = fetched.getId();
        String PLANETEXPRESS = "Owns Planet Express";

        fetched.setDescription( PLANETEXPRESS );

        PlexusJdoUtils.updateObject( getPersistenceManager(), fetched );

        // Ensure that only 1 still entry exists.
        assertEquals( 1, PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), Parent.class ).size() );

        Parent actual = (Parent) PlexusJdoUtils.getObjectById( getPersistenceManager(), Parent.class, id );
        assertEquals( PLANETEXPRESS, actual.getDescription() );
    }
    
}
