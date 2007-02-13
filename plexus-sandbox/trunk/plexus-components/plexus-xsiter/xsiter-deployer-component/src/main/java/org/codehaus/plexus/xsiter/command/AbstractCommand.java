package org.codehaus.plexus.xsiter.command;

/**
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public class AbstractCommand
{

    /**
     * Name of the deployment workspace descriptor.
     */
    protected static final String DESCRIPTOR_WORKSPACE_XML = "workspace.xml";

    /**
     * Label name or identifier for the command.
     */
    protected String label;

    /**
     * 
     */
    public AbstractCommand()
    {
        super();
    }

    /**
     * @param label the label to set
     */
    public void setLabel( String label )
    {
        this.label = label;
    }

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