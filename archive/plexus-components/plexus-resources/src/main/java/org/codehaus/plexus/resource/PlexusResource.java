package org.codehaus.plexus.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * A resource is a byte stream, possibly (but not necessarily) with
 * additional attributes like {@link File}, {@link URL}, or {@link URI}.
 * @since 1.0-alpha-5
 */
public interface PlexusResource {
    /**
     * <p>Returns the resource as an {@link InputStream}. In general, you
     * should not assume, that this method may me called more than once.
     * In typical cases (for example, if the Resource is backed by a
     * file or loaded through the classpath), one may create an
     * {@link InputStream} as often as is necessary. However, you should
     * think of cases like an URL pointing to a servlet, where the resource
     * contents change with every call.</p>
     * <p>If you need a reliable way of reloading the resource more than
     * once, then you should use
     * {@link ResourceManager#getResourceAsFile(Resource)}.</p>
     * @return An {@link InputStream} with the resources contents,
     *   never null.
     */
    public InputStream getInputStream() throws IOException;

    /**
     * <p>Returns the resource as a file, if possible. A resource
     * doesn't need to be available as a file: If you require a file,
     * use {@link ResourceManager#getResourceAsFile(Resource)}.</p>
     * @return A {@link File} containing the resources contents,
     *   if available, or null.
     */
    public File getFile() throws IOException;

    /**
     * <p>Returns the resources URL, if possible. A resource
     * doesn't need to have an URL.</p>
     * @return The resources URL, if available, or null.
     */
    public URL getURL() throws IOException;

    /**
     * <p>Returns the resources URI, if possible. A resource
     * doesn't need to have an URI.</p>
     * @return The resources URI, if available, or null.
     */
    public URI getURI() throws IOException;

    /**
     * Returns the resources name, if possible. A resources
     * name is a relatively unspecified thing. For example,
     * if the resource has an {@link URL}, the name might be created
     * by invoking {@link URL#toExternalForm()}. In the case
     * of a {@link File}, it might be {@link File#getPath()}.
     */
    public String getName();
}
