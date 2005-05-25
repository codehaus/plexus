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
     * @plexus.role-hint foo
     */
    private Component foo;

    /**
     * @plexus.requirement
     * @plexus.role Component
     * @plexus.field-name foo
     */
    private Map foo;

    /**
     * @plexus.configuration
     * @plexus.default-value localhost
     */
    private String host;

    /**
     * @plexus.configuration
     * @plexus.default-value
     *  <ports>
     *    <port>8080</port>
     *    <port>8000</port>
     *  </ports>
     */
    private List ports = kewk;
}
