package org.codehaus.plexus.formica.web;

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

import com.thoughtworks.xstream.xml.XMLWriter;
import com.thoughtworks.xstream.xml.text.PrettyPrintXMLWriter;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.i18n.I18N;

import java.io.Writer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractFormRenderer
    implements FormRenderer
{
    // ----------------------------------------------------------------------
    // Render
    // ----------------------------------------------------------------------

    public void render( Form form, Writer writer, I18N i18n, Object data, String baseUrl )
        throws FormRenderingException
    {
        XMLWriter w = new PrettyPrintXMLWriter( writer );

        header( form, w, i18n );

        body( form, w, i18n, data, baseUrl );

        footer( form, w, i18n );
    }

    // ----------------------------------------------------------------------
    // Header
    // ----------------------------------------------------------------------

    protected void header( Form form, XMLWriter w, I18N i18n )
    {
        w.startElement( "div" );

        w.addAttribute( "class", "app" );

        // ----------------------------------------------------------------------

        w.startElement( "h3" );

        w.writeText( getHeaderTitle( form, i18n ) );

        w.endElement();

        w.startElement( "p" );
    }

    // ----------------------------------------------------------------------
    // Body
    // ----------------------------------------------------------------------

    protected abstract String getHeaderTitle( Form form, I18N i18n );

    protected abstract void body( Form form, XMLWriter writer, I18N i18n, Object data, String baseUrl )
        throws FormRenderingException;

    // ----------------------------------------------------------------------
    // Footer
    // ----------------------------------------------------------------------

    protected void footer( Form form, XMLWriter w, I18N i18n )
    {
        w.endElement(); // p

        w.endElement(); // div
    }
}
