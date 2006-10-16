/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

public class Settings
{

    private boolean paramNeeded;
    private boolean middleTokenNeeded;
    private int minimumMiddleTokenNeeded;
    private int maximumMiddleTokenNeeded;

    /**
     * The default settings
     */
    Settings()
    {
    }

    /**
     * @param paramNeeded to tell if parameters are needed
     * @param middleTokenNeeded to tell if a middle token is needed
     * @param minimumMiddleTokenNeeded minimum of middle token needed
     * @param maximumMiddleTokenNeeded maximum of middle token needed
     */
    Settings( boolean paramNeeded, boolean middleTokenNeeded, int minimumMiddleTokenNeeded, int maximumMiddleTokenNeeded )
    {
        setParamNeeded( paramNeeded );
        setMiddleTokenNeeded( middleTokenNeeded );
        setMinimumMiddleTokenNeeded( minimumMiddleTokenNeeded );
        setMaximumMiddleTokenNeeded( maximumMiddleTokenNeeded );
    }

    /**
     * to get the maximum of middle token needed
     */
    int getMaximumMiddleTokenNeeded()
    {
        return maximumMiddleTokenNeeded;
    }

    /**
     * to get the minimum of middle token needed
     */
    int getMinimumMiddleTokenNeeded()
    {
        return minimumMiddleTokenNeeded;
    }

    /**
     * to know a middle token is needed
     */
    boolean isMiddleTokenNeeded()
    {
        return middleTokenNeeded;
    }

    /**
     * to know parameters are needed
     */
    boolean isParamNeeded()
    {
        return paramNeeded;
    }

    /**
     * to set the maximum of middle token needed
     */
    private void setMaximumMiddleTokenNeeded( int maximumMiddleTokenNeeded )
    {
        this.maximumMiddleTokenNeeded = maximumMiddleTokenNeeded;
    }

    /**
     * to tell if the middle token are needed
     */
    private void setMiddleTokenNeeded( boolean middleTokenNeeded )
    {
        this.middleTokenNeeded = middleTokenNeeded;
    }

    /**
     * to set the minimum of middle token needed
     */
    private void setMinimumMiddleTokenNeeded( int minimumMiddleTokenNeeded )
    {
        this.minimumMiddleTokenNeeded = minimumMiddleTokenNeeded;
    }

    /**
     * to tell if parameters are needed
     */
    private void setParamNeeded( boolean paramNeeded )
    {
        this.paramNeeded = paramNeeded;
    }
}
