package org.codehaus.plexus.classworlds.strategy;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.codehaus.plexus.classworlds.realm.ClassRealm;

public class OsgiBundleStrategy
    extends AbstractStrategy
{

    // java.* from parent
    // imported packages [Import-Package header with explicit constraints on the exporter]
    // requires bundle [Required-Bundle]
    // self [Bundle-Classpath header]
    // attached fragments
    //
    // We need to trya and be OSGi r4 compliant in the loading of all the bundles so that we can try to
    // load eclipse without requiring equinox. Or any other OSGi container for that matter.
    public OsgiBundleStrategy( ClassRealm realm )
    {
        super( realm );
    }

    public Class loadClass( String name )
        throws ClassNotFoundException
    {
        Class clazz = realm.loadClassFromImport( name );

        if ( clazz == null )
        {
            clazz = realm.loadClassFromSelf( name );

            if ( clazz == null )
            {
                clazz = realm.loadClassFromParent( name );

                if ( clazz == null )
                {
                    throw new ClassNotFoundException( name );
                }
            }
        }

        return clazz;
    }

    public URL getResource( String name )
    {
        URL resource = realm.loadResourceFromImport( name );

        if ( resource == null )
        {
            resource = realm.loadResourceFromSelf( name );

            if ( resource == null )
            {
                resource = realm.loadResourceFromParent( name );
            }
        }

        return resource;
    }

    public Enumeration getResources( String name )
        throws IOException
    {
        Enumeration imports = realm.loadResourcesFromImport( name );
        Enumeration self = realm.loadResourcesFromSelf( name );
        Enumeration parent = realm.loadResourcesFromParent( name );

        return combineResources( imports, self, parent );
    }

}
