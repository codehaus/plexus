package org.codehaus.plexus.command;

/**
 * <copyright>
 *
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.codehaus.org/legal/cpl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * plugins/org.codehaus.emf.common/src/org/codehaus/emf/common/command/CommandWrapper.java, emf.common, org.codehaus.dev, 20030620_1105VL
 * @version 1.6 6/20/03
 */
import java.util.Collection;
import java.util.Collections;

/**
 * A command that wraps another command.
 * All the {@link Command} methods are delegated to the wrapped command.
 *
 * <p>
 * There are two typical usage patterns.
 * One typical use for this command is to modify the behaviour of a command that you can't subclass, i.e., a decorator pattern:
 *<pre>
 *   Command decoratedCommand =
 *     new CommandWrapper(someOtherCommand)
 *     {
 *       public void execute()
 *       {
 *         doSomethingBeforeExecution();
 *         super.execute();
 *         doSomethingAfterExecution();
 *       }
 *       public Collection getResult()
 *       {
 *         return someOtherResult();
 *       }
 *     };
 *</pre>
 * The other typical use is to act as a proxy for a command who's creation is delayed:
 *<pre>
 *   Command proxyCommand =
 *     new CommandWrapper()
 *     {
 *       public Command createCommand()
 *       {
 *         return createACommandSomehow();
 *       }
 *     };
 *</pre>
 *
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 */
public class CommandWrapper extends AbstractCommand
{
    /**
     * The command for which this is a proxy or decorator.
     */
    protected Command command;

    /**
     * Creates a decorator instance for the given command.
     * @param command the command to wrap.
     */
    public CommandWrapper( Command command )
    {
        super( command.getLabel(), command.getDescription() );
        this.command = command;
    }

    /**
     * Creates a decorator instance with the given label for the given command.
     * @param label the label of the wrapper
     * @param command the command to wrap.
     */
    protected CommandWrapper( String label, Command command )
    {
        super( label, command.getDescription() );
        this.command = command;
    }

    /**
     * Creates a decorator instance with the given label and description for the given command.
     * @param label the label of the wrapper
     * @param description the description of the wrapper
     * @param command the command to wrap.
     */
    public CommandWrapper( String label, String description, Command command )
    {
        super( label, description );
        this.command = command;
    }

    /**
     * Creates a commandless proxy instance.
     * The wrapped command will be created by a {@link #createCommand} callback.
     * Since a proxy command like this is pointless unless you override some method, this constructor is protected.
     */
    protected CommandWrapper()
    {
        super();
    }

    /**
     * Creates a commandless proxy instance, with the given label.
     * The command will be created by a {@link #createCommand} callback.
     * Since a proxy command like this is pointless unless you override some method, this constructor is protected.
     * @param label the label of the wrapper
     */
    protected CommandWrapper( String label )
    {
        super( label );
    }

    /**
     * Creates a commandless proxy instance, with the given label and description.
     * The command will be created by a {@link #createCommand} callback.
     * Since a proxy command like this is pointless unless you override some method, this constructor is protected.
     * @param label the label of the wrapper
     * @param description the description of the wrapper
     */
    protected CommandWrapper( String label, String description )
    {
        super( label, description );
    }

    /**
     * Returns the command for which this is a proxy or decorator.
     * This may be <code>null</code> before {@link #createCommand} is called.
     * @return the command for which this is a proxy or decorator.
     */
    public Command getCommand()
    {
        return command;
    }

    /**
     * Create the command being proxied.
     * This implementation just return <code>null</code>.
     * It is called by {@link #prepare}.
     * @return the command being proxied.
     */
    protected Command createCommand()
    {
        return null;
    }

    /**
     * Returns whether the command can execute.
     * This implementation creates the command being proxied using {@link #createCommand},
     * if the command wasn't given in the constructor.
     * @return whether the command can execute.
     */
    protected boolean prepare()
    {
        if ( command == null )
        {
            command = createCommand();
        }

        boolean result = command.canExecute();
        return result;
    }

    /**
     * Delegates to the execute method of the command.
     */
    public void execute()
    {
        if ( command != null )
        {
            command.execute();
        }
    }

    /**
     * Delegates to the canUndo method of the command.
     */
    public boolean canUndo()
    {
        return command == null || command.canUndo();
    }

    /**
     * Delegates to the undo method of the command.
     */
    public void undo()
    {
        if ( command != null )
        {
            command.undo();
        }
    }

    /**
     * Delegates to the redo method of the command.
     */
    public void redo()
    {
        if ( command != null )
        {
            command.redo();
        }
    }

    /**
     * Delegates to the getResult method of the command.
     * @return the result.
     */
    public Collection getResult()
    {
        return
            command == null ?
            Collections.EMPTY_LIST :
            command.getResult();
    }

    /**
     * Delegates to the getAffectedObjects method of the command.
     * @return the result.
     */
    public Collection getAffectedObjects()
    {
        return
            command == null ?
            Collections.EMPTY_LIST :
            command.getAffectedObjects();
    }

    /**
     * Delegates to the getLabel method of the command.
     * @return the label.
     */
    public String getLabel()
    {
        return
            label == null ?
            command == null ?
            "CommandWrapper label" :
            command.getLabel() :
            label;
    }

    /**
     * Delegates to the getDescription method of the command.
     * @return the description.
     */
    public String getDescription()
    {
        return
            description == null ?
            command == null ?
            "CommandWrapper description" :
            command.getDescription() :
            description;
    }

    /**
     * Delegates to the dispose method of the command.
     */
    public void dispose()
    {
        if ( command != null )
        {
            command.dispose();
        }
    }

    /*
     * Javadoc copied from base class.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer( super.toString() );
        result.append( " (command: " + command + ")" );

        return result.toString();
    }
}
