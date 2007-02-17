package org.codehaus.plexus.spe.store;

import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ProcessInstanceStore
{
    String ROLE = ProcessInstanceStore.class.getName();

    ProcessInstance createInstance( ProcessDescriptor ProcessDescriptor, Map<String, Serializable> context )
        throws ProcessException;

    Collection<? extends ProcessInstance> getActiveInstances()
        throws ProcessException;

    Collection<? extends ProcessInstance> getInstances()
        throws ProcessException;

    void saveInstance( ProcessInstance processState )
        throws ProcessException;

    ProcessInstance getInstance( String id, boolean includeContext )
        throws ProcessException;

    void deleteInstance( String id )
        throws ProcessException;
}
