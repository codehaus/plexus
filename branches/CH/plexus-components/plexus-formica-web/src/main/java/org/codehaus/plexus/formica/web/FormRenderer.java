package org.codehaus.plexus.formica.web;

import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.i18n.I18N;

import java.io.Writer;

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

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface FormRenderer
{
    String ROLE = FormRenderer.class.getName();

    void render( Form form, Writer writer, I18N i18n, Object data, String baseUrl )
        throws FormRenderingException;
}
