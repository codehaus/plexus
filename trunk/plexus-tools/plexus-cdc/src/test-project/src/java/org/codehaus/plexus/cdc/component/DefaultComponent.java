package org.codehaus.plexus.cdc.component;

import java.util.Map;

/**
 * @plexus.component
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
    private Component foo;

    /**
     * @plexus.requirement
     *   role="Component"
     *   role-hint="foo"
     */
    private Map foo;

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
