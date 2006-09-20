package org.codehaus.plexus.logging.log4j;

import org.codehaus.plexus.util.StringUtils;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Level
{
    /** */
    private String hierarchy;

    /** */
    private String level;
    
    private String appenders;

    /**
     * @return Returns the hierarchy.
     */
    public String getHierarchy()
    {
        return hierarchy;
    }

    /**
     * @return Returns the level.
     */
    public String getLevel()
    {
        return level;
    }
    
    /**
     * @return Returns the appender list to use.
     */
    public String getAppenders()
    {
        if ( StringUtils.isEmpty( appenders ) )
        {
            return "";
        }
        else
        {
            if ( appenders.startsWith( "," ) )
            {
                return appenders;
            }
            else
            {
                return ", " + appenders;
            }
        }
    }
}
