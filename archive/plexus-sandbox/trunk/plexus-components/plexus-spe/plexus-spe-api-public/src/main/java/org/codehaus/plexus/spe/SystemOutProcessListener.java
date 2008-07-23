package org.codehaus.plexus.spe;

import org.codehaus.plexus.spe.event.StartingProcessEvent;
import org.codehaus.plexus.spe.event.StartingStepEvent;
import org.codehaus.plexus.spe.event.StepCompletedEvent;
import org.codehaus.plexus.spe.utils.DurationFormatUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SystemOutProcessListener
    implements ProcessListener
{
    public void onProcessEvent( ProcessEvent event )
    {
        if ( event instanceof StartingProcessEvent )
        {
            onStartingProcessEvent( (StartingProcessEvent) event );
        }
        else if ( event instanceof StartingStepEvent )
        {
            onStartingStepEvent( (StartingStepEvent) event );
        }
        else if ( event instanceof StepCompletedEvent )
        {
            onStepCompletedEvent( (StepCompletedEvent) event );
        }
    }

    private void onStartingProcessEvent( StartingProcessEvent event )
    {
        System.out.println( "Starting process " + event.getProcessId() + "." );
    }

    private void onStartingStepEvent( StartingStepEvent event )
    {
        System.out.println( "Starting step #" + event.getStepNumber() + " in process " + event.getProcessId() + ", instance: " + event.getProcessInstanceId() + "." );
    }

    private void onStepCompletedEvent( StepCompletedEvent event )
    {
        long interval = event.getStepInstance().getEndTime() - event.getProcessInstance().getCreatedTime();
        String duration = DurationFormatUtils.formatDuration( interval, "H:mm:ss.SSS", false );

        if ( event.getThrowable() == null )
        {
            System.out.println( "Step completed successully. Time elapsed so far: " + duration );
        }
        else
        {
            event.getThrowable().printStackTrace( System.out );

            System.out.println( "Step failed. Time elapsed so far: " + duration );
        }
    }
}
