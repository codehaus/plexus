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
 * plugins/org.codehaus.emf.common/src/org/codehaus/emf/common/command/IdentityCommand.java, emf.common, org.codehaus.dev, 20030620_1105VL
 * @version 1.5 6/20/03
 */

import java.util.Collection;
import java.util.Collections;

/**
 * A command that always produces the same result.
 *
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 */
public class IdentityCommand extends AbstractCommand
{
    /**
     * An empty instance of this object.
     */
    public static final IdentityCommand INSTANCE = new IdentityCommand();

    /**
     * Keeps track of the result returned from {@link #getResult}.
     */
    protected Collection result;

    {
        // This ensures that these useless state variables at least reflect the right value.
        //
        isPrepared = true;
        isExecutable = true;
    }

    /**
     * Creates an empty instance.
     */
    public IdentityCommand()
    {
        super();
        this.result = Collections.EMPTY_LIST;
    }

    /**
     * Creates an instance with a result collection containing the given result object.
     * @param result the one object in the result collection.
     */
    public IdentityCommand( Object result )
    {
        super();
        this.result = Collections.singleton( result );
    }

    /**
     * Creates an instance with the given result collection.
     * @param result the result collection.
     */
    public IdentityCommand( Collection result )
    {
        super();
        this.result = result;
    }

    /**
     * Creates an instance with the given label.
     * @param label the label.
     */
    public IdentityCommand( String label )
    {
        this.label = label;
        this.result = Collections.EMPTY_LIST;
    }

    /**
     * Creates an instance with the given label and a result collection containing the given result object.
     * @param label the label.
     * @param result the one object in the result collection.
     */
    public IdentityCommand( String label, Object result )
    {
        this.label = label;
        this.result = Collections.singleton( result );
    }

    /**
     * Creates an instance with the given label the result collection.
     * @param label the label.
     * @param result the result collection.
     */
    public IdentityCommand( String label, Collection result )
    {
        this.label = label;
        this.result = result;
    }

    /**
     * Creates an instance with the given label and description.
     * @param label the label.
     * @param description the description.
     */
    public IdentityCommand( String label, String description )
    {
        this.label = label;
        this.description = description;
        this.result = Collections.EMPTY_LIST;
    }

    /**
     * Creates an instance with the given label, description, and a result collection containing the given result object.
     * @param label the label.
     * @param description the description.
     * @param result the one object in the result collection.
     */
    public IdentityCommand( String label, String description, Object result )
    {
        this.label = label;
        this.description = description;
        this.result = Collections.singleton( result );
    }

    /**
     * Creates an instance with the given label, description, result collection.
     * @param label the label.
     * @param description the description.
     * @param result the result collection.
     */
    public IdentityCommand( String label, String description, Collection result )
    {
        this.label = label;
        this.description = description;
        this.result = result;
    }

    /**
     * Returns <code>true</code>.
     * @return <code>true</code>.
     */
    public boolean canExecute()
    {
        return true;
    }

    /**
     * Do nothing.
     */
    public void execute()
    {
    }

    /**
     * Do nothing.
     */
    public void undo()
    {
    }

    /**
     * Do nothing.
     */
    public void redo()
    {
    }

    /*
     * Javadoc copied from base class.
     */
    public String getLabel()
    {
        return label == null ? "_UI_IdentityCommand_label" : label;
    }

    /*
     * Javadoc copied from base class.
     */
    public String getDescription()
    {
        return description == null ? "_UI_IdentityCommand_description" : description;
    }

    /**
     * Return the identity result.
     * @return the identity result.
     */
    public Collection getResult()
    {
        return result;
    }
}
