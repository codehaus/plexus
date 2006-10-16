package org.codehaus.plexus.prompter.console;

import java.io.IOException;

public interface ConsoleWriter
{
    
    String ROLE = ConsoleWriter.class.getName();
    
    void write( String message ) throws IOException;

    void writeLine( String message ) throws IOException;

}
