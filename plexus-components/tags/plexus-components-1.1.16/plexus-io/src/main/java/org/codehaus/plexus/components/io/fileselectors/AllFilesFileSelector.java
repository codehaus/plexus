package org.codehaus.plexus.components.io.fileselectors;

/*
 * Copyright 2007 The Codehaus Foundation.
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

import java.io.IOException;


/**
 * The default file selector: Selects all files.
 */
public class AllFilesFileSelector implements FileSelector
{
    /**
     * The all files selectors role-hint: "all".
     */
    public static final String ROLE_HINT = "all";

    public boolean isSelected( FileInfo fileInfo ) throws IOException
    {
        return true;
    }
}