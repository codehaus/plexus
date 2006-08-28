package org.codehaus.bacon.component;

public final class ComponentUtilities
{
    
    private ComponentUtilities()
    {
    }
    
    public static String createDescriptorKey( ComponentDescriptor descriptor )
    {
        return createDescriptorKey( descriptor.getInterfaceName(), descriptor.getInstanceName() );
    }
    
    public static String createDescriptorKey( String interfaceName )
    {
        return createDescriptorKey( interfaceName, null );
    }
    
    public static String createDescriptorKey( String interfaceName, String instanceName )
    {
        String instance = instanceName;
        
        if ( instance != null && instance.trim().length() < 1 )
        {
            instance = null;
        }
        else if ( ComponentDescriptor.NO_INSTANCE_NAME.equals( instanceName ) )
        {
            instance = null;
        }
        
        return interfaceName + ( instance != null ? ":" + instance : "" );
    }

}
