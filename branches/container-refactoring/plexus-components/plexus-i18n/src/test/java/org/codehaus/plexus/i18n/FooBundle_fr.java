package org.codehaus.plexus.i18n;

import java.util.ListResourceBundle;

/**
 * A dummied up French resource bundle for use in testing.
 */
public class FooBundle_fr extends ListResourceBundle
{
    private static final Object[][] CONTENTS =
    {
        { "key1", "[fr] value1" },
        { "key2", "[fr] value2" },
        { "key3", "[fr] value3" },
        { "key4", "[fr] value4" }
    };
    
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
