package org.codehaus.plexus.component.factory.groovy;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

public interface Hello
    extends Initializable, Startable, Disposable
{
    static String ROLE = Hello.class.getName();

    void hello();
}
