package org.codehaus.plexus.summit.resolver.transformer;

public abstract class AbstractTransformer
    implements Transformer
{
    public abstract String transform( String view )
        throws Exception;
}
