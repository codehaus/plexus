package org.codehaus.plexus.formica.action;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import ognl.Ognl;
import org.codehaus.plexus.formica.Form;
import org.codehaus.tissue.Issue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class UpdateIssue
    extends AbstractEntityAction
{
    protected void uponSuccessfulValidation( Form form, String entityId, Map map )
        throws Exception
    {
        Map m = new HashMap();

        m.put( "id", entityId );

        Object entity = Ognl.getValue( form.getLookupExpression(), m, getApp() );

        fm.populate( form.getId(), map, entity );

        m.put( "entity", entity );

        Object o = Ognl.getValue( form.getUpdate().getExpression(), m, getApp() );
    }
}
