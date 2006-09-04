package $package;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

import java.util.Map;

/**
 * @plexus.component role="com.opensymphony.xwork.Action" role-hint="helloAction"
 */
public class DefaultHelloWorld
    extends ActionSupport
    implements HelloWorld
{
    public Person getPerson() {
        Map session = ActionContext.getContext().getSession();

        Person person = (Person) session.get( "person" );

        if ( person == null )
        {
            person = new Person();

            session.put( "person", person );
        }

        return person;
    }

    public String execute()
        throws Exception
    {
        return SUCCESS;
    }
}
