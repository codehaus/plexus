package org.codehaus.plexus.formica.web.add;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.action.AbstractEntityAction;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.formica.validation.ElementResult;
import org.codehaus.plexus.formica.web.element.TextElementRenderer;
import org.codehaus.plexus.formica.web.AbstractFormRenderer;
import org.codehaus.plexus.formica.web.FormRenderingException;
import org.codehaus.plexus.formica.web.SummitFormRenderer;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.summit.rundata.RunData;

import java.util.Iterator;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class AddFormRenderer
    extends AbstractFormRenderer
{
    public String getHeaderTitle( Form form, I18N i18n )
    {
        return i18n.getString( form.getAdd().getTitleKey() );
    }

    public void body( Form form, XMLWriter w, I18N i18n, Object data, String baseUrl, RunData rundata )
        throws FormRenderingException
    {
        String mode = rundata.getParameters().getString( "mode", "add" );

        // ----------------------------------------------------------------------

        w.startElement( "form" );

        w.addAttribute( "method", "post" );

        w.addAttribute( "action", baseUrl );

        // ----------------------------------------------------------------------

        // Action
        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "action" );

        w.addAttribute( "value", form.getAdd().getAction() );

        w.endElement();

        // View
        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "view" );

        w.addAttribute( "value", form.getAdd().getView() );

        w.endElement();

        // Mode
        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "mode" );

        w.addAttribute( "value", mode  );

        w.endElement();

        //

        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "formId" );

        w.addAttribute( "value", form.getId() );

        w.endElement();

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        if ( data != null )
        {

            try
            {
                String id = (String) Ognl.getValue( form.getKeyExpression(), data );

                w.startElement( "input" );

                w.addAttribute( "type", "hidden" );

                w.addAttribute( "name", "id" );

                w.addAttribute( "value", id );

                w.endElement();

            }
            catch ( OgnlException e )
            {
                throw new FormRenderingException( "Cannot retrieve id of target object: ", e );
            }
        }

        renderElements( form, w, i18n, data, rundata );
    }

    protected void renderElements( Form form, XMLWriter w, I18N i18n, Object data, RunData rundata )
        throws FormRenderingException
    {
        TextElementRenderer r = new TextElementRenderer();

        FormValidationResult fvr = (FormValidationResult) rundata.getMap().get( "fvr" );

        w.startElement( "div" );

        w.addAttribute( "class", "axial" );

        w.startElement( "table" );

        w.addAttribute( "cellpadding", "3" );

        w.addAttribute( "cellspacing", "2" );

        w.addAttribute( "border", "0" );

        for ( Iterator i = form.getElements().iterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();

            w.startElement( "tr" );

            // ----------------------------------------------------------------------

            w.startElement( "th" );

            w.writeText( i18n.getString( element.getLabelKey() ) );

            w.endElement();

            // ----------------------------------------------------------------------

            w.startElement( "td" );

            try
            {
                // This is for an update where we have a piece of data

                Object o = null;

                if ( data != null && element.getExpression() != null )
                {
                    o = Ognl.getValue( element.getExpression(), data );
                }
                else
                {
                    o = rundata.getParameters().getString( element.getId() );
                }

                r.render( element, o, w, i18n );
            }
            catch ( OgnlException e )
            {
                throw new FormRenderingException(
                    "Error extracting value in " + data + " with the expresion '" + element.getExpression() + "'" );
            }

            if ( fvr != null && !fvr.getElementResult( element.getId()) .valid() )
            {
                w.startElement( "td" );

                w.addAttribute( "class", "inputerror" );

                w.startElement( "p" );

                w.addAttribute( "class", "errormark" );

                w.writeText( fvr.getElementResult( element.getId()) .getErrorMessage() );

                w.endElement();

                w.endElement();
            }

            w.endElement();

            // ----------------------------------------------------------------------

            w.endElement();
        }

        w.endElement();  // table

        w.endElement();  // div

        // ----------------------------------------------------------------------

        w.startElement( "div" );

        w.addAttribute( "class", "functnbar2" );

        w.startElement( "input" );

        w.addAttribute( "type", "submit" );

        w.addAttribute( "value", "Submit" );

        w.endElement(); // input

        w.endElement(); // div

        w.endElement();
    }
}
