package org.codehaus.plexus.collections;

public class TestBadComponent
{
    
    public TestBadComponent( TestBadComponent component )
    {
        throw new IllegalStateException( "Make sure this will not construct!" );
    }

    private TestBadComponent()
    {
    }

    public static TestBadComponent newTestInstance()
    {
        return new TestBadComponent();
    }
}
