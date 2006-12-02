package org.codehaus.plexus.spe.execution.jython;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.execution.StepExecutor;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JythonStepExecutorTest
    extends PlexusTestCase
{
    public void testValidation()
        throws Exception
    {
        StepExecutor stepExecutor = (StepExecutor) lookup( StepExecutor.ROLE, "jython" );

        StepDescriptor descriptor = new StepDescriptor();

        try
        {
            stepExecutor.execute( descriptor, new HashMap<String, Serializable>() );

            fail( "Expected exception." );
        }
        catch ( ProcessException e )
        {
            // expected
        }

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        Xpp3Dom configuration = new Xpp3Dom( "configuration" );
        setScript( configuration );
        descriptor.setConfiguration( configuration );

        stepExecutor.execute( descriptor, new HashMap<String, Serializable>() );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        configuration = new Xpp3Dom( "configuration" );
        setScript( configuration, "testb.touch(); testa.beenTouched = 1; value = 'cool'" );

        Xpp3Dom requirements = new Xpp3Dom( "requirements" );

        requirements.addChild( addRequirement( Action.ROLE, "jython-a", "testa" ) );
        requirements.addChild( addRequirement( Action.ROLE, "testb" ) );

        configuration.addChild( requirements );

        descriptor.setConfiguration( configuration );

        stepExecutor.execute( descriptor, new HashMap<String, Serializable>() );

        assertTrue( TestActionA.beenTouched );
        assertTrue( TestActionB.beenTouched );
    }

    private Xpp3Dom addRequirement( String role, String roleHint, String variableName )
    {
        Xpp3Dom requirement = new Xpp3Dom( "requirement" );

        Xpp3Dom r = new Xpp3Dom( "role" );
        r.setValue( role );
        requirement.addChild( r );

        Xpp3Dom h = new Xpp3Dom( "role-hint" );
        h.setValue( roleHint );
        requirement.addChild( h );

        if ( variableName != null )
        {
            Xpp3Dom vn = new Xpp3Dom( "variable-name" );
            vn.setValue( variableName );
            requirement.addChild( vn );
        }

        return requirement;
    }

    private Xpp3Dom addRequirement( String role, String roleHint )
    {
        return addRequirement( role, roleHint, null );
    }

    private void setScript( Xpp3Dom configuration )
    {
        setScript( configuration, "import sys" );
    }

    private void setScript( Xpp3Dom configuration, String s )
    {
        Xpp3Dom script;
        script = new Xpp3Dom( "script" );
        script.setValue( s );
        configuration.addChild( script );
    }
}
