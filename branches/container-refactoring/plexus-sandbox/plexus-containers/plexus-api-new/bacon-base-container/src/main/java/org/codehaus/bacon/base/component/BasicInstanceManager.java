package org.codehaus.bacon.base.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.Ognl;
import ognl.OgnlException;

import org.codehaus.bacon.Container;
import org.codehaus.bacon.ContainerException;
import org.codehaus.bacon.LookupException;
import org.codehaus.bacon.component.ComponentAttribute;
import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentReference;
import org.codehaus.bacon.component.ComponentUtilities;
import org.codehaus.bacon.component.DuplicateComponentInstanceException;
import org.codehaus.bacon.component.factory.InstanceFactory;
import org.codehaus.bacon.component.factory.InstantiationException;
import org.codehaus.bacon.component.injection.ComponentInjectionException;
import org.codehaus.bacon.component.language.LanguagePack;
import org.codehaus.bacon.component.manager.InstanceManager;
import org.codehaus.bacon.session.ContainerSession;
import org.codehaus.bacon.session.ContainerSessionManager;
import org.codehaus.bacon.session.SessionKey;

public class BasicInstanceManager
    implements InstanceManager
{
    private static final String DEFAULT_IMPLEMENTATION_LANGUAGE = "java";

    private final Container container;

    private final ClassLoader containerLoader;

    private Map singletons = new HashMap();

    private final Map languagePacksByLanguageKey;

    public BasicInstanceManager( Map languagePacksByLanguageKey, Container container, ClassLoader containerLoader )
    {
        this.languagePacksByLanguageKey = languagePacksByLanguageKey;
        this.container = container;
        this.containerLoader = containerLoader;
    }

    public Object getInstance( ComponentDescriptor descriptor, SessionKey sessionKey, Map context, boolean nativeToContainer )
        throws InstantiationException
    {
        Object instance = null;

        try
        {
            instance = resolveCached( descriptor, sessionKey, nativeToContainer );
        }
        catch ( ContainerException e )
        {
            throw new InstantiationException( "Error looking up cached instance of: " + descriptor + ". Reason: "
                + e.getMessage(), e );
        }

        if ( instance == null )
        {
            LanguagePack langPack = getLanguagePack( descriptor );

            List constructorParameters = resolveConstructorParameters( descriptor, sessionKey, context );

            InstanceFactory factory = langPack.getInstanceFactory();

            instance = factory.instantiate( descriptor, constructorParameters, containerLoader );

            compose( instance, descriptor, sessionKey, context, langPack );

            try
            {
                cacheIfAppropriate( instance, descriptor, sessionKey, nativeToContainer );
            }
            catch ( DuplicateComponentInstanceException e )
            {
                throw new InstantiationException( "Error caching component instance. Duplicate of: " + descriptor
                    + " already registered.", e );
            }
        }

        return instance;
    }

    private LanguagePack getLanguagePack( ComponentDescriptor descriptor )
        throws InstantiationException
    {
        String lang = descriptor.getImplementationLanguage();

        if ( lang == null || lang.trim().length() < 1 )
        {
            lang = DEFAULT_IMPLEMENTATION_LANGUAGE;
        }

        // fail fast.
        if ( !languagePacksByLanguageKey.containsKey( lang ) )
        {
            throw new InstantiationException( "No InstanceFactory found for language: " + lang );
        }

        return (LanguagePack) languagePacksByLanguageKey.get( lang );
    }

    private void compose( Object instance, ComponentDescriptor descriptor, SessionKey sessionKey, Map context,
                         LanguagePack langPack )
        throws InstantiationException
    {
        Map valuesByCompositionSource = new HashMap();

        injectNonConstructorAttributes( instance, descriptor, sessionKey, context, valuesByCompositionSource );
        injectNonConstructorReferences( instance, descriptor, sessionKey, context, valuesByCompositionSource );
        
        try
        {
            langPack.getComponentInjector().inject( instance, valuesByCompositionSource, containerLoader );
        }
        catch ( ComponentInjectionException e )
        {
            throw new InstantiationException( "Error injecting non-constructor references and attributes into component. Reason: " + e.getMessage(), e );
        }
    }

    private void injectNonConstructorAttributes( Object instance, ComponentDescriptor descriptor,
                                                SessionKey sessionKey, Map context, Map valuesByCompositionSource )
        throws InstantiationException
    {
        Set attributes = descriptor.getComponentAttributes();

        if ( attributes == null || attributes.isEmpty() )
        {
            // nothing to do.
            return;
        }

        List constructorIds = descriptor.getConstructionRequirements();
        if ( constructorIds == null )
        {
            constructorIds = Collections.EMPTY_LIST;
        }

        for ( Iterator it = attributes.iterator(); it.hasNext(); )
        {
            ComponentAttribute attribute = (ComponentAttribute) it.next();

            Object value = resolveAttribute( attribute, context );

            valuesByCompositionSource.put( attribute, value );
        }
    }

    private void injectNonConstructorReferences( Object instance, ComponentDescriptor descriptor,
                                                SessionKey sessionKey, Map context, Map valuesByCompositionSource )
        throws InstantiationException
    {
        Set references = descriptor.getComponentReferences();

        if ( references == null || references.isEmpty() )
        {
            // nothing to do.
            return;
        }

        List constructorIds = descriptor.getConstructionRequirements();
        if ( constructorIds == null )
        {
            constructorIds = Collections.EMPTY_LIST;
        }

        for ( Iterator it = references.iterator(); it.hasNext(); )
        {
            ComponentReference reference = (ComponentReference) it.next();

            Object component = resolveReference( reference, sessionKey, context );

            valuesByCompositionSource.put( reference, component );
        }
    }

    private void cacheIfAppropriate( Object instance, ComponentDescriptor descriptor, SessionKey sessionKey, boolean nativeToContainer )
        throws DuplicateComponentInstanceException
    {
        if ( !nativeToContainer || ComponentDescriptor.SESSION_SINGLETON_INSTANTIATION_STRATEGY
            .equals( descriptor.getInstantiationStrategy() ) )
        {
            ContainerSession session = ContainerSessionManager.instance().get( sessionKey, container.getContainerId() );

            session.registerComponentInstance( descriptor, instance );
        }
        else if ( ComponentDescriptor.TRUE_SINGLETON_INSTANTIATION_STRATEGY.equals( descriptor
            .getInstantiationStrategy() ) )
        {
            String key = ComponentUtilities.createDescriptorKey( descriptor );

            singletons.put( key, instance );
        }
    }

    private Object resolveCached( ComponentDescriptor descriptor, SessionKey sessionKey, boolean nativeToContainer )
        throws ContainerException
    {
        Object instance = null;

        if ( !nativeToContainer || ComponentDescriptor.SESSION_SINGLETON_INSTANTIATION_STRATEGY
            .equals( descriptor.getInstantiationStrategy() ) )
        {
            ContainerSession session = ContainerSessionManager.instance().get( sessionKey, container.getContainerId() );

            instance = session.getComponentInstance( descriptor );
        }
        else if ( ComponentDescriptor.TRUE_SINGLETON_INSTANTIATION_STRATEGY.equals( descriptor
            .getInstantiationStrategy() ) )
        {
            String key = ComponentUtilities.createDescriptorKey( descriptor );

            instance = singletons.get( key );
        }

        return instance;
    }

    private List resolveConstructorParameters( ComponentDescriptor descriptor, SessionKey sessionKey, Map context )
        throws InstantiationException
    {
        List constructionRequirements = descriptor.getConstructionRequirements();

        List parameters;

        if ( constructionRequirements != null && !constructionRequirements.isEmpty() )
        {
            Map constructorReferences = resolveConstructorReferences( constructionRequirements, descriptor
                .getComponentReferencesById(), sessionKey, context );

            Map constructorAttributes = resolveConstructorAttributes( constructionRequirements, descriptor
                .getComponentAttributesById(), sessionKey, context );

            parameters = new ArrayList( constructionRequirements.size() );

            for ( Iterator it = constructionRequirements.iterator(); it.hasNext(); )
            {
                String id = (String) it.next();

                Object referent = constructorReferences.get( id );

                if ( referent == null )
                {
                    referent = constructorAttributes.get( id );
                }

                if ( referent == null )
                {
                    throw new InstantiationException( "Missing constructor requirement: \'" + id + "\'." );
                }

                parameters.add( referent );
            }
        }
        else
        {
            parameters = Collections.EMPTY_LIST;
        }

        return parameters;
    }

    private Map resolveConstructorAttributes( List constructionRequirements, Map componentAttributes,
                                             SessionKey sessionKey, Map context )
        throws InstantiationException
    {
        Map resolvedAttributes = new HashMap();

        for ( Iterator it = constructionRequirements.iterator(); it.hasNext(); )
        {
            String id = (String) it.next();

            ComponentAttribute attribute = (ComponentAttribute) componentAttributes.get( id );

            if ( attribute != null )
            {
                Object resolvedValue = resolveAttribute( attribute, context );

                resolvedAttributes.put( id, resolvedValue );
            }
        }

        return resolvedAttributes;
    }

    private Object resolveAttribute( ComponentAttribute attribute, Map context )
        throws InstantiationException
    {
        String id = attribute.getId();

        String expression = attribute.getExpression();
        String type = attribute.getType();

        Class expectedType;
        try
        {
            expectedType = containerLoader.loadClass( type );
        }
        catch ( ClassNotFoundException e )
        {
            throw new InstantiationException( "Error evaluating attribute expression for: \'" + id + "\'. Class: "
                + type + " could not be found." );
        }

        Object resolvedValue;
        try
        {
            resolvedValue = Ognl.getValue( expression, context, (Object) null, expectedType );
        }
        catch ( OgnlException e )
        {
            throw new InstantiationException( "Error evaluating attribute expression for: \'" + id + "\'. Reason: "
                + e.getMessage(), e );
        }

        return resolvedValue;
    }

    private Map resolveConstructorReferences( List constructionRequirements, Map componentReferences,
                                             SessionKey sessionKey, Map context )
        throws InstantiationException
    {
        Map resolvedReferences = new HashMap();

        for ( Iterator it = constructionRequirements.iterator(); it.hasNext(); )
        {
            String id = (String) it.next();

            ComponentReference reference = (ComponentReference) componentReferences.get( id );

            if ( reference != null )
            {
                Object component = resolveReference( reference, sessionKey, context );

                if ( component != null )
                {
                    resolvedReferences.put( id, component );
                }
            }
        }

        return resolvedReferences;
    }

    private Object resolveReference( ComponentReference reference, SessionKey sessionKey, Map context )
        throws InstantiationException
    {
        String id = reference.getId();

        String cardinality = reference.getCardinality();

        if ( !ComponentReference.CARDINALITY_1_1.equals( cardinality ) )
        {
            throw new UnsupportedOperationException( "Error evaluating reference: \'" + id
                + "\'. 1:M component references are not yet supported." );
        }
        else
        {
            boolean found;
            try
            {
                found = container.containsComponent( reference.getInterfaceName(), sessionKey );
            }
            catch ( ContainerException e )
            {
                throw new InstantiationException( "Error satisfying component reference: \'" + id
                    + "\'. Container failed while looking for component." );
            }

            if ( !found )
            {
                throw new InstantiationException( "Cannot satisfy component reference: \'" + id
                    + "\'. Component not found." );
            }

            Object component;
            try
            {
                component = container.lookup( reference.getInterfaceName(), sessionKey, context );
            }
            catch ( LookupException e )
            {
                throw new InstantiationException( "Error satisfying component reference: \'" + id
                    + "\'. Container failed while looking up component." );
            }
            catch ( ContainerException e )
            {
                throw new InstantiationException( "Error satisfying component reference: \'" + id
                    + "\'. Container failed while looking up component." );
            }

            return component;
        }
    }

}
