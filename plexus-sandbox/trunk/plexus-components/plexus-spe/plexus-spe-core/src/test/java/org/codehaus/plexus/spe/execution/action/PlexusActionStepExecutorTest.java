package org.codehaus.plexus.spe.execution.action;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.action.SaveUserAction;
import org.codehaus.plexus.spe.User;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusActionStepExecutorTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        PlexusActionStepExecutor executor = (PlexusActionStepExecutor) lookup( PlexusActionStepExecutor.ROLE );

        Xpp3Dom executorConfiguration = new Xpp3Dom( "executor-configuration" );
        Xpp3Dom actionId = new Xpp3Dom( "actionId" );
        actionId.setValue( "save-user" );
        executorConfiguration.addChild( actionId );

        Xpp3Dom configuration = new Xpp3Dom( "configuration" );
        Xpp3Dom username = new Xpp3Dom( "username" );
        username.setValue( "trygvis" );
        configuration.addChild( username );

        StepDescriptor descriptor = new StepDescriptor();
        descriptor.setExecutorId( "plexus-action" );
        descriptor.setExecutorConfiguration( executorConfiguration );
        descriptor.setConfiguration( configuration );

        Map<String, Serializable> context = new HashMap<String, Serializable>();

        User user = new User();
        user.setUsername( "trygvis" );
        user.setFirstName( "Trygve" );
        user.setLastName( "Laugstol" );
        context.put( "user", user );

        executor.execute( descriptor, context );

        // ----------------------------------------------------------------------
        // Assertions
        // ----------------------------------------------------------------------

        assertNotNull( SaveUserAction.staticUsername );
        assertEquals( "trygvis", SaveUserAction.staticUsername);

        assertNotNull( SaveUserAction.staticUser );
        assertEquals( "trygvis", SaveUserAction.staticUser.getUsername() );
        assertEquals( "Trygve", SaveUserAction.staticUser.getFirstName() );
        assertEquals( "Laugstol", SaveUserAction.staticUser.getLastName() );
    }
}
