package org.codehaus.plexus.security.authorisation;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;


/**
  * Intercepts method calls on SecureComponents. 
  * 
  * @todo: make this work.
  * <p>Created on 18/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public aspect SecureComponentAspect implements SecureComponent
{
	private MethodInterceptor interceptor;

	
	/**
	 * This pointcut identifies the role we want to protect access to
	 */
	pointcut roleInterceptor(Object role):
	call(public * myInterfaces.*(..) );
   
	//#foreach( $c in $roleAsAWhole )
	//	|| call( public * ${c.getRole()}.*(..))
	//#end
	//;
	
   /**
	* This pointcut identifies the methods we want to protect access to
	*/
   pointcut methodSigInterceptor(Object role):
   call(public * myInterfaces.*(..) );
   
   //#foreach( $c in $methodSigOnlyComponents )
   //	|| call( public * ${c.getRole()}.*(..))
   //#end
   //;

	pointcut methodArgsInterceptor(Object role):
	call(public * myOtherInterfaces.*(..) );
	
	//#foreach( $c in $methodArgsComponents )
	 //	|| call( public * ${c.getRole()}.*(..))
	 //#end
	 //;

   /**
    * Before the method is executed. For those methods where we _don't_ need
    * the argument list.
    */
   before(Object component): methodSigInterceptor(component)
   {
		  //System.out.println("Invoking method on:" + );
		  //check(targetRole,methodName,parameterClasses)
		   interceptor.check(
		   		thisJoinPointStaticPart.getSignature().getDeclaringType().getName(),
				thisJoinPointStaticPart.getSignature().getName(),
				Class[] types = ((CodeSignature)thisJoinPoint.getSignature()).getParameterTypes()
		    	);
}
	/**
	* Before the method is executed. For those methods where we  _need_
	* the argument list.
	*/
   before(Object component): methodArgsInterceptor(component)
   { 
	//System.out.println("Invoking method on:" + );
	//check(targetRole,methodName,args)
	 interceptor.check(
		  thisJoinPointStaticPart.getSignature().getDeclaringType().getName(),
		  thisJoinPointStaticPart.getSignature().getName(),
		  Class[] types = ((CodeSignature)thisJoinPoint.getArgs()
		  );
	}

	/**
	* Before the method is executed. For the roles we _only_ check access to the role
	* as a whole
	*/
   before(Object component): methodArgsInterceptor(component)
   { 
	//System.out.println("Invoking method on:" + );
	//check(targetRole,methodName,args)
	 interceptor.check(
		  thisJoinPointStaticPart.getSignature().getDeclaringType().getName(),
		  );
	}
	
}
