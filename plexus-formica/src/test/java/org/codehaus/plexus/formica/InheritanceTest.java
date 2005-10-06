package org.codehaus.plexus.formica;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.formica.validation.Validator;
import org.codehaus.plexus.i18n.I18N;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class InheritanceTest
    extends PlexusTestCase
{
    private FormManager formManager;

    public void setUp()
        throws Exception
    {
        super.setUp();

        formManager = (FormManager) lookup( FormManager.ROLE );
    }

    public void testChildInheritance()
        throws Exception
    {
         Form form = formManager.getForm( "child" );

        assertNotNull( form );

        assertEquals( "key-expression", form.getKeyExpression() );

        assertEquals( "lookup-expression", form.getLookupExpression() );

        assertEquals( "summary-collection-expression", form.getSummaryCollectionExpression() );

        assertEquals( "source-role", form.getSourceRole() );

        assertEquals( 4, form.getElements().size() );

        Add add = form.getAdd();

        assertEquals( "add-title-key", add.getTitleKey() );

        Update update = form.getUpdate();

        assertEquals( "update-title-key", update.getTitleKey() );

        View view = form.getView();

        assertEquals( "view-title-key", view.getTitleKey() );

        Delete delete = form.getDelete();

        assertEquals( "delete-title-key", delete.getTitleKey() );
    }


    public void testGrandchildInheritanceWhereAllTopLevelFieldsAreInherited()
        throws Exception
    {
         Form form = formManager.getForm( "grandchild0" );

        assertNotNull( form );

        assertEquals( "key-expression", form.getKeyExpression() );

        assertEquals( "lookup-expression", form.getLookupExpression() );

        assertEquals( "summary-collection-expression", form.getSummaryCollectionExpression() );

        assertEquals( "source-role", form.getSourceRole() );

        assertEquals( 5, form.getElements().size() );

        Add add = form.getAdd();

        assertEquals( "add-title-key", add.getTitleKey() );

        Update update = form.getUpdate();

        assertEquals( "update-title-key", update.getTitleKey() );

        View view = form.getView();

        assertEquals( "view-title-key", view.getTitleKey() );

        Delete delete = form.getDelete();

        assertEquals( "delete-title-key", delete.getTitleKey() );
    }

    public void testGrandchildInheritanceWhereSomeTopLevelFieldsAreInherited()
        throws Exception
    {
         Form form = formManager.getForm( "grandchild1" );

        assertNotNull( form );

        assertEquals( "key-expression", form.getKeyExpression() );

        assertEquals( "lookup-expression", form.getLookupExpression() );

        assertEquals( "grandchild1-summary-collection-expression", form.getSummaryCollectionExpression() );

        assertEquals( "source-role", form.getSourceRole() );

        assertEquals( 5, form.getElements().size() );

        Add add = form.getAdd();

        assertEquals( "add-title-key", add.getTitleKey() );

        Update update = form.getUpdate();

        assertEquals( "update-title-key", update.getTitleKey() );

        View view = form.getView();

        assertEquals( "view-title-key", view.getTitleKey() );

        Delete delete = form.getDelete();

        assertEquals( "delete-title-key", delete.getTitleKey() );

        Element overridden = form.getElement( "field-1" );

        assertEquals( "grandchild1-field1", overridden.getExpression() );

        assertTrue( overridden.isImmutable() );
    }
}
