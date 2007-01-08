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

import org.codehaus.plexus.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusJdoUtils
{
    /**
     * This operation as opposed to
     * {@link #updateObject(PersistenceManager, Object)} is an
     * <em>new school</em> technique, which <em><b>does not</b></em>
     * requires that the object be previously added using
     * {@link #addObject(PersistenceManager, Object)} or
     * {@link #addObject(PersistenceManager, Object, String[])}.
     * 
     * @param pm
     * @param object
     * @param fetchGroups
     * @return
     * @throws PlexusStoreException
     * @see {@link #updateObject(PersistenceManager, Object)} for old technique.
     */
    public static Object saveObject( PersistenceManager pm, Object object, String fetchGroups[] )
        throws PlexusStoreException
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            if ( ( JDOHelper.getObjectId( object ) != null ) && !JDOHelper.isDetached( object ) )
            {
                throw new PlexusStoreException( "Existing object is not detached: " + object );
            }

            if ( fetchGroups != null )
            {
                for ( int i = 0; i >= fetchGroups.length; i++ )
                {
                    pm.getFetchPlan().addGroup( fetchGroups[i] );
                }
            }

            pm.makePersistent( object );

            object = pm.detachCopy( object );

            tx.commit();

            return object;
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    public static Object addObject( PersistenceManager pm, Object object )
    {
        return addObject( pm, object, null );
    }

    public static Object addObject( PersistenceManager pm, Object object, String fetchGroups[] )
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            if ( fetchGroups != null )
            {
                for ( int i = 0; i >= fetchGroups.length; i++ )
                {
                    pm.getFetchPlan().addGroup( fetchGroups[i] );
                }
            }

            pm.makePersistent( object );

            object = pm.detachCopy( object );

            tx.commit();

            return object;
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    public static void removeObject( PersistenceManager pm, Object o )
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            o = pm.getObjectById( pm.getObjectId( o ) );

            pm.deletePersistent( o );

            tx.commit();
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    /**
     * This operation is an <em>old school</em> technique, which required that
     * the object be previously added using
     * {@link #addObject(PersistenceManager, Object)} or
     * {@link #addObject(PersistenceManager, Object, String[])}.
     * 
     * @param pm
     * @param object
     * @return
     * @throws PlexusStoreException
     * @see {@link #saveObject(PersistenceManager, Object, String[])}
     */
    public static Object updateObject( PersistenceManager pm, Object object ) throws PlexusStoreException
    {
        Object ret = object;
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            if ( !JDOHelper.isDetached( object ) )
            {
                throw new PlexusStoreException( "Not detached: " + object );
            }

            try
            {
                ret = pm.makePersistent( object );
            }
            catch ( NullPointerException npe )
            {
                // Do not hide useful error messages.
                // This exception can occur if you have an object with a List
                // that isn't initialized yet.
                throw new PlexusStoreException( "Unable to update object due to unexpected null value.", npe );
            }
            catch ( Exception e )
            {
                // TODO: Refactor to avoid using Exception catch-all.
                // We retry if we obtain an exception like a dead lock
                ret = pm.makePersistent( object );
            }

            tx.commit();
        }
        finally
        {
            rollbackIfActive( tx );
        }

        return ret;
    }

    public static Object makePersistent( PersistenceManager pm, Object object, boolean detach )
    {
        pm.makePersistent( object );

        Object id = pm.getObjectId( object );

        Object persistentObject = pm.getObjectById( id );

        if ( detach )
        {
            persistentObject = pm.detachCopy( persistentObject );
        }

        return persistentObject;
    }

    public static Object getObjectById( PersistenceManager pm, Class clazz, String id )
        throws PlexusObjectNotFoundException, PlexusStoreException
    {
        return getObjectById( pm, clazz, id, null );
    }

    public static Object getObjectById( PersistenceManager pm, Class clazz, String id, String fetchGroup )
        throws PlexusStoreException, PlexusObjectNotFoundException
    {
        if ( StringUtils.isEmpty( id ) )
        {
            throw new PlexusStoreException( "Unable to get object '" + clazz.getName() + "' from jdo using null id." );
        }

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            if ( fetchGroup != null )
            {
                pm.getFetchPlan().addGroup( fetchGroup );
            }

            Object objectId = pm.newObjectIdInstance( clazz, id );

            Object object = pm.getObjectById( objectId );

            object = pm.detachCopy( object );

            tx.commit();

            return object;
        }
        catch ( JDOObjectNotFoundException e )
        {
            throw new PlexusObjectNotFoundException( clazz.getName(), id );
        }
        catch ( JDOException e )
        {
            throw new PlexusStoreException( "Error handling JDO", e );
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    /**
     * @deprecated Use {@link #getObjectById(PersistenceManager,Class,long)}
     *             instead
     */
    public static Object getObjectById( PersistenceManager pm, Class clazz, int id )
        throws PlexusStoreException, PlexusObjectNotFoundException
    {
        return getObjectById( pm, clazz, (long) id );
    }

    /**
     * Obtain and return an {@link Object} instance from the underlying data
     * store based on the passed in identifier.
     * 
     * @param pm {@link PersistenceManager} manager to use to query database.
     * @param clazz Expected {@link Class} of the Object instance to be
     *            returned.
     * @param id Object identifier to match in the database.
     * @return Object instance that matches the passed in identifier.
     * @throws PlexusStoreException if there was an error querying the database
     *             for the object.
     * @throws PlexusObjectNotFoundException if a matching object could not be
     *             found.
     */
    public static Object getObjectById( PersistenceManager pm, Class clazz, long id )
        throws PlexusStoreException, PlexusObjectNotFoundException
    {
        return getObjectById( pm, clazz, id, null );
    }

    /**
     * @deprecated Use
     *             {@link #getObjectById(PersistenceManager,Class,long, String)}
     *             instead
     */
    public static Object getObjectById( PersistenceManager pm, Class clazz, int id, String fetchGroup )
        throws PlexusStoreException, PlexusObjectNotFoundException
    {
        return getObjectById( pm, clazz, (long) id, fetchGroup );
    }

    /**
     * Obtain and return an {@link Object} instance from the underlying data
     * store based on the passed in identifier.
     * 
     * @param pm {@link PersistenceManager} manager to use to query database.
     * @param clazz Expected {@link Class} of the Object instance to be
     *            returned.
     * @param id Object identifier to match in the database.
     * @param fetchGroup TODO: Document!
     * @return Object instance that matches the passed in identifier.
     * @throws PlexusStoreException if there was an error querying the database
     *             for the object.
     * @throws PlexusObjectNotFoundException if a matching object could not be
     *             found.
     */
    public static Object getObjectById( PersistenceManager pm, Class clazz, long id, String fetchGroup )
        throws PlexusStoreException, PlexusObjectNotFoundException
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            if ( fetchGroup != null )
            {
                pm.getFetchPlan().addGroup( fetchGroup );
            }

            Object objectId = pm.newObjectIdInstance( clazz, new Long( id ) );

            Object object = pm.getObjectById( objectId );

            object = pm.detachCopy( object );

            tx.commit();

            return object;
        }
        catch ( JDOObjectNotFoundException e )
        {
            throw new PlexusObjectNotFoundException( clazz.getName(), Long.toString( id ) );
        }
        catch ( JDOException e )
        {
            e.printStackTrace();
            throw new PlexusStoreException( "Error handling JDO", e );
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    public static Object getObjectFromQuery( PersistenceManager pm, Class clazz, String idField, String id,
                                             String fetchGroup )
        throws PlexusStoreException, PlexusObjectNotFoundException
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( clazz, true );

            Query query = pm.newQuery( extent );

            query.declareImports( "import java.lang.String" );

            query.declareParameters( "String " + idField );

            query.setFilter( "this." + idField + " == " + idField );

            Collection result = (Collection) query.execute( id );

            if ( result.size() == 0 )
            {
                throw new PlexusObjectNotFoundException( clazz.getName(), id );
            }

            if ( result.size() > 1 )
            {
                throw new PlexusStoreException( "A query for object of " + "type " + clazz.getName() + " on the "
                                + "field '" + idField + "' returned more than one object." );
            }

            pm.getFetchPlan().addGroup( fetchGroup );

            Object object = pm.detachCopy( result.iterator().next() );

            tx.commit();

            return object;
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    public static List getAllObjectsDetached( PersistenceManager pm, Class clazz )
    {
        return getAllObjectsDetached( pm, clazz, null );
    }

    public static List getAllObjectsDetached( PersistenceManager pm, Class clazz, String fetchGroup )
    {
        return getAllObjectsDetached( pm, clazz, null, fetchGroup );
    }

    public static List getAllObjectsDetached( PersistenceManager pm, Class clazz, String ordering, String fetchGroup )
    {
        if ( fetchGroup != null )
        {
            return getAllObjectsDetached( pm, clazz, ordering, Collections.singletonList( fetchGroup ) );
        }
        else
        {
            return getAllObjectsDetached( pm, clazz, ordering, Collections.EMPTY_LIST );
        }
    }

    public static List getAllObjectsDetached( PersistenceManager pm, Class clazz, String ordering,
                                              List/* <String> */fetchGroups )
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( clazz, true );

            Query query = pm.newQuery( extent );

            if ( ordering != null )
            {
                query.setOrdering( ordering );
            }

            for ( Iterator i = fetchGroups.iterator(); i.hasNext(); )
            {
                pm.getFetchPlan().addGroup( (String) i.next() );
            }

            List result = (List) query.execute();

            result = (List) pm.detachCopyAll( result );

            tx.commit();

            return result;
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    public static void attachAndDelete( PersistenceManager pm, Object object )
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            pm.makePersistent( object );

            pm.deletePersistent( object );

            tx.commit();
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }

    public static void rollbackIfActive( Transaction tx )
    {
        PersistenceManager pm = tx.getPersistenceManager();

        try
        {
            if ( tx.isActive() )
            {
                tx.rollback();
            }
        }
        finally
        {
            closePersistenceManager( pm );
        }
    }

    public static void closePersistenceManager( PersistenceManager pm )
    {
        try
        {
            pm.close();
        }
        catch ( JDOUserException e )
        {
            // ignore
        }
    }

    public static void removeAll( PersistenceManager pm, Class aClass )
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Query query = pm.newQuery( aClass );
            query.deletePersistentAll();

            tx.commit();
        }
        finally
        {
            rollbackIfActive( tx );
        }
    }
}
