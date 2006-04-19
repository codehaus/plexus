package org.codehaus.plexus.summit.pull.tools;

import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.rundata.RunData;

import java.util.Map;
import java.util.HashMap;

/**
 * Template context tool that will set various attributes of the HTML
 * page.  It is automatically placed in the Template context as
 * '$page'.  Here's an example of some uses:
 * <p/>
 * <p/>
 * $page.setBgColor("#ffffff");
 * $page.setBgColor("white");
 * $page.setBackground("/images/standardbg.jpeg");
 * $page.setTitle("This is the title!");
 * $page.setKeywords("turbine, cool, servlet framework");
 * $page.setStyleSheet("/style.css");
 * <p/>
 * This should become a general attribute storage class
 * for a page. We should have something general like:
 * <p/>
 * $page.setAttr("bgcolor", "#ffffff")
 * <p/>
 * Instead of set methods for HTML because we might want
 * to set attributes for WML output or anything else.
 *
 * @author <a href="mailto:sean@somacity.com">Sean Legassick</a>
 * @version $Id$
 */
public class TemplatePageAttributes
    implements RequestTool
{
    private RunData data;

    private String title;

    private Map attributes = new HashMap();

    public void setRunData( RunData data )
    {
        this.data = data;
    }

    public void refresh()
    {
        // empty
    }

    public TemplatePageAttributes setAttribute( String key, String value )
    {
        attributes.put( key, value );

        return this;
    }

    public Map getAttributes()
    {
        return attributes;
    }

    public TemplatePageAttributes setTitle( String title )
    {
        this.title = title;

        return this;
    }

    public String getTitle()
    {
        return title;
    }

    /**
     * A dummy toString method that returns an empty string.
     *
     * @return An empty String ("").
     */
    public String toString()
    {
        return "";
    }
}
