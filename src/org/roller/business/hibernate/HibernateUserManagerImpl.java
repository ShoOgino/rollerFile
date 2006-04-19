/*
 * Created on Jun 16, 2004
 */
package org.roller.business.hibernate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.SimpleExpression;
import org.roller.RollerException;
import org.roller.config.RollerConfig;
import org.roller.model.AutoPingManager;
import org.roller.model.BookmarkManager;
import org.roller.model.PingTargetManager;
import org.roller.model.RollerFactory;
import org.roller.model.UserManager;
import org.roller.model.WeblogManager;
import org.roller.pojos.AutoPingData;
import org.roller.pojos.BookmarkData;
import org.roller.pojos.FolderData;
import org.roller.pojos.WeblogTemplate;
import org.roller.pojos.PermissionsData;
import org.roller.pojos.PingQueueEntryData;
import org.roller.pojos.PingTargetData;
import org.roller.pojos.RefererData;
import org.roller.pojos.UserData;
import org.roller.pojos.WeblogCategoryData;
import org.roller.pojos.WeblogEntryData;
import org.roller.pojos.WebsiteData;


/**
 * Hibernate implementation of the UserManager.
 */
public class HibernateUserManagerImpl implements UserManager {
    
    static final long serialVersionUID = -5128460637997081121L;
    
    private static Log log = LogFactory.getLog(HibernateUserManagerImpl.class);
    
    private HibernatePersistenceStrategy strategy = null;
    
    // cached mapping of weblogHandles -> weblogIds
    private Map weblogHandleToIdMap = new Hashtable();
    
    // cached mapping of userNames -> userIds
    private Map userNameToIdMap = new Hashtable();
    
    
    /**
     * @param strategy
     */
    public HibernateUserManagerImpl(HibernatePersistenceStrategy strat) {
        log.debug("Instantiating Hibernate User Manager");
        
        this.strategy = strat;
    }
    
    
    /**
     * @see org.roller.model.UserManager#storeWebsite(org.roller.pojos.WebsiteData)
     */
    public void saveWebsite(WebsiteData data) throws RollerException {
        this.strategy.store(data);
    }
    
    
    public void removeWebsite(WebsiteData weblog) throws RollerException {
        
        // remove contents first, then remove website
        this.removeWebsiteContents(weblog);
        this.strategy.remove(weblog);
        
        // remove entry from cache mapping
        this.weblogHandleToIdMap.remove(weblog.getHandle());
    }
    

    /**
     * convenience method for removing contents of a weblog.
     *
     * TODO BACKEND: use manager methods instead of queries here
     */
    private void removeWebsiteContents(WebsiteData website) 
            throws HibernateException, RollerException {
        
        Session session = this.strategy.getSession();
        
        BookmarkManager bmgr = RollerFactory.getRoller().getBookmarkManager();
        WeblogManager wmgr = RollerFactory.getRoller().getWeblogManager();
        
        // Remove the website's ping queue entries
        Criteria criteria = session.createCriteria(PingQueueEntryData.class);
        criteria.add(Expression.eq("website", website));
        List queueEntries = criteria.list();
        
        // Remove the website's auto ping configurations
        AutoPingManager autoPingMgr = RollerFactory.getRoller().getAutopingManager();
        List autopings = autoPingMgr.getAutoPingsByWebsite(website);
        Iterator it = autopings.iterator();
        while(it.hasNext()) {
            this.strategy.remove((AutoPingData) it.next());
        }
        
        // Remove the website's custom ping targets
        PingTargetManager pingTargetMgr = RollerFactory.getRoller().getPingTargetManager();
        List pingtargets = pingTargetMgr.getCustomPingTargets(website);
        it = pingtargets.iterator();
        while(it.hasNext()) {
            this.strategy.remove((PingTargetData) it.next());
        }
        
        // remove entries
        Criteria entryQuery = session.createCriteria(WeblogEntryData.class);
        entryQuery.add(Expression.eq("website", website));
        List entries = entryQuery.list();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            WeblogEntryData entry = (WeblogEntryData) iter.next();
            
            this.strategy.remove(entry);
        }
        
        // remove associated referers
        Criteria refererQuery = session.createCriteria(RefererData.class);
        refererQuery.add(Expression.eq("website", website));
        List referers = refererQuery.list();
        for (Iterator iter = referers.iterator(); iter.hasNext();) {
            RefererData referer = (RefererData) iter.next();
            this.strategy.remove(referer);
        }
        
                
        // remove associated pages
        Criteria pageQuery = session.createCriteria(WeblogTemplate.class);
        pageQuery.add(Expression.eq("website", website));
        List pages = pageQuery.list();
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            WeblogTemplate page = (WeblogTemplate) iter.next();
            this.strategy.remove(page);
        }
        
        // remove folders (including bookmarks)
        FolderData rootFolder = bmgr.getRootFolder(website);
        if (null != rootFolder) {
            this.strategy.remove(rootFolder);
            
            // Still cannot get all Bookmarks cleared!
            Iterator allFolders = bmgr.getAllFolders(website).iterator();
            while (allFolders.hasNext()) {
                FolderData aFolder = (FolderData)allFolders.next();
                bmgr.removeFolderContents(aFolder);
                this.strategy.remove(aFolder);
            }
        }
        
        // remove categories
        WeblogCategoryData rootCat = wmgr.getRootWeblogCategory(website);
        if (null != rootCat) {
            this.strategy.remove(rootCat);
        }
        
    }
    
    
    public void saveUser(UserData data) throws RollerException {
        this.strategy.store(data);
    }
    
    
    public void removeUser(UserData user) throws RollerException {
        this.strategy.remove(user);
        
        // remove entry from cache mapping
        this.userNameToIdMap.remove(user.getUserName());
    }
    
    
    public void savePermissions(PermissionsData perms) throws RollerException {
        this.strategy.store(perms);
    }
    
    
    public void removePermissions(PermissionsData perms) throws RollerException {
        this.strategy.remove(perms);
    }
    
    
    /**
     * @see org.roller.model.UserManager#storePage(org.roller.pojos.WeblogTemplate)
     */
    public void savePage(WeblogTemplate data) throws RollerException {
        this.strategy.store(data);
    }
    
    
    public void removePage(WeblogTemplate page) throws RollerException {
        this.strategy.remove(page);
    }
    
    
    public void addUser(UserData newUser) throws RollerException {
        
        if(newUser == null)
            throw new RollerException("cannot add null user");
        
        // TODO BACKEND: we must do this in a better fashion, like getUserCnt()?
        boolean adminUser = false;
        List existingUsers = this.getUsers();
        if(existingUsers.size() == 0) {
            // Make first user an admin
            adminUser = true;
        }
        
        if(getUserByUsername(newUser.getUserName()) != null ||
                getUserByUsername(newUser.getUserName().toLowerCase()) != null) {
            throw new RollerException("error.add.user.userNameInUse");
        }
        
        newUser.grantRole("editor");
        if(adminUser) {
            newUser.grantRole("admin");
        }
        
        this.strategy.store(newUser);
    }
    
    
    public void addWebsite(WebsiteData newWeblog) throws RollerException {
        
        this.strategy.store(newWeblog);
        this.addWeblogContents(newWeblog);
    }
    
    
    private void addWeblogContents(WebsiteData newWeblog) throws RollerException {
        
        UserManager umgr = RollerFactory.getRoller().getUserManager();
        WeblogManager wmgr = RollerFactory.getRoller().getWeblogManager();
        
        // grant weblog creator ADMIN permissions
        PermissionsData perms = new PermissionsData();
        perms.setUser(newWeblog.getCreator());
        perms.setWebsite(newWeblog);
        perms.setPending(false);
        perms.setPermissionMask(PermissionsData.ADMIN);
        this.strategy.store(perms);
        
        // add default categories
        WeblogCategoryData rootCat = new WeblogCategoryData(
                null, // id
                newWeblog, // newWeblog
                null,   // parent
                "root",  // name
                "root",  // description
                null ); // image
        this.strategy.store(rootCat);
        
        String cats = RollerConfig.getProperty("newuser.categories");
        if (cats != null) {
            String[] splitcats = cats.split(",");
            for (int i=0; i<splitcats.length; i++) {
                WeblogCategoryData c = new WeblogCategoryData(
                        null,            // id
                        newWeblog,         // newWeblog
                        rootCat,         // parent
                        splitcats[i],    // name
                        splitcats[i],    // description
                        null );          // image
                this.strategy.store(c);
            }
        }
        newWeblog.setBloggerCategory(rootCat);
        newWeblog.setDefaultCategory(rootCat);
        this.strategy.store(newWeblog);
        
        // add default bookmarks
        FolderData root = new FolderData(
                null, "root", "root", newWeblog);
        this.strategy.store(root);
        
        Integer zero = new Integer(0);
        String blogroll = RollerConfig.getProperty("newuser.blogroll");
        if (blogroll != null) {
            String[] splitroll = blogroll.split(",");
            for (int i=0; i<splitroll.length; i++) {
                String[] rollitems = splitroll[i].split("\\|");
                if (rollitems != null && rollitems.length > 1) {
                    BookmarkData b = new BookmarkData(
                            root,                // parent
                            rollitems[0],        // name
                            "",                  // description
                            rollitems[1].trim(), // url
                            null,                // feedurl
                            zero,                // weight
                            zero,                // priority
                            null);               // image
                    this.strategy.store(b);
                }
            }
        }

    }
    
    
    /**
     * Creates and stores a pending PermissionsData for user and website specified.
     *
     * TODO BACKEND: do we really need this?  can't we just use storePermissions()?
     */
    public PermissionsData inviteUser(WebsiteData website,
            UserData user, short mask) throws RollerException {
        
        if (website == null) throw new RollerException("Website cannot be null");
        if (user == null) throw new RollerException("User cannot be null");
        
        PermissionsData perms = new PermissionsData();
        perms.setWebsite(website);
        perms.setUser(user);
        perms.setPermissionMask(mask);
        this.strategy.store(perms);
        
        return perms;
    }
    
    
    /**
     * Remove user permissions from a website.
     *
     * TODO: replace this with a domain model method like weblog.retireUser(user)
     */
    public void retireUser(WebsiteData website, UserData user) throws RollerException {
        
        if (website == null) throw new RollerException("Website cannot be null");
        if (user == null) throw new RollerException("User cannot be null");
        
        Iterator perms = website.getPermissions().iterator();
        PermissionsData target = null;
        while (perms.hasNext()) {
            PermissionsData pd = (PermissionsData)perms.next();
            if (pd.getUser().getId().equals(user.getId())) {
                target = pd;
                break;
            }
        }
        if (target == null) throw new RollerException("User not member of website");
        
        website.removePermission(target);
        this.strategy.remove(target);
    }
    
    
    public WebsiteData getWebsite(String id) throws RollerException {
        return (WebsiteData) this.strategy.load(id,WebsiteData.class);
    }
    
    
    public WebsiteData getWebsiteByHandle(String handle) throws RollerException {
        return getWebsiteByHandle(handle, Boolean.TRUE);
    }
    
    
    /**
     * Return website specified by handle.
     */
    public WebsiteData getWebsiteByHandle(String handle, Boolean enabled)
            throws RollerException {
        
        if (handle==null )
            throw new RollerException("Handle cannot be null");
        
        // check cache first
        // NOTE: if we ever allow changing handles then this needs updating
        if(this.weblogHandleToIdMap.containsKey(handle)) {
            
            WebsiteData weblog = this.getWebsite((String) this.weblogHandleToIdMap.get(handle));
            if(weblog != null) {
                // only return weblog if enabled status matches
                if(enabled == null || enabled.equals(weblog.getEnabled())) {
                    log.debug("weblogHandleToId CACHE HIT - "+handle);
                    return weblog;
                }
            } else {
                // mapping hit with lookup miss?  mapping must be old, remove it
                this.weblogHandleToIdMap.remove(handle);
            }
        }
        
        // cache failed, do lookup
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(WebsiteData.class);
            
            if (enabled != null) {
                criteria.add(
                        Expression.conjunction()
                        .add(new IgnoreCaseEqExpression("handle", handle))
                        .add(Expression.eq("enabled", enabled)));
            } else {
                criteria.add(
                        Expression.conjunction()
                        .add(Expression.eq("handle", handle)));
            }
            
            WebsiteData website = (WebsiteData) criteria.uniqueResult();
            
            // add mapping to cache
            if(website != null) {
                log.debug("weblogHandleToId CACHE MISS - "+handle);
                this.weblogHandleToIdMap.put(website.getHandle(), website.getId());
            }
            
            return website;
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * Get websites of a user
     */
    public List getWebsites(UserData user, Boolean enabled, Boolean active)  throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(WebsiteData.class);
            if (user != null) {
                criteria.createAlias("permissions","permissions");
                criteria.add(Expression.eq("permissions.user", user));
                criteria.add(Expression.eq("permissions.pending", Boolean.FALSE));
            }
            if (enabled != null) {
                criteria.add(Expression.eq("enabled", enabled));
            }
            if (active != null) {
                criteria.add(Expression.eq("active", active));
            }
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    public UserData getUser(String id) throws RollerException {
        return (UserData)this.strategy.load(id,UserData.class);
    }
    
    
    public UserData getUserByUsername(String userName) throws RollerException {
        return getUserByUsername(userName, Boolean.TRUE);
    }
    
    
    public UserData getUserByUsername(String userName, Boolean enabled)
            throws RollerException {
        
        if (userName==null )
            throw new RollerException("userName cannot be null");
        
        // check cache first
        // NOTE: if we ever allow changing usernames then this needs updating
        if(this.userNameToIdMap.containsKey(userName)) {
            
            UserData user = this.getUser((String) this.userNameToIdMap.get(userName));
            if(user != null) {
                // only return the user if the enabled status matches
                if(enabled == null || enabled.equals(user.getEnabled())) {
                    log.debug("userNameToIdMap CACHE HIT - "+userName);
                    return user;
                }
            } else {
                // mapping hit with lookup miss?  mapping must be old, remove it
                this.userNameToIdMap.remove(userName);
            }
        }
        
        // cache failed, do lookup
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(UserData.class);
            
            if (enabled != null) {
                criteria.add(
                        Expression.conjunction()
                        .add(Expression.eq("userName", userName))
                        .add(Expression.eq("enabled", enabled)));
            } else {
                criteria.add(
                        Expression.conjunction()
                        .add(Expression.eq("userName", userName)));
            }
            
            UserData user = (UserData) criteria.uniqueResult();
            
            // add mapping to cache
            if(user != null) {
                log.debug("userNameToIdMap CACHE MISS - "+userName);
                this.userNameToIdMap.put(user.getUserName(), user.getId());
            }
            
            return user;
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    public List getUsers() throws RollerException {
        return getUsers(Boolean.TRUE);
    }
    
    
    public List getUsers(Boolean enabled) throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(UserData.class);
            if (enabled != null) {
                criteria.add(Expression.eq("enabled", enabled));
            }
            
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * Get users of a website
     */
    public List getUsers(WebsiteData website, Boolean enabled) throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(UserData.class);
            if (website != null) {
                criteria.createAlias("permissions","permissions");
                criteria.add(Expression.eq("permissions.website", website));
            }
            if (enabled != null) {
                criteria.add(Expression.eq("enabled", enabled));
            }
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    public List getUsersStartingWith(String startsWith,
            int offset, int length, Boolean enabled) throws RollerException {
        
        List rawresults = new ArrayList();
        List results = new ArrayList();
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(UserData.class);
            
            if (enabled != null) {
                criteria.add(Expression.eq("enabled", enabled));
            }
            if (startsWith != null) {
                criteria.add(Expression.disjunction()
                .add(Expression.like("userName", startsWith, MatchMode.START))
                .add(Expression.like("emailAddress", startsWith, MatchMode.START)));
            }
            
            rawresults = criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
        int pos = 0;
        int count = 0;
        Iterator iter = rawresults.iterator();
        while (iter.hasNext() && count < length) {
            UserData user = (UserData)iter.next();
            if (pos++ >= offset) {
                results.add(user);
                count++;
            }
        }
        return results;
    }
    
    
    public WeblogTemplate getPage(String id) throws RollerException {
        // Don't hit database for templates stored on disk
        if (id != null && id.endsWith(".vm")) return null;
        
        return (WeblogTemplate)this.strategy.load(id,WeblogTemplate.class);
    }
    
    
    /**
     * Use Hibernate directly because Roller's Query API does too much allocation.
     */
    public WeblogTemplate getPageByLink(WebsiteData website, String pagelink)
            throws RollerException {
        
        if (website == null)
            throw new RollerException("userName is null");
        
        if (pagelink == null)
            throw new RollerException("Pagelink is null");
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(WeblogTemplate.class);
            criteria.add(Expression.eq("website",website));
            criteria.add(Expression.eq("link",pagelink));
            criteria.setMaxResults(1);
            
            List list = criteria.list();
            return list.size()!=0 ? (WeblogTemplate)list.get(0) : null;
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * @see org.roller.model.UserManager#getPageByName(WebsiteData, java.lang.String)
     */
    public WeblogTemplate getPageByName(WebsiteData website, String pagename)
            throws RollerException {
        
        if (website == null)
            throw new RollerException("website is null");
        
        if (pagename == null)
            throw new RollerException("Page name is null");
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(WeblogTemplate.class);
            criteria.add(Expression.eq("website", website));
            criteria.add(Expression.eq("name", pagename));
            criteria.setMaxResults(1);
            
            List list = criteria.list();
            return list.size()!=0? (WeblogTemplate)list.get(0) : null;
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * @see org.roller.model.UserManager#getPages(WebsiteData)
     */
    public List getPages(WebsiteData website) throws RollerException {
        
        if (website == null)
            throw new RollerException("website is null");
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(WeblogTemplate.class);
            criteria.add(Expression.eq("website",website));
            criteria.addOrder(Order.asc("name"));
            
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    public PermissionsData getPermissions(String inviteId) throws RollerException {
        return (PermissionsData)this.strategy.load(inviteId, PermissionsData.class);
    }
    
    
    /**
     * Return permissions for specified user in website
     */
    public PermissionsData getPermissions(
            WebsiteData website, UserData user) throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(PermissionsData.class);
            criteria.add(Expression.eq("website", website));
            criteria.add(Expression.eq("user", user));
            
            List list = criteria.list();
            return list.size()!=0 ? (PermissionsData)list.get(0) : null;
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * Get pending permissions for user
     */
    public List getPendingPermissions(UserData user) throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(PermissionsData.class);
            criteria.add(Expression.eq("user", user));
            criteria.add(Expression.eq("pending", Boolean.TRUE));
            
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * Get pending permissions for website
     */
    public List getPendingPermissions(WebsiteData website) throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(PermissionsData.class);
            criteria.add(Expression.eq("website", website));
            criteria.add(Expression.eq("pending", Boolean.TRUE));
            
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * Get all permissions of a website (pendings not including)
     */
    public List getAllPermissions(WebsiteData website) throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(PermissionsData.class);
            criteria.add(Expression.eq("website", website));
            criteria.add(Expression.eq("pending", Boolean.FALSE));
            
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    /**
     * Get all permissions of a user.
     */
    public List getAllPermissions(UserData user) throws RollerException {
        
        try {
            Session session = ((HibernatePersistenceStrategy)this.strategy).getSession();
            Criteria criteria = session.createCriteria(PermissionsData.class);
            criteria.add(Expression.eq("user", user));
            criteria.add(Expression.eq("pending", Boolean.FALSE));
            
            return criteria.list();
        } catch (HibernateException e) {
            throw new RollerException(e);
        }
    }
    
    
    public void release() {}
    
    
    /** Doesn't seem to be any other way to get ignore case w/o QBE */
    class IgnoreCaseEqExpression extends SimpleExpression {
        public IgnoreCaseEqExpression(String property, Object value) {
            super(property, value, "=", true);
        }
    }
    
}

