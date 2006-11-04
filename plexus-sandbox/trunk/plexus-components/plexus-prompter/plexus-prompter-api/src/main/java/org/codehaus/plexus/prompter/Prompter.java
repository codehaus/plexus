package org.codehaus.plexus.prompter;

import java.util.List;

public interface Prompter
{
    
    String prompt( String message )
        throws PromptException;
    
    String prompt( String message, String defaultReply )
        throws PromptException;
    
    String prompt( String message, List possibleValues )
        throws PromptException;

    String prompt( String message, List possibleValues, String defaultReply )
        throws PromptException;
    
    char[] promptForPassword( String message )
        throws PromptException;

}
