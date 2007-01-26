package org.codehaus.plexus.cdc.test;

import org.codehaus.plexus.cdc.annotation.Requirement;

public class Parent
{
    @Requirement(roleHint="myHint")
    private Object object;
}
