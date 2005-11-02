
package org.roller.pojos;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.roller.RollerException;
import org.roller.config.RollerConfig;
import org.roller.model.Roller;
import org.roller.model.RollerFactory;
import org.roller.model.UserManager;
import org.roller.util.PojoUtil;
import org.roller.util.Utilities;


/**
 * User bean.
 * @author David M Johnson
 *
 * @ejb:bean name="UserData"
 * @struts.form include-all="true"
 * @hibernate.class lazy="false" table="rolleruser"  
 */
public class UserData
   extends org.roller.pojos.PersistentObject
   implements java.io.Serializable
{
    public static final UserData SYSTEM_USER = new UserData(
        "n/a","systemuser","n/a","systemuser","n/a", 
        "en_US_WIN", "America/Los_Angeles", new Date(), Boolean.TRUE);
       
    public static final UserData ANONYMOUS_USER = new UserData(
        "n/a","anonymoususer","n/a","anonymoususer","n/a", 
        "en_US_WIN", "America/Los_Angeles", new Date(), Boolean.TRUE);
   
   static final long serialVersionUID = -6354583200913127874L;

   protected String  id;
   protected String  userName;
   protected String  password;
   protected String  fullName;
   protected String  emailAddress;
   protected Date    dateCreated;
   protected String  locale;
   protected String  timeZone;
   protected Boolean enabled = Boolean.TRUE;
   
   private Set roles = new TreeSet();
   private List permissions = new ArrayList();

   public UserData()
   {
   }

	public UserData( String id, String userName,
                     String password, String fullName,
                     String emailAddress, 
                     String locale, String timeZone,
                     Date dateCreated,
                     Boolean isEnabled)
	{
         this.id = id;
         this.userName = userName;
         this.password = password;
         this.fullName = fullName;
         this.emailAddress = emailAddress;
         this.dateCreated = (Date)dateCreated.clone();
         this.locale = locale;
         this.timeZone = timeZone;
         this.enabled = enabled;
	}

	public UserData( UserData otherData )
	{
		this.id = otherData.id;
		this.userName = otherData.userName;
		this.password = otherData.password;
		this.fullName = otherData.fullName;
		this.emailAddress = otherData.emailAddress;
		this.dateCreated = (Date)otherData.dateCreated.clone();

	}

    /** 
     * @hibernate.bag lazy="true" inverse="true" cascade="delete" 
     * @hibernate.collection-key column="user_id"
     * @hibernate.collection-one-to-many 
     *    class="org.roller.pojos.PermissionsData"
     */
    public List getPermissions() 
    {
        return permissions;
    }
    public void setPermissions(List perms)
    {
        permissions = perms;
    }

    /**
     * @ejb:persistent-field
     * @hibernate.property column="isenabled" non-null="true" unique="false"
     */
    public Boolean getEnabled()
    {
        return this.enabled;
    }
    
    /** @ejb:persistent-field */ 
    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }
    
    /** Id of the User.
     * Not remote since primary key may be extracted by other means.
     * 
     * @struts.validator type="required" msgkey="errors.required"
     * @ejb:persistent-field 
     * @hibernate.id column="id"
     *  generator-class="uuid.hex" unsaved-value="null"
     */
   public String getId()
   {
      return this.id;
   }

   /** @ejb:persistent-field */ 
   public void setId( String id )
   {
      this.id = id;
   }

   /** User name of the user.
     * @ejb:persistent-field 
     * @hibernate.property column="username" non-null="true" unique="true"
     */
   public String getUserName()
   {
      return this.userName;
   }
   /** @ejb:persistent-field */ 
   public void setUserName( String userName )
   {
      this.userName = userName;
   }

   /** 
    * Get password.
    * If password encryption is enabled, will return encrypted password.
    *
    * @ejb:persistent-field 
    * @hibernate.property column="passphrase" non-null="true"
    */
   public String getPassword()
   {
      return this.password;
   }
   /** 
    * Set password.
    * If password encryption is turned on, then pass in an encrypted password. 
    * @ejb:persistent-field 
    */ 
   public void setPassword( String password )
   {
      this.password = password;
   }

   /** 
    * Full name of the user.
    *
    * @roller.wrapPojoMethod type="simple"
    * @ejb:persistent-field
    * @hibernate.property column="fullname" non-null="true" unique="true"
    */
   public String getFullName()
   {
      return this.fullName;
   }
   /** @ejb:persistent-field */ 
   public void setFullName( String fullName )
   {
      this.fullName = fullName;
   }

   /**
    * E-mail address of the user.
    *
    * @roller.wrapPojoMethod type="simple"
    * @ejb:persistent-field
    * @hibernate.property column="emailaddress" non-null="true" unique="true"
    */
   public String getEmailAddress()
   {
      return this.emailAddress;
   }
   /** @ejb:persistent-field */ 
   public void setEmailAddress( String emailAddress )
   {
      this.emailAddress = emailAddress;
   }

   /** 
    * @roller.wrapPojoMethod type="simple"
    * @ejb:persistent-field
    * @hibernate.property column="datecreated" non-null="true" unique="false"
    */
   public Date getDateCreated()
   {
       if (dateCreated == null) 
       {
           return null;
       }
       else 
       {
           return (Date)dateCreated.clone();
       }
   }
   /** @ejb:persistent-field */ 
   public void setDateCreated(final Date date)
   {
	   if (date != null) 
	   {
	   	   dateCreated = (Date)date.clone();
	   }
	   else
	   {
	       dateCreated = null;
	   }
   }

   /**
    * Locale of the user.
    * @ejb:persistent-field
    * @hibernate.property column="locale" non-null="true" unique="false"
    */
   public String getLocale()
   {
       return this.locale;
   }

   /** @ejb:persistent-field */
   public void setLocale(String locale)
   {
       this.locale = locale;
   }

   /**
    * Timezone of the user.
    * @ejb:persistent-field
    * @hibernate.property column="timeZone" non-null="true" unique="false"
    */
   public String getTimeZone()
   {
       return this.timeZone;
   }

   /** @ejb:persistent-field */
   public void setTimeZone(String timeZone)
   {
       this.timeZone = timeZone;
   }

   //------------------------------------------------------------------- citizenship
   public String toString()
   {
		StringBuffer str = new StringBuffer("{");

		str.append("id=" + id + " ");
		str.append("userName=" + userName + " ");
		str.append("password=" + password + " ");
		str.append("fullName=" + fullName + " ");
		str.append("emailAddress=" + emailAddress + " ");
		str.append("dateCreated=" + dateCreated + " ");
		str.append('}');

		return(str.toString());
   }

   public boolean equals( Object pOther )
   {
       if (pOther instanceof UserData)
       {
           UserData lTest = (UserData) pOther;
           boolean lEquals = true;
           lEquals = PojoUtil.equals(lEquals, this.getId(), lTest.getId());
           lEquals = PojoUtil.equals(lEquals, this.getUserName(), lTest.getUserName());
           lEquals = PojoUtil.equals(lEquals, this.getPassword(), lTest.getPassword());
           lEquals = PojoUtil.equals(lEquals, this.getFullName(), lTest.getFullName());
           lEquals = PojoUtil.equals(lEquals, this.getEmailAddress(), lTest.getEmailAddress());
           return lEquals;
       }
       else
       {
           return false;
       }
    }
   
   /*public boolean equals( Object pOther )
   {
      if( pOther instanceof UserData )
      {
         UserData lTest = (UserData) pOther;
         boolean lEquals = true;

         if( this.id == null )
         {
            lEquals = lEquals && ( lTest.id == null );
         }
         else
         {
            lEquals = lEquals && this.id.equals( lTest.id );
         }
         if( this.userName == null )
         {
            lEquals = lEquals && ( lTest.userName == null );
         }
         else
         {
            lEquals = lEquals && this.userName.equals( lTest.userName );
         }
         if( this.password == null )
         {
            lEquals = lEquals && ( lTest.password == null );
         }
         else
         {
            lEquals = lEquals && this.password.equals( lTest.password );
         }
         if( this.fullName == null )
         {
            lEquals = lEquals && ( lTest.fullName == null );
         }
         else
         {
            lEquals = lEquals && this.fullName.equals( lTest.fullName );
         }
         if( this.emailAddress == null )
         {
            lEquals = lEquals && ( lTest.emailAddress == null );
         }
         else
         {
            lEquals = lEquals && this.emailAddress.equals( lTest.emailAddress );
         }
         
		if( this.dateCreated == null )
		{
		   lEquals = lEquals && ( lTest.dateCreated == null );
		}
		else
		{
		   lEquals = lEquals && datesEquivalent(this.dateCreated, lTest.dateCreated);
		}

        return lEquals;
      }
      else
      {
         return false;
      }
   }*/
   
    private boolean datesEquivalent(Date d1, Date d2)
    {
        boolean equiv = true;
        equiv = equiv && d1.getHours() == d1.getHours();
        equiv = equiv && d1.getMinutes() == d1.getMinutes();
        equiv = equiv && d1.getSeconds() == d1.getSeconds();
        equiv = equiv && d1.getMonth() == d1.getMonth();
        equiv = equiv && d1.getDay() == d1.getDay();
        equiv = equiv && d1.getYear() == d1.getYear();
        return equiv;
    }

   public int hashCode()
   {
      int result = 17;
      result = 37*result + ((this.id != null) ? this.id.hashCode() : 0);
      result = 37*result + ((this.userName != null) ? this.userName.hashCode() : 0);
      result = 37*result + ((this.password != null) ? this.password.hashCode() : 0);
      result = 37*result + ((this.fullName != null) ? this.fullName.hashCode() : 0);
      result = 37*result + ((this.emailAddress != null) ? this.emailAddress.hashCode() : 0);
      result = 37*result + ((this.dateCreated != null) ? this.dateCreated.hashCode() : 0);
      return result;
      }

   /**
	* Setter is needed in RollerImpl.storePersistentObject()
    */
   public void setData( org.roller.pojos.PersistentObject otherData )
   {
      this.id = ((UserData)otherData).id;
      this.userName = ((UserData)otherData).userName;
      this.password = ((UserData)otherData).password;
      this.fullName = ((UserData)otherData).fullName;
      this.emailAddress = ((UserData)otherData).emailAddress;
      this.locale = ((UserData)otherData).locale;
      this.timeZone = ((UserData)otherData).timeZone;
      this.dateCreated = ((UserData)otherData).dateCreated;
   }

    /** 
     * Removing a user also removes his/her website.
     * @see org.roller.pojos.PersistentObject#remove()
     */
    public void remove() throws RollerException
    {
        UserManager uMgr = RollerFactory.getRoller().getUserManager();   
        
        // remove user roles
        //Iterator roles = uMgr.getUserRoles(this).iterator();
        //while (roles.hasNext()) 
        //{
            //((RoleData)roles.next()).remove();
        //}
        super.remove();
    }
    
    /** 
     * Reset this user's password.
     * @param roller Roller instance to use for configuration information
     * @param new1 New password
     * @param new2 Confirm this matches new password
     * @author Dave Johnson
     */
    public void resetPassword(Roller roller, String new1, String new2) throws RollerException 
    {
        if (!new1.equals(new2))
        {
            throw new RollerException("newUser.error.mismatchedPasswords");
        }

        String encrypt = RollerConfig.getProperty("passwds.encryption.enabled");
        String algorithm = RollerConfig.getProperty("passwds.encryption.algorithm");
        if (new Boolean(encrypt).booleanValue()) 
        {
            password = Utilities.encodePassword(new1, algorithm);            
        }
        else
        {
            password = new1;
        }
    }
    
    /** 
     * @hibernate.set lazy="false" inverse="true" cascade="delete" 
     * @hibernate.collection-key column="userid"
     * @hibernate.collection-one-to-many class="org.roller.pojos.RoleData"
     */
    public Set getRoles() 
    {
        return roles;
    }
    
    public void setRoles(Set roles)
    {
        this.roles = roles;
    }
    

    /**
     * Returns true if user has role specified.
     * @param roleName Name of role
     * @return True if user has specified role.
     */
    public boolean hasRole(String roleName) 
    {
        Iterator iter = roles.iterator();
        while (iter.hasNext()) 
        {
            RoleData role = (RoleData) iter.next();
            if (role.getRole().equals(roleName)) 
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Revokes specified role from user.
     * @param roleName Name of role to be revoked.
     */
    public void revokeRole(String roleName) throws RollerException
    {
        RoleData removeme = null; 
        Iterator iter = roles.iterator();
        while (iter.hasNext()) 
        {
            RoleData role = (RoleData) iter.next();
            if (role.getRole().equals(roleName)) 
            {
                removeme = role;
            }
        }
        if (removeme != null)
        {
            roles.remove(removeme);
            RollerFactory.getRoller().getUserManager().removeRole(removeme.getId());
        }
    }

    /**
     * Grant to user role specified by role name.
     * @param roleName Name of role to be granted.
     */
    public void grantRole(String roleName) throws RollerException 
    {
        if (!hasRole(roleName))
        {
            RoleData role = new RoleData(null, this, roleName);
            RollerFactory.getRoller().getUserManager().storeRole(role);
            roles.add(role);
        }
    }
}
