package org.codehaus.plexus.scheduler;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

/**
 * Default <code>Scheduler</code> implementation, backed by quartz.
 *
 * @author <a href="john@zenplex.com">John Thorhauer</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 * @todo configuration needs to be easier
 * @todo logging needs to be reworked: logging per job
 */
public class DefaultScheduler
    extends AbstractLogEnabled
    implements Scheduler, Initializable, Startable
{
    private Properties properties;

    private StdScheduler scheduler;

    public void scheduleJob( JobDetail jobDetail, Trigger trigger )
        throws SchedulerException
    {
        scheduler.scheduleJob( jobDetail, trigger );
    }

    public void initialize()
        throws InitializationException
    {
        try
        {
            SchedulerFactory factory = new StdSchedulerFactory( properties );

            scheduler = (StdScheduler) factory.getScheduler();
        }
        catch ( SchedulerException e )
        {
            throw new InitializationException( "Cannot create scheduler.", e );
        }
    }

    public void start()
        throws StartingException
    {
        try
        {
            scheduler.start();
        }
        catch ( SchedulerException e )
        {
            throw new StartingException( "Cannot start scheduler.", e );
        }
    }

    public void stop()
        throws StoppingException
    {
        scheduler.shutdown();
    }
}
