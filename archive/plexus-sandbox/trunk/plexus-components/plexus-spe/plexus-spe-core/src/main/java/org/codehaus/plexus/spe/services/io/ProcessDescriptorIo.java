package org.codehaus.plexus.spe.services.io;

import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.ProcessException;

import java.net.URL;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ProcessDescriptorIo
{
    String ROLE = ProcessDescriptorIo.class.getName();

    ProcessDescriptor loadDescriptor( URL url )
        throws ProcessException;
}
