package org.codehaus.plexus.collections;

public class DefaultActiveSetTest
    extends ActiveSetTCK
{

    protected ActiveSet constructKnownBadActiveSet()
    {
        return new DefaultActiveSet( getContainer(), TestBadComponent.class );
    }

    protected String getTestSetRoleHint()
    {
        return "default-active-set-test";
    }

}
