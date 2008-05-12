package org.codehaus.plexus.spring;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.XmlValidationModeDetector;

public class PlexusXmlBeanDefinitionReader
    extends XmlBeanDefinitionReader
{
    private boolean fastDetection = false;

    private PlexusXmlValidationModeDetector validationModeDetector = new PlexusXmlValidationModeDetector();

    private PlexusBeanDefinitionDocumentReader reader = new PlexusBeanDefinitionDocumentReader();

    public PlexusXmlBeanDefinitionReader( BeanDefinitionRegistry registry )
    {
        super( registry );
    }

    /**
     * Override just for performance optimization
     */
    protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader()
    {
        return reader;
    }

    protected int detectValidationMode( Resource resource )
    {
        if ( resource.isOpen() )
        {
            throw new BeanDefinitionStoreException( "Passed-in Resource [" + resource + "] contains an open stream: " +
                "cannot determine validation mode automatically. Either pass in a Resource " +
                "that is able to create fresh streams, or explicitly specify the validationMode " +
                "on your XmlBeanDefinitionReader instance." );
        }

        Boolean isPlexusDefinition = null;

        /* making a guess based on the filename instead of parsing the file contents could be a little bit faster */
        if ( fastDetection )
        {
            isPlexusDefinition = isPlexusDefinitionName( resource );
        }

        if ( isPlexusDefinition == null )
        {
            InputStream inputStream;
            try
            {
                inputStream = resource.getInputStream();
            }
            catch ( IOException ex )
            {
                throw new BeanDefinitionStoreException( "Unable to determine validation mode for [" + resource +
                    "]: cannot open InputStream. " +
                    "Did you attempt to load directly from a SAX InputSource without specifying the " +
                    "validationMode on your XmlBeanDefinitionReader instance?", ex );
            }

            try
            {
                isPlexusDefinition = Boolean.valueOf( validationModeDetector.isPlexusDefinition( inputStream ) );
            }
            catch ( IOException ex )
            {
                throw new BeanDefinitionStoreException( "Unable to determine validation mode for [" + resource +
                    "]: an error occurred whilst reading from the InputStream.", ex );
            }
        }

        if ( isPlexusDefinition.booleanValue() )
        {
            return XmlValidationModeDetector.VALIDATION_NONE;
        }
        else
        {
            return super.detectValidationMode( resource );
        }
    }

    private Boolean isPlexusDefinitionName( Resource resource )
    {
        if ( resource.getFilename().equals( "components.xml" ) || resource.getFilename().equals( "application.xml" ) )
        {
            return Boolean.TRUE;
        }
        if ( resource.getFilename().equals( "applicationContext.xml" ) ||
            resource.getFilename().equals( "spring-context.xml" ) )
        {
            return Boolean.FALSE;
        }
        /* it may be any, like a test context */
        return null;
    }
}
