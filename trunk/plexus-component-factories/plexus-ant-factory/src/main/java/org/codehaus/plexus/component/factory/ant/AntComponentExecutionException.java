package org.codehaus.plexus.component.factory.ant;

import java.net.URL;

public class AntComponentExecutionException
    extends Exception
{
    
    static final long serialVersionUID = 1;

    private final URL script;
    private final String target;
    private final String originalMessage;

    public AntComponentExecutionException( URL script, String target, String message, Throwable cause )
    {
        super( "Executing Ant script: " + script + " [" + (( target == null ) ? ( "default-target" ) : ( target ) ) + "]: " + message, cause );
        
        this.script = script;
        this.target = target;
        this.originalMessage = message;
    }

    public AntComponentExecutionException( URL script, String target, String message )
    {
        super( "Executing Ant script: " + script + " [" + (( target == null ) ? ( "default-target" ) : ( target ) ) + "]: " + message );
        
        this.script = script;
        this.target = target;
        this.originalMessage = message;
    }

    public final String getOriginalMessage()
    {
        return originalMessage;
    }

    public final URL getScript()
    {
        return script;
    }

    public final String getTarget()
    {
        return target;
    }

}
