package org.codehaus.plexus.formica.runtime;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.FormManager;
import org.codehaus.plexus.formica.FormValidationResult;
import org.codehaus.plexus.formica.TargetObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class FormRuntimeTest
    extends PlexusTestCase
{
    private FormManager formManager;

    public void setUp()
        throws Exception
    {
        super.setUp();

        formManager = (FormManager) lookup( FormManager.ROLE );

        assertNotNull( formManager );
    }

    public void testTargetPopulation()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        FormRuntime formRuntime = new FormRuntime();

        formRuntime.setForm( form );

        Map formData = getValidData();

        formRuntime.setFormData( formData );

        Object targetObject = formRuntime.getTarget();

        assertEquals( TargetObject.class, targetObject.getClass() );

        TargetObject target = (TargetObject) targetObject;

        assertEquals( "5500000000000004", target.getField0() );

        assertEquals( "05/11/1972", target.getField1() );

        assertEquals( "jason@maven.org", target.getField2() );

        assertEquals( "1111111", target.getField3() );

        assertEquals( "http://www.maven.org/index.html", target.getField4() );
    }


    public void testFormPopulation()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        FormRuntime formRuntime = new FormRuntime();

        formRuntime.setForm( form );

        TargetObject target = getSampleTarget();

        formRuntime.setTarget( target );

        Map formData = formRuntime.getFormData();

        assertEquals( "field0", formData.get( "field-0" ) );

        assertEquals( "field1", formData.get( "field-1" ) );
    }

    public void testI8NWithoutLocale()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        FormRuntime formRuntime = new FormRuntime();

        formRuntime.setForm( form );

        // Make sure the localized form title is correct.
        assertEquals( "Trading Partner Information", formRuntime.getTitle() );

        ElementRuntime element = formRuntime.getElement( "field-0" );

        // Test the  label, message, and error message.

        assertEquals( "Field 0", element.getLabel() );

        assertEquals( "Message 0", element.getMessage() );

        assertEquals( "Field 0 is invalid", element.getErrorMessage() );
    }


    public void testI8NWithLocale() throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        FormRuntime formRuntime = new FormRuntime();

        formRuntime.setForm( form );

        formRuntime.setLocale( new Locale( "xx" ) );

        ElementRuntime element = formRuntime.getElement( "field-0" );

        // Test the localized label, message, and error message.

        assertEquals( "Etykieta 0", element.getLabel() );

        assertEquals( "Komunikat 0", element.getMessage() );

        assertEquals( "Blad 0", element.getErrorMessage() );
    }

    public void testFormValidationWithValuesThatAreAllCorrect()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        assertNotNull( form );

        Map formData = getValidData();

        FormRuntime formRuntime = new FormRuntime();

        formRuntime.setForm( form );

        formRuntime.setFormData( formData );

        FormValidationResult formValidationResult = formRuntime.validate();

        assertTrue( formValidationResult.isValid() );

        formRuntime.isValid();
    }

    private TargetObject getSampleTarget()
    {
        TargetObject target = new TargetObject();

        target.setField0( "field0" );

        target.setField1( "field1" );

        target.setField2( "field2" );

        target.setField3( "field3" );

        target.setField4( "field4" );

        target.setPasswordOne( "passwordOne" );

        target.setPasswordTwo( "passwordTwo" );

        target.setEmailOne( "emailOne" );

        target.setEmailTwo( "emailTwo" );

        return target;
    }

    private Map getValidData()
    {
        // Procsss the form
        Map formData = new HashMap();

        formData.put( "field-0", "5500000000000004" );

        formData.put( "field-1", "05/11/1972" );

        formData.put( "field-2", "jason@maven.org" );

        formData.put( "field-3", "1111111" );

        formData.put( "field-4", "http://www.maven.org/index.html" );

        formData.put( "password-one", "secret-password" );

        formData.put( "password-two", "secret-password" );

        formData.put( "email-one", "info@info.com" );

        formData.put( "email-two", "info@info.com" );

        return formData;
    }

    public void testFormValidationWithMixedValues()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );

        assertNotNull( form );

        Map formData = getMixedData();

        FormRuntime formRuntime = new FormRuntime();

        formRuntime.setForm( form );

        formRuntime.setFormData( formData );

        FormValidationResult result = formRuntime.validate();

        assertFalse( result.isValid() );

        assertTrue( formRuntime.getElement( "field-0" ).isValid() );

        assertFalse( formRuntime.getElement( "field-1" ).isValid() );

        assertFalse( formRuntime.getElement( "field-2" ).isValid() );

        assertFalse( formRuntime.getElement( "field-3" ).isValid() );

        assertFalse( formRuntime.getElement( "field-4" ).isValid() );

        assertTrue( result.isElementValid( "field-0" ) );

        assertFalse( result.isElementValid( "field-1" ) );

        assertFalse( result.isElementValid( "field-2" ) );

        assertFalse( result.isElementValid( "field-3" ) );

        assertFalse( result.isElementValid( "field-4" ) );

        assertFalse( result.isGroupValid( "passwords" ) );

        assertTrue( result.isGroupValid( "emails" ) );

        assertFalse( formRuntime.getGroup( "passwords" ).isValid() );

        assertTrue( formRuntime.getGroup( "emails" ).isValid() );

        // Now how to get the verbiage for error display in the view.

        validationVerbiage1( result );

        validationVerbiage2( formRuntime );
    }


    private void validationVerbiage1( FormValidationResult result )
    {
        for ( Iterator iterator = result.getInvalidElements().iterator(); iterator.hasNext(); )
        {
            ElementRuntime element = (ElementRuntime) iterator.next();
            System.out.println( "Element '" + element.getId() + "' is invalid: " + element.getErrorMessage() );
        }

        for ( Iterator iterator = result.getInvalidGroups().iterator(); iterator.hasNext(); )
        {
            ElementGroupRuntime group = (ElementGroupRuntime) iterator.next();
            System.out.println( "Group '" + group.getId() + "' is invalid: " + group.getErrorMessage() );
        }
    }


    private void validationVerbiage2( FormRuntime formRuntime )
    {
        // second version
        for ( Iterator iterator = formRuntime.getElements().iterator(); iterator.hasNext(); )
        {
            ElementRuntime element = (ElementRuntime) iterator.next();
            if ( !element.isValid() )
            {
                System.out.println( "Element '" + element.getId() + "' is invalid: " + element.getErrorMessage() );
            }
        }
        for ( Iterator iterator = formRuntime.getGroups().iterator(); iterator.hasNext(); )
        {
            ElementGroupRuntime group = (ElementGroupRuntime) iterator.next();
            if ( !group.isValid() )
            {
                System.out.println( "Group '" + group.getId() + "' is invalid: " + group.getErrorMessage() );
            }
        }
    }

    public void testFormValidationWithValuesThatAreIncorrect()
        throws Exception
    {
        Form form = formManager.getForm( "tpi" );
        assertNotNull( form );

        Map formData = getInvalidData();

        FormRuntime formRuntime = new FormRuntime();
        formRuntime.setForm( form );
        formRuntime.setFormData( formData );


        FormValidationResult result = formRuntime.validate();

        assertFalse( result.isValid() );
        assertFalse( formRuntime.isValid() );

        assertFalse( formRuntime.getElement( "field-0" ).isValid() );
        assertFalse( formRuntime.getElement( "field-1" ).isValid() );
        assertFalse( formRuntime.getElement( "field-2" ).isValid() );
        assertFalse( formRuntime.getElement( "field-3" ).isValid() );
        assertFalse( formRuntime.getElement( "field-4" ).isValid() );

        assertFalse( result.isElementValid( "field-1" ) );
        assertFalse( result.isElementValid( "field-2" ) );
        assertFalse( result.isElementValid( "field-3" ) );
        assertFalse( result.isElementValid( "field-4" ) );


        assertFalse( result.isGroupValid( "passwords" ) );


        // Now how to get the verbiage for error display in the view.

        for ( Iterator iterator = result.getInvalidElements().iterator(); iterator.hasNext(); )
        {
            ElementRuntime element = (ElementRuntime) iterator.next();
            System.out.println( "Element '" + element.getId() + "' is invalid: " + element.getErrorMessage() );
        }
        for ( Iterator iterator = result.getInvalidGroups().iterator(); iterator.hasNext(); )
        {
            ElementGroupRuntime group = (ElementGroupRuntime) iterator.next();
            System.out.println( "Group '" + group.getId() + "' is invalid: " + group.getErrorMessage() );
        }

        // second version
        for ( Iterator iterator = formRuntime.getElements().iterator(); iterator.hasNext(); )
        {
            ElementRuntime element = (ElementRuntime) iterator.next();
            if ( !element.isValid() )
            {
                System.out.println( "Element '" + element.getId() + "' is invalid: " + element.getErrorMessage() );
            }
        }

        // second version
        for ( Iterator iterator = formRuntime.getGroups().iterator(); iterator.hasNext(); )
        {
            ElementGroupRuntime group = (ElementGroupRuntime) iterator.next();
            if ( !group.isValid() )
            {
                System.out.println( "Group '" + group.getId() + "' is invalid: " + group.getErrorMessage() );
            }
        }


    }


    private Map getInvalidData()
    {
        // Procsss the form
        Map formData = new HashMap();

        // These elements are incorrect.

        formData.put( "field-0", "5500000000000004x" );
        formData.put( "field-1", "05/11/1972x" );
        formData.put( "field-2", "jasonxmaven.org" );
        formData.put( "field-3", "1111111x" );
        formData.put( "field-4", "www.maven.org/index.html" );

        // These elements are valid on the element level because they
        // just have to be present but will cause the group validator
        // to fail.

        formData.put( "password-one", "secret-passwordx" );
        formData.put( "password-two", "secret-passwordy" );

        formData.put( "email-one", "xxx" );
        formData.put( "email-two", "yyy" );
        return formData;
    }


    /**
     * Return data which is partially valid
     *
     * @return
     */
    private Map getMixedData()
    {
        // Procsss the form
        Map formData = new HashMap();

        // These elements are incorrect.

        // valid
        formData.put( "field-0", "5500000000000004" );

        //invalid
        formData.put( "field-1", "05/11/1972x" );

        //valid
        formData.put( "field-2", "jasonxmaven.org" );

        //invalid
        formData.put( "field-3", "1111111x" );

        formData.put( "field-4", "www.maven.org/index.html" );

        //invalid
        formData.put( "password-one", "secret-passwordx" );

        formData.put( "password-two", "secret-passwordy" );

        ///valid
        formData.put( "email-one", "xxx" );

        formData.put( "email-two", "xxx" );

        return formData;
    }
}
