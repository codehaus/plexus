package org.codehaus.plexus.formproc;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.formproc.Form;
import org.formproc.FormManager;
import org.formproc.FormResult;
import org.formproc.servlet.HttpForm;

/**
 * A template tool that provides easy access to FormProc.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 15, 2003
 */
public class FormTool
    implements RequestTool, Serviceable
{
    private static final String FORM_KEY = "form";

    private ServiceManager manager;

    private RunData data;

    public HttpForm getForm()
    {
        HttpForm form;
        
        if ( data.getSession().getAttribute( FORM_KEY ) != null )
        {
            form = (HttpForm) data.getSession().getAttribute( FORM_KEY );
        }
        else
        {
            form = new HttpForm();
            data.getSession().setAttribute( FORM_KEY, form ); 
        }
        return form;
    }

    public HttpForm getForm( String name, Object target ) throws Exception
    {
        HttpForm form;
        
        if ( data.getSession().getAttribute( FORM_KEY ) != null )
        {
            form = (HttpForm) data.getSession().getAttribute( FORM_KEY );
        }
        else
        {
            form = new HttpForm();    
            data.getSession().setAttribute( FORM_KEY, form );        
        }
        
        form.setName(name);
        form.setTarget(target);
        configure( form );

        return form;
    }
    
    /**
     * When processing a form, this method will retrieve the value from the
     * FormResult (via getOriginalValue()).  However, if there is no FormResult
     * yet (ie, a form that is modifying values on an object), it will return
     * the value that you specify.
     * 
     * @param name
     * @param value
     * @return
     */
    public Object getValue( String name, Object value )
    {
        ViewContext context =
            (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );
            
        if ( context.get( "results" ) != null )
        {
             FormResult result = (FormResult) context.get( "results" );
             return result.getOriginalValue( name );
        }
        
        return value;
    }
        
    public FormManager getFormManager() throws ServiceException
    {
        return ((FormProcService) manager.lookup(FormProcService.ROLE)).getFormManager();
    }
    
    public void configure( Form form ) throws Exception
    {
        getFormManager().configure( form );
    }
    
    /**
     * @see org.codehaus.plexus.summit.pull.RequestTool#setRunData(org.codehaus.plexus.summit.rundata.RunData)
     */
    public void setRunData(RunData data)
    {
        this.data = data;
    }

    /**
     * @see org.codehaus.plexus.summit.pull.RequestTool#refresh()
     */
    public void refresh()
    {
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.manager = manager;
    }
}
