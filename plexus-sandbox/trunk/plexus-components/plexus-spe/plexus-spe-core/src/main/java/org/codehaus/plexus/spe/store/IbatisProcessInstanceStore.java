package org.codehaus.plexus.spe.store;

import com.ibatis.sqlmap.client.SqlMapClient;
import org.codehaus.plexus.ibatis.PlexusIbatisHelper;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.model.LogMessage;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.ProcessInstanceLifecycleListener;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.StepInstance;
import org.codehaus.plexus.util.StringUtils;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class IbatisProcessInstanceStore
    extends AbstractLogEnabled
    implements ProcessInstanceStore, Initializable
{
    /**
     * @plexus.requirement role-hint="plexus-spe"
     */
    private PlexusIbatisHelper ibatisHelper;

    /**
     * @plexus.requirement
     */
    private IbatisSetupHelper setupHelper;

    private ProcessInstanceLifecycleListener processInstanceLifecycleListener;

    private SqlMapClient sqlMap;

    // ----------------------------------------------------------------------
    // ProcessInstanceStore Implementation
    // ----------------------------------------------------------------------

    public synchronized ProcessInstance createInstance( ProcessDescriptor processDescriptor,
                                                        Map<String, Serializable> context )
        throws ProcessException
    {
        getLogger().info( "Creating a new process instance: " + processDescriptor.getId() + "." );

        IbatisProcessInstance instance = new IbatisProcessInstance( processDescriptor.getId() );
        instance.setContext( context );
        instance.setCreatedTime( System.currentTimeMillis() );

        try
        {
            sqlMap.startTransaction();

            // -----------------------------------------------------------------------
            // Serialize the context
            // -----------------------------------------------------------------------

            instance.serialize();

            String id = sqlMap.insert( "insertProcessInstance", instance ).toString();

            if ( id == null )
            {
                throw new ProcessException( "Implementation error: the insert statement has to return an id." );
            }

            instance.setId( id );

            // -----------------------------------------------------------------------
            // Store all the steps
            // -----------------------------------------------------------------------

            int i = 0;
            for ( StepDescriptor action : processDescriptor.getSteps() )
            {
                if ( StringUtils.isEmpty( action.getExecutorId() ) )
                {
                    throw new ProcessException( "Each step has to have an executor id." );
                }

                StepInstance step = new StepInstance();
                step.setId( Integer.toString( i ) );
                step.setProcessInstanceId( id );
                step.setExecutorId( action.getExecutorId() );

                sqlMap.insert( "insertStepInstance", step );
                i++;
                instance.addStep( step );
            }

            sqlMap.commitTransaction();

            return getInstance( id, true );
        }
        catch ( SQLException e )
        {
            throw new ProcessException( "Error while storing process instance.", e );
        }
        finally
        {
            endTransaction();
        }
    }

    public synchronized Collection<? extends ProcessInstance> getActiveInstances()
        throws ProcessException
    {
        try
        {
            sqlMap.startTransaction();

            Collection<IbatisProcessInstance> processes = sqlMap.queryForList( "selectProcessInstanceBasicRunning", null );

            sqlMap.commitTransaction();

            return processes;
        }
        catch ( SQLException e )
        {
            throw new ProcessException( "Error while fetcing process instance.", e );
        }
        finally
        {
            endTransaction();
        }
    }

    public Collection<? extends ProcessInstance> getInstances()
        throws ProcessException
    {
        try
        {
            sqlMap.startTransaction();

            Collection<IbatisProcessInstance> processes = sqlMap.queryForList( "selectProcessInstanceBasic", null );

            sqlMap.commitTransaction();

            return processes;
        }
        catch ( SQLException e )
        {
            throw new ProcessException( "Error while fetcing process instance.", e );
        }
        finally
        {
            endTransaction();
        }
    }

    public synchronized void saveInstance( ProcessInstance processInstance )
        throws ProcessException
    {
        getLogger().debug( "Storing process instance " + processInstance.getId() + ", process: " +
            processInstance.getProcessId() + "." );

        IbatisProcessInstance instance = new IbatisProcessInstance( processInstance );

        instance.serialize();

        try
        {
            sqlMap.startTransaction();

            sqlMap.update( "updateProcessInstance", instance );

            // -----------------------------------------------------------------------
            // Empty all the existing log message array
            // -----------------------------------------------------------------------

            sqlMap.delete( "deleteStepInstanceLogMessage",
                           Collections.singletonMap( "processInstanceId", processInstance.getId() ) );

            for ( StepInstance step : processInstance.getSteps() )
            {
                sqlMap.update( "updateStepInstance", step );

                // -----------------------------------------------------------------------
                // Insert all the log messages
                // -----------------------------------------------------------------------

                int sequenceNo = 0;
                Map<String, Serializable> parameters = new HashMap<String, Serializable>();
                parameters.put( "stepInstanceId", step.getId() );
                parameters.put( "processInstanceId", processInstance.getId() );
                for ( LogMessage logMessage : (List<LogMessage>) step.getLogMessages() )
                {
                    parameters.put( "sequenceNo", sequenceNo++ );
                    parameters.put( "logMessage", logMessage.getMessage() );
                    parameters.put( "timestamp", logMessage.getTimestamp() );
                    sqlMap.insert( "insertStepInstanceLogMessage", parameters );
                }
            }

            sqlMap.commitTransaction();
        }
        catch ( SQLException e )
        {
            throw new ProcessException( "Error while fetcing process instance.", e );
        }
        finally
        {
            endTransaction();
        }
    }

    public synchronized ProcessInstance getInstance( String id, boolean includeContext )
        throws ProcessException
    {
        try
        {
            sqlMap.startTransaction();

            Map<Object, Object> map = new HashMap<Object, Object>();
            map.put( "id", id );

            IbatisProcessInstance processInstance =
                (IbatisProcessInstance) sqlMap.queryForObject( "selectProcessInstanceFull", map );

            processInstance.deserialize();

            // Because of some iBatis bug the log messages can't be loaded properly. Waiting for 2.2.0 and 2.3.0 to be
            // uploaded to the Maven repository

            for ( StepInstance stepInstance : processInstance.getSteps() )
            {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put( "processInstanceId", processInstance.getId() );
                parameters.put( "stepInstanceId", stepInstance.getId() );
                List<LogMessage> list = sqlMap.queryForList( "selectStepInstanceLogMessage", parameters );
                stepInstance.setLogMessages( list );
            }

            sqlMap.commitTransaction();

            return processInstance;
        }
        catch ( SQLException e )
        {
            throw new ProcessException( "Error while fetcing process instance.", e );
        }
        finally
        {
            endTransaction();
        }
    }

    public synchronized void deleteInstance( String id )
        throws ProcessException
    {
        getLogger().info( "Deleting process instance " + id );

        try
        {
            sqlMap.startTransaction();

            sqlMap.delete( "deleteStepInstanceLogMessage", Collections.singletonMap( "processInstanceId", id ) );
            sqlMap.delete( "deleteStepInstance", Collections.singletonMap( "processInstanceId", id ) );
            sqlMap.delete( "deleteProcessInstance", Collections.singletonMap( "processInstanceId", id ) );

            sqlMap.commitTransaction();
        }
        catch ( SQLException e )
        {
            throw new ProcessException( "Error while deleting process instance.", e );
        }
        finally
        {
            endTransaction();
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        sqlMap = ibatisHelper.getSqlMapClient();

        processInstanceLifecycleListener = new ProcessInstanceLifecycleListener();
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void endTransaction()
    {
        try
        {
            sqlMap.endTransaction();
        }
        catch ( SQLException e )
        {
            getLogger().error( "Error while ending transaction.", e );
        }
    }
}
