package org.codehaus.plexus.xsiter.command;

/**
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public class AbstractCommand
{

    /**
     * Label name or identifier for the command.
     */
    protected String label;

    /**
     * Command Constructor.
     * @param label name or identifier for the command.
     */
    public AbstractCommand( String label )
    {
        this.label = label;
    }

    /**
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

}