package org.codehaus.plexus.cdc.component;

import java.util.List;
import java.util.Map;

import org.codehaus.plexus.cdc.annotation.Configuration;
import org.codehaus.plexus.cdc.annotation.Requirement;
import org.codehaus.plexus.cdc.annotation.Component;

/**
 * My super component.
 */
@Component(role="org.codehaus.plexus.cdc.component.Component", alias="foo", roleHint="bar", version="1.2")
public class DefaultComponent
  implements Component
{
    @Requirement
    private ComponentA componentA;

    @Requirement(roleHint="foo")
    private Component fooComponent;

    @Requirement(role="org.codehaus.plexus.cdc.component.Component")
    private Map fooMap;

    @Requirement(role="org.codehaus.plexus.cdc.component.Component")
    private Map bar;

    @Configuration("localhost")
    private String host;

    @Configuration({"8000", "8080"})
    private List ports;
}
