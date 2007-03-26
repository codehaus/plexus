package org.codehaus.plexus.collections;

public class DefaultProtoCollectionTest
    extends ProtoCollectionTCK
{

    protected ProtoCollection getProtoCollection()
    {
        return new DefaultProtoCollection( getContainer(), TestComponent.class.getName() );
    }

}
