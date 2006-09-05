/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.roller.business.datamapper;

import java.sql.Timestamp;
import java.util.List;
import org.apache.roller.RollerException;
import org.apache.roller.model.PingQueueManager;
import org.apache.roller.pojos.AutoPingData;
import org.apache.roller.pojos.PingQueueEntryData;

/*
 * DatamapperPingQueueManagerImpl.java
 *
 * Created on May 28, 2006, 4:11 PM
 *
 */
public class DatamapperPingQueueManagerImpl implements PingQueueManager {

    /** The strategy for this manager. */
    private DatamapperPersistenceStrategy strategy;

    /** Creates a new instance of DatamapperPingQueueManagerImpl */
    public DatamapperPingQueueManagerImpl
            (DatamapperPersistenceStrategy strategy) {
        this.strategy =  strategy;
    }

    public void addQueueEntry(AutoPingData autoPing) 
            throws RollerException {
        // first, determine if an entry already exists
        int count = (Integer)strategy.newQuery(PingQueueEntryData.class,
                "countGetByPingTarget&&website")
                .execute(new Object[]
                    {autoPing.getPingTarget(), autoPing.getWebsite()});
        if (count > 0)
            return;

        // create and store a new entry
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PingQueueEntryData pingQueueEntry =
                new PingQueueEntryData(null, now, 
                autoPing.getPingTarget(), autoPing.getWebsite(), 0);
        this.saveQueueEntry(pingQueueEntry);
    }

    public void saveQueueEntry(PingQueueEntryData pingQueueEntry) 
            throws RollerException {
        strategy.store(pingQueueEntry);
    }

    public void removeQueueEntry(PingQueueEntryData pingQueueEntry) 
            throws RollerException {
        strategy.remove(pingQueueEntry);
    }

    public PingQueueEntryData getQueueEntry(String id) 
            throws RollerException {
        return (PingQueueEntryData)strategy.load(PingQueueEntryData.class, id);
    }

    public List getAllQueueEntries() 
            throws RollerException {
        return (List)strategy.newQuery(PingQueueEntryData.class,
                "getAll.orderByEntryTime");
    }

    public void release() {
    }
    
}
