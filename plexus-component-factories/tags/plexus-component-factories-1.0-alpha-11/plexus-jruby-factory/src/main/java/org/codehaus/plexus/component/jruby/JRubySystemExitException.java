package org.codehaus.plexus.component.jruby;

import org.jruby.exceptions.JumpException;

public class JRubySystemExitException extends JumpException
{
    private int status;

    public JRubySystemExitException( int status )
    {
        this.status = status;
    }

    public int getStatus()
    {
        return this.status;
    }
}
