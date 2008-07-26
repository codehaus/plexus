package org.codehaus.plexus.hibernate.util;

import java.sql.Connection;

import net.sf.hibernate.Session;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.hibernate.HibernateService;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 * @version $Id$
 */
public abstract class AbstractHibernateTest
    extends PlexusTestCase
{
    private Connection conn;

    public final static String SETUP_SQL = "plexus.hibernate.setupSql";
    public final static String TEARDOWN_SQL = "plexus.hibernate.teardownSql";
    
    protected void insertSqlFile( String filename ) throws Exception
    {
        HibernateService hib = (HibernateService) lookup( HibernateService.ROLE );

        Session sess = hib.getSessionFactory().openSession();
        conn = sess.connection();

        BatchSqlCommandRunner runner = new BatchSqlCommandRunner(conn);
        runner.runCommands( filename );

        sess.close();

        release( hib );
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        String filename = getTestPath( System.getProperty(SETUP_SQL) );

        try
        {
            insertSqlFile(filename);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void tearDown() throws Exception
    {
        String filename = getTestPath( System.getProperty(TEARDOWN_SQL) );

        try
        {
            insertSqlFile(filename);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        conn = null;

        super.tearDown();
    }
}
