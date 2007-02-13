package org.codehaus.plexus.graph.exception;

/*
 * Licensed to the Codehaus Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * GraphException This is the superclass of all exceptions that can be thrown.
 */

import java.io.PrintStream;
import java.io.PrintWriter;

public class GraphException
    extends RuntimeException
{
    private Throwable cause = null;

    /** Constructor for the GraphException object */
    public GraphException()
    {
        super();
    }

    /**
     * Constructor for the GraphException object
     *
     * @param msg
     */
    public GraphException( String msg )
    {
        super( msg );
    }

    /**
     * Constructor for the GraphException object
     *
     * @param cause
     */
    public GraphException( Throwable cause )
    {
        super( cause.getMessage() );
        this.cause = cause;
    }

    public Throwable getCause()
    {
        return cause;
    }

    public void printStackTrace()
    {
        cause.printStackTrace();
    }

    public void printStackTrace( PrintStream s )
    {
        cause.printStackTrace( s );
    }

    public void printStackTrace( PrintWriter w )
    {
        cause.printStackTrace( w );
    }

}
