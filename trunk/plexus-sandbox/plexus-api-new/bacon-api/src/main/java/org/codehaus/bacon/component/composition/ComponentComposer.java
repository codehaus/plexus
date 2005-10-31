package org.codehaus.bacon.component.composition;

import org.codehaus.bacon.Container;
import org.codehaus.bacon.session.SessionKey;

public interface ComponentComposer
{
    
    void compose( Object instance, Container container, SessionKey sessionKey )
        throws ComponentCompositionException;

}
