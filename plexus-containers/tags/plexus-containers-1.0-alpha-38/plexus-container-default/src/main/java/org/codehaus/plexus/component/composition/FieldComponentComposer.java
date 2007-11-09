package org.codehaus.plexus.component.composition;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class FieldComponentComposer
    extends AbstractComponentComposer
{
    public String getId()
    {
        return "field";
    }

    public void assignRequirement( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   ComponentRequirement requirement,
                                   PlexusContainer container,
                                   Map compositionContext,
                                   ClassRealm lookupRealm )
        throws CompositionException
    {
        Field field = findMatchingField( component, componentDescriptor, requirement, container );

        // we want to use private fields.
        if ( !field.isAccessible() )
        {
            field.setAccessible( true );
        }

        assignRequirementToField( component, componentDescriptor, field, container, requirement, lookupRealm );
    }

    private List assignRequirementToField( Object component,
                                           ComponentDescriptor hostComponentDescriptor,
                                           Field field,
                                           PlexusContainer container,
                                           ComponentRequirement requirementDescriptor, ClassRealm lookupRealm )
        throws CompositionException
    {
        Requirement requirement = findRequirement( component,
                                                   hostComponentDescriptor,
                                                   field.getType(),
                                                   container,
                                                   requirementDescriptor,
                                                   lookupRealm );

        try
        {
            field.set( component, requirement.getAssignment() );

            return requirement.getComponentDescriptors();
        }
        catch ( IllegalArgumentException e )
        {
            Class c = requirement.getAssignment().getClass();

            String msg="";
            while ( ( c != null ) && !c.isAssignableFrom( Object.class ) )
            {
                Class[] ifaces = c.getInterfaces();

                msg += "  Interfaces for " + c + ":";

                for ( int i = 0; i < ifaces.length; i++ )
                {
                    msg += "\n    Interface "
                        + ifaces[i];

                    if ( ifaces[i].getClassLoader() != null )
                    {
                        msg += "; realm: "
                        + ( ifaces[i].getClassLoader() instanceof ClassRealm ? ( (ClassRealm) ifaces[i]
                            .getClassLoader() ).getId() : ifaces[i].getClassLoader().toString() );

                        msg += getURLs(ifaces[i].getClassLoader());
                    }
                }

                c = c.getSuperclass();
            }

            String compositionMsg = "Composition failed for the field "
                + field.getName()
                + " "
                + "in object of type "
                + component.getClass().getName()
                + " (lookup realm: "
                + lookupRealm.getId()
                + ")"
                + "\nfield type: "
                + field.getType()
                + " realm: ";

            if ( field.getType().getClassLoader() != null )
            {
                compositionMsg += ( field.getType().getClassLoader() instanceof ClassRealm
                                                                                          ? ( (ClassRealm) field.getType().getClassLoader() ).getId()
                                                                                          : " classloader "
                                                                                              + field.getType().getClassLoader() );

                compositionMsg += getURLs( field.getType().getClassLoader() );
            }

            compositionMsg += "\nvalue type: " + requirement.getAssignment().getClass() + " realm: ";

            if ( requirement.getAssignment().getClass().getClassLoader() != null )
            {
                compositionMsg += ( requirement.getAssignment().getClass().getClassLoader() instanceof ClassRealm
                                                                                                                 ? ( (ClassRealm) requirement.getAssignment().getClass().getClassLoader() ).getId()
                                                                                                                 : " classloader "
                                                                                                                     + requirement.getAssignment().getClass().getClassLoader() );

                compositionMsg += getURLs( requirement.getAssignment().getClass().getClassLoader() );
            }

            compositionMsg += "\nassignable: " + field.getType().isAssignableFrom( requirement.getAssignment().getClass() );

            throw new CompositionException( compositionMsg + msg, e );
        }
        catch ( IllegalAccessException e )
        {
//            System.out.println( "[" + component + ":" + ((ClassRealm) component.getClass().getClassLoader() ).getId() + "]" +
//                "[" + assignment + ":" + ((ClassRealm)assignment.getClass().getClassLoader()).getId() + "]");

            throw new CompositionException( "Composition failed for the field " + field.getName() + " " +
                "in object of type " + component.getClass().getName(), e );
        }
    }

    private String getURLs( ClassLoader classLoader )
    {
        if ( classLoader == null )
        {
            return "";
        }

        String msg = "";

        if ( classLoader instanceof URLClassLoader )
        {
            URL [] urls = ((URLClassLoader)classLoader).getURLs();

            for ( int i = 0; i < urls.length; i ++ )
            {
                msg +="\n     " + urls[i];
            }
        }
        return msg;
    }

    protected Field findMatchingField( Object component,
                                       ComponentDescriptor componentDescriptor,
                                       ComponentRequirement requirement,
                                       PlexusContainer container )
        throws CompositionException
    {
        String fieldName = requirement.getFieldName();

        Field field;

        if ( fieldName != null )
        {
            field = getFieldByName( component, fieldName, componentDescriptor );
        }
        else
        {
            Class fieldClass;

            try
            {
                fieldClass = component.getClass().getClassLoader().loadClass( requirement.getRole() );
            }
            catch ( ClassNotFoundException e )
            {
                StringBuffer msg = new StringBuffer( "Component Composition failed for component: " );

                msg.append( componentDescriptor.getHumanReadableKey() );

                msg.append( ": Requirement class: '" );

                msg.append( requirement.getRole() );

                msg.append( "' not found." );

                throw new CompositionException( msg.toString(), e );
            }

            field = getFieldByType( component, fieldClass, componentDescriptor );
        }

        return field;
    }

    protected Field getFieldByName( Object component,
                                    String fieldName,
                                    ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Field field;
        try
        {
            field = ReflectionUtils.getFieldByNameIncludingSuperclasses( fieldName, component.getClass() );
        }
        catch( NoClassDefFoundError e )
        {
            throw new CompositionException( "Embedded NoClassDefFoundError while looking up " + Field.class.getName() + " for: " + fieldName + " in: " + componentDescriptor.getImplementation(), e );
        }

        if ( field == null )
        {
            StringBuffer msg = new StringBuffer( "Component Composition failed. No field of name: '" );

            msg.append( fieldName );

            msg.append( "' exists in component: " );

            msg.append( componentDescriptor.getHumanReadableKey() );

            throw new CompositionException( msg.toString() );
        }

        return field;
    }

    protected Field getFieldByTypeIncludingSuperclasses( Class componentClass,
                                                         Class type,
                                                         ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        List fields = getFieldsByTypeIncludingSuperclasses( componentClass, type, componentDescriptor );

        if ( fields.size() == 0 )
        {
            return null;
        }

        if ( fields.size() == 1 )
        {
            return (Field) fields.get( 0 );
        }

        throw new CompositionException(
            "There are several fields of type '" + type + "', " + "use 'field-name' to select the correct field." );
    }

    protected List getFieldsByTypeIncludingSuperclasses( Class componentClass,
                                                         Class type,
                                                         ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Class arrayType = Array.newInstance( type, 0 ).getClass();

        Field[] fields = componentClass.getDeclaredFields();

        List foundFields = new ArrayList();

        for ( int i = 0; i < fields.length; i++ )
        {
            Class fieldType = fields[i].getType();

            if ( fieldType.isAssignableFrom( type ) || fieldType.isAssignableFrom( arrayType ) )
            {
                foundFields.add( fields[i] );
            }
        }

        if ( componentClass.getSuperclass() != Object.class )
        {
            List superFields =
                getFieldsByTypeIncludingSuperclasses( componentClass.getSuperclass(), type, componentDescriptor );

            foundFields.addAll( superFields );
        }

        return foundFields;
    }

    protected Field getFieldByType( Object component,
                                    Class type,
                                    ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Field field = getFieldByTypeIncludingSuperclasses( component.getClass(), type, componentDescriptor );

        if ( field == null )
        {
            StringBuffer msg = new StringBuffer( "Component composition failed. No field of type: '" );

            msg.append( type );

            msg.append( "' exists in class '" );

            msg.append( component.getClass().getName() );

            msg.append( "'." );

            if ( componentDescriptor != null )
            {
                msg.append( " Component: " );

                msg.append( componentDescriptor.getHumanReadableKey() );
            }

            throw new CompositionException( msg.toString() );
        }

        return field;
    }
}
