package com.hayatsukikazumi.coc;
/*
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * created on 2019/11/03
 */
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Hayatsukikazumi
 *
 */
public class CaptureOutputStreamTest {

    enum STT {
        X, Y, Z
    };

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlushEndsWithoutNewLine() throws Exception {

        CaptureOutputStream cos = new CaptureOutputStream(STT.X, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                fail("onCapture called.");
            }
        });

        byte[] b = "ABC Win\r\n DEF OldMac\r GHI Unix\n JKL".getBytes();
        cos.write(b);
        cos.flush();
        assertEquals(b.length, cos.size());
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlushEndsWithNewLine() throws Exception {

        final int[] cnt = { 0 };

        CaptureOutputStream cos = new CaptureOutputStream(STT.Y, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                long endTime = System.currentTimeMillis();
                cnt[0]++;
                assertEquals(STT.Y, type);
                assertTrue((endTime - startTime) < 100);
                assertEquals("ABC DEF " + System.lineSeparator(), message);
            }
        });

        byte[] b = ("ABC DEF " + System.lineSeparator()).getBytes();
        cos.write(b);
        cos.flush();
        assertEquals(0, cos.size());
        assertEquals(1, cnt[0]);
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlushOnlyNewLine() throws Exception {

        final int[] cnt = { 0 };

        CaptureOutputStream cos = new CaptureOutputStream(STT.Y, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                long endTime = System.currentTimeMillis();
                cnt[0]++;
                assertEquals(STT.Y, type);
                assertTrue((endTime - startTime) < 100);
                assertEquals(System.lineSeparator(), message);
            }
        });

        byte[] b = (System.lineSeparator()).getBytes();
        cos.write(b);
        cos.flush();
        assertEquals(0, cos.size());
        assertEquals(1, cnt[0]);
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlush0byte() throws Exception {

        CaptureOutputStream cos = new CaptureOutputStream(STT.X, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                fail("onCapture called.");
            }
        });

        cos.flush();
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlush1byte() throws Exception {

        CaptureOutputStream cos = new CaptureOutputStream(STT.X, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                fail("onCapture called.");
            }
        });

        cos.write(65);
        cos.flush();
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlush2047bytes() throws Exception {

        final byte[] b = new byte[2047];
        Arrays.fill(b, (byte) 0x21);

        CaptureOutputStream cos = new CaptureOutputStream(STT.X, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                fail("onCapture called.");
            }
        });

        cos.write(b);
        cos.flush();
        assertEquals(2047, cos.size());
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlush2048bytes() throws Exception {

        final int[] cnt = { 0 };
        final byte[] b = new byte[2048];
        Arrays.fill(b, (byte) 0x21);

        CaptureOutputStream cos = new CaptureOutputStream(STT.X, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                long endTime = System.currentTimeMillis();
                cnt[0]++;
                assertEquals(STT.X, type);
                assertTrue((endTime - startTime) < 100);
                assertEquals(new String(b), message);
            }
        });

        cos.write(b);
        cos.flush();
        assertEquals(0, cos.size());
        assertEquals(1, cnt[0]);
    }

    @SuppressWarnings("resource")
    @Test
    public void testFlush2049bytes() throws Exception {

        final int[] cnt = { 0 };
        final byte[] b = new byte[2049];
        Arrays.fill(b, (byte) 0x21);

        CaptureOutputStream cos = new CaptureOutputStream(STT.X, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                long endTime = System.currentTimeMillis();
                cnt[0]++;
                assertEquals(STT.X, type);
                assertTrue((endTime - startTime) < 100);
                assertEquals(new String(b), message);
            }
        });

        cos.write(b);
        cos.flush();
        assertEquals(0, cos.size());
        assertEquals(1, cnt[0]);
    }

    @Test
    public void testClose() throws Exception {

        final int[] cnt = { 0 };

        CaptureOutputStream cos = new CaptureOutputStream(STT.Z, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                long endTime = System.currentTimeMillis();
                cnt[0]++;
                assertEquals(STT.Z, type);
                assertTrue((endTime - startTime) < 100);
                assertEquals("あいうえお", message);
            }
        });

        byte[] b = "あいうえお".getBytes();
        cos.write(b);
        cos.close();
        assertEquals(0, cos.size());
        assertEquals(1, cnt[0]);
    }

    @Test
    public void testClose0byte() throws Exception {

        CaptureOutputStream cos = new CaptureOutputStream(STT.X, new CaptureListener() {
            public void onCapture(Enum<?> type, long startTime, String message) {
                fail("onCapture called.");
            }
        });

        cos.close();
    }

    @Test
    public void testListenerNull() throws Exception {

        CaptureOutputStream cos = new CaptureOutputStream(STT.Y, null);

        byte[] b = ("ABC DEF " + System.lineSeparator()).getBytes();
        cos.write(b);
        cos.flush();
        assertEquals(0, cos.size());

        cos.close();
    }
}
