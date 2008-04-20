package org.codehaus.plexus.archiver.tar;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.File;

/**
 * Extract files in tar with gzip compression
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Revision: $
 */
public class TarGZipUnArchiver
    extends TarUnArchiver
{
    public TarGZipUnArchiver()
    {
    	this.setupCompressionMethod();
    }

    public TarGZipUnArchiver( File sourceFile )
    {
        super( sourceFile );
        this.setupCompressionMethod();
    }
    
    private void setupCompressionMethod()
    {
    	UntarCompressionMethod untarCompressionMethod = new UntarCompressionMethod( UntarCompressionMethod.GZIP );
    	this.setCompression( untarCompressionMethod );
    }

}