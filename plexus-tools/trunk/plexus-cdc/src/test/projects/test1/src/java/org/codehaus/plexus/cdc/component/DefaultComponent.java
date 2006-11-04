package org.codehaus.plexus.cdc.component;

import java.util.Map;

/**
 * My super component.
 *
 * @plexus.component
 *  alias="foo"
 *  role-hint="bar"
 *  version="1.2"
 */
public class DefaultComponent
  implements Component
{
    /**
     * @plexus.requirement
     */
    private ComponentA componentA;

    /**
     * @plexus.requirement
     *   role-hint="foo"
     */
    private Component fooComponent;

    /**
     * @plexus.requirement
     *   role="Component"
     */
    private Map fooMap;

    /**
     * @plexus.requirement
     *   role="Component"
     */
    private Map bar;

    /**
     * @plexus.configuration
     *   default-value="localhost"
     *   foo=bar
     */
    private String host;

    /**
     * @plexus.configuration
     *   default-value="8000, 8080"
     */
    private List ports;
}
