package org.codehaus.plexus.spe.execution.action;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.action.SaveUserAction;
import org.codehaus.plexus.spe.User;
import org.codehaus.plexus.spe.utils.DocumentWrapper;
import org.codehaus.plexus.spe.execution.StepExecutor;
import org.codehaus.plexus.spe.execution.CollectingStepEventListener;

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
        PlexusActionStepExecutor executor = (PlexusActionStepExecutor) lookup( StepExecutor.ROLE, "plexus-action" );

        DocumentWrapper executorConfiguration = new DocumentWrapper( "executor-configuration" );
        executorConfiguration.addChildText( "actionId", "save-user" );

        DocumentWrapper configuration = new DocumentWrapper( "configuration" );
        configuration.addChildText( "username", "trygvis" );

        StepDescriptor descriptor = new StepDescriptor( "plexus-action",
                                                        executorConfiguration.getDocument(),
                                                        configuration.getDocument() );

        Map<String, Serializable> context = new HashMap<String, Serializable>();

        User user = new User();
        user.setUsername( "trygvis" );
        user.setFirstName( "Trygve" );
        user.setLastName( "Laugstol" );
        context.put( "user", user );

        executor.execute( descriptor, context, new CollectingStepEventListener() );

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
