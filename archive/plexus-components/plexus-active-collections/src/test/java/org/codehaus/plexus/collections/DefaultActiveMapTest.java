package org.codehaus.plexus.collections;


public class DefaultActiveMapTest
    extends ActiveMapTCK
{

    protected ActiveMap constructKnownBadActiveMap()
    {
        return new DefaultActiveMap( getContainer(), TestBadComponent.class );
    }

    protected String getTestMapRoleHint()
    {
        return "default-active-map-test";
    }

}
