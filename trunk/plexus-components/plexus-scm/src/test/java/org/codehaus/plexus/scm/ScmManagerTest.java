package org.codehaus.plexus.scm;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ScmManagerTest
    extends PlexusTestCase
{
    protected String getRepositoryName()
    {
        return "scm-manager";
    }

    public void testNonExistingScmType()
        throws Exception
    {
        ScmManager scmManager = getScmManager();

        try
        {
            scmManager.checkOut( scmManager.makeScmRepository( "scm:non-existing-scm:" ),
                                 new ScmFileSet( getTestFile( "" ) ), null );

            fail( "Expected NoSuchScmTypeException" );
        }
        catch ( NoSuchScmProviderException ex )
        {
            assertEquals( "non-existing-scm", ex.getProviderName() );
        }
    }

    public void testNonExistingScmTypeWithBarAsDelimiter()
        throws Exception
    {
        ScmManager scmManager = getScmManager();

        try
        {
            scmManager.checkOut( scmManager.makeScmRepository( "scm:non-existing-scm|" ),
                                 new ScmFileSet( getTestFile( "" ) ), null );

            fail( "Expected NoSuchScmTypeException" );
        }
        catch ( NoSuchScmProviderException ex )
        {
            assertEquals( "non-existing-scm", ex.getProviderName() );
        }
    }

    public void testNonExistingScmTypeWithStarAsDelimiter()
        throws Exception
    {
        ScmManager scmManager = getScmManager();

        try
        {
            scmManager.checkOut( scmManager.makeScmRepository( "scm:non-existing-scm*" ),
                                 new ScmFileSet( getTestFile( "" ) ), null );

            fail( "Expected ScmRepositoryException" );
        }
        catch ( ScmRepositoryException ex )
        {
            // expected
        }
    }

    protected ScmManager getScmManager()
        throws Exception
    {
        return (ScmManager) lookup( ScmManager.ROLE );
    }
}
