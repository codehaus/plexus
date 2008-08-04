package org.codehaus.plexus.mimetyper;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultMimeTyperTest
    extends PlexusTestCase
{
    public void testMimeTyper() throws Exception
    {
        MimeTyper mimetyper = (MimeTyper) lookup(MimeTyper.ROLE);
        assertEquals("image/tiff", mimetyper.getContentType("test.tif"));
    }
}
