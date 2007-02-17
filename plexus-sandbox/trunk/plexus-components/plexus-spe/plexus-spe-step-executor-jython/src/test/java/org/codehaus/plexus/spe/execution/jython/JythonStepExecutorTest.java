package org.codehaus.plexus.spe.execution.jython;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.spe.execution.CollectingStepEventListener;
import org.codehaus.plexus.spe.execution.StepExecutor;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.utils.DocumentUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

        StepDescriptor descriptor = new StepDescriptor( "jython",
                                                        DocumentUtils.getEmptyDocument( "executor-configuration"),
                                                        DocumentUtils.getEmptyDocument( "configuration") );

/* I can't remember which exception that's expected here
        try
        {
            stepExecutor.execute( descriptor,
                                  new HashMap<String, Serializable>(),
                                  new CollectingStepEventListener() );

            fail( "Expected exception." );
        }
        catch ( ProcessException e )
        {
            e.printStackTrace();
            // expected
        }
*/

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        Document document = DocumentUtils.getEmptyDocument( "configuration" );

        setScript( document );
        System.err.println( "------------" );
        DocumentUtils.dumpDocument( document );
        System.err.println( "------------" );
        descriptor.setConfiguration( document );

        stepExecutor.execute( descriptor,
                              new HashMap<String, Serializable>(),
                              new CollectingStepEventListener() );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

/*
        configuration = new Xpp3Dom( "configuration" );
        setScript( configuration, "testb.touch(); testa.beenTouched = 1; barService.beenTouched = 1; context.put( 'trygve', 'cool' ); value = 'cool'" );

        Xpp3Dom requirements = new Xpp3Dom( "requirements" );

        requirements.addChild( addRequirement( FooService.ROLE, "jython-a", "testa" ) );
        requirements.addChild( addRequirement( FooService.ROLE, "testb" ) );
        requirements.addChild( addRequirement( BarService.ROLE ) );

        configuration.addChild( requirements );

        descriptor.setConfiguration( configuration );

        HashMap<String, Serializable> context = new HashMap<String, Serializable>();
        context.put( "a", "b" );

        stepExecutor.execute( descriptor, context, new CollectingStepEventListener() );

        assertTrue( AFooService.beenTouched );
        assertTrue( BFooService.beenTouched );
        assertTrue( DefaultBarService.beenTouched );
        assertEquals( "cool", context.get( "trygve" ) );
*/
    }
/*
    private Xpp3Dom addRequirement( String role, String roleHint, String variableName )
    {
        Xpp3Dom requirement = new Xpp3Dom( "requirement" );

        Xpp3Dom r = new Xpp3Dom( "role" );
        r.setValue( role );
        requirement.addChild( r );

        if ( roleHint != null )
        {
            Xpp3Dom h = new Xpp3Dom( "role-hint" );
            h.setValue( roleHint );
            requirement.addChild( h );
        }

        if ( variableName != null )
        {
            Xpp3Dom vn = new Xpp3Dom( "variable-name" );
            vn.setValue( variableName );
            requirement.addChild( vn );
        }

        return requirement;
    }

    private Xpp3Dom addRequirement( String role )
    {
        return addRequirement( role, null, null );
    }

    private Xpp3Dom addRequirement( String role, String roleHint )
    {
        return addRequirement( role, roleHint, null );
    }
*/
    private void setScript( Document document )
    {
        setScript( document, "import sys" );
    }

    private void setScript( Document document, String s )
    {
        Element script = document.createElement( "script" );
        script.setTextContent( s );
        document.getFirstChild().appendChild( script );
    }
}
