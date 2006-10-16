package org.codehaus.plexus.prompter;

public class PromptException
    extends Exception
{

    public PromptException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public PromptException( String message )
    {
        super( message );
    }

}
