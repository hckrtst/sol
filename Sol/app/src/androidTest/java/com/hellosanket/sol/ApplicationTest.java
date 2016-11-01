package com.hellosanket.sol;

/*
import android.app.Application;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;


public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}

@RunWith(AndroidJUnit4.class)
@MediumTest
public class MyServiceTest {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    // test for a service which is started with startService
    @Test
    public void testWithStartedService() {
        mServiceRule.
                startService(new Intent(InstrumentationRegistry.getTargetContext(),
                        MyService.class));
        // test code
    }

    @Test
    // test for a service which is started with bindService
    public void testWithBoundService() {
        IBinder binder = mServiceRule.
                bindService(new Intent(InstrumentationRegistry.getTargetContext(),
                        MyService.class));
        MyService service = ((MyService.LocalBinder) binder).getService();
        assertTrue("True wasn't returned", service.doSomethingToReturnTrue());
    }
}

*/