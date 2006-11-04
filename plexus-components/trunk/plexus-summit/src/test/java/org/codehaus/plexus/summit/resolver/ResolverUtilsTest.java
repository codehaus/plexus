package org.codehaus.plexus.summit.resolver;

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
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
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
 */

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.codehaus.plexus.summit.AbstractTest;

/**
 * <p>
 * Test suite for the {@link org.codehaus.plexus.summit.resolver.ResolverUtils}
 * class.
 * </p>
 *
 * <p>
 * The ResolverUtils class consists of a set of methods that assist
 * in locating Modules and sibling Views based on the name of the
 * view path.
 * </p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ResolverUtilsTest
    extends AbstractTest
{
    /**
     * Test that the view path is parsed correctly. The view path
     * can be specified from an URL or from within another view.
     */
    public void testParseTargetPath()
        throws Exception
    {
        // Make sure that multiple slashes are collapsed
        // into single slashes.
        String targetPath = "///science///physics///Index.vm";

        StringBuffer sb = new StringBuffer();

        ResolverUtils.parseTargetPath(targetPath,sb);

        assertEquals("/science/physics/Index.vm", sb.toString());
        
        // Make sure that commas are converted to slashes.
        targetPath = "///science,chemistry,Index.vm";

        sb = new StringBuffer();

        ResolverUtils.parseTargetPath(targetPath,sb);

        assertEquals("/science/chemistry/Index.vm", sb.toString());
    
        // Make sure that multiple commas are handled too.

        targetPath = "///science,,,biology,,,Index.vm";

        sb = new StringBuffer();

        ResolverUtils.parseTargetPath(targetPath,sb);

        assertEquals("/science/biology/Index.vm", sb.toString());
    }
}
