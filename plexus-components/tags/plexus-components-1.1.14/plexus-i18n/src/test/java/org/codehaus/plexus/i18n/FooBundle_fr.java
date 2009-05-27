package org.codehaus.plexus.i18n;

/*
 * Copyright 2001-2007 Codehaus Foundation.
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
