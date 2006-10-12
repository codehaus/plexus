package org.codehaus.plexus.i18n;

import java.util.ListResourceBundle;

/**
 * An english resource bundle for use in testing.
 */
public class FooBundle_en extends ListResourceBundle
{
    private static final Object[][] CONTENTS =
    {
        { "key1", "[en] value1" },
        { "key2", "[en] value2" },
        { "key3", "[en] value3" },
        { "key4", "[en] value4" }
    };
    
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
