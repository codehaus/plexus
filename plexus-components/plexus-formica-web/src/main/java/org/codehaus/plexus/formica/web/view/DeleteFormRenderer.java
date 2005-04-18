/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.formica.web.view;

import ognl.Ognl;
import ognl.OgnlException;
import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.web.AbstractFormRenderer;
import org.codehaus.plexus.formica.web.FormRenderingException;
import org.codehaus.plexus.formica.web.SummitFormRenderer;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.summit.rundata.RunData;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DeleteFormRenderer
    extends AbstractFormRenderer
{
    public String getHeaderTitle( Form form, I18N i18n )
    {
        return i18n.getString( form.getDelete().getTitleKey() );
    }

    protected void header( Form form, XMLWriter w, I18N i18n )
    {
        w.startElement( "div" );

        w.addAttribute( "class", "app" );

        w.startElement( "h3" );

        w.writeText( getHeaderTitle( form, i18n ) );

        w.endElement();

        w.startElement( "p" );
    }

    public void body( Form form, XMLWriter w, I18N i18n, Object data, String baseUrl, RunData rundata )
        throws FormRenderingException
    {
        String id = rundata.getParameters().getString( "id" );

        String mode = rundata.getParameters().getString( "mode" );

        w.startElement( "div" );

        w.addAttribute( "class", "warningmessage" );

        w.startElement( "p" );

        w.startElement( "strong" );

        String deleteConfirmationMessage = i18n.getString( "delete.confirmation.message" );

        w.writeText( deleteConfirmationMessage );

        w.endElement();

        w.endElement();

        w.endElement();

        // ----------------------------------------------------------------------

        w.startElement( "form" );

        w.addAttribute( "method", "post" );

        w.addAttribute( "action", baseUrl + "/target/" + form.getDelete().getView() );

        // ----------------------------------------------------------------------

        // Action
        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "action" );

        w.addAttribute( "value", form.getDelete().getAction() );

        w.endElement();

        // View
        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "view" );

        w.addAttribute( "value", form.getDelete().getView() );

        w.endElement();

        // Id
        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "id" );

        w.addAttribute( "value", id );

        w.endElement();

        // ----------------------------------------------------------------------

        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "formId" );

        w.addAttribute( "value", form.getId() );

        w.endElement();

        // ----------------------------------------------------------------------

        w.startElement( "div" );

        w.addAttribute( "class", "functnbar2" );

        // ----------------------------------------------------------------------

        w.startElement( "input" );

        w.addAttribute( "type", "submit" );

        w.addAttribute( "name", "deleteEntity" );

        w.addAttribute( "value", "Delete" );

        w.endElement();

        // ----------------------------------------------------------------------

        w.startElement( "input" );

        w.addAttribute( "type", "submit" );

        w.addAttribute( "name", "cancel" );

        w.addAttribute( "value", "Cancel" );

        w.endElement();

        // ----------------------------------------------------------------------

        w.endElement(); // div

        w.endElement();
    }
}
