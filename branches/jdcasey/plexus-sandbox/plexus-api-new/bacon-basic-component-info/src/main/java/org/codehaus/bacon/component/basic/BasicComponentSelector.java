package org.codehaus.bacon.component.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentSelector;

public class BasicComponentSelector
    implements ComponentSelector
{
    
    private List instanceNames = new ArrayList();
    private String interfaceName;
    
    public String getInterfaceName()
    {
        return interfaceName;
    }
    
    public void setInterfaceName( String interfaceName )
    {
        this.interfaceName = interfaceName;
    }

    public void addInstanceName( String instanceName )
    {
        instanceNames.add( instanceName );
    }
    
    public void addEmptyInstance()
    {
        instanceNames.add( ComponentDescriptor.NO_INSTANCE_NAME );
    }
    
    public void setInstanceNames( List instanceNames )
    {
        this.instanceNames.addAll( instanceNames );
    }
    
    public List getSelectedInstancesInOrder()
    {
        List names = instanceNames;
        
        if ( names.isEmpty() )
        {
            names = Collections.singletonList( ComponentDescriptor.NO_INSTANCE_NAME );
        }
        
        return Collections.unmodifiableList( names );
    }

}
