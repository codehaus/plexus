package org.codehaus.plexus.werkflow;

import org.codehaus.werkflow.Engine;

/**
 * The Plexus Werkflow component.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WerkflowService
{
    String ROLE = WerkflowService.class.getName();
    
	public Engine getEngine();
}
