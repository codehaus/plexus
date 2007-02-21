package org.codehaus.plexus.redback.ui.web.eXc;

/*
 * Copyright 2005-2006 The Codehaus.
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

import org.codehaus.plexus.redback.ui.web.util.UserComparator;
import org.extremecomponents.table.callback.ProcessRowsCallback;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.limit.Sort;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ProcessUserRowsCallback - Efficient and safe sort callback for user manager provided user lists.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ProcessUserRowsCallback
    extends ProcessRowsCallback
{

    public Collection sortRows( TableModel model, Collection rows )
        throws Exception
    {
        boolean sorted = model.getLimit().isSorted();

        if ( !sorted )
        {
            return rows;
        }

        Sort sort = model.getLimit().getSort();
        String property = sort.getProperty();
        String sortOrder = sort.getSortOrder();

        System.out.println( "SORTING: " + property + " - " + sortOrder );

        UserComparator comparator = new UserComparator( property, TableConstants.SORT_ASC.equals( sortOrder ) );
        Collections.sort( (List) rows, comparator );

        return rows;
    }

}
