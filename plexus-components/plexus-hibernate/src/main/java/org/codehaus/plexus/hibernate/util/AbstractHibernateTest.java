package org.codehaus.plexus.hibernate.util;

import java.sql.Connection;

import net.sf.hibernate.Session;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.hibernate.HibernateService;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public abstract class AbstractHibernateTest
    extends PlexusTestCase
{
    Connection conn;

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
        
        String filename = getTestPath( System.getProperty("plexus.hibernate.setupSql") );
        insertSqlFile(filename);
    }
    
    public void tearDown() throws Exception
    {
        String basedir = System.getProperty("basedir", ".");
        
        String filename = getTestPath( System.getProperty("plexus.hibernate.teardownSql") );

        insertSqlFile(filename);
        
        conn = null;
        
        super.tearDown();
    }
}
