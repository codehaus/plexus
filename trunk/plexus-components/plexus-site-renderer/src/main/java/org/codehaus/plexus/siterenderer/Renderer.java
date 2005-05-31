package org.codehaus.plexus.siterenderer;

import org.codehaus.plexus.siterenderer.sink.SiteRendererSink;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
 * @author <a href="mailto:evenisse@codehaus.org>Emmanuel Venisse</a>
 * @version $Id$
 */
public interface Renderer
{
    String ROLE = Renderer.class.getName();

    void render( File siteDirectory, File outputDirectory, InputStream siteDescriptor, String templateName,
                Map templateProperties )
        throws RendererException, IOException;

    void render( File siteDirectory, File outputDirectory, String siteDescriptor, String templateName,
                Map templateProperties )
        throws RendererException, IOException;

    void generateDocument( Writer writer, String templateName, Map templateProperties, SiteRendererSink sink )
        throws RendererException;

    SiteRendererSink createSink( File moduleBaseDir, String document, InputStream siteDescriptor )
        throws RendererException;

    SiteRendererSink createSink( File moduleBaseDir, String document, String siteDescriptor )
        throws RendererException;
    
    void setTemplateClassLoader( ClassLoader templateClassLoader );
}
