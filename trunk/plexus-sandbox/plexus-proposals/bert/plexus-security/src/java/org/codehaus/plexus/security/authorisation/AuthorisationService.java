package org.codehaus.plexus.security.authorisation;

/**
  * AUthorises access to components based on the ACL
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface AuthorisationService
{
    public static final String ROLE = AuthorisationService.class.getName().toString();

    /**
    * Return the method interceptor used to intercept method calls to components, or
    * null if this feature is not currently supported.
    * 
    * @return MethodInterceptor the method interceptor
    */
    public MethodInterceptor getMethodInterceptor();

}
