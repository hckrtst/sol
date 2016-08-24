package com.hellosanket.sol;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class CalendarDataHelperTest {
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