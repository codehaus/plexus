package org.codehaus.plexus.i18n;

import java.util.ListResourceBundle;

/**
 * An english resource bundle for use in testing.
 */
public class BarBundle_en_US
    extends ListResourceBundle
{
    private static final Object[][] CONTENTS =
    {
        { "key1", "[en_US] value1" },
        { "key2", "[en_US] value2" },
        { "key3", "[en_US] value3" },
        { "key4", "[en_US] value4" }
    };
    
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
