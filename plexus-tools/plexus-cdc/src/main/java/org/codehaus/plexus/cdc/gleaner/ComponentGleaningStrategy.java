package org.codehaus.plexus.cdc.gleaner;

import com.thoughtworks.qdox.model.JavaClass;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentGleaningStrategy
{
    ComponentDescriptor gleanComponent( JavaClass javaClass );
}
