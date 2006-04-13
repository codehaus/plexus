/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.meridian;

import java.io.Writer;
import java.io.IOException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Meridian
{
    public void render( String template, Writer writer )
        throws IOException;
}
