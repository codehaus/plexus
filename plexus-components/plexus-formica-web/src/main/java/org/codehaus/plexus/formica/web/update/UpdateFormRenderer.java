package org.codehaus.plexus.formica.web.update;

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
import org.codehaus.plexus.formica.web.element.TextElementRenderer;
import org.codehaus.plexus.formica.web.AbstractFormRenderer;
import org.codehaus.plexus.formica.web.FormRenderingException;
import org.codehaus.plexus.formica.web.SummitFormRenderer;
import org.codehaus.plexus.formica.web.add.AddFormRenderer;
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
public class UpdateFormRenderer
    extends AddFormRenderer
{
    public String getHeaderTitle( Form form, I18N i18n )
    {
        return i18n.getString( form.getUpdate().getTitleKey() );
    }
}
