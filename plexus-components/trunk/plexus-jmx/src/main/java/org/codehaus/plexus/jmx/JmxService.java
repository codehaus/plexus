package org.codehaus.plexus.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface JmxService
{
    String ROLE = JmxService.class.getName();

    /**
     * Returns the default {@link MBeanServer}.
     *
     * @return
     */
    MBeanServer getMBeanServer();

    /**
     * Registers the object in the MBeanServer returned by {@link #getMBeanServer()} by using
     * {@link MBeanServer#registerMBean(Object, javax.management.ObjectName)}.
     *
     * It will also record all the MBeans in an internal list. By calling {@link #releaseAllMBeansQuiet()} all the
     * MBeans in the list will be released. This is a useful feature for management components as they can register
     * any number of beans runtime and release all registered beans with one call in their <code>stop()</code> method.
     *
     * <it>NOTE:</it> instances registered in the MBeanServer returned by {@link #getMBeanServer()} will not be
     * registered.
     *
     * @param object
     * @param objectName
     * @throws MBeanRegistrationException
     * @throws InstanceAlreadyExistsException
     * @throws NotCompliantMBeanException
     * @throws MalformedObjectNameException
     */
    ObjectName registerMBean( Object object, String objectName )
        throws MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException,
        MalformedObjectNameException;

    /**
     * Releases all MBeans registered through {@link #registerMBean(Object, String)}.
     *
     * Note that it will swallow all exceptions and only log them to the logger.
     */
    public void releaseAllMBeansQuiet();

    // TODO: A method to release a specific MBean
}
