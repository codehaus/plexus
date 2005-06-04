package org.codehaus.plexus.summit.resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of utilities that help with fulling resolving a
 * target view.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ResolverUtils
{
    /**
     * Get the parsed module name for the specified template.
     *
     * @param target        The target name.
     * @param defaultTarget The target to view in the current directory if the
     *                      specified target doesn't exist.
     * @return The possible views.
     * @throws Exception a generaic exception.
     */
    public static List getPossibleViews( String target, String defaultTarget )
        throws Exception
    {
        List views = new ArrayList();

        StringBuffer view = new StringBuffer();

        int i = parseTargetPath( target, view );

        // Remove leading slash if present
        if ( view.charAt( 0 ) == '/' )
        {
            view.deleteCharAt( 0 );

            i--;
        }

        // Try first an exact match for a module having the same
        // name as the input template, traverse then upper level
        // views to find a default module named Default.
        int j = 9999;
        String module;
        while ( j-- > 0 )
        {
            module = view.toString();

            views.add( module );

            view.setLength( i + 1 );

            if ( i > 0 )
            {
                // We have still views to traverse.
                for ( i = view.length() - 2; i >= 0; i-- )
                {
                    if ( view.charAt( i ) == '/' )
                    {
                        break;
                    }
                }
            }
            else if ( j > 0 )
            {
                // Only the main level left.
                j = 1;
            }

            view.append( defaultTarget );
        }

        // Not found, return the default module name.
        return views;
    }

    /**
     * Parse the template name collected from URL parameters or template context
     * to a path name. Double slashes are changed into single ones and commas
     * used as path delemiters in URL parameters are changed into slashes. Empty
     * names or names without a file part are not accepted. NOTE: this
     * particular method is public so that it can be tested.
     *
     * @param target The template name.
     * @param buffer A buffer for the result.
     * @return The index of the separator between the path and the name.
     * @throws Exception Malformed template name.
     */
    public static int parseTargetPath( String target, StringBuffer buffer )
        throws Exception
    {
        char c;

        int j = 0;

        int index = -1;

        buffer.setLength( 0 );

        buffer.append( target );

        int len = buffer.length();

        while ( j < len )
        {
            c = buffer.charAt( j );
            if ( c == ',' )
            {
                c = '/';
                buffer.setCharAt( j, c );
            }
            if ( c == '/' )
            {
                index = j;
                if ( j < ( len - 1 ) )
                {
                    c = buffer.charAt( j + 1 );
                    if ( ( c == '/' ) ||
                        ( c == ',' ) )
                    {
                        buffer.deleteCharAt( j );
                        len--;
                        continue;
                    }
                }
            }
            j++;
        }
        if ( ( len == 0 ) ||
            ( index >= ( len - 1 ) ) )
        {
            throw new Exception( "Syntax error in template name '" + target + '\'' );
        }
        return index;
    }
}
