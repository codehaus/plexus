package $package;

/**
 * This component produces a greeting.
 *
 * @author
 * @version $$Id$$
 */
public interface HelloWorld
{
    /** The role associated with the component. */
    String ROLE = HelloWorld.class.getName();

    /**
     * Says hello by returning a greeting to the caller.
     *
     * @return A greeting.
     */
    public String sayHello();
}

