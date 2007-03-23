package org.codehaus.plexus.collections;

public class DefaultActiveListTest
    extends ActiveListTCK
{

    protected ActiveList constructKnownBadActiveList()
    {
        return new DefaultActiveList( getContainer(), TestBadComponent.class );
    }

    protected String getTestListRoleHint()
    {
        return "default-active-list-test";
    }

}
