package com.hayatsukikazumi.coc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CaptureBufferTest.class, CaptureElementTest.class, CaptureOutputStreamTest.class,
        ConsoleCaptureTest.class, SampleTest.class })
public class AllTests {

}
