package org.apache.plexus.ogger;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.plexus.service.Serviceable;
import org.apache.plexus.service.ServiceBroker;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.thread.ThreadSafe;

import java.io.File;

public class DefaultOgger
    extends AbstractLogEnabled
    implements Ogger
    //, Serviceable, Configurable, Initializable, ThreadSafe
{
    private ServiceBroker serviceBroker;
    private Configuration configuration;

    public DefaultOgger()
    {
    }
}
