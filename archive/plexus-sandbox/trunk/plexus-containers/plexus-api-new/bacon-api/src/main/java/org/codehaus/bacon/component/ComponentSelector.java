package org.codehaus.bacon.component;

import java.util.List;

public interface ComponentSelector
{
    
    String getInterfaceName();
    
    List getSelectedInstancesInOrder();

}
