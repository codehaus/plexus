package org.codehaus.plexus.security.authorization.rbac.store.jdo;

import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;
import org.codehaus.plexus.security.authorization.rbac.Role;
import org.codehaus.plexus.security.authorization.rbac.UserAssignment;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.jdo.PlexusJdoUtils;
import org.codehaus.plexus.jdo.PlexusStoreException;
import org.codehaus.plexus.jdo.PlexusObjectNotFoundException;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import java.util.List;
/*
 * Copyright 2005 The Apache Software Foundation.
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
 * JdoRbacStore:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.authorization.rbac.store.RbacStore"
 *   role-hint="jdo"
 */
public class JdoRbacStore
    implements RbacStore, Initializable
{

    /**
     * @plexus.requirement
     */
    private JdoFactory jdoFactory;

    private PersistenceManagerFactory pmf;



    // ----------------------------------------------------------------------
    // Role methods
    // ----------------------------------------------------------------------
    public void addRole( Role role)
        throws RbacStoreException
    {
        addObject( role );
    }

    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------


    // ----------------------------------------------------------------------
    // Actor methods
    // ----------------------------------------------------------------------


    // ----------------------------------------------------------------------
    // User Assignment methods
    // ----------------------------------------------------------------------
    public List getRoleAssignments ( int principal )
        throws RbacStoreException
    {
        try
        {
            UserAssignment ua = (UserAssignment) getObjectById( UserAssignment.class, principal );

            return ua.getRoles();
        }
        catch ( PlexusStoreException pse )
        {
            throw new RbacStoreException( pse );
        }
    }

    public void addRoleAssignment( int principal, int roleId )
        throws RbacStoreException
    {
        try
        {
            UserAssignment ua = (UserAssignment) getObjectById( UserAssignment.class, principal );
            Role role = (Role) getObjectById( Role.class, roleId );

            ua.addRole( role );
        }
        catch ( PlexusStoreException pse )
        {
            throw new RbacStoreException( pse );
        }

    }


    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();
    }


    // ----------------------------------------------------------------------
    // jdo utility methods
    // ----------------------------------------------------------------------

    private Object addObject( Object object )
    {
        return PlexusJdoUtils.addObject( getPersistenceManager(), object );
    }

    private Object getObjectById( Class clazz, int id )
        throws PlexusStoreException
    {
        return getObjectById( clazz, id, null );
    }

    private Object getObjectById( Class clazz, int id, String fetchGroup )
        throws PlexusStoreException
    {
        try
        {
            return PlexusJdoUtils.getObjectById( getPersistenceManager(), clazz, id, fetchGroup );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            // TODO make PlexusObjectNotFoundException runtime or change plexus not to wrap jdo exceptions
            throw new RuntimeException( e.getMessage() );
        }
        catch ( PlexusStoreException e )
        {
            throw new PlexusStoreException( e.getMessage(), e );
        }
    }

    private List getAllObjectsDetached( Class clazz )
    {
        return getAllObjectsDetached( clazz, null );
    }

    private List getAllObjectsDetached( Class clazz, String fetchGroup )
    {
        return getAllObjectsDetached( clazz, null, fetchGroup );
    }

    private List getAllObjectsDetached( Class clazz, String ordering, String fetchGroup )
    {
        return PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), clazz, ordering, fetchGroup );
    }

    private void removeObject( Object o )
    {
        PlexusJdoUtils.removeObject( getPersistenceManager(), o );
    }

    private void updateObject( Object object )
        throws PlexusStoreException
    {
        PlexusJdoUtils.updateObject( getPersistenceManager(), object );
    }

    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.getFetchPlan().setMaxFetchDepth( -1 );

        return pm;
    }

    private void rollback( Transaction tx )
    {
        PlexusJdoUtils.rollbackIfActive( tx );
    }

}
