package org.codehaus.plexus.formproc;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.formproc.Form;
import org.formproc.FormData;
import org.formproc.FormManager;
import org.formproc.FormResult;

/**
 * TODO Document DefaultFormProcServiceTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 15, 2003
 */
public class DefaultFormProcServiceTest
    extends PlexusTestCase
{
    public DefaultFormProcServiceTest(String name)
    {
        super(name);
    }

    public void testService() throws Exception
    {
        FormProcService service = (FormProcService) lookup( FormProcService.ROLE );
        
        FormManager man = service.getFormManager();
        
        List values = new ArrayList();
        values.add( new FormData("firstName", "Dan"));
        values.add( new FormData("lastName", "Diephouse"));
        values.add( new FormData("age", "21"));
        values.add( new FormData("skill", "Obfuscator"));
        
        Form f = new Form( );
        f.setName( "test" );
        man.configure( f );
        
        FormResult result = f.process( values );
        
        if ( !result.isValid() )
        {
            System.out.println( result.getErrorOrMessage( "firstName") );
            System.out.println( result.getOriginalValue( "firstName") );
            System.out.println( result.getErrorOrMessage( "lastName") );
            System.out.println( result.getOriginalValue( "lastName") );
            System.out.println( result.getErrorOrMessage( "skill") );
            System.out.println( result.getOriginalValue( "skill") );
            fail( "Form wasn't valid." );
        }
        
        release(service);
    }
}
