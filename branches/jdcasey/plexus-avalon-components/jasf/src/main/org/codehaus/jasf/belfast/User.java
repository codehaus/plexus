package org.codehaus.jasf.belfast;

import java.util.List;

/**
 * A user class that contains basic security features.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 13, 2003
 */
public interface User
    extends org.codehaus.jasf.entities.web.User
{

    public List getRoles();

}
