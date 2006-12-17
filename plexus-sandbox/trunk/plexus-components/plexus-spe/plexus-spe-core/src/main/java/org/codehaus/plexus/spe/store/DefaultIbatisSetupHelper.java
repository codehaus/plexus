package org.codehaus.plexus.spe.store;

import com.ibatis.sqlmap.client.SqlMapClient;
import org.codehaus.plexus.ibatis.PlexusIbatisHelper;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultIbatisSetupHelper
    extends AbstractLogEnabled
    implements IbatisSetupHelper, Initializable
{
    /**
     * @plexus.requirement role-hint="plexus-spe"
     */
    private PlexusIbatisHelper ibatisHelper;

    public void initialize()
        throws InitializationException
    {
        SqlMapClient sqlMap = ibatisHelper.getSqlMapClient();

        try
        {
            sqlMap.startTransaction();

            Connection con = sqlMap.getCurrentConnection();

            DatabaseMetaData databaseMetaData = con.getMetaData();

            ResultSet rs = databaseMetaData.getTables( con.getCatalog(), null, null, null );

            while ( rs.next() )
            {
                String tableName = rs.getString( "TABLE_NAME" );

                if ( tableName.toLowerCase().equals( "processinstance" ) )
                {
                    return;
                }
            }

            getLogger().info( "Creating process instance table" );
            sqlMap.update( "createTableProcessInstance", null );

            getLogger().info( "Creating step instance table" );
            sqlMap.update( "createTableStepInstance", null );

            sqlMap.commitTransaction();
        }
        catch ( SQLException e )
        {
            throw new InitializationException( "Error while setting up database.", e );
        }
        finally
        {
            try
            {
                sqlMap.endTransaction();
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }
        }
    }
}
