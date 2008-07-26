package org.codehaus.plexus.spring;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A custom ClassPathXmlApplicationContext to support plexus
 * <tr>components.xml</tt> descriptors in Spring, with no changes required to
 * neither plexus nor spring beans.
 *
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 * @version $Id$
 */
public class PlexusClassPathXmlApplicationContext
    extends ClassPathXmlApplicationContext
{
    private static PlexusApplicationContextDelegate delegate = new PlexusApplicationContextDelegate();

    public PlexusClassPathXmlApplicationContext( String path, Class clazz )
        throws BeansException
    {
        super( path, clazz );
    }

    public PlexusClassPathXmlApplicationContext( String configLocation )
        throws BeansException
    {
        super( configLocation );
    }

    public PlexusClassPathXmlApplicationContext( String[] configLocations, ApplicationContext parent )
        throws BeansException
    {
        super( configLocations, parent );
    }

    public PlexusClassPathXmlApplicationContext( String[] configLocations, boolean refresh, ApplicationContext parent )
        throws BeansException
    {
        super( configLocations, refresh, parent );
    }

    public PlexusClassPathXmlApplicationContext( String[] configLocations, boolean refresh )
        throws BeansException
    {
        super( configLocations, refresh );
    }

    public PlexusClassPathXmlApplicationContext( String[] paths, Class clazz, ApplicationContext parent )
        throws BeansException
    {
        super( paths, clazz, parent );
    }

    public PlexusClassPathXmlApplicationContext( String[] paths, Class clazz )
        throws BeansException
    {
        super( paths, clazz );
    }

    public PlexusClassPathXmlApplicationContext( String[] configLocations )
        throws BeansException
    {
        super( configLocations );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.context.support.AbstractXmlApplicationContext#loadBeanDefinitions(org.springframework.beans.factory.xml.XmlBeanDefinitionReader)
     */
    protected void loadBeanDefinitions( XmlBeanDefinitionReader reader )
        throws BeansException, IOException
    {
        delegate.loadBeanDefinitions( reader );
        super.loadBeanDefinitions( reader );
    }

    /**
     * Copied from {@link AbstractXmlApplicationContext}
     * Loads the bean definitions via an XmlBeanDefinitionReader.
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     * @see #initBeanDefinitionReader
     * @see #loadBeanDefinitions
     */
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException {
        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        XmlBeanDefinitionReader beanDefinitionReader = new PlexusXmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.context.support.AbstractApplicationContext#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    protected void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory )
    {
        delegate.postProcessBeanFactory( beanFactory, this );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.context.support.AbstractApplicationContext#doClose()
     */
    protected void doClose()
    {
        delegate.doClose();
        super.doClose();
    }

}
