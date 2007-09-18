package org.codehaus.plexus.redback.role.merger;

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

import java.util.List;

import org.codehaus.plexus.redback.role.RoleManagerException;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;

/**
 * RoleModelValidator:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public interface RoleModelMerger
{
    public static final String ROLE = RoleModelMerger.class.getName();
    
    public boolean hasMergeErrors();
    
    public List getMergeErrors();
    
    public RedbackRoleModel merge( RedbackRoleModel originalModel, RedbackRoleModel newModel ) throws RoleManagerException;
}