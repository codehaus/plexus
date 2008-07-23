package org.codehaus.plexus.spe.execution.jython;

import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.execution.AbstractStepExecutor;
import org.codehaus.plexus.spe.execution.StepEventListener;
import org.codehaus.plexus.spe.execution.StepExecutor;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.utils.DocumentWrapper;
import org.codehaus.plexus.util.StringUtils;
import org.python.util.PythonInterpreter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component role-hint="jython"
 */
public class JythonStepExecutor
    extends AbstractStepExecutor
    implements StepExecutor
{
    // ----------------------------------------------------------------------
    // StepExecutor Implementation
    // ----------------------------------------------------------------------

    public void execute( StepDescriptor stepDescriptor,
                         Map<String, Serializable> context,
                         StepEventListener eventListener )
        throws ProcessException
    {
        DocumentWrapper configuration = new DocumentWrapper( stepDescriptor.getConfiguration() );

        // -----------------------------------------------------------------------
        // Load and validate script
        // -----------------------------------------------------------------------

        String script = configuration.getChildText( "script" );

        // -----------------------------------------------------------------------
        // Load all requirements
        // -----------------------------------------------------------------------

        DocumentWrapper requirements = configuration.getOptionalChild( "requirements" );

        Map<String, Object> components = new HashMap<String, Object>();

        if ( requirements != null )
        {
            try
            {
                lookupComponents( requirements, components );
            }
            catch ( ProcessException e )
            {
                release( components.values() );

                throw e;
            }
        }

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        try
        {
            executeScript( components, script, context );
        }
        finally
        {
            release( components.values() );
        }
    }

    private void lookupComponents( DocumentWrapper requirements, Map<String, Object> components )
        throws ProcessException
    {
        for ( DocumentWrapper requirement : requirements.getChildren() )
        {
            String role = requirement.getChildText( "role" );

            String roleHint = requirement.getChildText( "role-hint", null );

            // -----------------------------------------------------------------------
            // Get the variable name
            // -----------------------------------------------------------------------

            String variableName = requirement.getChildText( "variable-name", null );

            // Unless the variable name is specified, use the role and role hint to calculate a variable name
            if ( variableName != null )
            {
                if ( StringUtils.isEmpty( variableName ) )
                {
                    throw new ProcessException( "Variable name cannot be null." );
                }
            }
            else
            {
                // If the role hint is set, use that as the variable name
                if ( roleHint != null )
                {
                    variableName = roleHint;
                }
                else
                {
                    // Use the last part of the role as variable name, but with the first letter lower cased

                    int i = role.lastIndexOf( '.' );

                    if ( i > 0 )
                    {
                        variableName = role.substring( i + 1 );
                    }
                    else
                    {
                        variableName = role;
                    }

                    variableName = Character.toLowerCase( variableName.charAt( 0 ) ) + variableName.substring( 1 );
                }
            }

            // -----------------------------------------------------------------------
            // Look up the component
            // -----------------------------------------------------------------------

            Object component;

            if ( roleHint == null )
            {
                component = lookup( role );
            }
            else
            {
                component = lookup( role, roleHint );
            }

            components.put( variableName, component );
        }
    }

    private void executeScript( Map<String, Object> components, String script, Map<String, Serializable> context )
    {
        PythonInterpreter interpreter = new PythonInterpreter();

        interpreter.setOut( System.out );
        interpreter.setErr( System.err );

        // -----------------------------------------------------------------------
        // Build the context
        // -----------------------------------------------------------------------

        interpreter.set( "context", context );

        // -----------------------------------------------------------------------
        // Add all the components to the context
        // -----------------------------------------------------------------------

        for ( Map.Entry<String, Object> entry : components.entrySet() )
        {
            interpreter.set( entry.getKey(), entry.getValue() );
        }

        interpreter.exec( script );

        System.out.flush();
        System.err.flush();
    }
}
