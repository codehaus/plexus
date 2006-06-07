package org.codehaus.xfire.plexus.config;

/**
 * Loads up an XML document describing the services and then
 * builds the services using ServiceConfigurators.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public interface ConfigurationService
{
    String ROLE = ConfigurationService.class.getName();
}
