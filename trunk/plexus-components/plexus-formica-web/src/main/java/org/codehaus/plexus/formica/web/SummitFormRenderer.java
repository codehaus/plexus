package org.codehaus.plexus.formica.web;

import ognl.Ognl;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.FormManager;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pull.tools.TemplateLink;
import org.codehaus.plexus.summit.renderer.AbstractRenderer;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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

        Form form = formManager.getForm( id );

        if ( mode == null )
        {
            mode = MODE_ADD;
        }

        if ( mode.equals( MODE_SUMMARY ) )
        {
            Object o = lookup( form.getSourceRole() );

            formData = Ognl.getValue( form.getSummaryCollectionExpression(), o );
        }
        else if ( mode.equals( MODE_UPDATE ) || mode.equals( MODE_VIEW ) || mode.equals( MODE_DELETE ) )
        {
            //TODO: thrown an exception if there is no source role
            //TODO: thrown an exception if there is no lookup expression

            Object o = lookup( form.getSourceRole() );

            String i = data.getParameters().getString( "id" );

            Map map = new HashMap();

            map.put( "id", i );

            formData = Ognl.getValue( form.getLookupExpression(), map, o );
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
    }

    public boolean viewExists( String view )
    {
        return true;
    }
}
