/*
 * Copyright (c) 2005
 * Anil R. Gangolli. All rights reserved.
 *
 * Distributed with the Roller Weblogger Project under the terms of the Roller Software
 * License
 */

package org.roller.model;

import org.roller.pojos.AutoPingData;
import org.roller.pojos.PingTargetData;
import org.roller.pojos.WebsiteData;
import org.roller.pojos.WeblogEntryData;
import org.roller.RollerException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface AutoPingManager extends Serializable
{
    /**
     * Release all resources.
     */
    public void release();

    /**
     * Create an auto ping configuration specifying that the given ping target is to be pinged when the given website
     * changes.
     *
     * @param pingTarget target to ping
     * @param website    website whose changes should trigger the ping
     * @return new auto ping configuration
     * @throws RollerException
     */
    public AutoPingData createAutoPing(PingTargetData pingTarget, WebsiteData website)
        throws RollerException;

    /*
     * Create an auto ping configuration specifying that the given pingTarget is to be pinged when the
     * given website changes, but only for changes on one of the specified categories of that website.
     * If the collection of categories is empty, this has the same effect as creating an unrestricted
     * auto ping configuration.
     *
     * @param pingTarget
     * @param website  website whose changes should trigger the ping (subject to category restrictions)
     * @param categories categories in which changes should trigger the ping
     * @return  new auto ping configuration
     * @throws RollerException

    public AutoPingData createAutoPing(PingTargetData pingTarget,
                                           WebsiteData website,
                                           Collection categories) throws RollerException;
    */

    /**
     * Retrieve an auto ping configuration by id.
     *
     * @param id the id of the auto ping configuration to retrieve.
     * @return the auto ping configuration with specified id or null if not found
     * @throws RollerException
     */
    public AutoPingData retrieveAutoPing(String id) throws RollerException;

    /**
     * Store an auto ping configuration.
     *
     * @param autoPing the auto ping configuration
     * @throws RollerException
     */
    public void storeAutoPing(AutoPingData autoPing) throws RollerException;

    /**
     * Remove the auto ping configuration with given id.
     *
     * @param id the id of the auto ping configuration to remove
     * @throws RollerException
     */
    public void removeAutoPing(String id) throws RollerException;

    /**
     * Remove the auto ping configuration with given id.
     *
     * @param autoPing the auto ping configuration to remove
     * @throws RollerException
     */
    public void removeAutoPing(AutoPingData autoPing) throws RollerException;

    /**
     * Remove the auto ping configuration for the given ping target and website, if one exists.  Returns silently if it
     * doesn't exist.
     *
     * @param pingTarget the ping target
     * @param website    the website
     * @throws RollerException
     */
    public void removeAutoPing(PingTargetData pingTarget, WebsiteData website) throws RollerException;

    /**
     * Remove a collection of auto ping configurations.
     *
     * @param autopings a <code>Collection</code> of <code>AutoPingData</code> objects
     * @throws RollerException
     */
    public void removeAutoPings(Collection autopings) throws RollerException;

    /**
     * Remove all auto ping configurations for all websites.
     *
     * @throws RollerException
     */
    public void removeAllAutoPings() throws RollerException;

    /**
     * Get all of the auto ping configurations for the given website.
     *
     * @param website
     * @return a list of auto ping configurations for the given website as <code>AutoPingData</code> objects.
     */
    public List getAutoPingsByWebsite(WebsiteData website) throws RollerException;

    /**
     * Get all of the auto ping configurations for a given target (across all websites).
     *
     * @param pingTarget
     * @return a list of auto ping configurations for the given target as <code>AutoPingData</code> objects.
     */
    public List getAutoPingsByTarget(PingTargetData pingTarget) throws RollerException;

    /**
     * Get the category restrictions on the given auto ping configuration.
     *
     * @param autoPing
     * @return the category restrictions as a collection of <code>WeblogCategoryData</code> objects.  This collection
     *         will be empty if there are no restrictions (meaning that the auto ping configuration applies to changes
     *         in any category of the website).
     */
    public List getCategoryRestrictions(AutoPingData autoPing) throws RollerException;

    /**
     * Set the category restrictions on the given ping configuration to the specified ones.  If the new collection is
     * empty, all category restrictions are removed.
     *
     * @param autoPing      auto ping configuration to change
     * @param newCategories a collection of <code>WeblogCategoryData</code> objects for the new category restrictions
     */
    public void setCategoryRestrictions(AutoPingData autoPing,
                                        Collection newCategories);

    /**
     * Get the auto ping configurations that should be pinged upon creation/change of the given weblog entry.
     *
     * @param changedWeblogEntry the entry that has been created or changed
     * @return a list of the ping configurations that should be applied due to this change
     */
    public List getApplicableAutoPings(WeblogEntryData changedWeblogEntry) throws RollerException;

    /**
     * Queue the auto ping configurations that should be pinged upon change to the given weblog entry.  This calls the
     * {@link PingQueueManager} to queue ping requests for each ping configuration that should be applied on change to
     * the given weblog entry.  If ping processing is suspended, this returns without doing anything.
     *
     * @param changedWeblogEntry the entry that has been created or changed
     */
    public void queueApplicableAutoPings(WeblogEntryData changedWeblogEntry) throws RollerException;
}
