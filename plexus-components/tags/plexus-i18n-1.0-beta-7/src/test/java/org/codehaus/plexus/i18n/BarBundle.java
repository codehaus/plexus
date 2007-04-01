package org.codehaus.plexus.i18n;

import java.util.ListResourceBundle;

/**
 * A default resource bundle for use in testing.
 */
public class BarBundle
    extends ListResourceBundle
{
    private static final Object[][] CONTENTS =
    {
        { "key1", "[] value1" },
        { "key2", "[] value2" },
        { "key3", "[] value3" },
        { "key4", "[] value4" }
    };
    
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
