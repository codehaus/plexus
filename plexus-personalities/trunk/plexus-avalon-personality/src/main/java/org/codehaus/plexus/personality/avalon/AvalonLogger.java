package org.codehaus.plexus.personality.avalon;

import org.apache.avalon.framework.logger.Logger;

/**
 * Avalon wrapper around the plexus logger.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 6, 2004
 */
public class AvalonLogger
    implements Logger
{
    org.codehaus.plexus.logging.Logger logger;
    
    public AvalonLogger(org.codehaus.plexus.logging.Logger logger)
    {
        this.logger = logger;
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#debug(java.lang.String)
     */
    public void debug(String message)
    {
        logger.debug(message);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    public void debug(String message, Throwable throwable)
    {
        logger.debug(message, throwable);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#info(java.lang.String)
     */
    public void info(String message)
    {
        logger.info(message);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#info(java.lang.String, java.lang.Throwable)
     */
    public void info(String message, Throwable throwable)
    {
        logger.info(message, throwable);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#warn(java.lang.String)
     */
    public void warn(String message)
    {
        logger.warn(message);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#warn(java.lang.String, java.lang.Throwable)
     */
    public void warn(String message, Throwable throwable)
    {
        logger.warn(message, throwable);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isWarnEnabled()
     */
    public boolean isWarnEnabled()
    {
        return logger.isWarnEnabled();
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#error(java.lang.String)
     */
    public void error(String message)
    {
        logger.error(message);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#error(java.lang.String, java.lang.Throwable)
     */
    public void error(String message, Throwable throwable)
    {
        logger.error(message, throwable);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isErrorEnabled()
     */
    public boolean isErrorEnabled()
    {
        return logger.isErrorEnabled();
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#fatalError(java.lang.String)
     */
    public void fatalError(String message)
    {
        logger.fatalError(message);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#fatalError(java.lang.String, java.lang.Throwable)
     */
    public void fatalError(String message, Throwable throwable)
    {
        logger.fatalError(message, throwable);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isFatalErrorEnabled()
     */
    public boolean isFatalErrorEnabled()
    {
        return logger.isFatalErrorEnabled();
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#getChildLogger(java.lang.String)
     */
    public Logger getChildLogger(String message)
    {
        return new AvalonLogger( logger.getChildLogger(message) );
    }

}
