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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Suspendable;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;
import com.thoughtworks.qdox.model.JavaField;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusDefaultComponentGleaner
    extends AbstractLogEnabled
    implements ComponentGleaner
{
    public static final String PLEXUS_COMPONENT_TAG = "plexus.component";

    public static final String PLEXUS_VERSION_TAG = "plexus.version";

    public static final String PLEXUS_ROLE_TAG = "plexus.role";

    public static final String PLEXUS_ROLE_HINT_TAG = "plexus.role-hint";

    public static final String PLEXUS_REQUIREMENT_TAG = "plexus.requirement";

    private static final String PLEXUS_CONFIGURATION_TAG = "plexus.configuration";

    private static final String PLEXUS_DEFAULT_VALUE_TAG = "plexus.default-value";

    private static final String PLEXUS_LIFECYCLE_HANDLER_TAG = "plexus.lifecycle-handler";

    private static final String PLEXUS_INSTANTIATION_STARTEGY_TAG = "plexus.instantiation-strategy";

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

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String fqn = javaClass.getFullyQualifiedName();

        getLogger().debug( "Creating descriptor for component: '" + fqn + "'." );

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setImplementation( fqn );

        // ----------------------------------------------------------------------
        // Role
        // ----------------------------------------------------------------------

        String role = getOptionalTag( javaClass, PLEXUS_ROLE_TAG );

        if ( role == null )
        {
            role = findRole( javaClass );

            if ( role == null )
            {
                getLogger().warn( "Could not figure out a role for this class. " +
                                  "Please specify a role with the @" + PLEXUS_ROLE_TAG + " tag." );

                return null;
            }
        }

        getLogger().debug( " Role: " + role );

        componentDescriptor.setRole( role );

        // ----------------------------------------------------------------------
        // Role hint
        // ----------------------------------------------------------------------

        String roleHint = getOptionalTag( javaClass, PLEXUS_ROLE_HINT_TAG );

        getLogger().debug( " Role hint: " + roleHint );

        componentDescriptor.setRoleHint( roleHint );

        // ----------------------------------------------------------------------
        // Version
        // ----------------------------------------------------------------------

        String version = getOptionalTag( javaClass, PLEXUS_VERSION_TAG );

        componentDescriptor.setVersion( version );

        // ----------------------------------------------------------------------
        // Lifecycle handler
        // ----------------------------------------------------------------------

        String lifecycleHandler = getOptionalTag( javaClass, PLEXUS_LIFECYCLE_HANDLER_TAG );

        componentDescriptor.setLifecycleHandler( lifecycleHandler );

        // ----------------------------------------------------------------------
        // Lifecycle handler
        // ----------------------------------------------------------------------

        String instatiationStrategy = getOptionalTag( javaClass, PLEXUS_INSTANTIATION_STARTEGY_TAG );

        componentDescriptor.setInstantiationStrategy( instatiationStrategy );

        // ----------------------------------------------------------------------
        // Requirements
        // ----------------------------------------------------------------------

        findRequirements( classCache, componentDescriptor, javaClass );

        // ----------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------

        XmlPlexusConfiguration configuration = new XmlPlexusConfiguration( "configuration" );

        findConfiguration( classCache, configuration, javaClass );

        System.out.println( "---------------------------" );
        Writer w = new OutputStreamWriter( System.out );
        Xpp3DomWriter.write( w, configuration.getXpp3Dom() );
        try
        {
            w.flush();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println( "---------------------------" );

        componentDescriptor.setConfiguration( configuration );

        return componentDescriptor;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private final static List IGNORED_INTERFACES = Arrays.asList( new String[]{
        Initializable.class.getName(),
        Configurable.class.getName(),
        Contextualizable.class.getName(),
        Disposable.class.getName(),
        Startable.class.getName(),
        Suspendable.class.getName(),
        Serviceable.class.getName(),
    } );

    private String findRole( JavaClass javaClass )
    {
        // ----------------------------------------------------------------------
        // Remove any Plexus specific interfaces from the calculation
        // ----------------------------------------------------------------------

        List interfaces = Arrays.asList( javaClass.getImplementedInterfaces() );

        interfaces.removeAll( IGNORED_INTERFACES );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String role = null;

        String className = javaClass.getName();

        for ( Iterator it = interfaces.iterator(); it.hasNext(); )
        {
            JavaClass ifc = (JavaClass) it.next();

            if ( className.indexOf( ifc.getName() ) >= 0 )
            {
                if ( role != null )
                {
                    getLogger().warn( "Found several possible roles for this component, " +
                                      "will use " + role + ", found: " + ifc.getName() + "." );
                }

                role = ifc.getFullyQualifiedName();
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
            JavaField field = fields[ i ];

            if ( field.getTagByName( PLEXUS_REQUIREMENT_TAG ) == null )
            {
                continue;
            }

            // ----------------------------------------------------------------------
            // Role
            // ----------------------------------------------------------------------

            String requirementClass = field.getType().getJavaClass().getFullyQualifiedName();

            ComponentRequirement cr = new ComponentRequirement();

            cr.setRole( requirementClass );

            cr.setRoleHint( getOptionalTag( field, PLEXUS_ROLE_HINT_TAG ) );

            cr.setFieldName( field.getName() );

            boolean isMap = requirementClass.equals( Map.class.getName() );

            boolean isList = requirementClass.equals( List.class.getName() );

            if ( isMap || isList )
            {
                if ( cr.getRoleHint() != null )
                {
                    getLogger().warn( "A role hint cannot be specified if the field is a java.util.Map or a " +
                                      "java.util.List" );

                    continue;
                }

                String role = getOptionalTag( field, PLEXUS_ROLE_TAG );

                if ( role == null )
                {
                    getLogger().warn( "A java.util.Map or java.utils.List requirement has to specify a " +
                                      "@" + PLEXUS_ROLE_TAG + " tag so Plexus can know which components to put in " +
                                      "the map or list." );

                    continue;
                }

                JavaClass roleClass = classCache.getClassByName( role );

                if ( !StringUtils.isEmpty( roleClass.getPackage() ) )
                {
                    role = roleClass.getFullyQualifiedName();
                }
                else
                {
                    role = javaClass.getPackage() + roleClass.getName();
                }

                cr.setRole( role );
            }

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            componentDescriptor.addRequirement( cr );
        }
    }

    private void findConfiguration( JavaClassCache classCache,
                                    XmlPlexusConfiguration configuration,
                                    JavaClass javaClass )
        throws ComponentDescriptorCreatorException
    {
        JavaField[] fields = javaClass.getFields();

        // ----------------------------------------------------------------------
        // Search the super class for configurable fields.
        // ----------------------------------------------------------------------

        if ( javaClass.getSuperJavaClass() != null )
        {
            findConfiguration( classCache, configuration, javaClass.getSuperJavaClass() );
        }

        // ----------------------------------------------------------------------
        // Search the current class for configurable fields.
        // ----------------------------------------------------------------------

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[ i ];

            if ( field.getTagByName( PLEXUS_CONFIGURATION_TAG ) == null )
            {
                continue;
            }

            String defaultValue = getOptionalTag( field, PLEXUS_DEFAULT_VALUE_TAG );

            if ( defaultValue == null )
            {
                continue;
            }

            String name = deHump( field.getName() );

            XmlPlexusConfiguration c;

            c = new XmlPlexusConfiguration( name );

            c.setValue( defaultValue );

            getLogger().debug( " Configuration: " + name + "=" + defaultValue );

            configuration.addChild( c );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String getOptionalTag( AbstractJavaEntity entity, String tagName )
    {
        DocletTag tag = entity.getTagByName( tagName );

        String value = null;

        if ( tag == null )
        {
            return null;
        }

        value = tag.getValue();

        if ( StringUtils.isEmpty( value ) )
        {
            return null;
        }

        return value;
    }

    private String deHump( String string )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < string.length(); i++ )
        {
            if ( i != 0 &&
                 Character.isUpperCase( string.charAt( i ) ) )
            {
                sb.append( '-' );
            }

            sb.append( string.charAt( i ) );
        }

        return sb.toString().trim().toLowerCase();
    }
}
