package org.codehaus.plexus.scheduler;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.codehaus.plexus.logging.AbstractLogEnabled;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;

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
    implements Scheduler, Contextualizable, Configurable, Initializable, Startable, ThreadSafe, Serviceable
{
    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** Scheduler properties. */
    private Properties schedulerProperties;

    /** Quartz Scheduler */
    private StdScheduler scheduler;

    /** * Job directory. */
    private File jobDirectory;

    /** ServiceBroker */
    private ServiceManager serviceManager;

    /** Inline job descriptions in the components configuration file **/
    private Configuration inlineJobConfigurations;

    private File workDirectory;

    private Context context;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /** Construct. */
    public DefaultScheduler()
    {
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public Context getContext()
    {
        return context;
    }

    public void setContext( Context context )
    {
        this.context = context;
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * Sets the jobDirectory attribute of the DefaultScheduler object
     */
    public void setJobDirectory( File jobDirectory )
    {
        this.jobDirectory = jobDirectory;
    }

    /**
     * Gets the jobDirectory attribute of the DefaultScheduler object
     */
    public File getJobDirectory()
    {
        return this.jobDirectory;
    }

    /**
     * Sets the inline job configurations that were specified directly
     * in the scheduler's configuration.
     *
     * @param configurations Configuration of inline job specifications.
     */
    public void setInlineJobConfigurations( Configuration configurations )
    {
        this.inlineJobConfigurations = configurations;
    }

    /**
     * Gets the inline job configurations that were specified directly
     * in the scheduler's configuration.
     *
     * @return Configuration of inline job specifications.
     */
    public Configuration getInlineJobConfigurations()
    {
        return this.inlineJobConfigurations;
    }

    /**
     * Gets the schedulerProperties attribute of the DefaultScheduler object
     */
    Properties getSchedulerProperties()
    {
        return this.schedulerProperties;
    }

    /**
     * Gets the scheduler attribute of the DefaultScheduler object
     */
    StdScheduler getScheduler()
    {
        return this.scheduler;
    }

    /**
     * Gets the serviceManager attribute of the DefaultScheduler object
     */
    ServiceManager getServiceManager()
    {
        return serviceManager;
    }

    /**
     * Scheduler a new job for the scheduler to run
     *
     * @param jobDetail that the scheduler will run
     * @param trigger to run the job
     * @throws SchedulerException If an error occurs while attempting to
     *      schedule the job.
     */
    public void scheduleJob( JobDetail jobDetail,
                             Trigger trigger )
        throws SchedulerException
    {
        getLogger().info( "scheduling job: " + jobDetail + " " + trigger );
        getScheduler().scheduleJob( jobDetail, trigger );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        this.context = context;

        try
        {
            workDirectory = (File) context.get( "work.directory" );
        }
        catch ( ContextException e )
        {
            // do nothing.
        }
    }

    public void service( ServiceManager serviceManager )
    {
        this.serviceManager = serviceManager;
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        getLogger().info( "configure" );
        configureComponent( configuration );
        configureScheduler( configuration.getChild( "scheduler", true ) );
    }

    /**
     * Initialize scheduler component with any available jobs found in jars in
     * the jobDirectory specified in the configuration file.
     */
    public void initialize()
        throws Exception
    {
        initializeScheduler();
        initializeJobsFiles();
        initializeInlineJobs();
    }

    /**
     * Start the scheduler.
     *
     * @throws Exception If an error occurs while attempting to start.
     */
    public void start()
        throws Exception
    {
        getLogger().info( "starting scheduler" );
        getScheduler().start();
    }

    /**
     * Stop the scheduler.
     *
     * @throws Exception If an error occurs while attempting to stop.
     */
    public void stop()
        throws Exception
    {
        getLogger().info( "stopping scheduler" );
        getScheduler().shutdown();
    }

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    void configureComponent( Configuration configuration )
        throws ConfigurationException
    {
        setInlineJobConfigurations( configuration.getChild( "jobs" ) );

        String jobDir = configuration.getChild( "job-directory" ).getValue( "" );

        if ( jobDir == null
             ||
             jobDir.equals( "" ) )
        {
            getLogger().info( "job directory specified" );
            return;
        }

        if ( workDirectory == null )
        {
            getLogger().warn( "no plexus deployment directory - static job jars will not be loaded" );
            return;
        }

        File jobDirectory = new File( workDirectory, jobDir );
        if ( jobDirectory.exists() == false )
        {
            jobDirectory.mkdirs();
        }

        setJobDirectory( jobDirectory );
    }

    void configureScheduler( Configuration configuration )
        throws ConfigurationException
    {
        this.schedulerProperties = new Properties();

        Configuration[] propConfigs = configuration.getChildren( "property" );

        String name = null;
        String value = null;

        for ( int i = 0; i < propConfigs.length; ++i )
        {
            name = propConfigs[i].getAttribute( "name" );
            value = propConfigs[i].getAttribute( "value" );

            this.schedulerProperties.setProperty( name, value );
        }
    }


    void initializeScheduler()
        throws Exception
    {
        SchedulerFactory factory = new StdSchedulerFactory( getSchedulerProperties() );

        scheduler = (StdScheduler) factory.getScheduler();
        DefaultJobListener jobListener = new DefaultJobListener();
        scheduler.addGlobalJobListener( jobListener );
    }

    void initializeJobsFiles()
        throws Exception
    {
        File jobDir = getJobDirectory();

        if ( jobDir == null )
        {
            getLogger().info( "no job directory configured" );
            return;
        }

        File[] jobFiles = jobDir.listFiles(
            new FileFilter()
            {
                public boolean accept( File file )
                {
                    return ( file.getName().endsWith( ".jar" )
                        ||
                        file.getName().endsWith( ".zip" ) );
                }
            }
        );

        for ( int i = 0; i < jobFiles.length; ++i )
        {
            initializeJobFile( jobFiles[i] );
        }
    }

    /**
     * Adds a feature to the JobFile attribute of the DefaultScheduler object
     */
    public void addJobFile( File jobFile )
        throws Exception
    {
        getLogger().info( "adding job file: " + jobFile.getPath() );
        initializeJobFile( jobFile );
    }

    /**
     * Description of the Method
     */
    void initializeJobFile( File jobFile )
        throws Exception
    {
        JarFile jarFile = new JarFile( jobFile );

        Enumeration entryEnum = jarFile.entries();
        ZipEntry eachEntry = null;

        while ( entryEnum.hasMoreElements() )
        {
            eachEntry = (ZipEntry) entryEnum.nextElement();

            if ( eachEntry.getName().equals( "jobs.xml" ) )
            {
                initializeJobFile( jobFile, jarFile.getInputStream( eachEntry ) );
                break;
            }
        }
    }

    /**
     * Description of the Method
     */
    void initializeJobFile( File jobFile,
                            InputStream jobsConfigStream )
        throws Exception
    {
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        getLogger().info( "reading jobs.xml" );

        Configuration jobsConfig = builder.parse( new InputStreamReader( jobsConfigStream ) );

        jobsConfigStream.close();

        // The classloader used to load the scheduler itself is the
        // PlexusClassLoader brought to life by the ResourceManager.
        // This will be cleaner with Classworlds but this works just
        // fine for now.
        ClassLoader parent = DefaultScheduler.class.getClassLoader();
        PlexusClassLoader loader = new PlexusClassLoader( parent );
        loader.addURL( jobFile.toURL() );

        initializeJobs( loader, jobsConfig, jobFile.toString() );
    }

    /**
     * Initializes the jobs that have been specified inline.
     */
    void initializeInlineJobs()
    {
        if ( getInlineJobConfigurations() == null )
        {
            return;
        }

        getLogger().info( "reading inline job specifications" );

        ClassLoader loader = DefaultScheduler.class.getClassLoader();

        initializeJobs( loader, getInlineJobConfigurations(), "inline source" );
    }

    /**
     * Initializes a group of jobs.
     *
     * @param loader The classloader to use when instantiating the class
     * @param jobsConfig A configuration representing one or more job
     * configurations.
     * @param source A string identifying the source of this group of
     * jobs.
     */
    void initializeJobs( ClassLoader loader, Configuration jobsConfig, String source )
    {
        Configuration[] jobConfigs = jobsConfig.getChildren( "job" );

        for ( int i = 0; i < jobConfigs.length; ++i )
        {
            try
            {
                initializeJob( loader, jobConfigs[i] );
            }
            catch ( Exception e )
            {
                getLogger().error( "error initializing job from " + source, e );
            }
        }
    }

    /**
     * Description of the Method
     */
    void initializeJob( ClassLoader loader,
                        Configuration jobConfig )
        throws Exception
    {
        String name = jobConfig.getChild( "name" ).getValue();
        String group = jobConfig.getChild( "group" ).getValue();
        String className = jobConfig.getChild( "class" ).getValue();
        String cronSpec = jobConfig.getChild( "cron-spec" ).getValue();
        Configuration jobExecutionConfiguration = jobConfig.getChild( "configuration" );

        getLogger().info( "new job: " + group + "/" + name + " " + className + " " + cronSpec );

        Class jobClass = loader.loadClass( className );

        JobDetail jobDetail = new JobDetail( name, group, jobClass );

        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        jobDataMap.put( AbstractJob.LOGGER, getLogger() );
        jobDataMap.put( AbstractJob.CONTEXT, getContext() );
        jobDataMap.put( AbstractJob.SERVICE_MANAGER, getServiceManager() );
        jobDataMap.put( AbstractJob.EXECUTION_CONFIGURATION, jobExecutionConfiguration );


        // Allow transient data in the job data map. This will make sure
        // that non-serializable objects are nulled out before persistence
        // is attempted in order to prevent errors.
        jobDataMap.setAllowsTransientData( true );

        CronTrigger trigger = new CronTrigger( name,
                                               group,
                                               name,
                                               group,
                                               cronSpec );

        scheduleJob( jobDetail, trigger );
    }
}
