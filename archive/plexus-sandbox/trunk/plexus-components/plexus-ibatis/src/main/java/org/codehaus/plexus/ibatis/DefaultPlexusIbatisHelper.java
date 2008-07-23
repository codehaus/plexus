package org.codehaus.plexus.ibatis;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.Reader;
import java.io.IOException;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.common.resources.Resources;

/**
 * A simple class to initialize an iBatis {@link SqlMapClient}.
 * <ul>
 *   <li>Add support for lazy initialization</li>
 * </ul>
 *
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPlexusIbatisHelper
    extends AbstractLogEnabled
    implements PlexusIbatisHelper, Initializable
{
    /**
     * @plexus.configuration default-value="ibatis-config.xml"
     */
    private String resource;

    /**
     * @plexus.configuration
     */
    private Properties properties;

    private SqlMapClient sqlMap;

    // -----------------------------------------------------------------------
    // PlexusIbatisHelper Implementation
    // -----------------------------------------------------------------------

    public SqlMapClient getSqlMapClient()
    {
        return sqlMap;
    }

    // -----------------------------------------------------------------------
    // Component Lifecycle
    // -----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        if ( resource == null || resource.trim().length() == 0 )
        {
            throw new InitializationException( "Invalid configuration, the 'resource' configuration parameter has to be set." );
        }

        // -----------------------------------------------------------------------
        // Dump configuration
        // -----------------------------------------------------------------------

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Initializing iBatis from resource: " + resource );

            if ( properties != null )
            {
                getLogger().debug( "Extra properties:" );

                for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); )
                {
                    Map.Entry entry = (Map.Entry) it.next();

                    getLogger().debug( entry.getKey() + "='" + entry.getValue() + "'" );
                }
            }
        }

        try
        {
            Reader reader = Resources.getResourceAsReader (resource);
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader, properties );
        }
        catch ( IOException e )
        {
            throw new InitializationException( "Error while initializing iBatis.", e );
        }
    }
}
