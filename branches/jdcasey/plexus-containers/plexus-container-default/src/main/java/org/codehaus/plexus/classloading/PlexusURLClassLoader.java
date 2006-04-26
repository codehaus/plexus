package org.codehaus.plexus.classloading;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class PlexusURLClassLoader
    extends URLClassLoader
{

    public PlexusURLClassLoader( URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory )
    {
        super( urls, parent, factory );
    }

    public PlexusURLClassLoader( URL[] urls, ClassLoader parent )
    {
        super( urls, parent );
    }

    public PlexusURLClassLoader( URL[] urls )
    {
        super( urls );
    }

    public PlexusURLClassLoader( ClassLoader parent )
    {
        super( new URL[0], parent );
    }

    public void addURL( URL url )
    {
        super.addURL( url );
    }

}
