package org.codehaus.plexus.spring;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.XmlValidationModeDetector;

public class PlexusXmlBeanDefinitionReader
    extends XmlBeanDefinitionReader
{
    private PlexusXmlValidationModeDetector validationModeDetector = new PlexusXmlValidationModeDetector();

    public PlexusXmlBeanDefinitionReader( BeanDefinitionRegistry registry )
    {
        super( registry );
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

        boolean isPlexusDefinition = false;
        try
        {
            isPlexusDefinition = validationModeDetector.isPlexusDefinition( inputStream );
        }
        catch ( IOException ex )
        {
            throw new BeanDefinitionStoreException( "Unable to determine validation mode for [" + resource +
                "]: an error occurred whilst reading from the InputStream.", ex );
        }

        if ( isPlexusDefinition )
        {
            return XmlValidationModeDetector.VALIDATION_NONE;
        }
        else
        {
            return super.detectValidationMode( resource );
        }
    }
}
