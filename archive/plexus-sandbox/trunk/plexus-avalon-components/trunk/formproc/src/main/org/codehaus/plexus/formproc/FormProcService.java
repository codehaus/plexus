package org.codehaus.plexus.formproc;

import org.formproc.FormManager;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 5, 2003
 */
public interface FormProcService
{
    final public static String ROLE = FormProcService.class.getName();
    
    FormManager getFormManager();
}
