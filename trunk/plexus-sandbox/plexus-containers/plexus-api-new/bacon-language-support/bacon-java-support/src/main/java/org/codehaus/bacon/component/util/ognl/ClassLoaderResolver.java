package org.codehaus.bacon.component.util.ognl;

import java.util.Map;

import ognl.ClassResolver;

public class ClassLoaderResolver
    implements ClassResolver
{
    
    private final ClassLoader loader;

    public ClassLoaderResolver( ClassLoader loader )
    {
        this.loader = loader;
    }

    public Class classForName( String className, Map context )
        throws ClassNotFoundException
    {
        return loader.loadClass( className );
    }

}
