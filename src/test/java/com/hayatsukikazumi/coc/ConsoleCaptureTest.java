package com.hayatsukikazumi.coc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsoleCaptureTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        wait100msec();
        ConsoleCapture.getInstance().stop();
    }

    @Test
    public void testSameBuffer() {
        System.out.println("ああああああ");

        CaptureBuffer buf = new CaptureBuffer();
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.start(buf, buf);
        System.err.println("A");
        wait100msec();
        System.out.print("B");
        wait100msec();
        wait100msec();
        System.out.println("C");
        cap.stop();

        CaptureElement el;

        List<CaptureElement> ls1 = buf.getListAndClear();
        assertEquals(2, ls1.size());

        el = ls1.get(0);
        assertEquals(ConsoleCapture.Type.ERR, el.getType());
        assertEquals("A" + System.lineSeparator(), el.getMessage());

        el = ls1.get(1);
        assertEquals(ConsoleCapture.Type.OUT, el.getType());
        assertEquals("BC" + System.lineSeparator(), el.getMessage());
        assertTrue("getEndTime", 190 <= (el.getEndTime().getTime() - el.getStartTime().getTime())); // クロック誤差
    }

    @Test
    public void testStartStop() {
        System.out.println("X");
        CaptureBuffer buf = new CaptureBuffer();
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.start(buf, buf);
        System.out.println("A");
        cap.stop();
        cap.stop();
        System.out.print("B");
        cap.start(buf, buf);
        cap.start(null, null);
        System.out.println("C");
        cap.stop();

        CaptureElement el;

        List<CaptureElement> ls1 = buf.getListAndClear();
        assertEquals(2, ls1.size());

        el = ls1.get(0);
        assertEquals(ConsoleCapture.Type.OUT, el.getType());
        assertEquals("A" + System.lineSeparator(), el.getMessage());

        el = ls1.get(1);
        assertEquals(ConsoleCapture.Type.OUT, el.getType());
        assertEquals("C" + System.lineSeparator(), el.getMessage());
    }

    @Test
    public void testSingleton() {

        System.out.println("X");
        CaptureBuffer buf1 = new CaptureBuffer();
        ConsoleCapture cap1 = ConsoleCapture.getInstance();
        cap1.start(buf1, buf1);
        System.out.println("A");

        ConsoleCapture cap2 = ConsoleCapture.getInstance();
        assertSame(cap1, cap2);
    }

    @Test
    public void testOutNullCapture() throws Throwable {
        CaptureBuffer buf = new CaptureBuffer();
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.redirectToOriginal(true);
        cap.start(null, buf);
        System.out.println("☆これがコンソールに出なかったらエラー1");
        System.err.println("☆これがコンソールに出なかったらエラー2");
        cap.stop();

        List<CaptureElement> ls1 = buf.getList();
        assertEquals(1, ls1.size());
        assertEquals(ConsoleCapture.Type.ERR, ls1.get(0).getType());
    }

    @Test
    public void testErrNullCapture() throws Throwable {
        CaptureBuffer buf = new CaptureBuffer();
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.redirectToOriginal(false);
        cap.start(buf, null);
        System.out.println("★N これがコンソールに出たらエラー1");
        System.err.println("★N これがコンソールに出たらエラー2");
        cap.stop();

        List<CaptureElement> ls1 = buf.getList();
        assertEquals(1, ls1.size());
        assertEquals(ConsoleCapture.Type.OUT, ls1.get(0).getType());
    }

    @Test
    public void testStartWithOutputToOriginal() throws Throwable {
        CaptureBuffer b1 = new CaptureBuffer();
        CaptureBuffer b2 = new CaptureBuffer();
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.start(b1, b2, false);

        System.out.println("★これがコンソールに出たらエラー1");
        System.err.println("★これがコンソールに出たらエラー2");

        cap.redirectToOriginal(true);

        System.err.println("☆これがコンソールに出なかったらエラー1");
        System.out.println("☆これがコンソールに出なかったらエラー2");

        cap.stop();

        System.out.println("☆停止中1");
        System.err.println("☆停止中2");

        CaptureElement el;

        List<CaptureElement> ls1 = b1.getList();
        assertEquals(2, ls1.size());

        el = ls1.get(0);
        assertEquals(ConsoleCapture.Type.OUT, el.getType());
        assertEquals("★これがコンソールに出たらエラー1" + System.lineSeparator(), el.getMessage());

        el = ls1.get(1);
        assertEquals(ConsoleCapture.Type.OUT, el.getType());
        assertEquals("☆これがコンソールに出なかったらエラー2" + System.lineSeparator(), el.getMessage());

        List<CaptureElement> ls2 = b2.getList();
        assertEquals(2, ls2.size());

        el = ls2.get(0);
        assertEquals(ConsoleCapture.Type.ERR, el.getType());
        assertEquals("★これがコンソールに出たらエラー2" + System.lineSeparator(), el.getMessage());

        el = ls2.get(1);
        assertEquals(ConsoleCapture.Type.ERR, el.getType());
        assertEquals("☆これがコンソールに出なかったらエラー1" + System.lineSeparator(), el.getMessage());
    }

    @Test
    public void testSetOutputToOriginal() throws Throwable {
        CaptureBuffer b1 = new CaptureBuffer();
        CaptureBuffer b2 = new CaptureBuffer();
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.redirectToOriginal(false);
        cap.start(b1, b2);

        System.out.println("★これがコンソールに出たらエラー1");
        System.err.println("★これがコンソールに出たらエラー2");

        cap.redirectToOriginal(true);

        System.err.println("☆これがコンソールに出なかったらエラー1");
        System.out.println("☆これがコンソールに出なかったらエラー2");

        cap.stop();

        System.out.println("☆停止中1");
        System.err.println("☆停止中2");

        CaptureElement el;

        List<CaptureElement> ls1 = b1.getList();
        assertEquals(2, ls1.size());

        el = ls1.get(0);
        assertEquals(ConsoleCapture.Type.OUT, el.getType());
        assertEquals("★これがコンソールに出たらエラー1" + System.lineSeparator(), el.getMessage());

        el = ls1.get(1);
        assertEquals(ConsoleCapture.Type.OUT, el.getType());
        assertEquals("☆これがコンソールに出なかったらエラー2" + System.lineSeparator(), el.getMessage());

        List<CaptureElement> ls2 = b2.getList();
        assertEquals(2, ls2.size());

        el = ls2.get(0);
        assertEquals(ConsoleCapture.Type.ERR, el.getType());
        assertEquals("★これがコンソールに出たらエラー2" + System.lineSeparator(), el.getMessage());

        el = ls2.get(1);
        assertEquals(ConsoleCapture.Type.ERR, el.getType());
        assertEquals("☆これがコンソールに出なかったらエラー1" + System.lineSeparator(), el.getMessage());
    }

    @Test
    public void testFinalize() throws Throwable {
        CaptureBuffer buf = new CaptureBuffer();
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.redirectToOriginal(false);
        cap.start(buf, buf);

        System.out.println("★F これがコンソールに出たらエラー1");
        System.err.println("★F これがコンソールに出たらエラー2");

        cap.finalize();
        cap = null;

        System.out.println("☆F これがコンソールに出なかったらエラー1");
        System.err.println("☆F これがコンソールに出なかったらエラー2");
    }

    private void wait100msec() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
    }
}
