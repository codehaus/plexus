/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.channel;

public class Rights
{

    private boolean operator;
    private boolean ableToSpeakIfModerated;

    /**
     * to give the default rights
     */
    public Rights()
    {
    }

    /**
     * @param operator to give or not the operator's rights
     */
    public Rights( boolean operator )
    {
        this.operator = operator;
    }

    /**
     * to know if the rights permit to speak on a moderated channel
     */
    public boolean isAbleToSpeakIfModerated()
    {
        return ableToSpeakIfModerated;
    }

    /**
     * to know if it's an operator's rights
     */
    public boolean isOperator()
    {
        return operator;
    }

    /**
     * to permit or not to speak on a moderated channel
     */
    public void setAbleToSpeakIfModerated( boolean ableToSpeakIfModerated )
    {
        this.ableToSpeakIfModerated = ableToSpeakIfModerated;
    }

    /**
     * to set the operator's rights
     */
    public void setOperator( boolean operator )
    {
        this.operator = operator;
    }
}
