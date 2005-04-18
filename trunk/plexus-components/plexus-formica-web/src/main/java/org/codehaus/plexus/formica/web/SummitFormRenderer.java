package org.codehaus.plexus.formica.web;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.FormManager;
import org.codehaus.plexus.formica.FormNotFoundException;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pull.tools.TemplateLink;
import org.codehaus.plexus.summit.renderer.AbstractRenderer;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.util.StringUtils;

import ognl.Ognl;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo the collection stuff needs to be refined, how to create the collection subset for massive collections
 *       you don't want the whole collection ...
 * @todo need to throw a rendering exception here and get rid of Exception and SummitException
 */
public class SummitFormRenderer
    extends AbstractRenderer
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static String MODE  = "mode";

    public static String MODE_ADD  = "add";

    public static String MODE_UPDATE = "update";

    public static String MODE_VIEW = "view";

    public static String MODE_DELETE = "delete";

    public static String MODE_SUMMARY = "summary";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private I18N i18n;

    private FormManager formManager;

    private FormRendererManager formRendererManager;

    public void render( RunData data, String view, Writer writer )
        throws SummitException, Exception
    {
        String target = data.getTarget();

        String id = target.substring( 0, target.indexOf( "." ) );

        String mode = (String) data.getMap().get( MODE );

        if ( mode == null )
        {
            mode = data.getParameters().getString( MODE );
        }

        Object formData = null;

        Form form = null;

        try
        {
            form = formManager.getForm( id );
        }
        catch ( FormNotFoundException e )
        {
            getLogger().error( "Could not find form with id '" + id + "'.", e );

            return;
        }

        if ( mode == null )
        {
            mode = MODE_ADD;
        }

        if ( mode.equals( MODE_SUMMARY ) )
        {
            Object o = lookup( assertNotEmpty( form, form.getSourceRole(), "source role" ) );

            String expr = form.getSummaryCollectionExpression();

            formData = getValue( expr, Collections.EMPTY_MAP, o );
        }
        else if ( mode.equals( MODE_UPDATE ) || mode.equals( MODE_VIEW ) || mode.equals( MODE_DELETE ) )
        {
            Object o = lookup( assertNotEmpty( form, form.getSourceRole(), "source role" ) );

            String i = data.getParameters().getString( "id" );

            if ( StringUtils.isEmpty( i ) )
            {
                throw new SummitException( "Missing parameter 'id'." );
            }

            Map map = new HashMap();

            map.put( "id", i );

            String expr = assertNotEmpty( form, form.getLookupExpression(), "lookup expression" );

            formData = getValue( expr, map, o );
        }

        // ----------------------------------------------------------------------

        TemplateLink tl = new TemplateLink();

        tl.setRunData( data );

        FormRenderer renderer = null;

        try
        {
            renderer = formRendererManager.lookup( mode );

            renderer.render( form, writer, i18n, formData, tl.toString(), data );
        }
        catch ( FormRendererNotFoundException e )
        {
            getLogger().fatalError( "Could not find form renderer, type: '" + mode + "'.", e );

            throw new SummitException( "Could not find form renderer, type: '" + mode + "'.", e );
        }
        catch ( FormRenderingException e )
        {
            getLogger().fatalError( "Could not find form renderer, type: '" + mode + "'.", e );

            throw new SummitException( "Could not find form renderer, type: '" + mode + "'.", e );
        }
    }

    public boolean viewExists( String view )
    {
        return true;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String assertNotEmpty( Form form, String value, String field )
        throws SummitException
    {
        if ( StringUtils.isEmpty( value ) )
        {
            throw new SummitException( "Missing " + field + " from form '" + form.getId() + "'." );
        }

        return value;
    }

    private Object getValue( String expr, Map map, Object o )
        throws SummitException
    {
        Object date;

        try
        {
            date = Ognl.getValue( expr, map, o );
        }
        catch ( Throwable e )
        {
            getLogger().error( "Error while evaluation OGNL expression '" + expr + "'.", e );

            throw new SummitException( "Error while evaluation OGNL expression '" + expr + "'.", e );
        }

        return date;
    }
}
