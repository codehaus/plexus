package org.codehaus.plexus.velocity;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.LogSystem;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * A simple velocity component implementation.
 * <p/>
 * A typical configuration will look like this:
 * <pre>
 *      <configuration>
 *        <properties>
 *          <property>
 *            <name>runtime.log.logsystem.class</name>
 *            <value>org.codehaus.plexus.velocity.Log4JLoggingSystem</value>
 *          </property>
 *          <property>
 *            <name>runtime.log.logsystem.log4j.category</name>
 *            <value>org.codehaus.plexus.velocity.DefaultVelocityComponentTest</value>
 *          </property>
 *          <property>
 *            <name>resource.loader</name>
 *            <value>classpath</value>
 *          </property>
 *          <property>
 *            <name>classpath.resource.loader.class</name>
 *            <value>org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader</value>
 *          </property>
 *        </properties>
 *      </configuration>
 * </pre>
 */
public class DefaultVelocityComponent
    extends AbstractLogEnabled
    implements VelocityComponent, Initializable, LogSystem
{
    private VelocityEngine engine;

    private Properties properties;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------
    
    public void initialize()
        throws InitializationException
    {
        engine = new VelocityEngine();

        engine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, this );

        if ( properties != null )
        {
            for ( Enumeration e = properties.propertyNames(); e.hasMoreElements(); )
            {
                String key = e.nextElement().toString();
    
                String value = properties.getProperty( key );
    
                engine.setProperty( key, value );
    
                getLogger().info( "Setting property: " + key + " => '" + value + "'." );
            }
        }

        try
        {
            engine.init();
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Cannot start the velocity engine: ", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public VelocityEngine getEngine()
    {
        return engine;
    }
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private RuntimeServices runtimeServices;

    public void init( RuntimeServices runtimeServices )
    {
        this.runtimeServices = runtimeServices;
    }

    public void logVelocityMessage( int level, String message )
    {
        switch ( level )
        {
            case LogSystem.WARN_ID:
                getLogger().warn( message );
                break;
            case LogSystem.INFO_ID:
                getLogger().info( message );
                break;
            case LogSystem.DEBUG_ID:
                getLogger().debug( message );
                break;
            case LogSystem.ERROR_ID:
                getLogger().error( message );
                break;
            default:
                getLogger().info( message );
                break;
        }
    }
}
