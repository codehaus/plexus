package org.codehaus.plexus.security.ui.web.eXc.views;

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.security.rbac.Role;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.view.html.BuilderConstants;
import org.extremecomponents.table.view.html.BuilderUtils;
import org.extremecomponents.table.view.html.TableActions;
import org.extremecomponents.table.view.html.ToolbarBuilder;
import org.extremecomponents.table.view.html.toolbar.ButtonItem;
import org.extremecomponents.util.HtmlBuilder;

/**
 * FilterToolbar
 *
 * @author Lester Ecarma
 */
public class FilterToolbar
{
    protected static String ATTRIBUTE_ROLENAME = "roleName";
    protected static String ATTRIBUTE_ROLES = "roles";
    protected static String CSS_CLASS_FILTER_INPUT = "filterInput";
    protected static String CSS_CLASS_FILTER_TOOLBAR = "filterToolbar";
    protected static String CSS_CLASS_ROLE_DROPDOWN = "roleSelect";
    
    private HtmlBuilder html; 
    private TableModel model;
    
    public FilterToolbar(HtmlBuilder html, TableModel model) {
        this.html = html;
        this.model = model;
    }
    
    protected HtmlBuilder getHtmlBuilder() {
        return html;
    }

    protected TableModel getTableModel() {
        return model;
    }
    
    public void layout() {
        if( !BuilderUtils.filterable( model ) )
        {
            return;
        }
        
        html.tr(1).close();

        html.td(2).colSpan(String.valueOf(model.getColumnHandler().columnCount()))
            .styleClass( CSS_CLASS_FILTER_TOOLBAR ).close();
        html.table(2).border("0").cellPadding("0").cellSpacing("0").width("100%").close();
        
        html.tr(3).close();
        
        html.td(3).close();
        html.bold().append("Filter by:");
        html.boldEnd();
        html.tdEnd();
        html.trEnd(3);
        
        html.tr(3).close();
        layoutFilterInputFields();
        layoutButtons();
        html.trEnd(3);
        
        html.tableEnd(2);
        html.tdEnd();
        html.trEnd(1);
    }
    
    protected void layoutFilterInputFields()
    {
        List columns = model.getColumnHandler().getFilterColumns();
        for ( Iterator iter = columns.iterator(); iter.hasNext(); )
        {
            Column column = (Column) iter.next();
            if( column.isFilterable() )
            {
                html.td( 4 ).styleClass( CSS_CLASS_FILTER_INPUT ).close();
                html.append( column.getTitle() );
                html.tdEnd();
                html.append( column.getCellDisplay() );
            }
        }

        html.td( 4 ).styleClass( CSS_CLASS_FILTER_INPUT ).close();
        html.append( "Role" );
        html.tdEnd();
        
        html.td( 4 ).close();
        Context context = model.getContext();
        Collection roles = (Collection) context.getRequestAttribute( ATTRIBUTE_ROLES );
        
        if( roles != null )
        {
            String selected = context.getParameter( ATTRIBUTE_ROLENAME );
            
            html.select().name( ATTRIBUTE_ROLENAME ).styleClass( CSS_CLASS_ROLE_DROPDOWN ).close();
            html.option().value( "" );
            if( selected == null )
            {
                html.selected();
            }
            html.close();
            html.append( "Any" );
            html.optionEnd();
            
            Iterator i = roles.iterator();
            while( i.hasNext() )
            {
                Role role = (Role) i.next();
                html.option().value( role.getName() );
                if( role.getName().equals( selected ) )
                {
                    html.selected();
                }
                html.close();
                html.append( role.getName() );
                html.optionEnd();
            }
            html.selectEnd();
        }
        html.tdEnd();
    }
    
    protected void layoutButtons()
    {
        ToolbarBuilder toolbarBuilder = new ToolbarBuilder( html, model );
        
        html.td( 4 ).width("100%").close();
        toolbarBuilder.filterItemAsButton();
        html.nbsp();
        
        ButtonItem item = new ButtonItem();
        item.setTooltip( model.getMessages().getMessage( BuilderConstants.TOOLBAR_CLEAR_TOOLTIP ) );
        item.setContents( model.getMessages().getMessage( BuilderConstants.TOOLBAR_CLEAR_TEXT ) );
        
        TableActions actions = new TableActions( model );
        
        StringBuffer action = new StringBuffer( "javascript:" );
        action.append( actions.getClearedExportTableIdParameters() );
        action.append( "document.forms." ).append( BuilderUtils.getForm( model ) )
            .append( "." ).append( ATTRIBUTE_ROLENAME ).append( ".value='';" );
        action.append(actions.getFormParameter( TableConstants.FILTER + TableConstants.ACTION, 
                                                TableConstants.CLEAR_ACTION ) );
        action.append( actions.getFormParameter( TableConstants.PAGE, "1" ) );
        action.append( actions.getOnInvokeAction() );
        
        item.setAction( action.toString() );
        item.enabled( html, model );
        html.tdEnd();
    }

}
