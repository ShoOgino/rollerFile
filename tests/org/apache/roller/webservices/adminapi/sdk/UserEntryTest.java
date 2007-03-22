/*
* Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.roller.webservices.adminapi.sdk;

import org.apache.roller.webservices.adminprotocol.sdk.UnexpectedRootElementException;
import org.apache.roller.webservices.adminprotocol.sdk.UserEntrySet;
import org.jdom.Document;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.roller.webservices.adminapi.AappTest;
import org.jdom.JDOMException;

public class UserEntryTest extends AappTest {
    public void testEquals() {
        UserEntrySet ues1 = getSampleUserEntrySet();
        UserEntrySet ues2 = getSampleUserEntrySet();
        
        assertEquals(ues1, ues2);
    }
    
    public void testDocumentMarshal() {
        try {
            UserEntrySet ues1 = getSampleUserEntrySet();
            Document d = ues1.toDocument();
            
            UserEntrySet ues2 = new UserEntrySet(d, getEndpointUrl());
            
            assertEquals(ues1, ues2);
        } catch (UnexpectedRootElementException uree) {
            fail(uree.getMessage());
        }
    }
    
    public void testStreamMarshal() {
        try {
            UserEntrySet ues1 = getSampleUserEntrySet();
            String s = ues1.toString();
            InputStream stream = new ByteArrayInputStream(s.getBytes("UTF-8")); 
            
            UserEntrySet ues2 = new UserEntrySet(stream, getEndpointUrl());
            
            assertEquals(ues1, ues2);
        } catch (UnexpectedRootElementException uree) {
            fail(uree.getMessage());
        } catch (IOException ioe) {
            fail(ioe.getMessage());
        } catch (JDOMException je) {
            fail(je.getMessage());
        }
    }
}
