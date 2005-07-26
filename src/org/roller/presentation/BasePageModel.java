/*
 * Created on Mar 10, 2004
 */
package org.roller.presentation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;
import org.roller.RollerException;
import org.roller.presentation.util.StrutsUtil;

/**
 * Re-usable base for page models.
 * @author David M Johnson
 */
public class BasePageModel
{
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private ActionMapping mapping = null;
    
    public BasePageModel(
            HttpServletRequest request,
            HttpServletResponse response,
            ActionMapping mapping)
    {
        this.request = request;
        this.response = response;
        this.mapping = mapping;
        request.setAttribute("locales", StrutsUtil.getLocaleBeans());        
        request.setAttribute("timezones", StrutsUtil.getTimeZoneBeans());        
    }

    public String getBaseURL()
    {
		return request.getContextPath();
	}

    public String getShortDateFormat()
    {
        DateFormat sdf = DateFormat.getDateInstance(
                DateFormat.SHORT, request.getLocale());
        if (sdf instanceof SimpleDateFormat)
        {
            return ((SimpleDateFormat)sdf).toLocalizedPattern();
        }
        return "yyyy/MM/dd";
    }

    public String getMediumDateFormat()
    {
        DateFormat sdf = DateFormat.getDateInstance(
                DateFormat.MEDIUM, request.getLocale());
        if (sdf instanceof SimpleDateFormat)
        {
            return ((SimpleDateFormat)sdf).toLocalizedPattern();
        }
        return "MMM dd, yyyy";
    }

    /**
     * @return Returns the mapping.
     */
    public ActionMapping getMapping() 
    {
        return mapping;
    }
    
    /**
     * @return Returns the request.
     */
    public HttpServletRequest getRequest() 
    {
        return request;
    }
    
    /**
     * @return Returns the response.
     */
    public HttpServletResponse getResponse() 
    {
        return response;
    }
    
    public boolean getIsAdmin() throws RollerException
    {
        return RollerSession.getRollerSession(request).isAdminUser(); 
    }

    public List getLocales()
    {
        return StrutsUtil.getLocaleBeans();
    }

    public List getTimeZones()
    {
        return StrutsUtil.getTimeZoneBeans();
    }
}
