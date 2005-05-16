package org.codehaus.plexus.summit.pull.tools;

import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.rundata.RunData;

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
    /**
     * The RunData object.
     */
    private RunData data = null;

    /**
     * The title.
     */
    private String title = null;
    private String bgColor = null;

    /**
     * Default constructor. The init method must be called before use
     */
    public TemplatePageAttributes()
    {
    }

    /**
     * Initialise this instance with the given RunData object.
     * (RequestTool method)
     *
     * @param data Assumed to be a RunData instance
     */
    public void setRunData( RunData data )
    {
        // we blithely cast to RunData as the runtime error thrown
        // if data is null or not RunData is appropriate.
        this.data = data;

        // clear cached title
        title = null;
        bgColor = null;
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Set the title in the page.  This returns an empty String so
     * that the template doesn't complain about getting a null return
     * value.
     *
     * @param title A String with the title.
     */
    public TemplatePageAttributes setTitle( String title )
    {
        this.title = title;
        return this;
    }

    /**
     * Get the title in the page.  This returns an empty String if
     * empty so that the template doesn't complain about getting a null
     * return value.
     *
     * @return A String with the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set the background color for the BODY tag.  You can use either
     * color names or color values (e.g. "white" or "#ffffff" or
     * "ffffff").
     *
     * @param bgColor the background color.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setBgColor( String bgColor )
    {
        this.bgColor = bgColor;
        return this;
    }

    public String getBgColor()
    {
        return bgColor;
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
