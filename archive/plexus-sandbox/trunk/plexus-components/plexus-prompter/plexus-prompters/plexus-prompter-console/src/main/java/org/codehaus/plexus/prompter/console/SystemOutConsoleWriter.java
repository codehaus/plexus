package org.codehaus.plexus.prompter.console;

public class SystemOutConsoleWriter
    implements ConsoleWriter
{

    public void write( String message )
    {
        System.out.print( message );
    }

    public void writeLine( String message )
    {
        System.out.println( message );
    }

}
