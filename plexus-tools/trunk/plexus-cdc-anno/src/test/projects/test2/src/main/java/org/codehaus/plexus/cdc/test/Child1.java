package org.codehaus.plexus.cdc.test;

import org.codehaus.plexus.cdc.annotation.Component;
import org.codehaus.plexus.cdc.annotation.Configuration;

@Component(role="org.codehaus.plexus.maven.Child1")
public class Child1
    extends Parent
{
    public static final String ROLE = "org.codehaus.plexus.maven.Child1";

    @Configuration("[default name]")
    private String name;

    @Configuration
    private String mood;
}
