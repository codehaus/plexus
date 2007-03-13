package org.codehaus.plexus.cdc.test;

import org.codehaus.plexus.cdc.annotation.Component;

@Component(role="org.codehaus.plexus.maven.Child2")
public class Child2
    extends Parent
{
    public static final String ROLE = "org.codehaus.plexus.maven.Child2";
}
