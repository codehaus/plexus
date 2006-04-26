package org.codehaus.plexus.classloading;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderUtils
{
    
    private ClassLoaderUtils(){}
    
    public static String getClassLoaderInfo( ClassLoader cloader )
    {
        StringBuffer info = new StringBuffer();
        
        info.append( cloader );
        
        if ( cloader instanceof URLClassLoader )
        {
            URL[] urls = ((URLClassLoader) cloader).getURLs();
            
            info.append( "\nURLs:" );
            
            for ( int i = 0; i < urls.length; i++ )
            {
                info.append( "\n\to ").append( urls[i] );
            }
            
            info.append( '\n' );
        }
        
        return info.toString();
    }

}
