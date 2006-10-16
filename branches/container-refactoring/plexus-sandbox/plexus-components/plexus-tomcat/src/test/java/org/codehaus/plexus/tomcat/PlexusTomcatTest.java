package org.codehaus.plexus.tomcat;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.startup.Embedded;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusTomcatTest
    extends PlexusTestCase
{
    public void testTomcat()
        throws Exception
    {
        String catalinaHome = "/home/trygvis/servers/jakarta-tomcat-5.0.28/";
        String contextRoot = "src/main/webapp";

        FileUtils.mkdir( catalinaHome );
        FileUtils.mkdir( contextRoot );

        System.setProperty( "catalina.home", catalinaHome );

        Embedded embedded = new Embedded();
//        embedded.setDebug( 1000 );
//        embedded.setLogger( new SystemOutLogger() );

        Engine engine = embedded.createEngine();
        engine.setDefaultHost( "localhost" );

        Host host = embedded.createHost( "localhost", catalinaHome + "/webapps" );
        engine.addChild( host );

        Context context = embedded.createContext( "", getTestPath( "src/main/webapp" ) );
//        context.setName( "Unprotected Context" );
        host.addChild( context );

        context = embedded.createContext( "/protected", getTestPath( "src/main/webapp" ) );
//        context.setName( "Protected Context");
        host.addChild( context );

        embedded.addEngine( engine );

        Connector connector = embedded.createConnector( "0.0.0.0", 8080, false );
        embedded.addConnector( connector );

        embedded.start();

        System.err.println( "RUNNING" );
        getUrl( "http://localhost:8080" );
        System.err.println( "STOPPING" );

        embedded.stop();

        embedded.destroy();
    }

    public void testBasic()
        throws Exception
    {
        File catalinaHome = new File( "/home/trygvis/servers/jakarta-tomcat-5.0.28/" );
        File contextRoot = getTestFile( "target/context-root" );

        System.setProperty( "catalina.home", catalinaHome.getAbsolutePath() );

        FileUtils.deleteDirectory( contextRoot );
        FileUtils.mkdir( contextRoot.getAbsolutePath() );

        PlexusTomcat tomcat = (PlexusTomcat) lookup( PlexusTomcat.ROLE );
        tomcat.setContextRoot( contextRoot );
        tomcat.setCatalinaHome( catalinaHome );

        Engine engine = tomcat.getEngine();

        Host host = tomcat.addHost( "localhost", "/tmp/foo" );
        engine.addChild( host );

        Context context = tomcat.addContext( host.getName(), "Unprotected Context", "", new File( "/" ) );
        host.addChild( context );

        context = tomcat.addContext( host.getName(), "Protected Context", "/protected", new File( "/" ) );
        host.addChild( context );

//        JNDIRealm realm = new JNDIRealm();
//        realm.setConnectionURL( "ldap://localhost:10389" );
//        realm.setAlternateURL( "ldap://localhost:10390" );
//        realm.setRoleBase( "ou=Groups,dc=objectware,dc=no" );
//        realm.setRoleName( "roleOccupant" );
//        realm.setUserBase( "ou=People,dc=objectware,dc=no" );
//
//        context.setRealm( realm );

        tomcat.startTomcat();
        System.err.println( "RUNNING" );

//        tomcat.addConnector( "localhost", 8080, false );

//        Thread.sleep( 60000 );
        Thread.sleep( 10000 );

        getUrl( "http://localhost:8080" );
        System.err.println( "STOPPING" );

        tomcat.stopTomcat();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void getUrl( String url )
        throws IOException
    {
        InputStream inputStream = new URL( url ).openStream();
        String content = IOUtil.toString( inputStream );
        System.err.println( content );
    }
}
