package org.codehaus.plexus.summit.pull.tools;

/**
 * This class allows one to specify paths in the setPage method
 * using '/' slash as opposed to the ',' used in TemplateLink.
 * It is less efficient as the '/' are converted to ',' to avoid
 * problems parsing the pathinfo after conversion in a web server.
 * <p/>
 * It is recommended that projects standardize on using the ','
 * separator and use TemplateLink.  But this class is available for
 * those who do not mind the inefficiency.
 *
 * @author <a href="jmcnally@collab.net">John D. McNally</a>
 * @version $Id$
 */
public class TemplateLinkWithSlash
    extends TemplateLink
{
    /**
     * Default constructor
     * <p/>
     * The init method must be called before use.
     */
    public TemplateLinkWithSlash()
    {
    }

    /**
     * Sets the template variable used by the Template Service.
     * This method allows slashes '/' as the path separator.
     *
     * @param t A String with the template name.
     * @return A TemplateLink.
     */
    public TemplateLink setPage( String t )
    {
        super.setPage( t.replace( '/', ',' ) );
        return this;
    }
}


