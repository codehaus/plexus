package org.codehaus.plexus.spe.execution.jython;

import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.execution.AbstractStepExecutor;
import org.codehaus.plexus.spe.execution.StepExecutor;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.StringUtils;
import org.python.util.PythonInterpreter;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JythonStepExecutor
    extends AbstractStepExecutor
    implements StepExecutor
{
    // ----------------------------------------------------------------------
    // StepExecutor Implementation
    // ----------------------------------------------------------------------

    public void execute( StepDescriptor stepDescriptor, Map<String, Serializable> context )
        throws ProcessException
    {
        Xpp3Dom configuration = (Xpp3Dom) stepDescriptor.getConfiguration();

        // -----------------------------------------------------------------------
        // Load and validate script
        // -----------------------------------------------------------------------

        String script = getChild( configuration, "script" );

        // -----------------------------------------------------------------------
        // Load all requirements
        // -----------------------------------------------------------------------

        Xpp3Dom requirements = configuration.getChild( "requirements" );

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
            executeScript( components, script );
        }
        finally
        {
            release( components.values() );
        }
    }

    private void lookupComponents( Xpp3Dom requirements, Map<String, Object> components )
        throws ProcessException
    {
        for ( Xpp3Dom requirement : requirements.getChildren() )
        {
            Xpp3Dom r = requirement.getChild( "role" );

            if ( r == null )
            {
                throw new ProcessException( "Invalid configuration: each <requirement> must have a <role>." );
            }

            String role = r.getValue();

            Xpp3Dom rh = requirement.getChild( "role-hint" );

            String roleHint = null;

            if ( rh != null )
            {
                roleHint = rh.getValue();
            }

            // -----------------------------------------------------------------------
            // Get the variable name
            // -----------------------------------------------------------------------

            Xpp3Dom vn = requirement.getChild( "variable-name" );

            String variableName;

            // Unless the variable name is specified, use the role and role hint to calculate a variable name
            if ( vn != null )
            {
                variableName = vn.getValue();

                if ( StringUtils.isEmpty( variableName ) )
                {
                    throw new ProcessException( "Variable name cannot be null." );
                }
            }
            else
            {
                if ( roleHint != null )
                {
                    variableName = roleHint;
                }
                else
                {
                    int i = role.lastIndexOf( '.' );

                    if ( i > 0 )
                    {
                        variableName = role.substring( i );
                    }
                    else
                    {
                        variableName = role;
                    }
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

    private void executeScript( Map<String, Object> components, String script )
    {
        PythonInterpreter interpreter = new PythonInterpreter();

        interpreter.setOut( System.out );
        interpreter.setErr( System.err );

        for ( Map.Entry<String, Object> entry : components.entrySet() )
        {
            interpreter.set( entry.getKey(), entry.getValue() );
        }

        interpreter.exec( script );


        System.out.flush();
        System.err.flush();
    }
}
