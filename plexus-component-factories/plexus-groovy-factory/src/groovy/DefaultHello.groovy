import org.codehaus.plexus.component.factory.groovy.Hello;

class DefaultHello
    implements Hello
{
    void initialize()
    {
        System.out.println( "initialize()" );
    }

    void start()
    {
        System.out.println( "start()" );
    }

    void stop()
    {
        System.out.println( "stop()" );
    }

    void dispose()
    {
        System.out.println( "dispose()" );
    }

    void hello()
    {
        System.out.println( "hello!" );
    }
}
