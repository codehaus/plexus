package org.codehaus.plexus.service.jetty;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.FileReader;

public class JettyServiceTest
    extends PlexusTestCase
{
    public void testJettyService()
        throws Exception
    {
        PlexusService service = (PlexusService) lookup( PlexusService.ROLE, "jetty" );

        DefaultPlexusContainer container = (DefaultPlexusContainer) getContainer();
        AppRuntimeProfile profile = new AppRuntimeProfile( "test", null, null, container, null );

        Xpp3Dom dom = Xpp3DomBuilder.build( new FileReader( getTestFile( "src/test/resources/test-service.xml" ) ) );

        PlexusConfiguration configuration = new XmlPlexusConfiguration( dom );

        service.beforeApplicationStart( profile, configuration );

        service.afterApplicationStart( profile, configuration );

    }
}
