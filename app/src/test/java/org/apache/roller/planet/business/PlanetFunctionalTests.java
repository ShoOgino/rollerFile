/*
 * Copyright 2005 Sun Microsystems, Inc.
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

package org.apache.roller.planet.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.planet.pojos.Planet;
import org.apache.roller.testing.DerbyExtension;
import org.apache.roller.weblogger.TestUtils;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test Planet functionality.
 */
//@ExtendWith(DerbyExtension.class)
public class PlanetFunctionalTests  {
    
    public static Log log = LogFactory.getLog(PlanetFunctionalTests.class);
    
    private Planet testPlanet = null;
    
    @BeforeEach
    public void setUp() throws Exception {
        // setup planet
        TestUtils.setupWeblogger();

        testPlanet = TestUtils.setupPlanet("planetFuncTest");
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        TestUtils.teardownPlanet(testPlanet.getId());
    }
    
    
    /**
     * Test lookup mechanisms.
     */
    @Test
    public void testPlanetLookups() throws Exception {
        
        PlanetManager mgr = WebloggerFactory.getWeblogger().getPlanetManager();
        
        // by id
        Planet planet = mgr.getWebloggerById(testPlanet.getId());
        assertNotNull(planet);
        assertEquals("planetFuncTest", planet.getHandle());
        
        // by handle
        planet = mgr.getWeblogger("planetFuncTest");
        assertNotNull(planet);
        assertEquals("planetFuncTest", planet.getHandle());
        
        // all planets (should be 2, the default and the one we created)
        List planets = mgr.getWebloggers();
        assertNotNull(planets);
        assertEquals(2, planets.size());
    }
    
}
