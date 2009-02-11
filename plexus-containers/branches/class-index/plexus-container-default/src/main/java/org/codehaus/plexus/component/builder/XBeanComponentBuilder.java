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
package org.codehaus.plexus.component.builder;

import static org.apache.xbean.recipe.RecipeHelper.toClass;

import org.apache.xbean.recipe.AbstractRecipe;
import org.apache.xbean.recipe.ConstructionException;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;
import org.apache.xbean.recipe.RecipeHelper;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import static org.codehaus.plexus.PlexusConstants.PLEXUS_DEFAULT_HINT;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.MapOrientedComponent;
import static org.codehaus.plexus.component.ComponentStack.setComponentStackProperty;
import static org.codehaus.plexus.component.CastUtils.isAssignableFrom;
import org.codehaus.plexus.component.collections.LiveMap;
import org.codehaus.plexus.component.collections.LiveList;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.MapConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.factory.UndefinedComponentFactoryException;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XBeanComponentBuilder<T> implements ComponentBuilder<T> {
    private ComponentManager<T> componentManager;

    public XBeanComponentBuilder() {
    }

    public XBeanComponentBuilder(ComponentManager<T> componentManager) {
        setComponentManager(componentManager);
    }

    public ComponentManager<T> getComponentManager() {
        return componentManager;
    }

    public void setComponentManager(ComponentManager<T> componentManager) {
        this.componentManager = componentManager;
    }

    protected MutablePlexusContainer getContainer() {
        return componentManager.getContainer();
    }

    public T build(ComponentDescriptor<T> descriptor, ClassRealm realm, ComponentBuildListener listener) throws ComponentInstantiationException, ComponentLifecycleException {
        if (listener != null) {
            listener.beforeComponentCreate(descriptor, realm);
        }

        T component = createComponentInstance(descriptor, realm);

        if (listener != null) {
            listener.componentCreated(descriptor, component, realm);
        }

        startComponentLifecycle(component, realm);

        if (listener != null) {
            listener.componentConfigured(descriptor, component, realm);
        }

        return component;
    }

    protected T createComponentInstance(ComponentDescriptor<T> descriptor, ClassRealm realm) throws ComponentInstantiationException {
        MutablePlexusContainer container = getContainer();
        if (realm == null) {
            realm = descriptor.getRealm();
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(realm);
        try {
            ObjectRecipe recipe = createObjectRecipe(descriptor, realm);

            T instance;
            ComponentFactory componentFactory = container.getComponentFactoryManager().findComponentFactory(descriptor.getComponentFactory());
            if (JavaComponentFactory.class.equals(componentFactory.getClass())) {
                // xbean-reflect will create object and do injection
                instance = (T) recipe.create();
            } else {
                // todo figure out how to easily let xbean use the factory to construct the component
                // use object factory to construct component and then inject into that object
                instance = (T) componentFactory.newInstance(descriptor, realm, container);
                recipe.setProperties( instance );
            }

            // todo figure out how to easily let xbean do this map oriented stuff (if it is actually used in plexus)
            if ( instance instanceof MapOrientedComponent) {
                MapOrientedComponent mapOrientedComponent = (MapOrientedComponent) instance;
                processMapOrientedComponent(descriptor, mapOrientedComponent, realm);
            }

            return instance;
        }
        catch ( ConstructionException e )
        {
            Throwable cause = unwrapConstructionException( e );

            // do not rewrap
            if (cause instanceof ComponentInstantiationException)
            {
                throw (ComponentInstantiationException) cause;
            }

            // reuse original exception message.. the ones from XBean contain a lot of information
            if ( cause != null )
            {
                // wrap real cause if we got one
                throw new ComponentInstantiationException( e.getMessage(), cause );
            }
            else
            {
                throw new ComponentInstantiationException( e.getMessage() );
            }
        }
        catch ( UndefinedComponentFactoryException e )
        {
            throw new ComponentInstantiationException( e );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldClassLoader );
        }
    }

    public ObjectRecipe createObjectRecipe(ComponentDescriptor<T> descriptor, ClassRealm realm) throws ComponentInstantiationException {
        String factoryMethod = null;
        String[] constructorArgNames = null;
        Class[] constructorArgTypes = null;

        ObjectRecipe recipe = new ObjectRecipe(descriptor.getImplementationClass(),
                factoryMethod,
                constructorArgNames,
                constructorArgTypes);
        recipe.allow(Option.FIELD_INJECTION);
        recipe.allow(Option.PRIVATE_PROPERTIES);

        // MapOrientedComponents don't get normal injection
        if (!MapOrientedComponent.class.isAssignableFrom(descriptor.getImplementationClass())) {
            for (ComponentRequirement requirement : descriptor.getRequirements() ) {
                String name = requirement.getFieldName();
                RequirementRecipe requirementRecipe = new RequirementRecipe(descriptor, requirement, getContainer(), name == null);

                if (name != null) {
                    recipe.setProperty(name, requirementRecipe);
                } else {
                    recipe.setAutoMatchProperty(requirement.getRole(), requirementRecipe);
                }
            }

            // add configuration data
            if (shouldConfigure(descriptor )) {
                PlexusConfiguration configuration = descriptor.getConfiguration();
                if (configuration != null) {
                    for (String name : configuration.getAttributeNames()) {
                        String value;
                        try {
                            value = configuration.getAttribute(name);
                        } catch (PlexusConfigurationException e) {
                            throw new ComponentInstantiationException("Error getting value for attribute " + name, e);
                        }
                        name = fromXML(name);
                        recipe.setProperty(name, value);
                    }
                    for (PlexusConfiguration child : configuration.getChildren()) {
                        String name = child.getName();
                        name = fromXML(name);
                        if (child.getChildCount() == 0) {
                            recipe.setProperty(name, child.getValue());
                        } else {
                            recipe.setProperty(name, new PlexusConfigurationRecipe(child));
                        }
                    }
                }
            }
        }
        return recipe;
    }

    protected boolean shouldConfigure( ComponentDescriptor<T> descriptor ) {
        String configuratorId = descriptor.getComponentConfigurator();

        if (StringUtils.isEmpty(configuratorId)) {
            return true;
        }

        try {
            ComponentConfigurator componentConfigurator = getContainer().lookup(ComponentConfigurator.class, configuratorId);
            return componentConfigurator == null || componentConfigurator.getClass().equals(BasicComponentConfigurator.class);
        } catch (ComponentLookupException e) {
        }

        return true;
    }

    protected String fromXML(String elementName) {
        return StringUtils.lowercaseFirstLetter(StringUtils.removeAndHump(elementName, "-"));
    }

    protected void startComponentLifecycle(Object component, ClassRealm realm) throws ComponentLifecycleException {
        try {
            componentManager.start(component);
        } catch (Exception e) {
            Throwable cause = e;
            // if we got a PhaseExecutionException, unwrap it
            if ( e instanceof PhaseExecutionException && e.getCause() != null )
            {
                cause = e.getCause();
            }

            throw new ComponentLifecycleException("Error invoking start method", cause);
        }
    }

    public static class RequirementRecipe<T> extends AbstractRecipe {
        private ComponentDescriptor<T> componentDescriptor;
        private ComponentRequirement requirement;
        private MutablePlexusContainer container;
        private boolean autoMatch;

        public RequirementRecipe(ComponentDescriptor<T> componentDescriptor, ComponentRequirement requirement, MutablePlexusContainer container, boolean autoMatch) {
            this.componentDescriptor = componentDescriptor;
            this.requirement = requirement;
            this.container = container;
            this.autoMatch = autoMatch;
        }

        public boolean canCreate(Type expectedType) {
            if (!autoMatch)
            {
                return true;
            }

            Class<?> propertyType = toClass(expectedType);

            // Never auto match array, map or collection
            if (propertyType.isArray() || Map.class.isAssignableFrom(propertyType) || Collection.class.isAssignableFrom(propertyType) || requirement instanceof ComponentRequirementList) {
                return false;
            }

            // if the type to be created is an instance of the expected type, return true
            try {

                String roleHint = requirement.getRoleHint();
                Class<?> roleType = getInterfaceClass( container, requirement.getRole(), roleHint );

                for ( ComponentDescriptor<?> descriptor : container.getComponentDescriptorList( roleType ) )
                {
                    if ( descriptor.getRoleHint().equals( roleHint ) && isAssignableFrom( propertyType, descriptor.getImplementationClass() ) )
                    {
                        return true;
                    }
                }
            } catch (Exception e) {
            }

            return false;
        }

        @Override
        protected Object internalCreate(Type expectedType, boolean lazyRefAllowed) throws ConstructionException
        {
            Class<?> propertyType = toClass(expectedType);

            // push requirement property name on the stack
            setComponentStackProperty( requirement.getFieldName() );
            try
            {
                Class<?> roleType = getInterfaceClass( container, requirement.getRole(), requirement.getRoleHint() );
                List<String> roleHints = null;
                if ( requirement instanceof ComponentRequirementList )
                {
                    roleHints = ( (ComponentRequirementList) requirement ).getRoleHints();
                }

                Object assignment;
                if ( propertyType.isArray() )
                {
                    assignment = new ArrayList<Object>( container.lookupList( roleType, roleHints ) );
                }

                // Map.class.isAssignableFrom( clazz ) doesn't make sense, since Map.class doesn't really
                // have a meaningful superclass.
                else
                {
                    if ( Map.class.equals( propertyType ) )
                    {
                        // todo this is a lazy map

                        // get component type
                        Type keyType = Object.class;
                        Type valueType = Object.class;
                        Type[] typeParameters = RecipeHelper.getTypeParameters( Collection.class, expectedType );
                        if ( typeParameters != null && typeParameters.length == 2 )
                        {
                            if ( typeParameters[0] instanceof Class )
                            {
                                keyType = typeParameters[0];
                            }
                            if ( typeParameters[1] instanceof Class )
                            {
                                valueType = typeParameters[1];
                            }
                        }

                        // if no generic type, load the roll as a class
                        Class<?> valueClass = toClass( valueType );
                        if ( valueClass.equals( Object.class ) )
                        {
                            valueClass = roleType;
                        }

                        // todo verify key type is String

                        assignment = new LiveMap( container,
                            roleType,
                            roleHints,
                            componentDescriptor.getHumanReadableKey() );
                    }
                    // List.class.isAssignableFrom( clazz ) doesn't make sense, since List.class doesn't really
                    // have a meaningful superclass other than Collection.class, which we'll handle next.
                    else if ( List.class.equals( propertyType ) )
                    {
                        // get component type
                        Type[] typeParameters = RecipeHelper.getTypeParameters( Collection.class, expectedType );
                        Type componentType = Object.class;
                        if ( typeParameters != null && typeParameters.length == 1 && typeParameters[0] instanceof Class )
                        {
                            componentType = typeParameters[0];
                        }

                        // if no generic type, load the roll as a class
                        Class<?> componentClass = toClass( componentType );
                        if ( componentClass.equals( Object.class ) )
                        {
                            componentClass = roleType;
                        }

                        assignment = new LiveList( container,
                            roleType,
                            roleHints,
                            componentDescriptor.getHumanReadableKey() );
                    }
                    // Set.class.isAssignableFrom( clazz ) doesn't make sense, since Set.class doesn't really
                    // have a meaningful superclass other than Collection.class, and that would make this
                    // if-else cascade unpredictable (both List and Set extend Collection, so we'll put another
                    // check in for Collection.class.
                    else if ( Set.class.equals( propertyType ) || Collection.class.isAssignableFrom( propertyType ) )
                    {
                        // todo why isn't this lazy as above?
                        assignment = container.lookupMap( roleType, roleHints );
                    }
                    else if ( Logger.class.equals( propertyType ) )
                    {
                        // todo magic reference types should not be handled here
                        assignment = container.getLoggerManager().getLoggerForComponent(
                            componentDescriptor.getRole() );
                    }
                    else if ( PlexusContainer.class.equals( propertyType ) )
                    {
                        // todo magic reference types should not be handled here
                        assignment = container;
                    }
                    else
                    {
                        String roleHint = requirement.getRoleHint();
                        assignment = container.lookup( roleType, roleHint );
                    }
                }

                return assignment;
            }
            catch ( ComponentLookupException e )
            {
                // simply wrap exception, so it can be unwrapped and rethrown
                throw new ConstructionException( e );
            }
            finally
            {
                setComponentStackProperty( null );
            }
        }

        @Override
        public String toString() {
            return "RequirementRecipe[fieldName=" + requirement.getFieldName() + ", role=" + componentDescriptor.getRole() + "]";
        }
    }

    private class PlexusConfigurationRecipe extends AbstractRecipe
    {
        private final PlexusConfiguration child;

        public PlexusConfigurationRecipe( PlexusConfiguration child )
        {
            this.child = child;
        }

        public boolean canCreate( Type type )
        {
            try
            {
                ConverterLookup lookup = createConverterLookup();
                lookup.lookupConverterForType( toClass( type ) );
                return true;
            }
            catch ( ComponentConfigurationException e )
            {
                return false;
            }
        }

        @Override
        protected Object internalCreate( Type expectedType, boolean lazyRefAllowed ) throws ConstructionException
        {
            setComponentStackProperty( child.getName() );
            try
            {
                ConverterLookup lookup = createConverterLookup();
                ConfigurationConverter converter = lookup.lookupConverterForType( toClass( expectedType ) );

                // todo this will not work for static factories
                ObjectRecipe caller = (ObjectRecipe) RecipeHelper.getCaller();
                Class parentClass = toClass( caller.getType() );

                Object value = converter.fromConfiguration(
                    lookup,
                    child,
                    toClass( expectedType ),
                    parentClass,
                    Thread.currentThread().getContextClassLoader(),
                    new DefaultExpressionEvaluator() );

                return value;
            }
            catch ( ComponentConfigurationException e )
            {
                // simply wrap exception, so it can be unwrapped and rethrown
                throw new ConstructionException( e );
            }
            finally
            {
                setComponentStackProperty( null );
            }
        }

        private ConverterLookup createConverterLookup()
        {
            ClassRealm realm = (ClassRealm) Thread.currentThread().getContextClassLoader();
            ConverterLookup lookup = new DefaultConverterLookup();
            lookup.registerConverter( new ClassRealmConverter( realm ) );
            return lookup;
        }
    }


    private void processMapOrientedComponent(ComponentDescriptor<?> descriptor, MapOrientedComponent mapOrientedComponent, ClassRealm realm) throws ComponentInstantiationException {
        MutablePlexusContainer container = getContainer();

        for (ComponentRequirement requirement : descriptor.getRequirements()) {
            String role = requirement.getRole();
            String hint = requirement.getRoleHint();
            String mappingType = requirement.getFieldMappingType();


            try
            {
                // if the hint is not empty (and not default), we don't care about mapping type...
                // it's a single-value, not a collection.
                Object value;
                if (StringUtils.isNotEmpty(hint) && !hint.equals(PlexusConstants.PLEXUS_DEFAULT_HINT)) {
                    value = container.lookup(role, hint);
                } else if ("single".equals(mappingType)) {
                    value = container.lookup(role, hint);
                } else if ("map".equals(mappingType)) {
                    value = container.lookupMap(role);
                } else if ("set".equals(mappingType)) {
                    value = new HashSet<Object>(container.lookupList(role));
                } else {
                    value = container.lookup(role, hint);
                }

                mapOrientedComponent.addComponentRequirement(requirement, value);
            }
            catch ( ComponentLookupException e )
            {
                throw new ComponentInstantiationException( "Error looking up requirement of MapOrientedComponent ", e );
            }
            catch ( ComponentConfigurationException e )
            {
                throw new ComponentInstantiationException( "Error adding requirement to MapOrientedComponent ", e );
            }
        }

        MapConverter converter = new MapConverter();
        ConverterLookup converterLookup = new DefaultConverterLookup();
        DefaultExpressionEvaluator expressionEvaluator = new DefaultExpressionEvaluator();
        PlexusConfiguration configuration = container.getConfigurationSource().getConfiguration( descriptor );

        if ( configuration != null )
        {
            try
            {
                Map context = (Map) converter.fromConfiguration(converterLookup,
                                                                configuration,
                                                                null,
                                                                null,
                                                                realm,
                                                                expressionEvaluator,
                                                                null );

                mapOrientedComponent.setComponentConfiguration( context );
            }
            catch ( ComponentConfigurationException e )
            {
                throw new ComponentInstantiationException( "Error adding configuration to MapOrientedComponent ", e );
            }
        }
    }

    private static Class<?> getInterfaceClass( PlexusContainer container, String role, String hint )
    {
        if ( hint == null ) hint = PLEXUS_DEFAULT_HINT;

        try
        {
            ClassRealm realm = container.getLookupRealm();

            if ( realm != null )
            {
                return realm.loadClass( role );
            }
        }
        catch ( Throwable e )
        {
        }

        try
        {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if ( loader != null )
            {
                return loader.loadClass( role );
            }
        }
        catch ( Throwable e )
        {
        }

        try
        {
            ComponentDescriptor<?> cd = container.getComponentDescriptor( role, hint );
            if ( cd != null )
            {
                ClassLoader loader = cd.getImplementationClass().getClassLoader();
                if ( loader != null )
                {
                    return loader.loadClass( role );
                }
            }
        }
        catch ( Throwable ignored )
        {
        }

        return Object.class;
    }

    /**
     * There are a few bugs in XBean reflect where a constuction exception is wrapped with another constuction exception.
     */
    private static Throwable unwrapConstructionException( ConstructionException e )
    {
        Throwable cause = e;
        while ( cause instanceof ConstructionException && e.getCause() != null)
        {
            cause = cause.getCause();
        }
        return cause;
    }
}
