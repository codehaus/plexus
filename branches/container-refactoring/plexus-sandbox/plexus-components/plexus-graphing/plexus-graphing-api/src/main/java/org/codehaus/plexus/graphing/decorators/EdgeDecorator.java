/**
 * 
 */
package org.codehaus.plexus.graphing.decorators;

import java.awt.Color;

/**
 * EdgeDecorator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EdgeDecorator
{
    // Line Styles
    public static final int NORMAL = 0;

    public static final int BOLD = 1;

    public static final int DASHED = 2;

    // Line Ending Types
    public static final int NONE = 0;

    public static final int ARROW = 1;

    public static final int DOT = 2;

    public static final int HOLLOW_DOT = 3;

    public static final int INVERT_ARROW = 4;

    public static final int INVERT_ARROW_DOT = 5;

    public static final int INVERT_ARROW_HOLLOW_DOT = 6;

    private Color lineColor;

    private int lineHead = ARROW;

    private int lineTail = NONE;

    private String lineLabel;

    private int style;

    public Color getLineColor()
    {
        return lineColor;
    }

    public void setLineColor( Color lineColor )
    {
        this.lineColor = lineColor;
    }

    public int getLineHead()
    {
        return lineHead;
    }

    public void setLineHead( int lineHead )
    {
        this.lineHead = lineHead;
    }

    public String getLineLabel()
    {
        return lineLabel;
    }

    public void setLineLabel( String lineLabel )
    {
        this.lineLabel = lineLabel;
    }

    public int getLineTail()
    {
        return lineTail;
    }

    public void setLineTail( int lineTail )
    {
        this.lineTail = lineTail;
    }

    public int getStyle()
    {
        return style;
    }

    public void setStyle( int style )
    {
        this.style = style;
    }

}
