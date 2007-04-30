package org.codehaus.plexus.redback.role.validator;

/*
 * Copyright 2005 The Codehaus.
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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.model.io.stax.RedbackRoleModelStaxReader;

/**
 * RoleModelMergerTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public class RoleModelValidatorTest
    extends PlexusTestCase
{

    /**
     * Creates a new RbacStore which contains no data.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }
    
    public void testGood() throws Exception 
    {
        File resource = new File( getBasedir() + "/src/test/validation-tests/redback-good.xml");
        
        assertNotNull( resource );
        
        RedbackRoleModelStaxReader modelReader = new RedbackRoleModelStaxReader();
        
        RedbackRoleModel redback = modelReader.read( resource.getAbsolutePath() );
        
        assertNotNull( redback );
        
        RoleModelValidator modelValidator = (RoleModelValidator)lookup( RoleModelValidator.ROLE, "default" );
        
        assertTrue( modelValidator.validate( redback ) );
        
        assertNull( modelValidator.getValidationErrors() );
    }
 
    public void testBad() throws Exception 
    {
        File resource = new File( getBasedir() + "/src/test/validation-tests/redback-bad.xml");
        
        assertNotNull( resource );
        
        RedbackRoleModelStaxReader modelReader = new RedbackRoleModelStaxReader();
        
        RedbackRoleModel redback = modelReader.read( resource.getAbsolutePath() );
        
        assertNotNull( redback );
        
        RoleModelValidator modelValidator = (RoleModelValidator)lookup( RoleModelValidator.ROLE, "default" );
        
        assertFalse( modelValidator.validate( redback ) );
        
        assertNotNull( modelValidator.getValidationErrors() );
          
        assertTrue( checkForValidationError( modelValidator.getValidationErrors(), "eat-cornflakes-missing-operation-in-template" ) );
     
        assertTrue( checkForValidationError( modelValidator.getValidationErrors(), "can-drink-the-milk-missing-child-role" ) );
        
        assertTrue( checkForValidationError( modelValidator.getValidationErrors(), "test-template-missing-child-template" ) );
        
        assertTrue( checkForValidationError( modelValidator.getValidationErrors(), "cycle detected" ) );
     
        assertTrue( checkForValidationError( modelValidator.getValidationErrors(), "template cycle detected" ) );
        
    }
    
    private boolean checkForValidationError( List validationErrors, String errorText )    
    {
        for ( Iterator i = validationErrors.iterator(); i.hasNext(); )
        {
            String error = (String)i.next();
            
            if ( error.indexOf( errorText ) != -1 )
            {
                return true;
            }
        }
        return false;        
    }
 
}