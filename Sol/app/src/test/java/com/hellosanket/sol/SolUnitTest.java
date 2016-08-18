package com.hellosanket.sol;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import java.util.Calendar;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SolUnitTest {
    @Test
    public void check_calendar_data_integrity() throws Exception {
        CalendarDataHelper helper = CalendarDataHelper.getInstance();

        // Use now for sunrise
        Calendar now = Calendar.getInstance();
        helper.setCalFor("sunrise", now);

        // now get it back
        Calendar sunrise = helper.getCalFor("sunrise");
        assertNotNull(sunrise);

        assertEquals(now, sunrise);

        // add a second to the local object
        sunrise.add(Calendar.SECOND, 1);

        assertNotEquals(now, sunrise);

        // originally set value should be the same
        assertEquals(now, helper.getCalFor("sunrise"));

    }
}