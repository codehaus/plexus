package $package;

/**
 *  @plexus.component role="${package}.Component" role-hint="default"
 */
public class DefaultComponent implements Component
{
    public String sayHello()
    {
        return "<h1>Hello PlexusServlet World!</h1>";
    }
}
