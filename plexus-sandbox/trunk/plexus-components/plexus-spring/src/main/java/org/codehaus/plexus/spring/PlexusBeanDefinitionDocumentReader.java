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

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Document;

/**
 * A Spring {@link BeanDefinitionDocumentReader} that converts on the fly the
 * Plexus components descriptor to a spring XML context.
 *
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 * @version $Id$
 */
public class PlexusBeanDefinitionDocumentReader
    extends DefaultBeanDefinitionDocumentReader
{
    private static final String XSL = "PlexusBeanDefinitionDocumentReader.xsl";

    private Transformer transformer;

    public PlexusBeanDefinitionDocumentReader()
    {
        super();

        InputStream is = getClass().getResourceAsStream( XSL );
        if ( is == null )
        {
            throw new BeanDefinitionStoreException( "XSL not found in the classpath: " + XSL );
        }
        Source xsltSource = new StreamSource( is );

        TransformerFactory tf = TransformerFactory.newInstance();

        try
        {
            transformer = tf.newTransformer( xsltSource );
        }
        catch ( TransformerConfigurationException e )
        {
            String msg = "Failed to load Plexus to Spring XSL " + XSL;
            throw new BeanDefinitionStoreException( msg, e );
        }
    }

    public void registerBeanDefinitions( Document doc, XmlReaderContext readerContext )
    {
        doc = convertPlexusDescriptorToSpringBeans( doc, readerContext );
        if ( Boolean.getBoolean( "plexus-spring.debug" ) )
        {
            try
            {
                XMLWriter writer = new XMLWriter( System.out, OutputFormat.createPrettyPrint() );
                writer.write( new DOMReader().read( doc ) );
            }
            catch ( Exception e )
            {
                // ignored
            }
        }

        super.registerBeanDefinitions( doc, readerContext );
    }

    /**
     * @deprecated
     */
    protected Document convertPlexusDescriptorToSpringBeans( Document doc )
    {
        return convertPlexusDescriptorToSpringBeans( doc, null );
    }

    protected Document convertPlexusDescriptorToSpringBeans( Document doc, XmlReaderContext readerContext )
    {
        if ( "component-set".equals( doc.getDocumentElement().getNodeName() ) )
        {
            return translatePlexusDescriptor( doc, readerContext );
        }
        if ( "plexus".equals( doc.getDocumentElement().getNodeName() ) )
        {
            return translatePlexusDescriptor( doc, readerContext );
        }

        return doc;
    }

    private Document translatePlexusDescriptor( Document doc, XmlReaderContext readerContext )
    {
        Source xmlSource = new DOMSource( doc );
        DOMResult transResult = new DOMResult();

        try
        {
            transformer.transform( xmlSource, transResult );

            // DOM3 Only - logger.debug( doc.getDocumentURI() + " successfully translated to Spring" );
            if ( logger.isDebugEnabled() )
            {
                logger.debug( "Plexus Bean Definition Document successfully translated to Spring" );
                StringWriter stringWriter = new StringWriter();
                transformer.transform( xmlSource, new StreamResult( stringWriter ) );
                logger.debug( "result " + stringWriter.toString() );
            }
            return (Document) transResult.getNode();
        }
        catch ( TransformerException e )
        {
            String msg = "Failed to translate plexus component descriptor to Spring XML context";
            if ( readerContext != null )
            {
                msg += " : " + readerContext.getResource();
            }
            throw new BeanDefinitionStoreException( msg, e );
        }
    }
}
