package org.codehaus.plexus.security.authorisation.lifecycle.phase;

import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.security.authorisation.AuthorisationService;
import org.codehaus.plexus.security.authorisation.MethodInterceptor;
import org.codehaus.plexus.security.authorisation.lifecycle.SecureComponent;


/**
 * Sets the MethodInterceptor for components which implement the SecureComponent interface. Uses
 * the ServiceManager contained in the entities map to lookup the AuthorisationService  which is inturn
 * asked for the MethodInterceptor. This phase will fail silently if no AuthorisationService or
 * MethodInterceptor exists. If the lookup for the AuthorisationService fails then no further 
 * lookups will be performed.
 * 
  * <p>Created on 20/08/2003</p>
  *
  * @todo how to return the AuthorisationService?? current implementation there will always be one
 * non-released connection to the AuthorisationService 
 * 
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
 */
public class SecureComponentPhase extends AbstractPhase
{
	/** Flag to turn off lookups for the AuthorisationService and MethodInterceptor
	 * if these don't exist.
	 * */
	private boolean lookupPerformed = false;
    private MethodInterceptor interceptor;

    public void execute(Object object, ComponentManager manager) throws Exception
    {
        if (object instanceof SecureComponent)
        {
            if (interceptor == null && lookupPerformed == false)
            {
                ServiceManager serviceManager =
                    (ServiceManager) manager.getLifecycleHandler().getEntities().get(
                        "component.manager");
                if (serviceManager == null)
                {
                    final String message = "ServiceManager is null";
                    throw new IllegalArgumentException(message);
                }

                if (serviceManager.hasService(AuthorisationService.ROLE))
                {
                    AuthorisationService authorisation =
                        (AuthorisationService) serviceManager.lookup(AuthorisationService.ROLE);
                    interceptor = authorisation.getMethodInterceptor();
                }
                lookupPerformed = true;
            }
            ((SecureComponent) object).setMethodInterceptor(interceptor);
        }
    }
}
