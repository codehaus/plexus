package org.codehaus.plexus.interpolation.object;

/*
 * Copyright 2001-2008 Codehaus Foundation.
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

/**
 * Reflectively traverses an object graph and uses an {@link Interpolator} instance to resolve any String fields in the
 * graph. 
 * <br/>
 * <br/>
 * NOTE: This code is based on a reimplementation of ModelInterpolator in 
 * maven-project 2.1.0-M1, which became a performance bottleneck when the 
 * interpolation process became a hotspot.
 * 
 * @author jdcasey
 */
public class FieldBasedObjectInterpolator
    implements ObjectInterpolator
{
    public static final Set DEFAULT_BLACKLISTED_FIELD_NAMES;

    public static final Set DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;

    private static final Map fieldsByClass = new WeakHashMap();

    private static final Map fieldIsPrimitiveByClass = new WeakHashMap();

    static
    {
        Set blacklistedFields = new HashSet();
        blacklistedFields.add( "parent" );

        DEFAULT_BLACKLISTED_FIELD_NAMES = Collections.unmodifiableSet( blacklistedFields );

        Set blacklistedPackages = new HashSet();
        blacklistedPackages.add( "java" );

        DEFAULT_BLACKLISTED_PACKAGE_PREFIXES = Collections.unmodifiableSet( blacklistedPackages );
    }

    /**
     * Clear out the Reflection caches kept for the most expensive operations encountered: field lookup and primitive
     * queries for fields. These caches are static since they apply at the class level, not the instance level.
     */
    public static void clearCaches()
    {
        fieldsByClass.clear();
        fieldIsPrimitiveByClass.clear();
    }

    private Set blacklistedFieldNames;

    private Set blacklistedPackagePrefixes;

    private List warnings = new ArrayList();

    /**
     * Use the default settings for blacklisted fields and packages, where fields named 'parent' and classes in packages
     * starting with 'java' will not be interpolated.
     */
    public FieldBasedObjectInterpolator()
    {
        this.blacklistedFieldNames = DEFAULT_BLACKLISTED_FIELD_NAMES;
        this.blacklistedPackagePrefixes = DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;
    }

    /**
     * Use the given black-lists to limit the interpolation of fields and classes (by package).
     * 
     * @param blacklistedFieldNames The list of field names to ignore
     * @param blacklistedPackagePrefixes The list of package prefixes whose classes should be ignored
     */
    public FieldBasedObjectInterpolator( Set blacklistedFieldNames, Set blacklistedPackagePrefixes )
    {
        this.blacklistedFieldNames = blacklistedFieldNames;
        this.blacklistedPackagePrefixes = blacklistedPackagePrefixes;
    }

    /**
     * Returns true if the last interpolation execution generated warnings.
     */
    public boolean hasWarnings()
    {
        return warnings != null && !warnings.isEmpty();
    }

    /**
     * Retrieve the {@link List} of warnings ({@link ObjectInterpolationWarning}
     * instances) generated during the last interpolation execution.
     */
    public List getWarnings()
    {
        return new ArrayList( warnings );
    }

    /**
     * Using reflective field access and mutation, traverse the object graph from the given starting point and
     * interpolate any Strings found in that graph using the given {@link Interpolator}. Limits to this process can be
     * managed using the black lists configured in the constructor.
     * 
     * @param target The starting point of the object graph to traverse
     * @param interpolator The {@link Interpolator} used to resolve any Strings encountered during traversal.
     * 
     * NOTE: Uses {@link SimpleRecursionInterceptor}.
     */
    public void interpolate( Object target, Interpolator interpolator )
        throws InterpolationException
    {
        interpolate( target, interpolator, new SimpleRecursionInterceptor() );
    }
    
    /**
     * Using reflective field access and mutation, traverse the object graph from the given starting point and
     * interpolate any Strings found in that graph using the given {@link Interpolator}. Limits to this process can be
     * managed using the black lists configured in the constructor.
     * 
     * @param target The starting point of the object graph to traverse
     * @param interpolator The {@link Interpolator} used to resolve any Strings encountered during traversal.
     * @param recursionInterceptor The {@link RecursionInterceptor} used to detect cyclical expressions in the graph
     */
    public void interpolate( Object target, Interpolator interpolator, RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        warnings.clear();

        InterpolateObjectAction action =
            new InterpolateObjectAction( target, interpolator, recursionInterceptor, blacklistedFieldNames, blacklistedPackagePrefixes,
                                         warnings );

        InterpolationException error = (InterpolationException) AccessController.doPrivileged( action );

        if ( error != null )
        {
            throw error;
        }
    }

    private static final class InterpolateObjectAction
        implements PrivilegedAction
    {

        private final LinkedList interpolationTargets;

        private final Interpolator interpolator;

        private final Set blacklistedFieldNames;

        private final String[] blacklistedPackagePrefixes;

        private final List warningCollector;

        private final RecursionInterceptor recursionInterceptor;

        /**
         * Setup an object graph traversal for the given target starting point. This will initialize a queue of objects
         * to traverse and interpolate by adding the target object.
         */
        public InterpolateObjectAction( Object target, Interpolator interpolator,
                                        RecursionInterceptor recursionInterceptor, Set blacklistedFieldNames,
                                        Set blacklistedPackagePrefixes, List warningCollector )
        {
            this.recursionInterceptor = recursionInterceptor;
            this.blacklistedFieldNames = blacklistedFieldNames;
            this.warningCollector = warningCollector;
            this.blacklistedPackagePrefixes = (String[]) blacklistedPackagePrefixes.toArray( new String[0] );

            this.interpolationTargets = new LinkedList();
            interpolationTargets.add( new InterpolationTarget( target, "" ) );

            this.interpolator = interpolator;
        }

        /**
         * As long as the traversal queue is non-empty, traverse the next object in the queue. If an interpolation error
         * occurs, return it immediately.
         */
        public Object run()
        {
            while ( !interpolationTargets.isEmpty() )
            {
                InterpolationTarget target = (InterpolationTarget) interpolationTargets.removeFirst();

                try
                {
                    traverseObjectWithParents( target.value.getClass(), target );
                }
                catch ( InterpolationException e )
                {
                    return e;
                }
            }

            return null;
        }

        /**
         * Traverse the given object, interpolating any String fields and adding non-primitive field values to the
         * interpolation queue for later processing.
         */
        private void traverseObjectWithParents( Class cls, InterpolationTarget target )
            throws InterpolationException
        {
            Object obj = target.value;
            String basePath = target.path;

            if ( cls == null )
            {
                return;
            }

            if ( cls.isArray() )
            {
                evaluateArray( obj, basePath );
            }
            else if ( isQualifiedForInterpolation( cls ) )
            {
                Field[] fields = (Field[]) fieldsByClass.get( cls );
                if ( fields == null )
                {
                    fields = cls.getDeclaredFields();
                    fieldsByClass.put( cls, fields );
                }

                for ( int i = 0; i < fields.length; i++ )
                {
                    Class type = fields[i].getType();
                    if ( isQualifiedForInterpolation( fields[i], type ) )
                    {
                        boolean isAccessible = fields[i].isAccessible();
                        fields[i].setAccessible( true );
                        try
                        {
                            try
                            {
                                if ( String.class == type )
                                {
                                    String value = (String) fields[i].get( obj );
                                    if ( value != null )
                                    {
                                        String interpolated = interpolator.interpolate( value, recursionInterceptor );

                                        if ( !interpolated.equals( value ) )
                                        {
                                            fields[i].set( obj, interpolated );
                                        }
                                    }
                                }
                                else if ( Collection.class.isAssignableFrom( type ) )
                                {
                                    Collection c = (Collection) fields[i].get( obj );
                                    if ( c != null && !c.isEmpty() )
                                    {
                                        List originalValues = new ArrayList( c );
                                        try
                                        {
                                            c.clear();
                                        }
                                        catch ( UnsupportedOperationException e )
                                        {
                                            warningCollector.add( new ObjectInterpolationWarning(
                                                                                                  "Field is an unmodifiable collection. Skipping interpolation.",
                                                                                                  basePath
                                                                                                      + "."
                                                                                                      + fields[i].getName(),
                                                                                                  e ) );
                                            continue;
                                        }

                                        for ( Iterator it = originalValues.iterator(); it.hasNext(); )
                                        {
                                            Object value = it.next();
                                            if ( value != null )
                                            {
                                                if ( String.class == value.getClass() )
                                                {
                                                    String interpolated = interpolator.interpolate( (String) value, recursionInterceptor );

                                                    if ( !interpolated.equals( value ) )
                                                    {
                                                        c.add( interpolated );
                                                    }
                                                    else
                                                    {
                                                        c.add( value );
                                                    }
                                                }
                                                else
                                                {
                                                    c.add( value );
                                                    if ( value.getClass().isArray() )
                                                    {
                                                        evaluateArray( value, basePath + "." + fields[i].getName() );
                                                    }
                                                    else
                                                    {
                                                        interpolationTargets.add( new InterpolationTarget(
                                                                                                           value,
                                                                                                           basePath
                                                                                                               + "."
                                                                                                               + fields[i].getName() ) );
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                // add the null back in...not sure what else to do...
                                                c.add( value );
                                            }
                                        }
                                    }
                                }
                                else if ( Map.class.isAssignableFrom( type ) )
                                {
                                    Map m = (Map) fields[i].get( obj );
                                    if ( m != null && !m.isEmpty() )
                                    {
                                        for ( Iterator it = m.entrySet().iterator(); it.hasNext(); )
                                        {
                                            Map.Entry entry = (Map.Entry) it.next();

                                            Object value = entry.getValue();

                                            if ( value != null )
                                            {
                                                if ( String.class == value.getClass() )
                                                {
                                                    String interpolated = interpolator.interpolate( (String) value, recursionInterceptor );

                                                    if ( !interpolated.equals( value ) )
                                                    {
                                                        try
                                                        {
                                                            entry.setValue( interpolated );
                                                        }
                                                        catch ( UnsupportedOperationException e )
                                                        {
                                                            warningCollector.add( new ObjectInterpolationWarning(
                                                                                                                  "Field is an unmodifiable collection. Skipping interpolation.",
                                                                                                                  basePath
                                                                                                                      + "."
                                                                                                                      + fields[i].getName(),
                                                                                                                  e ) );
                                                            continue;
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    if ( value.getClass().isArray() )
                                                    {
                                                        evaluateArray( value, basePath + "." + fields[i].getName() );
                                                    }
                                                    else
                                                    {
                                                        interpolationTargets.add( new InterpolationTarget(
                                                                                                           value,
                                                                                                           basePath
                                                                                                               + "."
                                                                                                               + fields[i].getName() ) );
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    Object value = fields[i].get( obj );
                                    if ( value != null )
                                    {
                                        if ( fields[i].getType().isArray() )
                                        {
                                            evaluateArray( value, basePath + "." + fields[i].getName() );
                                        }
                                        else
                                        {
                                            interpolationTargets.add( new InterpolationTarget( value, basePath + "."
                                                + fields[i].getName() ) );
                                        }
                                    }
                                }
                            }
                            catch ( IllegalArgumentException e )
                            {
                                warningCollector.add( new ObjectInterpolationWarning(
                                                                                      "Failed to interpolate field. Skipping.",
                                                                                      basePath + "."
                                                                                          + fields[i].getName(), e ) );
                            }
                            catch ( IllegalAccessException e )
                            {
                                warningCollector.add( new ObjectInterpolationWarning(
                                                                                      "Failed to interpolate field. Skipping.",
                                                                                      basePath + "."
                                                                                          + fields[i].getName(), e ) );
                            }
                        }
                        finally
                        {
                            fields[i].setAccessible( isAccessible );
                        }
                    }
                }

                traverseObjectWithParents( cls.getSuperclass(), target );
            }
        }

        /**
         * Using the package-prefix blacklist, determine whether the given class is qualified for interpolation, or
         * whether it should be ignored.
         */
        private boolean isQualifiedForInterpolation( Class cls )
        {
            String pkgName = cls.getPackage().getName();
            for ( int i = 0; i < blacklistedPackagePrefixes.length; i++ )
            {
                String prefix = blacklistedPackagePrefixes[i];
                if ( pkgName.startsWith( prefix ) )
                {
                    return false;
                }
            }

            return true;
        }

        /**
         * Using the field-name blacklist and the primitive-field cache, determine whether the given field in the given
         * class is qualified for interpolation. Primitive fields and fields listed in the blacklist will be ignored.
         * The primitive-field cache is used to improve the performance of the reflective operations in this method,
         * since this method is a hotspot.
         */
        private boolean isQualifiedForInterpolation( Field field, Class fieldType )
        {
            if ( !fieldIsPrimitiveByClass.containsKey( fieldType ) )
            {
                fieldIsPrimitiveByClass.put( fieldType, Boolean.valueOf( fieldType.isPrimitive() ) );
            }

            if ( ( (Boolean) fieldIsPrimitiveByClass.get( fieldType ) ).booleanValue() )
            {
                return false;
            }

            if ( blacklistedFieldNames.contains( field.getName() ) )
            {
                return false;
            }

            return true;
        }

        /**
         * Traverse the elements of an array, and interpolate any qualified objects or add them to the traversal queue.
         */
        private void evaluateArray( Object target, String basePath )
            throws InterpolationException
        {
            int len = Array.getLength( target );
            for ( int i = 0; i < len; i++ )
            {
                Object value = Array.get( target, i );
                if ( value != null )
                {
                    if ( String.class == value.getClass() )
                    {
                        String interpolated = interpolator.interpolate( (String) value, recursionInterceptor );

                        if ( !interpolated.equals( value ) )
                        {
                            Array.set( target, i, interpolated );
                        }
                    }
                    else
                    {
                        interpolationTargets.add( new InterpolationTarget( value, basePath + "[" + i + "]" ) );
                    }
                }
            }
        }
    }

    private static final class InterpolationTarget
    {
        private Object value;

        private String path;

        private InterpolationTarget( Object value, String path )
        {
            this.value = value;
            this.path = path;
        }
    }
}
