package org.codehaus.plexus.security.ui.web.action;

public interface CancellableAction 
{
	public static final String CANCEL = "cancel";
	
	/**
	 * Returns the cancel result.
	 * 
	 * A basic implementation would simply be to return CANCEL.
	 * @return
	 */
	public String cancel();
}
