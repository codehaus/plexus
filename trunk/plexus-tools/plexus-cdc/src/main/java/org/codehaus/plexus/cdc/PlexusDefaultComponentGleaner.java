package org.codehaus.plexus.cdc;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;
import com.thoughtworks.qdox.model.JavaField;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Suspendable;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusDefaultComponentGleaner
    extends AbstractLogEnabled
    implements ComponentGleaner
{
    public static final String PLEXUS_COMPONENT_TAG = "plexus.component";

    public static final String PLEXUS_REQUIREMENT_TAG = "plexus.requirement";

    private static final String PLEXUS_CONFIGURATION_TAG = "plexus.configuration";

    public static final String PLEXUS_VERSION_PARAMETER = "version";

    public static final String PLEXUS_ROLE_PARAMETER = "role";

    public static final String PLEXUS_ROLE_HINT_PARAMETER = "role-hint";

    public static final String PLEXUS_ALIAS_PARAMETER = "alias";

    private static final String PLEXUS_DEFAULT_VALUE_PARAMETER = "default-value";

    private static final String PLEXUS_LIFECYCLE_HANDLER_PARAMETER = "lifecycle-handler";

    private static final String PLEXUS_INSTANTIATION_STARTEGY_PARAMETER = "instantiation-strategy";

    // ----------------------------------------------------------------------
    // ComponentGleaner Implementation
    // ----------------------------------------------------------------------

    public ComponentDescriptor glean( JavaClassCache classCache, JavaClass javaClass )
        throws ComponentDescriptorCreatorException
    {
        DocletTag tag = javaClass.getTagByName( PLEXUS_COMPONENT_TAG );

        if ( tag == null )
        {
            return null;
        }

        Map parameters = tag.getNamedParameterMap();

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String fqn = javaClass.getFullyQualifiedName();

        getLogger().debug( "Creating descriptor for component: " + fqn );

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setImplementation( fqn );

        // ----------------------------------------------------------------------
        // Role
        // ----------------------------------------------------------------------

        String role = getParameter( parameters, PLEXUS_ROLE_PARAMETER );

        if ( role == null )
        {
            role = findRole( javaClass );

            if ( role == null )
            {
                getLogger().warn( "Could not figure out a role for the component '" + fqn + "'. " +
                                  "Please specify a role with a parameter '" + PLEXUS_ROLE_PARAMETER + " " +
                                  "on the @" + PLEXUS_COMPONENT_TAG + " tag." );

                return null;
            }
        }

        getLogger().debug( " Role: " + role );

        componentDescriptor.setRole( role );

        // ----------------------------------------------------------------------
        // Role hint
        // ----------------------------------------------------------------------

        String roleHint = getParameter( parameters, PLEXUS_ROLE_HINT_PARAMETER );

        if ( roleHint != null )
        {
            getLogger().debug( " Role hint: " + roleHint );
        }

        componentDescriptor.setRoleHint( roleHint );

        // ----------------------------------------------------------------------
        // Version
        // ----------------------------------------------------------------------

        String version = getParameter( parameters, PLEXUS_VERSION_PARAMETER );

        componentDescriptor.setVersion( version );

        // ----------------------------------------------------------------------
        // Lifecycle handler
        // ----------------------------------------------------------------------

        String lifecycleHandler = getParameter( parameters, PLEXUS_LIFECYCLE_HANDLER_PARAMETER );

        componentDescriptor.setLifecycleHandler( lifecycleHandler );

        // ----------------------------------------------------------------------
        // Lifecycle handler
        // ----------------------------------------------------------------------

        String instatiationStrategy = getParameter( parameters, PLEXUS_INSTANTIATION_STARTEGY_PARAMETER );

        componentDescriptor.setInstantiationStrategy( instatiationStrategy );

        // ----------------------------------------------------------------------
        // Alias
        // ----------------------------------------------------------------------

        componentDescriptor.setAlias( getParameter( parameters, PLEXUS_ALIAS_PARAMETER ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        findExtraParameters( PLEXUS_COMPONENT_TAG, parameters );

        // ----------------------------------------------------------------------
        // Requirements
        // ----------------------------------------------------------------------

        findRequirements( classCache, componentDescriptor, javaClass );

        // ----------------------------------------------------------------------
        // Description
        // ----------------------------------------------------------------------

        String comment = javaClass.getComment();

        if ( comment != null )
        {
            int i = comment.indexOf( '.' );

            if ( i > 0 )
            {
                comment = comment.substring( 0, i + 1 ); // include the dot
            }
        }

        componentDescriptor.setDescription( comment );

        // ----------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------

        XmlPlexusConfiguration configuration = new XmlPlexusConfiguration( "configuration" );

        findConfiguration( configuration, javaClass );

        componentDescriptor.setConfiguration( configuration );

        return componentDescriptor;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private final static List IGNORED_INTERFACES = Collections.unmodifiableList( Arrays.asList( new String[]{
        LogEnabled.class.getName(),
        Initializable.class.getName(),
        Configurable.class.getName(),
        Contextualizable.class.getName(),
        Disposable.class.getName(),
        Startable.class.getName(),
        Suspendable.class.getName(),
        Serviceable.class.getName(),
    } ) );

    private String findRole( JavaClass javaClass )
    {
        // ----------------------------------------------------------------------
        // Remove any Plexus specific interfaces from the calculation
        // ----------------------------------------------------------------------

        List interfaces = new ArrayList( Arrays.asList( javaClass.getImplementedInterfaces() ) );

        for ( Iterator it = interfaces.iterator(); it.hasNext(); )
        {
            JavaClass ifc = (JavaClass) it.next();

            if ( IGNORED_INTERFACES.contains( ifc.getFullyQualifiedName() ) )
            {
                it.remove();
            }
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String role = null;

        String className = javaClass.getName();

        for ( Iterator it = interfaces.iterator(); it.hasNext(); )
        {
            JavaClass ifc = (JavaClass) it.next();

            String fqn = ifc.getFullyQualifiedName();

            String pkg = ifc.getPackage();

            if ( pkg == null )
            {
                pkg = fqn.substring( 0, fqn.lastIndexOf( '.' ) );
            }

            if ( fqn == null )
            {
                fqn = ifc.getName();
            }

            String name = fqn.substring( pkg.length() + 1 );

            if ( className.endsWith( name ) )
            {
                if ( role != null )
                {
                    getLogger().warn( "Found several possible roles for component " +
                                      "'" + javaClass.getFullyQualifiedName() + "', " +
                                      "will use '" + role + "', found: " + ifc.getName() + "." );
                }

                role = ifc.getFullyQualifiedName();
            }
        }

        if ( role == null )
        {
            JavaClass superClass = javaClass.getSuperJavaClass();

            if ( superClass != null )
            {
                role = findRole( superClass );
            }
        }

        return role;
    }

    private void findRequirements( JavaClassCache classCache,
                                   ComponentDescriptor componentDescriptor,
                                   JavaClass javaClass )
    {
        JavaField[] fields = javaClass.getFields();

        // ----------------------------------------------------------------------
        // Search the super class for requirements
        // ----------------------------------------------------------------------

        if ( javaClass.getSuperJavaClass() != null )
        {
            findRequirements( classCache, componentDescriptor, javaClass.getSuperJavaClass() );
        }

        // ----------------------------------------------------------------------
        // Search the current class for requirements
        // ----------------------------------------------------------------------

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[i];

            DocletTag tag = field.getTagByName( PLEXUS_REQUIREMENT_TAG );

            if ( tag == null )
            {
                continue;
            }

            Map parameters = tag.getNamedParameterMap();

            // ----------------------------------------------------------------------
            // Role
            // ----------------------------------------------------------------------

            String requirementClass = field.getType().getJavaClass().getFullyQualifiedName();

            ComponentRequirement cr = new ComponentRequirement();

            cr.setRole( requirementClass );

            cr.setRoleHint( getParameter( parameters, PLEXUS_ROLE_HINT_PARAMETER ) );

            cr.setFieldName( field.getName() );

            boolean isMap = requirementClass.equals( Map.class.getName() );

            boolean isList = requirementClass.equals( List.class.getName() );

            if ( isMap || isList )
            {
                if ( cr.getRoleHint() != null )
                {
                    getLogger().warn( "Field: '" + field.getName() + "': A role hint cannot be specified if the " +
                                      "field is a java.util.Map or a java.util.List" );

                    continue;
                }

                String role = getParameter( parameters, PLEXUS_ROLE_PARAMETER );

                if ( role == null )
                {
                    getLogger().warn( "Field: '" + field.getName() + "': A java.util.Map or java.util.List " +
                                      "requirement has to specify a '" + PLEXUS_ROLE_PARAMETER + "' parameter on " +
                                      "the @" + PLEXUS_REQUIREMENT_TAG + " tag so Plexus can know which components to " +
                                      "put in the map or list." );

                    continue;
                }

                JavaClass roleClass = classCache.getClassByName( role );

                if ( role.indexOf( '.' ) == -1 && StringUtils.isEmpty( roleClass.getPackage() ) )
                {
                    role = javaClass.getPackage() + "." + roleClass.getName();
                }

                cr.setRole( role );

                findExtraParameters( PLEXUS_REQUIREMENT_TAG, parameters );
            }

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            componentDescriptor.addRequirement( cr );
        }
    }

    private void findConfiguration( XmlPlexusConfiguration configuration, JavaClass javaClass )
        throws ComponentDescriptorCreatorException
    {
        JavaField[] fields = javaClass.getFields();

        // ----------------------------------------------------------------------
        // Search the super class for configurable fields.
        // ----------------------------------------------------------------------

        if ( javaClass.getSuperJavaClass() != null )
        {
            findConfiguration( configuration, javaClass.getSuperJavaClass() );
        }

        // ----------------------------------------------------------------------
        // Search the current class for configurable fields.
        // ----------------------------------------------------------------------

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[i];

            DocletTag tag = field.getTagByName( PLEXUS_CONFIGURATION_TAG );

            if ( tag == null )
            {
                continue;
            }

            Map parameters = new HashMap( tag.getNamedParameterMap() );

            String defaultValue = getParameter( parameters, PLEXUS_DEFAULT_VALUE_PARAMETER );

            if ( defaultValue == null )
            {
                getLogger().warn( "Component: " + javaClass.getName() + ", field name: '" + field.getName() + "': " +
                                  "Currently configurable fields will not be written to the descriptor " +
                                  "without a default value." );

                continue;
            }

            String name = deHump( field.getName() );

            XmlPlexusConfiguration c;

            c = new XmlPlexusConfiguration( name );

            c.setValue( defaultValue );

            getLogger().debug( " Configuration: " + name + "=" + defaultValue );

            configuration.addChild( c );

            findExtraParameters( PLEXUS_CONFIGURATION_TAG, parameters );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String deHump( String string )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < string.length(); i++ )
        {
            if ( i != 0 && Character.isUpperCase( string.charAt( i ) ) )
            {
                sb.append( '-' );
            }

            sb.append( string.charAt( i ) );
        }

        return sb.toString().trim().toLowerCase();
    }

    private void findExtraParameters( String tagName, Map parameters )
    {
        for ( Iterator it = parameters.keySet().iterator(); it.hasNext(); )
        {
            String s = (String) it.next();

            getLogger().warn( "Extra parameter on the '" + tagName + "' tag: '" + s + "'." );
        }
    }

    private String getParameter( Map parameters, String parameter )
    {
        String value = (String) parameters.remove( parameter );

        if ( StringUtils.isEmpty( value ) )
        {
            return null;
        }

        return value;
    }
}
