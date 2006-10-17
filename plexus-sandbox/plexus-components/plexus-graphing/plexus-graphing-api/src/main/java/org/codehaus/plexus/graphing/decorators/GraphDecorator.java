package org.codehaus.plexus.graphing.decorators;

/*
 * Copyright 2001-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Color;

/**
 * GraphDecorator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class GraphDecorator
{
    // Orientation
    public static final int LEFT_TO_RIGHT = 1;

    public static final int TOP_TO_BOTTOM = 2;

    private Color backgroundColor;

    private Color titleColor;

    private int orientation;

    private String title;

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor( Color backgroundColor )
    {
        this.backgroundColor = backgroundColor;
    }

    public int getOrientation()
    {
        return orientation;
    }

    public void setOrientation( int orientation )
    {
        this.orientation = orientation;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public Color getTitleColor()
    {
        return titleColor;
    }

    public void setTitleColor( Color titleColor )
    {
        this.titleColor = titleColor;
    }

}
