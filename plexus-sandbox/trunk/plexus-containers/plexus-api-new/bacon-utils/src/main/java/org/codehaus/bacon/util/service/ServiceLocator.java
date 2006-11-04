package org.codehaus.bacon.util.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.bacon.util.io.IOUtil;

public final class ServiceLocator
{
    
    private static final String BACON_SERVICE_BASE = "META-INF/bacon/";
    
    private ServiceLocator(){}

    public static Object find( Class serviceType, String serviceInstance, ClassLoader targetLoader )
        throws ServiceLookupException
    {
        String servicePointer = BACON_SERVICE_BASE + serviceType.getName() + "/" + serviceInstance;
        
        BufferedReader reader = null;
        
        try
        {
            InputStream resourceIn = targetLoader.getResourceAsStream( servicePointer );
            
            if ( resourceIn != null )
            {
                reader = new BufferedReader( new InputStreamReader( resourceIn ) );
                
                String implName;
                
                try
                {
                    implName = reader.readLine();
                }
                catch ( IOException e )
                {
                    throw new ServiceLookupException( "Cannot load service [class: " + serviceType + "; instance: " + serviceInstance + "]. Cannot read pointer resource: " + servicePointer, e );
                }
                
                try
                {
                    return targetLoader.loadClass( implName ).newInstance();
                }
                catch ( ClassNotFoundException e )
                {
                    throw new ServiceLookupException( "Cannot load service [class: " + serviceType + "; instance: " + serviceInstance + "]. Class: " + implName + " not found.", e );
                }
                catch ( InstantiationException e )
                {
                    throw new ServiceLookupException( "Cannot load service [class: " + serviceType + "; instance: " + serviceInstance + "]. Cannot instantiate class: " + implName, e );
                }
                catch ( IllegalAccessException e )
                {
                    throw new ServiceLookupException( "Cannot load service [class: " + serviceType + "; instance: " + serviceInstance + "]. Cannot instantiate class: " + implName, e );
                }
            }
            else
            {
                throw new ServiceLookupException( "Cannot find service [class: " + serviceType + "; instance: " + serviceInstance + "]" );
            }
        }
        finally
        {
            IOUtil.close( reader );
        }
    }
}
