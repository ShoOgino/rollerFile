package org.roller.business; import java.sql.Timestamp;import java.util.Calendar;import java.util.Date;import java.util.List;import junit.framework.Test;import junit.framework.TestSuite; import org.roller.RollerException;import org.roller.model.PropertiesManager;import org.roller.model.RefererManager;import org.roller.model.Roller;import org.roller.pojos.RefererData;import org.roller.pojos.RollerPropertyData;import org.roller.pojos.UserData;import org.roller.pojos.WeblogEntryData;import org.roller.pojos.WebsiteData;import org.roller.util.DateUtil;import org.roller.RollerTestBase;/** * Test Roller Referer Management. */public class RefererManagerTest extends RollerTestBase {    RefererManager rmgr;    //List refs;    int count = 20;            String testDay;    String origSpamWords;    //------------------------------------------------------------------------	public RefererManagerTest(String name)     {		super(name);	}        //------------------------------------------------------------------------	public static void main(String args[])     {		junit.textui.TestRunner.run(RefererManagerTest.class);	}        //------------------------------------------------------------------------    public static Test suite()     {        return new TestSuite(RefererManagerTest.class);    }    protected void setUp() throws Exception    {        super.setUp();                // add "spamtest" to refererSpamWords        Roller mRoller = getRoller();        PropertiesManager pmgr = mRoller.getPropertiesManager();        RollerPropertyData spamprop = pmgr.getProperty("spam.blacklist");        this.origSpamWords = spamprop.getValue();        spamprop.setValue(spamprop.getValue() + ", spamtest");        pmgr.store(spamprop);                // Process count unique referers        rmgr = getRoller().getRefererManager();        Calendar lCalendar = Calendar.getInstance();        lCalendar.setTime(new Date());        for (int i = 0; i < count; i++)        {            lCalendar.add(Calendar.DATE, -1);            Timestamp day = new Timestamp(lCalendar.getTime().getTime());            getRoller().begin(UserData.SYSTEM_USER);            MockRequest mock = new MockRequest(                                DateUtil.format8chars(day),                                "http://test"+i,                                "http://test"+i,                                null,                                mWebsite            );            rmgr.processRequest(mock);            getRoller().commit();                        testDay = mock.getDateString();        }    }    public void tearDown() throws Exception    {              List refs = rmgr.getReferers(mWebsite);        // Remove all referers processes        for (int i = 0; i < refs.size(); i++)        {            rmgr.removeReferer(((RefererData)refs.get(i)).getId());            }                // Make sure all were removed        refs = rmgr.getReferers(mWebsite);                    assertEquals(0,refs.size());        // reset refererSpamWords to original value        Roller mRoller = getRoller();        PropertiesManager pmgr = mRoller.getPropertiesManager();        RollerPropertyData spamprop = pmgr.getProperty("spam.blacklist");        spamprop.setValue(this.origSpamWords);        pmgr.store(spamprop);                super.tearDown();    }        //------------------------------------------------------------------------    public void testGetReferersToDate() throws Exception    {        List referers = rmgr.getReferersToDate(mWebsite, testDay);        assertEquals("Should be one Referer.", referers.size(), 1);    }        //------------------------------------------------------------------------    public void testRefererProcessing() throws RollerException    {        List refs = rmgr.getReferers(mWebsite);                    assertEquals("number of referers should equal count", count, refs.size());                int hits = rmgr.getDayHits(mWebsite);        assertEquals("There should be one fewer hits than referers", count, hits);             }        public void testSelfRefererDenial() throws RollerException    {        // test against "self referrals"        getRoller().begin(UserData.SYSTEM_USER);        // create "direct" referer        boolean isSpam = rmgr.processRequest(            new MockRequest(                "20020101",                "direct",                "http://test.com",                null, mWebsite            )        );        getRoller().commit();        assertFalse("is not spam", isSpam);        int newRefCount = rmgr.getReferers(mWebsite).size();                // now create self-referer        getRoller().begin(UserData.SYSTEM_USER);                isSpam = rmgr.processRequest(            new MockRequest(                "20020202",                "http://test.com/page/" + mWebsite.getHandle(),                "http://test.com",                null, mWebsite            )        );        getRoller().commit();        assertFalse("is not spam", isSpam);                // number of referrers should not have changed        List refs = rmgr.getReferers(mWebsite);                    assertEquals("self referal not ignored", newRefCount, refs.size());                 // now create self-referer from editor page        isSpam = rmgr.processRequest(            new MockRequest(                "20020202",                "http://test.com/weblog.do",                "http://test.com",                null, mWebsite            )        );        getRoller().commit();        assertFalse("is not spam", isSpam);                // number of referrers should not have changed        refs = rmgr.getReferers(mWebsite);                    assertEquals("editor referal not ignored", newRefCount, refs.size());     }        /**     * Test to see if Referer Spam detection works.     */    public void testSpamBlocking()    {        boolean isSpam = rmgr.processRequest(            new MockRequest(                "20040101",                "http://www.spamtest.com",                "http://test.com",                null, mWebsite            )        );        //assertTrue("failed to detect referer spam", isSpam);    }        public void testApplyRefererFilters() throws Exception    {        List refs = rmgr.getReferers(mWebsite);        assertEquals(count, refs.size());        String origWords = null;                getRoller().begin(UserData.SYSTEM_USER);        Roller mRoller = getRoller();        PropertiesManager pmgr = mRoller.getPropertiesManager();        RollerPropertyData spamprop = pmgr.getProperty("spam.blacklist");        origWords = spamprop.getValue();        spamprop.setValue(spamprop.getValue() + ", test");        pmgr.store(spamprop);        getRoller().commit();                getRoller().begin(UserData.SYSTEM_USER);        getRoller().getRefererManager().applyRefererFilters();        getRoller().commit();                refs = rmgr.getReferers(mWebsite);        assertEquals(0, refs.size());        getRoller().begin(UserData.SYSTEM_USER);        spamprop = pmgr.getProperty("spam.blacklist");        spamprop.setValue(origWords);        pmgr.store(spamprop);        getRoller().commit();    }        public void testApplyRefererFiltersWebsite() throws Exception    {        List refs = rmgr.getReferers(mWebsite);        assertEquals(count, refs.size());        String origWords = null;                getRoller().begin(UserData.SYSTEM_USER);        mWebsite = getRoller().getUserManager().retrieveWebsite(mWebsite.getId());        origWords = mWebsite.getBlacklist();        mWebsite.setBlacklist("test");        mWebsite.save();        getRoller().commit();                getRoller().begin(UserData.SYSTEM_USER);        getRoller().getRefererManager().applyRefererFilters();        getRoller().commit();                refs = rmgr.getReferers(mWebsite);        assertEquals(0, refs.size());    }}class MockRequest implements org.roller.model.ParsedRequest{      private String mDateStr = null;    private String mRefUrl = null;    private String mReqUrl = null;    private WeblogEntryData mEntry = null;    private WebsiteData mWebsite = null;    private boolean mIsDateSpecified = false;        public MockRequest(         String dateStr, String refUrl, String reqUrl,         WeblogEntryData entry, WebsiteData website)     {        mDateStr = dateStr;        mRefUrl = refUrl;        mReqUrl = reqUrl;        mEntry = entry;        mWebsite = website;                if (mDateStr != null) mIsDateSpecified = true;    }        /**     * @see org.roller.pojos.ParsedRequest#getDateString()     */    public String getDateString()    {        return mDateStr;    }    /**     * @see org.roller.pojos.ParsedRequest#getRefererURL()     */    public String getRefererURL()    {        return mRefUrl;    }    /**     * @see org.roller.pojos.ParsedRequest#getRequestURL()     */    public String getRequestURL()    {        return mReqUrl;    }    /**     * @see org.roller.pojos.ParsedRequest#getWeblogEntry()     */    public WeblogEntryData getWeblogEntry()    {        return mEntry;    }    /**     * @see org.roller.pojos.ParsedRequest#getWebsite()     */    public WebsiteData getWebsite()    {        return mWebsite;    }    /**     * Returns the isDateSpecified.     * @return boolean     */    public boolean isDateSpecified()    {        return mIsDateSpecified;    }    /**     * Sets the isDateSpecified.     * @param isDateSpecified The isDateSpecified to set     */    public void setDateSpecified(boolean isDateSpecified)    {        mIsDateSpecified = isDateSpecified;    }    /**      * @see org.roller.pojos.ParsedRequest#isEnableLinkback()     */    public boolean isEnableLinkback()    {        return false;    }}