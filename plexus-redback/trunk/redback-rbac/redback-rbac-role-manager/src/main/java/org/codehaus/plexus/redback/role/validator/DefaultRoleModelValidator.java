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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.redback.role.RoleManagerException;
import org.codehaus.plexus.redback.role.model.ModelPermission;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.ModelTemplate;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.util.RoleModelUtils;
import org.codehaus.plexus.util.dag.CycleDetectedException;

/**
 * DefaultRoleModelValidator: validates completeness of the model
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.role.validator.RoleModelValidator"
 *   role-hint="default"
 */
public class DefaultRoleModelValidator implements RoleModelValidator
{
    private List validationErrors;

    public boolean validate( RedbackRoleModel model ) throws RoleManagerException
    {
        validationErrors = null;

        validateRequiredStructure( model );
        validateResourceClosure( model );
        validateOperationClosure( model );
        validateChildRoleClosure( model );
        validateParentRoleClosure( model );
        validateTemplateClosure( model );
        validateNoRoleCycles( model );
        validateNoTemplateCycles( model );

        if ( validationErrors == null )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public List getValidationErrors()
    {
        return validationErrors;
    }

    private void addValidationError( String error )
    {
        if ( validationErrors == null )
        {
            validationErrors = new ArrayList();
        }

        validationErrors.add( error );
    }

    /**
     * FIXME this should be taken care of by <required/> in modello, figure out why its not
     * @param model
     */
    private void validateRequiredStructure( RedbackRoleModel model )
    {

    }

    /**
     * validate all operations in all declared permissions exist as declared in the operations section
     * 
     * @param validationErrors
     * @param model
     */
    private void validateOperationClosure( RedbackRoleModel model )
    {
        List operationIdList = RoleModelUtils.getOperationIdList( model );

        // check the operations in role permissions
        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole) i.next();

            if ( role.getPermissions() != null )
            {
                for ( Iterator j = role.getPermissions().iterator(); j.hasNext(); )
                {
                    ModelPermission permission = (ModelPermission) j.next();

                    if ( !operationIdList.contains( permission.getOperation() ) )
                    {
                        addValidationError( "missing operation: " + permission.getOperation() + " in permission "
                                        + permission.getId() );
                    }
                }
            }
        }

        // check the operations in template permissions
        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate) i.next();

            if ( template.getPermissions() != null )
            {
                for ( Iterator j = template.getPermissions().iterator(); j.hasNext(); )
                {
                    ModelPermission permission = (ModelPermission) j.next();

                    if ( !operationIdList.contains( permission.getOperation() ) )
                    {
                        addValidationError( "missing operation: " + permission.getOperation() + " in permission "
                                        + permission.getId() );
                    }
                }
            }
        }
    }

    private void validateResourceClosure( RedbackRoleModel model )
    {
        List resourceIdList = RoleModelUtils.getResourceIdList( model );

        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole) i.next();

            if ( role.getPermissions() != null )
            {
                for ( Iterator j = role.getPermissions().iterator(); j.hasNext(); )
                {
                    ModelPermission permission = (ModelPermission) j.next();

                    if ( !resourceIdList.contains( permission.getResource() ) )
                    {
                        addValidationError( "missing operation: " + permission.getResource() + " in permission "
                                        + permission.getId() );
                    }
                }
            }
        }
    }

    private void validateChildRoleClosure( RedbackRoleModel model )
    {
        List roleIdList = RoleModelUtils.getRoleIdList( model );

        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole) i.next();

            if ( role.getChildRoles() != null )
            {
                for ( Iterator j = role.getChildRoles().iterator(); j.hasNext(); )
                {
                    String childRoleId = (String) j.next();

                    if ( !roleIdList.contains( childRoleId ) )
                    {
                        addValidationError( "missing role id: " + childRoleId + " in child roles of role "
                                        + role.getId() );
                    }
                }
            }
        }

        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate) i.next();

            if ( template.getChildRoles() != null )
            {
                for ( Iterator j = template.getChildRoles().iterator(); j.hasNext(); )
                {
                    String childRoleId = (String) j.next();

                    if ( !roleIdList.contains( childRoleId ) )
                    {
                        addValidationError( "missing role id: " + childRoleId + " in child roles of template "
                                        + template.getId() );
                    }
                }
            }
        }
    }

    private void validateParentRoleClosure( RedbackRoleModel model )
    {
        List roleIdList = RoleModelUtils.getRoleIdList( model );

        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole) i.next();

            if ( role.getParentRoles() != null )
            {
                for ( Iterator j = role.getParentRoles().iterator(); j.hasNext(); )
                {
                    String parentRoleId = (String) j.next();

                    if ( !roleIdList.contains( parentRoleId ) )
                    {
                        addValidationError( "missing role id: " + parentRoleId + " in parent roles of role "
                                        + role.getId() );
                    }
                }
            }
        }

        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate) i.next();

            if ( template.getParentRoles() != null )
            {
                for ( Iterator j = template.getParentRoles().iterator(); j.hasNext(); )
                {
                    String parentRoleId = (String) j.next();

                    if ( !roleIdList.contains( parentRoleId ) )
                    {
                        addValidationError( "missing role id: " + parentRoleId + " in parent roles of template "
                                        + template.getId() );
                    }
                }
            }
        }
    }

    private void validateTemplateClosure( RedbackRoleModel model )
    {
        List templateIdList = RoleModelUtils.getTemplateIdList( model );

        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate) i.next();

            if ( template.getParentTemplates() != null )
            {
                for ( Iterator j = template.getParentTemplates().iterator(); j.hasNext(); )
                {
                    String parentTemplateId = (String) j.next();

                    if ( !templateIdList.contains( parentTemplateId ) )
                    {
                        addValidationError( "missing template id: " + parentTemplateId
                                        + " in parent templates of template " + template.getId() );
                    }
                }
            }

            if ( template.getChildTemplates() != null )
            {
                for ( Iterator j = template.getChildTemplates().iterator(); j.hasNext(); )
                {
                    String childTemplateId = (String) j.next();

                    if ( !templateIdList.contains( childTemplateId ) )
                    {
                        addValidationError( "missing template id: " + childTemplateId
                                        + " in child templates of template " + template.getId() );
                    }
                }
            }
        }
    }

    /**
     * We are not allowed to have cycles between roles, this method is to detect and raise a red flag when that happens.
     * 
     * @param model
     */
    private void validateNoRoleCycles( RedbackRoleModel model )
    {
        try
        {
            RoleModelUtils.generateRoleGraph( model );
        }
        catch ( CycleDetectedException e )
        {
            addValidationError( "cycle detected: " + e.getMessage() );
        }
    }
    
   
    /**
     * We are not allowed to have cycles between template either, this method is to detect and 
     * raise a red flag when that happens.  Templates are a bit more complex since they have both
     * child and parent roles, as well as runtime parent and child templates
     * 
     * the id should be sufficient to test cycles here even though in runtime the id's do not need to be
     * unique since it is the binding of a namePrefix and a resource that makes them unique
     * 
     * @param model
     */
    private void validateNoTemplateCycles( RedbackRoleModel model )
    {
        try
        {
            RoleModelUtils.generateTemplateGraph( model );
        }
        catch ( CycleDetectedException e )
        {
            addValidationError( "template cycle detected: " + e.getMessage() );
        }
    }
}
