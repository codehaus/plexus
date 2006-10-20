package org.codehaus.plexus.jmx;

import javax.management.MBeanServer;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface JmxService {
    String ROLE = JmxService.class.getName();

    MBeanServer getMBeanServer();
}
