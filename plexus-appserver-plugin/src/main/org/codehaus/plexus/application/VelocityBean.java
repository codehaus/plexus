package org.codehaus.plexus.application;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
 */

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

/**
 * A tag that uses Velocity to render a specified template with the
 * JellyContext storing the results in either a variable in the
 * JellyContext or in a specified file.
 *
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id$
 */
public class VelocityBean
{
    private static final String ENCODING = "ISO-8859-1";

    private String name;
    private String basedir;
    private String template;
    private String inputEncoding;
    private String outputEncoding;
    private VelocityEngine velocityEngine;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    /**
     * Sets the file name for the merged output.
     *
     * @param name The name of the output file that is used to store the
     * results of the merge.
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Sets the base directory used for loading of templates by the
     * Velocity file resource loader.
     *
     * @param basedir The directory where templates can be located by
     * the Velocity file resource loader.
     */
    public void setBasedir( String basedir )
    {
        this.basedir = basedir;
    }

    /**
     * Sets the filename of the template used to merge with the
     * JellyContext.
     *
     * @param template The filename of the template to be merged.
     */
    public void setTemplate( String template )
    {
        this.template = template;
    }

    /**
     * Sets the output encoding mode which defaults to ISO-8859-1 used
     * when storing the results of a merge in a file.
     *
     * @param encoding  The file encoding to use when writing the
     * output.
     */
    public void setOutputEncoding( String encoding )
    {
        this.outputEncoding = encoding;
    }

    /**
     * Sets the input encoding used in the specified template which
     * defaults to ISO-8859-1.
     *
     * @param encoding  The encoding used in the template.
     */
    public void setInputEncoding( String encoding )
    {
        this.inputEncoding = encoding;
    }

    // -- Implementation ----------------------------------------------------

    /**
     * Merges the Velocity template with the Jelly context.
     *
     * @throws Exception If an exception occurs during the merge.
     */
    public void mergeTemplate( Context context )
        throws Exception
    {
        Writer writer = new OutputStreamWriter( new FileOutputStream( name ),
                                                outputEncoding == null ? ENCODING : outputEncoding );

        getVelocityEngine().mergeTemplate( template,
                                           inputEncoding == null ? ENCODING : inputEncoding,
                                           context,
                                           writer );
        writer.close();
    }

    /**
     * Gets or creates a VelocityEngine if one doesn't already exist for
     * the specified base directory.
     *
     * @return A VelocityEngine with a file resource loader configured
     * for the specified base directory.
     */
    private VelocityEngine getVelocityEngine()
        throws Exception
    {
        if ( velocityEngine == null )
        {
            velocityEngine = new VelocityEngine();
            
            velocityEngine.setProperty( VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this );

            velocityEngine.setProperty( "resource.loader", "class" );

            velocityEngine.setProperty( "class.resource.loader.class",
                                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );

            velocityEngine.init();
        }

        return velocityEngine;
    }

}

