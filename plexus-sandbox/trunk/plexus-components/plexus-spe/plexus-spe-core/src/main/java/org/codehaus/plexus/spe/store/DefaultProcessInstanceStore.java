package org.codehaus.plexus.spe.store;

import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.jdo.PlexusJdoUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.model.ProcessInstanceLifecycleListener;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.StepInstance;

import javax.jdo.Extent;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultProcessInstanceStore
    extends AbstractLogEnabled
    implements ProcessInstanceStore, Initializable
{
    private PersistenceManagerFactory pmf;

    private ProcessInstanceLifecycleListener processInstanceLifecycleListener;

    /**
     * @plexus.requirement
     */
    private JdoFactory jdoFactory;

    // ----------------------------------------------------------------------
    // ProcessInstanceStore Implementation
    // ----------------------------------------------------------------------

    public synchronized ProcessInstance createInstance( ProcessDescriptor ProcessDescriptor,
                                                        Map<String, Serializable> context )
        throws ProcessException
    {
        getLogger().info( "Creating a new process instance: " + ProcessDescriptor.getId() + "." );
        PersistenceManager pm = getPm();

        Transaction transaction = pm.currentTransaction();

        ProcessInstance instance = new ProcessInstance();
        instance.setProcessId( ProcessDescriptor.getId() );
        instance.setContext( context );
        instance.setCreatedTime( System.currentTimeMillis() );

        for ( StepDescriptor action : (List<StepDescriptor>) ProcessDescriptor.getSteps() )
        {
            StepInstance step = new StepInstance();
            step.setExecutorId( action.getExecutorId() );
            instance.getSteps().add( step );
        }

        try
        {
            transaction.begin();

            pm.makePersistent( instance );

            pm.getFetchPlan().addGroup( "StepInstance_detail" );
            ProcessInstance processInstance = (ProcessInstance) pm.detachCopy( instance );

            transaction.commit();

            return processInstance;
        }
        finally
        {
            PlexusJdoUtils.rollbackIfActive( transaction );
        }
    }

    public synchronized Collection<? extends ProcessInstance> getActiveInstances()
    {
        PersistenceManager pm = getPm();

        Transaction transaction = pm.currentTransaction();

        try
        {
            transaction.begin();

            Extent extent = pm.getExtent( ProcessInstance.class );
            Query query = pm.newQuery( extent );
            Collection<ProcessInstance> collection = pm.detachCopyAll( (Collection<ProcessInstance>) query.execute() );

            transaction.commit();

            return collection;
        }
        finally
        {
            PlexusJdoUtils.rollbackIfActive( transaction );
        }
    }

    public synchronized void saveInstance( ProcessInstance processInstance )
    {
        getLogger().debug( "Storing process instance " + processInstance.getId() + ", process: " +
            processInstance.getProcessId() + "." );

        updateObject( getPm(), processInstance );
    }

    public synchronized ProcessInstance getInstance( int id, boolean includeContext )
        throws ProcessException
    {
        PersistenceManager pm = getPm();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Object objectId = pm.newObjectIdInstance( ProcessInstance.class, (Integer) id );

            Object object = pm.getObjectById( objectId );

            pm.getFetchPlan().addGroup( "ProcessInstance_detail" );

            if ( includeContext )
            {
                pm.getFetchPlan().addGroup( "ContextValue_detail" );
            }

            ProcessInstance processInstance = (ProcessInstance) pm.detachCopy( object );

            tx.commit();

            return processInstance;
        }
        catch ( JDOObjectNotFoundException e )
        {
            throw new ProcessException(
                "Could not load object with id " + id + " of class : " + ProcessInstance.class.getName(), e );
        }
        catch ( JDOException e )
        {
            throw new ProcessException( "Error handling JDO.", e );
        }
        finally
        {
            PlexusJdoUtils.rollbackIfActive( tx );
        }
    }

    public synchronized void deleteInstance( int id )
        throws ProcessException
    {
        PersistenceManager pm = getPm();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Object objectId = pm.newObjectIdInstance( ProcessInstance.class, (Integer) id );

            Object object = pm.getObjectById( objectId );

            pm.deletePersistent( object );

            tx.commit();
        }
        catch ( JDOObjectNotFoundException e )
        {
            throw new ProcessException(
                "Could not delete object with id " + id + " of class : " + ProcessInstance.class.getName(), e );
        }
        catch ( JDOException e )
        {
            throw new ProcessException( "Error handling JDO.", e );
        }
        finally
        {
            PlexusJdoUtils.rollbackIfActive( tx );
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();

        processInstanceLifecycleListener = new ProcessInstanceLifecycleListener();
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private PersistenceManager getPm()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.addInstanceLifecycleListener( processInstanceLifecycleListener, new Class[]{ProcessInstance.class} );

        return pm;
    }

    private void updateObject( PersistenceManager pm, Object object )
        throws RuntimeException
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            if ( !JDOHelper.isDetached( object ) )
            {
                throw new RuntimeException( "Not detached: " + object );
            }

            pm.makePersistent( object );

            tx.commit();
        }
        finally
        {
            PlexusJdoUtils.rollbackIfActive( tx );
        }
    }

    private Object getObjectById( Class clazz, int id )
        throws ProcessException
    {
        return getObjectById( clazz, id, null );
    }

    private Object getObjectById( Class clazz, int id, String fetchGroup )
        throws ProcessException
    {
        PersistenceManager pm = getPm();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            if ( fetchGroup != null )
            {
                pm.getFetchPlan().addGroup( fetchGroup );
            }

            Object objectId = pm.newObjectIdInstance( clazz, new Integer( id ) );

            Object object = pm.getObjectById( objectId );

            object = pm.detachCopy( object );

            tx.commit();

            return object;
        }
        catch ( JDOObjectNotFoundException e )
        {
            throw new ProcessException( "Could not load object with id " + id + " of class : " + clazz.getName(), e );
        }
        catch ( JDOException e )
        {
            throw new ProcessException( "Error handling JDO.", e );
        }
        finally
        {
            PlexusJdoUtils.rollbackIfActive( tx );
        }
    }
}
