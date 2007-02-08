package org.codehaus.plexus.server.ftp;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.server.ftp.ip.IpRestrictor;
import org.codehaus.plexus.server.ftp.usermanager.UserManager;
import org.codehaus.plexus.server.ftp.util.AsyncMessageQueue;

import java.io.File;

/** @author Jason van Zyl */
public class FtpConfig
{
    private Logger logger;
    private FtpStatistics statistics;
    private ConnectionService connectionService;
    private String systemName;
    private boolean isAnonymousLoginAllowed;
    private FtpStatus status;
    private IpRestrictor ipRestrictor;
    private AsyncMessageQueue messageQueue;
    private int defaultIdleTime;
    private int schedulerInterval;
    private UserManager userManager;
    private File defaultRoot;
    private int maxConnections;
    private int maxAnonymousLogins;
    private boolean createHome;
    private int dataPort;
    private DefaultFtpServer ftpServer;

    public FtpConfig( Logger logger,
                      FtpStatistics statistics,
                      ConnectionService connectionService,
                      String systemName,
                      boolean anonymousLoginAllowed,
                      FtpStatus status,
                      IpRestrictor ipRestrictor,
                      AsyncMessageQueue messageQueue,
                      int defaultIdleTime,
                      int schedulerInterval,
                      UserManager userManager,
                      File defaultRoot,
                      int maxConnections,
                      int maxAnonymousLogins,
                      boolean createHome,
                      int dataPort,
                      DefaultFtpServer ftpServer )
    {
        this.logger = logger;
        this.statistics = statistics;
        this.connectionService = connectionService;
        this.systemName = systemName;
        isAnonymousLoginAllowed = anonymousLoginAllowed;
        this.status = status;
        this.ipRestrictor = ipRestrictor;
        this.messageQueue = messageQueue;
        this.defaultIdleTime = defaultIdleTime;
        this.schedulerInterval = schedulerInterval;
        this.userManager = userManager;
        this.defaultRoot = defaultRoot;
        this.maxConnections = maxConnections;
        this.maxAnonymousLogins = maxAnonymousLogins;
        this.createHome = createHome;
        this.dataPort = dataPort;
        this.ftpServer = ftpServer;
    }

    public void releaseDataPort( int port )
    {
        ftpServer.releaseDataPort( port );
    }

    public int getDataPort()
    {
        return dataPort;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public FtpStatistics getStatistics()
    {
        return statistics;
    }

    public ConnectionService getConnectionService()
    {
        return connectionService;
    }

    public String getSystemName()
    {
        return systemName;
    }

    public boolean isAnonymousLoginAllowed()
    {
        return isAnonymousLoginAllowed;
    }

    public FtpStatus getStatus()
    {
        return status;
    }

    public IpRestrictor getIpRestrictor()
    {
        return ipRestrictor;
    }

    public AsyncMessageQueue getMessageQueue()
    {
        return messageQueue;
    }

    public int getDefaultIdleTime()
    {
        return defaultIdleTime;
    }

    public int getSchedulerInterval()
    {
        return schedulerInterval;
    }

    public UserManager getUserManager()
    {
        return userManager;
    }

    public File getDefaultRoot()
    {
        return defaultRoot;
    }

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public int getMaxAnonymousLogins()
    {
        return maxAnonymousLogins;
    }

    public boolean isCreateHome()
    {
        return createHome;
    }

    public DefaultFtpServer getFtpServer()
    {
        return ftpServer;
    }

    public void setConnectionService( ConnectionService connectionService )
    {
        this.connectionService = connectionService;
    }

    public void setStatistics( FtpStatistics statistics )
    {
        this.statistics = statistics;
    }
}
