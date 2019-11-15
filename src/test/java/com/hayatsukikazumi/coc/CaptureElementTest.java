package com.hayatsukikazumi.coc;
/*
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * created on 2019/11/03
 */
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CaptureElementTest {

    enum CETT { A, B, C };

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testProperties() {

        Date st = new Date();
        try {
            Thread.sleep(200);
        } catch(Exception e) {}

        CaptureElement elem = new CaptureElement(4385, CETT.B, st.getTime(), "メッセージA");

        assertEquals("getLineNumber", 4385, elem.getLineNumber());
        assertEquals("getType", CETT.B, elem.getType());
        assertEquals("getStartTime", st, elem.getStartTime());
        assertEquals("getMessage", "メッセージA", elem.getMessage());
        assertTrue("getEndTime", 190 <= (elem.getEndTime().getTime() - st.getTime())); //クロック誤差
    }

    @Test
    public void testMessageNull() {

        CaptureElement elem = new CaptureElement(2, CETT.A, 100, null);

        assertEquals("getLineNumber", 2, elem.getLineNumber());
        assertEquals("getType", CETT.A, elem.getType());
        assertEquals("getStartTime", new Date(100), elem.getStartTime());
        assertEquals("getMessage", "", elem.getMessage());
    }

    @Test
    public void testToString() {

        CaptureElement elem = new CaptureElement(1234567890, CETT.C, 1234, "Message C");

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String s1 = sf.format(new Date(1234));
        String s2 = sf.format(elem.getEndTime());

        assertEquals("1234567890\tC\t" + s1 + "\t" + s2 + "\tMessage C", elem.toString());
    }

    @Test
    public void testToStringTypeNull() {

        CaptureElement elem = new CaptureElement(0, null, 1515151515000L, "AAA");

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String s1 = sf.format(new Date(1515151515000L));
        String s2 = sf.format(elem.getEndTime());

        assertEquals("0\tnull\t" + s1 + "\t" + s2 + "\tAAA", elem.toString());
    }

}
