package org.codehaus.plexus.spe;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.spe.core.ProcessEventManager;
import org.codehaus.plexus.spe.execution.ProcessExecutor;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.io.xpp3.ProcessDescriptorXpp3Reader;
import org.codehaus.plexus.spe.store.ProcessInstanceStore;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultProcessService
    extends AbstractLogEnabled
    implements ProcessService, Initializable, Startable
{
    /**
     * @plexus.requirement
     */
    private ProcessInstanceStore store;

    /**
     * @plexus.requirement
     */
    private ProcessExecutor executor;

    /**
     * @plexus.requirement
     */
    private ProcessEventManager eventManager;

    private Map<String, ProcessDescriptor> processes = new HashMap<String, ProcessDescriptor>( );

    // ----------------------------------------------------------------------
    // ProcessService Implementation
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // System Control
    // ----------------------------------------------------------------------

    public ProcessDescriptor loadProcess( URL url )
        throws ProcessException
    {
        ProcessDescriptor process = loadAndValidate( url );

        processes.put( process.getId(), process );

        getLogger().info( "Loaded process " + process.getId() + " from " + url.toExternalForm() );

        return process;
    }

    public Collection<ProcessDescriptor> loadProcessDirectory( File directory )
        throws ProcessException
    {
        if ( ! directory.isDirectory() )
        {
            throw new ProcessException( "The specified file is not a directory: '" + directory.getAbsolutePath() + "'." );
        }

        File[] files = directory.listFiles();

        Map<String, ProcessDescriptor> descriptors = new HashMap<String, ProcessDescriptor>();

        for ( File file : files )
        {
            if ( !file.isFile() )
            {
                continue;
            }

            try
            {
                URL url = file.toURL();

                ProcessDescriptor descriptor = loadAndValidate( url );

                descriptors.put( descriptor.getId(), descriptor );

                getLogger().info( "Loaded process " + descriptor.getId() + " from " + url.toExternalForm() );
            }
            catch ( MalformedURLException e )
            {
                throw new ProcessException( "Error while reading process descriptor from '" + file.getAbsolutePath() + "'.", e );
            }
        }

        processes.putAll( descriptors );

        return descriptors.values();
    }

    public void addProcessListener( ProcessListener processListener )
    {
        eventManager.addProcessListener( processListener );
    }

    public void removeProcessListener( ProcessListener processListener )
    {
        eventManager.removeProcessListener( processListener );
    }

    // ----------------------------------------------------------------------
    // Process Control
    // ----------------------------------------------------------------------

    public int executeProcess( String processId, Map<String, Serializable> context )
        throws ProcessException
    {
        ProcessDescriptor process = processes.get( processId );

        if ( process == null )
        {
            throw new ProcessException( "No such process: " + processId );
        }

        ProcessInstance processState = store.createInstance( process, context );

        executor.startProcess( process, processState );

        return processState.getInstanceId();
    }

    /**
     * Returns true if this process has stopped, either because it completed successfully or because it failed
     * and cannot be started again.
     */
    public boolean hasCompleted( int instanceId )
        throws ProcessException
    {
        ProcessInstance instance = store.getInstance( instanceId, false );

        return instance.getEndTime() > 0 || StringUtils.isNotEmpty( instance.getErrorMessage() );
    }

    public ProcessInstance getProcessInstance( int instanceId )
        throws ProcessException
    {
        return store.getInstance( instanceId, true );
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
    }

    public void start()
        throws StartingException
    {
        try
        {
            Collection<ProcessInstance> processInstances = store.getActiveInstances();

            getLogger().info( "Active processes:" );

            for ( ProcessInstance instance : processInstances )
            {
                getLogger().info( " Instance: " + instance.getInstanceId() + " of process: " + instance.getProcessId() );
            }
        }
        catch ( ProcessException e )
        {
            throw new StartingException( "Error while loading processes.", e );
        }

    }

    public void stop()
        throws StoppingException
    {
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void validateProcess( ProcessDescriptor process )
        throws ProcessException
    {
        String id = process.getId();

        if ( StringUtils.isEmpty( id ) )
        {
            throw new ProcessException( "Invalid process descriptor: Missing id." );
        }

        String defaultExecutorId = process.getDefaultExecutorId();

        int i = 0;

        for ( StepDescriptor stepDescriptor : (List<StepDescriptor>) process.getSteps() )
        {
            if ( StringUtils.isEmpty( stepDescriptor.getExecutorId() ) )
            {
                stepDescriptor.setExecutorId( defaultExecutorId );

                if ( StringUtils.isEmpty( stepDescriptor.getExecutorId() ) )
                {
                    throw new ProcessException( "Invalid process descriptor: Step #" + i + " is missing 'executor id'." );
                }
            }

            if ( stepDescriptor.getExecutorConfiguration() == null )
            {
                stepDescriptor.setExecutorConfiguration( new Xpp3Dom( "executorConfiguration" ) );
            }

            if ( stepDescriptor.getConfiguration() == null )
            {
                stepDescriptor.setConfiguration( new Xpp3Dom( "configuration" ) );
            }

            i++;
        }
    }

    private ProcessDescriptor loadAndValidate( URL url )
        throws ProcessException
    {
        ProcessDescriptorXpp3Reader reader = new ProcessDescriptorXpp3Reader();

        try
        {
            ProcessDescriptor processDescriptor = reader.read( new InputStreamReader( url.openStream() ) );

            validateProcess( processDescriptor );

            return processDescriptor;
        }
        catch ( IOException e )
        {
            throw new ProcessException( "Error while reading process descriptor from '" + url.toExternalForm() + "'.", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new ProcessException( "Error while reading process descriptor from '" + url.toExternalForm() + "'.", e );
        }
    }
}
