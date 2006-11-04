package org.codehaus.bacon.component.basic;

import org.codehaus.bacon.component.ComponentReference;

public class BasicComponentReference
    extends BasicCompositionSource
    implements ComponentReference
{

    private String interfaceName;

    private String cardinality;

    public String getCardinality()
    {
        return cardinality;
    }

    public void setCardinality( String cardinality )
    {
        this.cardinality = cardinality;
    }

    public String getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName( String interfaceName )
    {
        this.interfaceName = interfaceName;
    }

}
