package org.codehaus.plexus.logging;


/**
 * Utility class to allow construction of easy components that will perform logging.
 *
 * @author <a href="mailto:dev@avalon.codehaus.org">Avalon Development Team</a>
 * @version CVS $Revision$ $Date$
 */
public abstract class AbstractLogEnabled
    implements LogEnabled
{
    ///Base Logger manager
    private Logger logger;

    /**
     * Set the components logger.
     *
     * @param logger the logger
     */
    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    /**
     * Helper method to allow sub-classes to aquire logger.
     * This method exists rather than exposing a member variable
     * because it protects other users against future changes. It
     * also means they do not have to use our naming convention.
     *
     * <p>There is no performance penalty as this is a  method
     * and will be inlined by the JVM.</p>
     *
     * @return the Logger
     */
    protected Logger getLogger()
    {
        return logger;
    }

    /**
     * Helper method to setup other components with same logger.
     *
     * @param component the component to pass logger object to
     */
    protected void setupLogger( Object component )
    {
        setupLogger( component, logger );
    }

    /**
     * Helper method to setup other components with logger.
     * The logger has the subcategory of this components logger.
     *
     * @param component the component to pass logger object to
     * @param subCategory the subcategory to use (may be null)
     */
    protected void setupLogger( Object component, String subCategory )
    {
        if ( subCategory == null )
        {
            throw new IllegalStateException( "Logging category must be defined." );
        }

        Logger logger = this.logger.getChildLogger( subCategory );

        setupLogger( component, logger );
    }

    /**
     * Helper method to setup other components with logger.
     *
     * @param component the component to pass logger object to
     * @param logger the Logger
     */
    protected void setupLogger( Object component, Logger logger )
    {
        if ( component instanceof LogEnabled )
        {
            ( (LogEnabled) component ).enableLogging( logger );
        }
    }
}
