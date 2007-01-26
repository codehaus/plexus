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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.cdc.annotation.Component;
import org.codehaus.plexus.cdc.annotation.Configuration;
import org.codehaus.plexus.cdc.annotation.Requirement;
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

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.Type;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PlexusDefaultComponentGleaner.java 4497 2006-10-20 08:40:03Z trygvis $
 */
public class PlexusAnnotationComponentGleaner
    extends AbstractLogEnabled
    implements ComponentGleaner
{
    public static final String PLEXUS_VERSION_PARAMETER = "version";

    public static final String PLEXUS_ROLE_PARAMETER = "role";

    public static final String PLEXUS_ROLE_HINT_PARAMETER = "roleHint";

    public static final String PLEXUS_ALIAS_PARAMETER = "alias";

    private static final String PLEXUS_DEFAULT_VALUE_PARAMETER = "value";

    private static final String PLEXUS_LIFECYCLE_HANDLER_PARAMETER = "lifecycleHandler";

    private static final String PLEXUS_INSTANTIATION_STARTEGY_PARAMETER = "instantiationStrategy";

    // ----------------------------------------------------------------------
    // ComponentGleaner Implementation
    // ----------------------------------------------------------------------

    public ComponentDescriptor glean( JavaClassCache classCache, JavaClass javaClass )
        throws ComponentDescriptorCreatorException
    {
        Annotation componentAnnotation = getAnnotationByClass( javaClass, Component.class );

        if ( componentAnnotation == null )
        {
            PlexusDefaultComponentGleaner defaultGleaner = new PlexusDefaultComponentGleaner();
            return defaultGleaner.glean( classCache, javaClass );
        }

        Map parameters = componentAnnotation.getNamedParameterMap();

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String fqn = javaClass.getFullyQualifiedName();

        if( getLogger().isDebugEnabled() )
        {
        	getLogger().debug( "Creating descriptor for component: " + fqn );
        }

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setImplementation( fqn );

        // ----------------------------------------------------------------------
        // Role
        // ----------------------------------------------------------------------

        String role = findRole( javaClass, parameters );

        if( role == null )
        {
        	getLogger().warn( "Could not figure out a role for the component '"
        			+ fqn + "'. Please specify a role with a field 'role' on the @"
                    + Component.class.getName() + " annotation." );

            return null;
        }

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( " Role: " + role );
        }

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

        componentDescriptor.setVersion( getParameter( parameters, PLEXUS_VERSION_PARAMETER ) );

        // ----------------------------------------------------------------------
        // Lifecycle handler
        // ----------------------------------------------------------------------

        componentDescriptor.setLifecycleHandler( getParameter( parameters, PLEXUS_LIFECYCLE_HANDLER_PARAMETER ) );

        // ----------------------------------------------------------------------
        // Lifecycle handler
        // ----------------------------------------------------------------------

        componentDescriptor.setInstantiationStrategy( getParameter( parameters, PLEXUS_INSTANTIATION_STARTEGY_PARAMETER ) );

        // ----------------------------------------------------------------------
        // Alias
        // ----------------------------------------------------------------------

        componentDescriptor.setAlias( getParameter( parameters, PLEXUS_ALIAS_PARAMETER ) );

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

    private String findRole( JavaClass javaClass, Map parameters )
    {
        String role = getParameter( parameters, PLEXUS_ROLE_PARAMETER );

        if( role != null )
        {
        	return role;
        }

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
        // For each implemented interface, check to see if it's a candiate
        // interface
        // ----------------------------------------------------------------------

        String className = javaClass.getName();

        for ( Iterator it = interfaces.iterator(); it.hasNext(); )
        {
            JavaClass ifc = (JavaClass) it.next();

            String fqn = ifc.getFullyQualifiedName();

            String pkg = ifc.getPackage();

            if ( pkg == null )
            {
                int index = fqn.lastIndexOf( '.' );

                if ( index == -1 )
                {
                    // -----------------------------------------------------------------------
                    // This is a special case which will happen in two cases:
                    // 1) The component is in the default/root package
                    // 2) The interface is in another build, typically in an -api package
                    // while the code beeing gleaned in in the -impl build.
                    //
                    // Since it's most likely in another package than in the default package
                    // prepend the gleaned class' package
                    // -----------------------------------------------------------------------

                    pkg = javaClass.getPackage();

                    fqn = pkg + "." + fqn;
                }
                else
                {
                    pkg = fqn.substring( 0, index );
                }
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
                    getLogger().warn( "Found several possible roles for component '" +
                        javaClass.getFullyQualifiedName() + "', will use '" + role + 
                        "', found: " + ifc.getName() + "." );
                }

                role = fqn;
            }
        }

        if ( role == null )
        {
            JavaClass superClass = javaClass.getSuperJavaClass();

            if ( superClass != null )
            {
                Annotation componentAnnotation = getAnnotationByClass( superClass, Component.class );

                role = findRole( superClass, componentAnnotation.getNamedParameterMap() );
            }
        }

        return role;
    }

    private void findRequirements( JavaClassCache classCache, ComponentDescriptor componentDescriptor,
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

        for( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[i];

            Annotation requirementAnnotation = getAnnotationByClass( field, Requirement.class );

            if( requirementAnnotation == null )
            {
                continue;
            }

            Map parameters = new HashMap( requirementAnnotation.getNamedParameterMap() );

            // ----------------------------------------------------------------------
            // Role
            // ----------------------------------------------------------------------

            String requirementClass = field.getType().getJavaClass().getFullyQualifiedName();

            ComponentRequirement cr = new ComponentRequirement();

            String role = getParameter( parameters, PLEXUS_ROLE_PARAMETER );

            if ( role == null )
            {
                cr.setRole( requirementClass );
            }
            else
            {
                cr.setRole( role );
            }

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

                if ( role == null )
                {
                    getLogger().warn( "Field: '" + field.getName()
                            + "': A java.util.Map or java.util.List "
                            + "requirement has to specify a 'role' parameter on the @"
                            + Requirement.class.getName()
                            + " tag so Plexus can know which components to "
                            + "put in the map or list." );

                    continue;
                }

                JavaClass roleClass = classCache.getClassByName( role );

                if ( role.indexOf( '.' ) == -1 && StringUtils.isEmpty( roleClass.getPackage() ) )
                {
                    role = javaClass.getPackage() + "." + roleClass.getName();
                }

                cr.setRole( role );
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

            Annotation configurationAnnotation = getAnnotationByClass( field, Configuration.class );

            if( configurationAnnotation == null )
            {
                continue;
            }

            Map parameters = new HashMap( configurationAnnotation.getNamedParameterMap() );

            /* don't use the getParameter helper as we like empty strings */
            Object defaultValue = parameters.remove( PLEXUS_DEFAULT_VALUE_PARAMETER );

            if( defaultValue == null )
            {
                getLogger().warn( "Component: " + javaClass.getName() + ", field name: '" + field.getName() +
                    "': Currently configurable fields will not be written to the descriptor " +
                    "without a default value." );

                continue;
            }

            String setValue = "";
            if( defaultValue instanceof List )
            {
            	StringBuffer buffer = new StringBuffer();
            	for (Iterator iter = ((List)defaultValue).iterator(); iter.hasNext();)
            	{
            		defaultValue = iter.next();
            		if( defaultValue instanceof List )
            		{
            			buffer.append( ((List)defaultValue).get( 0 ) );
            		}
            		else
            		{
                		buffer.append( defaultValue );
            		}
            		buffer.append( ',' );
				}
            	if( buffer.length() > 0 )
            	{
            		buffer.deleteCharAt( buffer.length()-1 );
            	}
            	setValue = buffer.toString();
            }
            else if ( defaultValue instanceof String )
            {
            	setValue = (String)defaultValue;
            }
            else
            {
                getLogger().warn( "Component: " + javaClass.getName() + ", field name: '" + field.getName() +
                	"': Currently configurable fields will not be written to the descriptor " +
                	"without a default value of String or String[]." );
            }

            String name = deHump( field.getName() );

            XmlPlexusConfiguration c = new XmlPlexusConfiguration( name );

            c.setValue( setValue );

            if( getLogger().isDebugEnabled() )
            {
            	getLogger().debug( " Configuration: " + name + "=" + defaultValue );
            }

            configuration.addChild( c );
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

    private String getParameter( Map parameters, String parameter )
    {
        String value = (String) parameters.remove( parameter );

        if ( StringUtils.isEmpty( value ) )
        {
            return null;
        }

        return value;
    }

    private Annotation getAnnotationByClass( AbstractJavaEntity entity, Class annoClass )
    {
    	Annotation[] annotations = entity.getAnnotations();
    	for( int i = 0; i < annotations.length; i++ )
    	{
    		Type type = annotations[i].getType();
    		if( annoClass.getName().equals( type.getValue() ) )
    		{
    			return annotations[i];
    		}
    	}
    	return null;
    }
}
