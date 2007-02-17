package org.codehaus.plexus.spe.services.io;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.spe.model.ContextValue;
import org.codehaus.plexus.spe.model.LogMessage;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.util.IOUtil;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultProcessDescriptorIo
    extends AbstractLogEnabled
    implements ProcessDescriptorIo, Initializable
{
    private XStream xstream;

    // -----------------------------------------------------------------------
    // ProcessDescriptorIo Implementation
    // -----------------------------------------------------------------------

    public ProcessDescriptor loadDescriptor( URL url )
        throws ProcessException
    {
        InputStream inputStream = null;

        try
        {
            inputStream = url.openStream();

            ProcessDescriptor processDescriptor =
                (ProcessDescriptor) xstream.fromXML( inputStream, new ProcessDescriptor() );

/*
            xstream.marshal( processDescriptor, new PrettyPrintWriter( new OutputStreamWriter( System.out ) )
            {
                public void addAttribute( String key, String value )
                {
                    if ( !key.equals( "class" ) )
                    {
                        super.addAttribute( key, value );
                    }
                }
            } );
*/

            return processDescriptor;
        }
        catch ( IOException e )
        {
            throw new ProcessException( "Error while reading process descriptor from '" + url.toExternalForm() + "'.",
                                        e );
        }
        finally
        {
            IOUtil.close( inputStream );
        }
    }

    // -----------------------------------------------------------------------
    // Component Lifecycle
    // -----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        xstream = new XStream();

        xstream.alias( "context-value", ContextValue.class );
        xstream.alias( "log-message", LogMessage.class );
        xstream.alias( "process", ProcessDescriptor.class );
        xstream.alias( "step", StepDescriptor.class );

        xstream.registerConverter( new DocumentConverter() );
    }
}
