package org.codehaus.plexus.summit.resolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A set of utilities that help with fulling resolving a
 * target view.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class ResolverUtils
{
    /**
     * Get the parsed module name for the specified template.
     *
     * @param target The target name.
     * @param defaultBaseName The module type key.
     * @return The parsed module name.
     * @exception Exception a generaic exception.
     */
    public static List getPossibleViews( String target, String defaultBaseName )
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

        // Remove a possible file extension.
        for ( int j = i + 1; j < view.length(); j++ )
        {
            if ( view.charAt( j ) == '.' )
            {
                view.delete( j, view.length() );
                break;
            }
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

            view.append( defaultBaseName );
        }

        // Not found, return the default module name.
        return views;
    }

    /**
     * Gets the possibleModules attribute of the ResolverUtils class
     */
    public static List getPossibleModules( List views )
        throws Exception
    {
        List modules = new ArrayList();

        for ( Iterator i = views.iterator(); i.hasNext(); )
        {
            String view = (String) i.next();
            modules.add( viewToClassName( view ) );
        }

        return modules;
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
     * @exception Exception Malformed template name.
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
            throw new Exception(
                "Syntax error in template name '" + target + '\'' );
        }
        return index;
    }

    /**
     * Convert a view pattern to a class pattern. Remove a leading slash if
     * present and convert all remaining slashes to dots.
     *
     * @param view View to convert to a class name
     * @return String The class name produced
     */
    public static String viewToClassName( String view )
    {
        StringBuffer sb = new StringBuffer( view );

        if ( sb.charAt( 0 ) == '/' )
        {
            sb.deleteCharAt( 0 );
        }

        for ( int j = 0; j < sb.length(); j++ )
        {
            if ( sb.charAt( j ) == '/' )
            {
                sb.setCharAt( j, '.' );
            }
        }

        return sb.toString();
    }

    /**
     * For a given target and default base name produce a set of valid module
     * suffixes. The module suffixes combined with a set of module packages can
     * be used to produce the possible list of fully qualified modules class
     * names.
     */
    public static List getPossibleModuleSuffixes( String target, String defaultBaseName )
        throws Exception
    {
        List views = ResolverUtils.getPossibleViews( target, defaultBaseName );

        // Now what we have is a set of views which look like paths so we need
        // to convert these into elements that look like classes in order to
        // create a list of possible modules.
        List modules = new ArrayList();
        for ( Iterator i = views.iterator(); i.hasNext(); )
        {
            modules.add( ResolverUtils.viewToClassName( (String) i.next() ) );
        }

        return modules;
    }

    /**
     * Description of the Method
     */
    public boolean targetHasExtension( String target )
    {
        return target.indexOf( "." ) > 0;
    }
}
