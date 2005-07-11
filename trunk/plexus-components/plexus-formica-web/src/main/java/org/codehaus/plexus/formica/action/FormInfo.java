/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.formica.action;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class FormInfo
{
    private String view;

    private String fid;

    public String getView()
    {
        return view;
    }

    public void setView( String view )
    {
        this.view = view;
    }

    public String getFid()
    {
        return fid;
    }

    public void setFid( String fid )
    {
        this.fid = fid;
    }
}
