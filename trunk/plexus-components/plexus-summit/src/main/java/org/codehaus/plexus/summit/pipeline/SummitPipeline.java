package org.codehaus.plexus.summit.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.Valve;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Flexible implementation of a {@link org.codehaus.plexus.summit.pipeline.Pipeline}.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 */
public class SummitPipeline
    extends AbstractPipeline
{
}
