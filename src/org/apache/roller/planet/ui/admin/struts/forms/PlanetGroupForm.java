package org.apache.roller.planet.ui.admin.struts.forms;

import org.apache.roller.RollerException;
import java.util.Locale;

/**
 * Generated by XDoclet/ejbdoclet/strutsform. This class can be further processed with XDoclet/webdoclet/strutsconfigxml and XDoclet/webdoclet/strutsvalidationxml.
 *
 * @struts.form name="planetGroupForm"
 */
public class PlanetGroupForm
    extends    org.apache.struts.action.ActionForm
    implements java.io.Serializable
{
    protected java.lang.String id;
    protected java.util.Set subscriptions;
    protected java.lang.String categoryRestriction;
    protected java.lang.String description;
    protected java.lang.String handle;
    protected int maxFeedEntries;
    protected int maxPageEntries;
    protected java.lang.String title;
    protected java.lang.String[] categoryRestrictionAsArray;

    /** Default empty constructor. */
    public PlanetGroupForm() {}

    /** Constructor that takes the data object as argument. */
    public PlanetGroupForm(org.apache.roller.planet.pojos.PlanetGroupData dataHolder, java.util.Locale locale) throws RollerException
    {
       copyFrom(dataHolder, locale);
    }

    public java.lang.String getId()
    {
        return this.id;
    }

   /** 
    */
    public void setId( java.lang.String id )
    {
        this.id = id;
    }

    public java.util.Set getSubscriptions()
    {
        return this.subscriptions;
    }

   /** 
    */
    public void setSubscriptions( java.util.Set subscriptions )
    {
        this.subscriptions = subscriptions;
    }

    public java.lang.String getCategoryRestriction()
    {
        return this.categoryRestriction;
    }

   /** 
    */
    public void setCategoryRestriction( java.lang.String categoryRestriction )
    {
        this.categoryRestriction = categoryRestriction;
    }

    public java.lang.String getDescription()
    {
        return this.description;
    }

   /** 
    */
    public void setDescription( java.lang.String description )
    {
        this.description = description;
    }

    public java.lang.String getHandle()
    {
        return this.handle;
    }

   /** 
    */
    public void setHandle( java.lang.String handle )
    {
        this.handle = handle;
    }

    public int getMaxFeedEntries()
    {
        return this.maxFeedEntries;
    }

   /** 
    */
    public void setMaxFeedEntries( int maxFeedEntries )
    {
        this.maxFeedEntries = maxFeedEntries;
    }

    public int getMaxPageEntries()
    {
        return this.maxPageEntries;
    }

   /** 
    */
    public void setMaxPageEntries( int maxPageEntries )
    {
        this.maxPageEntries = maxPageEntries;
    }

    public java.lang.String getTitle()
    {
        return this.title;
    }

   /** 
    */
    public void setTitle( java.lang.String title )
    {
        this.title = title;
    }

    public java.lang.String[] getCategoryRestrictionAsArray()
    {
        return this.categoryRestrictionAsArray;
    }

   /** 
    */
    public void setCategoryRestrictionAsArray( java.lang.String[] categoryRestrictionAsArray )
    {
        this.categoryRestrictionAsArray = categoryRestrictionAsArray;
    }

    /**
     * Copy values from this form bean to the specified data object.
     * Only copies primitive types (Boolean, boolean, String, Integer, int, Timestamp, Date)
     */
    public void copyTo(org.apache.roller.planet.pojos.PlanetGroupData dataHolder, Locale locale) throws RollerException
    {

        dataHolder.setId(this.id);

        dataHolder.setCategoryRestriction(this.categoryRestriction);

        dataHolder.setDescription(this.description);

        dataHolder.setHandle(this.handle);

        dataHolder.setMaxFeedEntries(this.maxFeedEntries);

        dataHolder.setMaxPageEntries(this.maxPageEntries);

        dataHolder.setTitle(this.title);

    }

    /**
     * Copy values from specified data object to this form bean.
     * Includes all types.
     */
    public void copyFrom(org.apache.roller.planet.pojos.PlanetGroupData dataHolder, Locale locale) throws RollerException
    {

        this.id = dataHolder.getId();

        this.categoryRestriction = dataHolder.getCategoryRestriction();

        this.description = dataHolder.getDescription();

        this.handle = dataHolder.getHandle();

        this.maxFeedEntries = dataHolder.getMaxFeedEntries();

        this.maxPageEntries = dataHolder.getMaxPageEntries();

        this.title = dataHolder.getTitle();

    }

    public void doReset(
    	org.apache.struts.action.ActionMapping mapping, 
    	javax.servlet.ServletRequest request)
    {

        this.id = null;

        this.categoryRestriction = null;

        this.description = null;

        this.handle = null;

        this.maxFeedEntries = 0;

        this.maxPageEntries = 0;

        this.title = null;

    }
    public void reset(
    	org.apache.struts.action.ActionMapping mapping, 
    	javax.servlet.ServletRequest request)
    {
        doReset(mapping, request);
    }
    public void reset(
    	org.apache.struts.action.ActionMapping mapping, 
    	javax.servlet.http.HttpServletRequest request)
    {
        doReset(mapping, request);
    }
}
