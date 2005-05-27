package org.codehaus.plexus.formica;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
public class DefaultFormManagerTest
    extends PlexusTestCase
{
    private FormManager formManager;

    public void setUp()
        throws Exception
    {
        super.setUp();

        formManager = (FormManager) lookup( FormManager.ROLE );
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

        assertEquals( "default.title", form.getAdd().getTitleKey() );

        Element element = form.getElement( "form.field0" );

        assertNotNull( element );

        assertEquals( "form.field0", element.getId() );

        assertEquals( "form.field0.label", element.getLabelKey() );

        assertEquals( "form.field0.message", element.getMessageKey() );

        org.codehaus.plexus.formica.Validator v = (org.codehaus.plexus.formica.Validator) element.getValidators().get( 0 );

        assertEquals( "form.field0.error", v.getErrorMessageKey() );
    }

    public void testFormValues()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        assertNotNull( form );

        assertEquals( "tpi.title", form.getAdd().getTitleKey() );

        Element e = form.getElement( "field-0" );

        assertEquals( "50", e.getAttributes().get( "size" ) );
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
}
