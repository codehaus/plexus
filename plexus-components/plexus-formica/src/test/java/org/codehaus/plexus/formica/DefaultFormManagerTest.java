package org.codehaus.plexus.formica;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.formica.validation.Validator;
import org.codehaus.plexus.formica.web.FormRenderer;
import org.codehaus.plexus.formica.web.FormRendererManager;
import org.codehaus.plexus.i18n.I18N;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultFormManagerTest
    extends PlexusTestCase
{
    private FormManager formManager;

    private FormRendererManager formRendererManager;

    public DefaultFormManagerTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        super.setUp();

        formManager = (FormManager) lookup( FormManager.ROLE );

        formRendererManager = (FormRendererManager) lookup( FormRendererManager.ROLE );
    }

    public void testValidatorInstantiation()
        throws Exception
    {
        Validator creditCardValidator = formManager.getValidator( "credit-card" );

        assertNotNull( creditCardValidator );

        Validator dateValidator = formManager.getValidator( "date" );

        assertNotNull( dateValidator );

        Validator emailValidator = formManager.getValidator( "email" );

        assertNotNull( emailValidator );

        Validator regexValidator = formManager.getValidator( "pattern-digits" );

        assertNotNull( regexValidator );

        Validator urlValidator = formManager.getValidator( "url" );

        assertNotNull( urlValidator );
    }

    public void testDefaultI18NKeyPopulation()
        throws Exception
    {
        Form form = formManager.getForm( "default" );

        assertNotNull( form );

        TargetObject target = new TargetObject();

        target.setField0( "field0-value" );

        // Make sure the title key is correct.
        assertEquals( "default.title", form.getAdd().getTitleKey() );

        Element element = form.getElement( "form.field0" );

        assertNotNull( element );

        assertEquals( "form.field0", element.getId() );

        assertEquals( "form.field0.label", element.getLabelKey() );

        assertEquals( "form.field0.message", element.getMessageKey() );

        assertEquals( "form.field0.error", element.getErrorMessageKey() );
    }

    public void testFormValues()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        assertNotNull( form );

        assertEquals( "tpi.title", form.getAdd().getTitleKey() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void testFormValidationWhereAllDataIsValid()
        throws Exception
    {
        Map data = getValidElementData();

        FormValidationResult result = formManager.validate( formManager.getForm( "tpi" ), data );

        assertTrue( result.elementsValid() );

        assertTrue( result.valid() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void testFormValidationWhereAFieldIsInvalidAndThatErrorMessageIsCorrect()
        throws Exception
    {
        Map data = getValidElementData();

        data.put( "field-0", "xxxx" );

        FormValidationResult result = formManager.validate( formManager.getForm( "tpi" ), data );

        assertFalse( result.elementsValid() );

        assertFalse( result.valid() );

        assertEquals( "Field 0 is invalid", result.getElementResult( "field-0" ).getErrorMessage() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void testFormValidationUsingFormIdToRetrieveForm()
        throws Exception
    {
        Map data = getValidElementData();

        FormValidationResult result = formManager.validate( "tpi", data );

        assertTrue( result.elementsValid() );

        assertTrue( result.valid() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void testTargetPopulation()
        throws Exception
    {
        Map data = getValidElementData();

        TargetObject target = new TargetObject();

        formManager.populate( formManager.getForm( "tpi" ), data, target );

        assertEquals( "5500000000000004", target.getField0() );

        assertEquals( "05/11/1972", target.getField1() );

        assertEquals( "jason@maven.org", target.getField2() );

        assertEquals( "1111111", target.getField3() );

        assertEquals( "http://www.maven.org/index.html", target.getField4() );
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    private Map getValidElementData()
    {
        Map data = new HashMap();

        data.put( "field-0", "5500000000000004" );

        data.put( "field-1", "05/11/1972" );

        data.put( "field-2", "jason@maven.org" );

        data.put( "field-3", "1111111" );

        data.put( "field-4", "http://www.maven.org/index.html" );

        return data;
    }

    private Map getValidGroupData()
    {
        Map data = new HashMap();

        data.put( "password-one", "secret-password" );

        data.put( "password-two", "secret-password" );

        data.put( "email-one", "info@info.com" );

        data.put( "email-two", "info@info.com" );

        return data;
    }

    public void testAddFormRending()
        throws Exception
    {
        Writer w = new OutputStreamWriter( System.out );

        Form form = formManager.getForm( "login" );

        I18N i18n = (I18N) lookup( I18N.ROLE );

        FormRenderer renderer = formRendererManager.getFormRenderer( "add" );

        renderer.render( form, w, i18n, getValidElementData(), "http://foo/bar" );

        w.flush();
    }

    public void testSummaryFormRendering()
        throws Exception
    {
        Writer w = new OutputStreamWriter( System.out );

        Form form = formManager.getForm( "tpi" );

        I18N i18n = (I18N) lookup( I18N.ROLE );

        FormRenderer renderer = formRendererManager.getFormRenderer( "summary" );

        Collection c = new ArrayList();

        TargetObject o1 = new TargetObject();

        o1.setField0( "field 0 value [1]" );

        o1.setField1( "field 1 value [1]" );

        c.add( o1 );

        TargetObject o2 = new TargetObject();

        o2.setField0( "field 0 value [2]" );

        o2.setField1( "field 1 value [2]" );

        c.add( o2 );

        renderer.render( form, w, i18n, c, "http://foo/bar" );

        w.flush();
    }

    public void testViewFormRendering()
        throws Exception
    {
        Writer w = new OutputStreamWriter( System.out );

        Form form = formManager.getForm( "tpi" );

        I18N i18n = (I18N) lookup( I18N.ROLE );

        FormRenderer renderer = formRendererManager.getFormRenderer( "view" );

        TargetObject o1 = new TargetObject();

        o1.setField0( "field 0 value [1]" );

        o1.setField1( "field 1 value [1]" );

        renderer.render( form, w, i18n, o1, "http://foo/bar" );

        w.flush();
    }

}
