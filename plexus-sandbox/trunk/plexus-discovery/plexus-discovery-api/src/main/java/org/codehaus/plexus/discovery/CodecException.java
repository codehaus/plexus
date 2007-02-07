/**
 * Copyright 2006 Aldrin Leal, aldrin at leal dot eng dot bee ar
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.codehaus.plexus.discovery;

/**
 * A Codec Exception
 *
 * @author Aldrin Leal
 */
public class CodecException
    extends RuntimeException
{
    /** serialVersionUID */
    private static final long serialVersionUID = 7544149157359063449L;

    /** Constructor */
    public CodecException()
    {
        super();
    }

    /**
     * Constructor
     *
     * @param message message
     */
    public CodecException( String message )
    {
        super( message );
    }

    /**
     * Constructor
     *
     * @param cause Originating Exception
     */
    public CodecException( Throwable cause )
    {
        super( cause );
    }

    /**
     * Constructor
     *
     * @param message message
     * @param cause   Originating Exception
     */
    public CodecException( String message,
                           Throwable cause )
    {
        super( message, cause );
    }
}
