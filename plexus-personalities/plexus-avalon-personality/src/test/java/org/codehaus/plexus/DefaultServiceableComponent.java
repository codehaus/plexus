package org.codehaus.plexus;

import junit.framework.Assert;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;

public class DefaultServiceableComponent
    implements ServiceableComponent, Serviceable
{
    private ServiceManager serviceManager;

    public void service( ServiceManager serviceManager )
    {
        this.serviceManager = serviceManager;
    }

    public boolean simpleServiceLookup()
        throws Exception
    {
        DefaultServiceA serviceA = (DefaultServiceA) serviceManager.lookup( ServiceA.ROLE );

        Assert.assertEquals( true, serviceA.enableLogging );

        Assert.assertEquals( true, serviceA.contextualize );

        Assert.assertEquals( true, serviceA.service );

        Assert.assertEquals( true, serviceA.configure );

        Assert.assertEquals( true, serviceA.initialize );

        Assert.assertEquals( true, serviceA.start );

        serviceManager.release( serviceA );

        Assert.assertEquals( true, serviceA.stop );

        Assert.assertEquals( true, serviceA.dispose );

        return true;
    }

    public boolean roleBasedServiceLookup()
        throws Exception
    {
        ServiceSelector selector = (ServiceSelector) serviceManager.lookup( ServiceC.ROLE + "Selector");

        ServiceC serviceC1 = (ServiceC) selector.select( "first-instance" );

        Assert.assertNotNull( serviceC1 );

        ServiceC serviceC2 = (ServiceC) selector.select( "second-instance" );

        Assert.assertNotNull( serviceC2 );

        Assert.assertNotSame( serviceC1, serviceC2 );

        return true;
    }
}
