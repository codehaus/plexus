package org.codehaus.plexus.component.repository.synthesizer;

import org.codehaus.plexus.component.repository.ComponentConfigurationDescriptor;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.classworlds.ClassRealm;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ComponentConfigurationDescriptorSynthesizer
{
    String ROLE = ComponentConfigurationDescriptorSynthesizer.class.getName();

    ComponentConfigurationDescriptor synthesizeDescriptor( ClassRealm classRealm, ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException, ComponentImplementationNotFoundException;
}
