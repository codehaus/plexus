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
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A spring namespace handler to support plexus components creation and direct
 * field-injection in a spring XmlApplicationContext.
 *
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 * @since 1.1
 * @version $Id$
 */
public class PlexusNamespaceHandler
    extends NamespaceHandlerSupport
{
    
    private Logger log = LoggerFactory.getLogger( getClass() );
    
    public void init()
    {
        registerBeanDefinitionParser( "component", new PlexusComponentBeanDefinitionParser() );
        registerBeanDefinitionParser( "requirement", new NopBeanDefinitionParser() );
        registerBeanDefinitionParser( "configuration", new NopBeanDefinitionParser() );
    }

    private class NopBeanDefinitionParser
        extends AbstractBeanDefinitionParser
    {
        protected AbstractBeanDefinition parseInternal( Element element, ParserContext parserContext )
        {
            return null;
        }

    }

    /**
     * BeanDefinitionParser for &lt;plexus:component&gt;. Register a bean
     * definition for a PlexusComponentFactoryBean with all nested requirement /
     * configuration injected using direct field injection.
     * <p>
     * Also register an alias for the Plexus component using spring conventions
     * (interface class simple name + "#" role-hint)
     */
    private class PlexusComponentBeanDefinitionParser
        extends AbstractSingleBeanDefinitionParser
    {
        private int count;

        protected void doParse( Element element, BeanDefinitionBuilder builder )
        {
            builder.addPropertyValue( "role", element.getAttribute( "role" ) );
            String beanRef = PlexusToSpringUtils.buildSpringId( element.getAttribute( "role" ), element.getAttribute( "role-hint" ) );
            builder.addPropertyValue( "beanRef", beanRef );
            String implementation = element.getAttribute( "implementation" );
            builder.addPropertyValue( "implementation", implementation );
            builder.addPropertyValue( "instantiationStrategy", element.getAttribute( "instantiation-strategy" ) );

            Map dependencies = new HashMap();

            List requirements = DomUtils.getChildElementsByTagName( element, "requirement" );
            for ( Iterator iterator = requirements.iterator(); iterator.hasNext(); )
            {
                Element child = (Element) iterator.next();
                String name = child.getAttribute( "field-name" );
                if ( name.length() == 0 )
                {
                    // Plexus doesn't require to specify the field-name if only
                    // one field matches the injected type
                    name = "#" + count++;
                }
                String role = child.getAttribute( "role" );
                String roleHint = child.getAttribute( "role-hint" );
                String ref = PlexusToSpringUtils.buildSpringId( role, roleHint );
                PlexusRuntimeBeanReference runtimeBeanReference = new PlexusRuntimeBeanReference( ref, role, roleHint );
                dependencies.put( name, runtimeBeanReference );

            }

            List configurations = DomUtils.getChildElementsByTagName( element, "configuration" );
            StringBuilder value = new StringBuilder();
            for ( Iterator iterator = configurations.iterator(); iterator.hasNext(); )
            {
                Element child = (Element) iterator.next();
                String name = child.getAttribute( "name" );
                
                if ( child.getChildNodes().getLength() == 1 )
                {
                    String dependencyValue = DOM2Utils.getTextContext( child );
                    value.append( DOM2Utils.escapeText( dependencyValue ) );
                    dependencyValue = StringUtils.replace( dependencyValue, "${basedir}", PlexusToSpringUtils.getBasedir() );
                    dependencies.put( name, dependencyValue );                    
                }
                else if ( child.getChildNodes().getLength() == 0 )
                {
                    dependencies.put( name, null );  
                }
                else
                {
                    StringWriter xml = new StringWriter();
                    xml.write( '<' + name + '>' );
                    flatten( child.getChildNodes(), new PrintWriter( xml ) );
                    xml.write( "</" + name + '>' );
                    value.append( xml.toString());
                    String dependencyValue = StringUtils.replace( xml.toString(), "${basedir}", PlexusToSpringUtils.getBasedir() );
                    dependencies.put( name, dependencyValue );
                }

            }
            
            if ( value.length() > 0 )
            {
                String fullConfigurationValue = StringUtils.replace( value.toString(), "${basedir}", PlexusToSpringUtils.getBasedir() );
                StringBuilder configurationContent = new StringBuilder();
                configurationContent.append( "<configuration>" );
                configurationContent.append( fullConfigurationValue ).append( "</configuration>");
                try
                {
                    Xpp3Dom plexusConfiguration = Xpp3DomBuilder.build( new StringReader( configurationContent.toString() ) );
                    builder.addPropertyValue( "configuration", new Xpp3DomPlexusConfiguration( plexusConfiguration ) );
                }
                catch ( XmlPullParserException e )
                {
                    log.error( "configuration Content: " + configurationContent );
                    log.error( e.getMessage(), e );
                    throw new RuntimeException( e.getMessage(), e );
                }
                catch ( IOException e )
                {
                    log.error( e.getMessage(), e );
                    throw new RuntimeException( e.getMessage(), e );
                }
            }
            builder.addPropertyValue( "requirements", dependencies );
        }

        protected String resolveId( Element element, AbstractBeanDefinition definition, ParserContext parserContext )
            throws BeanDefinitionStoreException
        {
            String role = element.getAttribute( "role" );
            String roleHint = element.getAttribute( "role-hint" );
            return PlexusToSpringUtils.buildSpringId( role, roleHint );
        }

        protected Class getBeanClass( Element element )
        {
            return PlexusComponentFactoryBean.class;
        }

    }
    /**
     * @param childNodes
     * @return
     */
    private void flatten( NodeList childNodes, PrintWriter out )
    {
        for ( int i = 0; i < childNodes.getLength(); i++ )
        {
            Node node = childNodes.item( i );
            if (node.getNodeType() == Node.TEXT_NODE )
            {
                out.print( DOM2Utils.escapeText( DOM2Utils.getTextContext( node ) ) );
            }
            else if (node.getNodeType() == Node.ELEMENT_NODE )
            {
                flatten( (Element) node, out );
            }
        }
    }
    /**
     * @param item
     * @param out
     */
    private void flatten( Element el, PrintWriter out )
    {
        out.print( '<' );
        out.print( el.getTagName() );
        NamedNodeMap attributes = el.getAttributes();
        for ( int i = 0; i < attributes.getLength(); i++ )
        {
            Node attribute = attributes.item( i );
            out.print( " ");
            out.print( attribute.getLocalName() );
            out.print( "=\"" );
            out.print( DOM2Utils.escapeAttributeValue( attribute.getNodeValue() ) );
            out.print( "\"" );
        }
        if (el.getChildNodes().getLength() == 0)
        {
            out.print( "/>" );
            return;
        }
        out.print( '>' );
        flatten( el.getChildNodes(), out );
        out.print( "</" );
        out.print( el.getTagName() );
        out.print( '>' );
    }
}
