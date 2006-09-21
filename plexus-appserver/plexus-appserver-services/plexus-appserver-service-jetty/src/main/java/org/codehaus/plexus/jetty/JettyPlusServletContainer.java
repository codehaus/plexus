package org.codehaus.plexus.jetty;

import org.mortbay.xml.XmlConfiguration;
import org.mortbay.jetty.plus.Server;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;

import java.io.File;

public class JettyPlusServletContainer
    extends AbstractJettyServletContainer
{
    private File jettyXmlFile;

    public void setJettyXmlFile( File jettyXmlFile )
    {
        this.jettyXmlFile = jettyXmlFile;
    }

    public File getJettyXmlFile()
    {
        return jettyXmlFile;
    }

    public void applyJettyXml()
        throws Exception
    {
        if ( jettyXmlFile == null )
        {
            return;
        }

        if ( jettyXmlFile.exists() )
        {
            getLogger().info( "Configuring Jetty from xml configuration file = " + jettyXmlFile.getCanonicalPath() );
            XmlConfiguration xmlConfiguration = new XmlConfiguration( jettyXmlFile.getCanonicalFile().toURL() );
            xmlConfiguration.configure( server );
        }
    }

    public void start()
        throws StartingException
    {
        server = new Server();

        try
        {
            applyJettyXml();

            server.start();
        }
        catch ( Exception e )
        {
            throw new StartingException( "Error while starting Jetty", e );
        }
    }
}
