package org.codehaus.jasf.summit.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.summit.activity.DefaultActionEventService;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.exception.SummitRuntimeException;
import org.codehaus.plexus.summit.rundata.RunData;

import org.codehaus.jasf.ResourceController;
import org.codehaus.jasf.resources.ClassMethodResource;
import org.codehaus.jasf.summit.SecureRunData;

/**
 * A version of ActionEvent which checks to make sure the user is authorized
 * to access the method on the action using the ResourceController for the 
 * ClassResource.
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 1, 2003
 */
public class SecureActionEvent
    extends DefaultActionEventService
    implements Serviceable
{
    private ServiceManager manager;

    /**
     * @see org.codehaus.plexus.summit.activity.ActionEvent#perform(org.codehaus.plexus.summit.rundata.RunData)
     */
    public void perform(RunData data) throws Exception
    {
        String action = data.getParameters().getString("action");
        
        if ( action != null )
        {
            String methodName = null;
            try
            {
                methodName = getMethodName( data, DEFAULT_METHOD );
                Class actionClass = getClass( action );
                Method method = getMethod( actionClass, data.getClass(), methodName, DEFAULT_METHOD );
                
                if ( action.equals("LoginUser") ||
                     isAuthorized( (SecureRunData) data, method ) )
                {
                    // The arguments to pass to the method to execute.
                    Object[] args = new Object[1];
                    args[0] = data;

                    method.invoke( actionClass.newInstance(), args );                         
                }
                else
                {
                    ((SecureRunData) data).setMessage( 
                        "You do not have permission to perform that action." );
                }
            }
            catch ( ClassNotFoundException e )
            {
                getLogger().debug( "Could not find the action.", e );
            } 
            catch (InvocationTargetException ite)
            {
                // i have not seen this exception, in stacktraces generated
                // while doing my own testing on jdk1.3.1 and earlier.  But
                // see it increasingly from stacktraces reported by others.
                // Its printStackTrace method should do The Right Thing, but
                // I suspect some implementation is not.
                // Unwrap it here, so that the original cause does not get lost.
                Throwable t = ite.getTargetException();
                if (t instanceof Exception) 
                {
                    throw (Exception)t;
                }
                else if (t instanceof java.lang.Error) 
                {
                    throw (java.lang.Error)t;
                }
                else 
                {
                    // this should not happen, but something could throw
                    // an instance of Throwable
                    throw new SummitRuntimeException("",t);
                }
            }
        }
    }

    /**
     * @param data
     * @param method
     * @return boolean
     */
    protected boolean isAuthorized(SecureRunData data, Method method)
        throws SummitException
    {
        ServiceSelector security;
        boolean isAuthorized;
        
        if ( data.getUser() == null ||
             !data.getUser().isLoggedIn() )
            return false;
            
        try
        {
            security = (ServiceSelector) manager.lookup(ResourceController.SELECTOR_ROLE);
        
            ResourceController controller = 
                (ResourceController) security.select( ClassMethodResource.RESOURCE_TYPE );
            
            isAuthorized = controller.isAuthorized( data.getUser(), method );
            
            manager.release( controller );
            
            return isAuthorized;
        }
        catch (ServiceException e)
        {
            throw new SummitException( "Could not find the SecurityService!", 
                                       e );
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.manager = manager;
    }
}
