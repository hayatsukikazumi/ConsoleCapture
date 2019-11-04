package com.hayatsukikazumi.coc;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CaptureBufferTest {

    enum CBTT {
        A, B
    };

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDefaultSize() {
        CaptureBuffer buf = new CaptureBuffer();

        for (int i = 0; i < 1024; i++) {
            buf.add(CBTT.A, i, "Message" + i);
        }

        assertEquals(1024, buf.size());

        buf.add(CBTT.A, 1111, "Message1111");
        assertEquals(1024, buf.size());
    }

    @Test
    public void testSetSize() {
        CaptureBuffer buf = new CaptureBuffer(500);

        for (int i = 0; i < 1024; i++) {
            buf.add(CBTT.B, i, "Message" + i);
        }

        assertEquals(500, buf.size());

        buf.add(CBTT.B, 1111, "Message1111");
        assertEquals(500, buf.size());
    }

    @Test
    public void testIllegalSize() {
        try {
            new CaptureBuffer(-1);
        } catch (IllegalArgumentException e) {
            return;
        }

        fail("IllegalArgumentException not occured.");
    }

    @Test
    public void testGetList() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");

        CaptureElement el;

        List<CaptureElement> ls1 = buf.getList();
        assertEquals("size of getList", 3, ls1.size());
        assertEquals("size of list", 3, buf.size());

        el = ls1.get(0);
        assertEquals("0", 0, el.getLineNumber());
        assertEquals("0", CBTT.A, el.getType());
        assertEquals("0", new Date(100), el.getStartTime());
        assertEquals("0", "hoge", el.getMessage());

        el = ls1.get(1);
        assertEquals("1", 1, el.getLineNumber());
        assertEquals("1", CBTT.B, el.getType());
        assertEquals("1", new Date(200), el.getStartTime());
        assertEquals("1", "foo", el.getMessage());

        el = ls1.get(2);
        assertEquals("2", 2, el.getLineNumber());
        assertEquals("2", CBTT.A, el.getType());
        assertEquals("2", new Date(300), el.getStartTime());
        assertEquals("2", "bar", el.getMessage());

        buf.add(CBTT.A, 400, "tako");
        List<CaptureElement> ls2 = buf.getList();
        assertEquals("size of getList", 4, ls2.size());
        assertEquals("size of list", 4, buf.size());

        assertNotSame(ls1, ls2);
        assertSame(ls1.get(0), ls2.get(0));
        assertSame(ls1.get(1), ls2.get(1));
        assertSame(ls1.get(2), ls2.get(2));

        el = ls2.get(3);
        assertEquals("3", 3, el.getLineNumber());
        assertEquals("3", CBTT.A, el.getType());
        assertEquals("3", new Date(400), el.getStartTime());
        assertEquals("3", "tako", el.getMessage());
    }

    @Test
    public void testFIFO() {
        CaptureBuffer buf = new CaptureBuffer(3);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");

        CaptureElement el;

        List<CaptureElement> ls1 = buf.getList();
        assertEquals("size of getList", 3, ls1.size());
        assertEquals("size of list", 3, buf.size());

        el = ls1.get(0);
        assertEquals("0", 0, el.getLineNumber());
        assertEquals("0", CBTT.A, el.getType());
        assertEquals("0", new Date(100), el.getStartTime());
        assertEquals("0", "hoge", el.getMessage());

        buf.add(CBTT.A, 400, "tako");
        List<CaptureElement> ls2 = buf.getList();
        assertEquals("size of getList", 3, ls2.size());
        assertSame(ls1.get(1), ls2.get(0));
        assertSame(ls1.get(2), ls2.get(1));

        el = ls2.get(0);
        assertEquals("0*", 1, el.getLineNumber());
        assertEquals("0*", CBTT.B, el.getType());
        assertEquals("0*", new Date(200), el.getStartTime());
        assertEquals("0*", "foo", el.getMessage());

        el = ls2.get(2);
        assertEquals("2*", 3, el.getLineNumber());
        assertEquals("2*", CBTT.A, el.getType());
        assertEquals("2*", new Date(400), el.getStartTime());
        assertEquals("2*", "tako", el.getMessage());
    }

    @Test
    public void testGetListAndClear() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");

        CaptureElement el;

        List<CaptureElement> ls1 = buf.getListAndClear();
        assertEquals("size of getList", 3, ls1.size());
        assertEquals("size of list", 0, buf.size());

        el = ls1.get(0);
        assertEquals("0", 0, el.getLineNumber());
        assertEquals("0", CBTT.A, el.getType());
        assertEquals("0", new Date(100), el.getStartTime());
        assertEquals("0", "hoge", el.getMessage());

        buf.add(CBTT.A, 400, "tako");
        List<CaptureElement> ls2 = buf.getList();
        assertEquals("size of getList", 1, ls2.size());
        assertEquals("size of list", 1, buf.size());

        el = ls2.get(0);
        assertEquals("0*", 3, el.getLineNumber());
        assertEquals("0*", CBTT.A, el.getType());
        assertEquals("0*", new Date(400), el.getStartTime());
        assertEquals("0*", "tako", el.getMessage());
    }

    @Test
    public void testClear() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");

        buf.clear();
        assertEquals("size of list", 0, buf.size());
        assertEquals("current LineNumber", 3, buf.getCurrentLineNumber());

        buf.add(CBTT.A, 400, "tako");
        assertEquals("size of list", 1, buf.size());
        assertEquals("current LineNumber", 4, buf.getCurrentLineNumber());
    }

    @Test
    public void testAllClear() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");

        buf.allClear();
        assertEquals("size of list", 0, buf.size());
        assertEquals("current LineNumber", 0, buf.getCurrentLineNumber());

        buf.add(CBTT.A, 400, "tako");
        assertEquals("size of list", 1, buf.size());
        assertEquals("current LineNumber", 1, buf.getCurrentLineNumber());
    }

    @Test
    public void testFind() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");
        buf.add(CBTT.A, 400, "oob");

        CaptureElement el = buf.find("oo");
        assertEquals("foo", el.getMessage());

        CaptureElement e2 = buf.find("oo", 2);
        assertEquals("oob", e2.getMessage());

        CaptureElement e3 = buf.find("ba");
        assertEquals("bar", e3.getMessage());

        CaptureElement e4 = buf.find("ba", 3);
        assertNull(e4);
    }

    @Test
    public void testFindLast() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");
        buf.add(CBTT.A, 400, "oob");

        CaptureElement el = buf.findLast("oo");
        assertEquals("oob", el.getMessage());

        CaptureElement e2 = buf.findLast("oo", 2);
        assertEquals("foo", e2.getMessage());

        CaptureElement e3 = buf.findLast("bo");
        assertNull(e3);

        CaptureElement e4 = buf.findLast("ho", 0);
        assertEquals("hoge", e4.getMessage());
}

    @Test
    public void testMatch() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");
        buf.add(CBTT.A, 400, "tako");

        CaptureElement el = buf.match("o$");
        assertEquals("foo", el.getMessage());

        CaptureElement e2 = buf.match("o$", 2);
        assertEquals("tako", e2.getMessage());

        CaptureElement e3 = buf.match("^b");
        assertEquals("bar", e3.getMessage());

        CaptureElement e4 = buf.match("[cde]", 1);
        assertNull(e4);
    }

    @Test
    public void testMatchLast() {
        CaptureBuffer buf = new CaptureBuffer(5);

        buf.add(CBTT.A, 100, "hoge");
        buf.add(CBTT.B, 200, "foo");
        buf.add(CBTT.A, 300, "bar");
        buf.add(CBTT.A, 400, "tako");

        CaptureElement el = buf.matchLast("[a-z]a");
        assertEquals("tako", el.getMessage());

        CaptureElement e2 = buf.matchLast("[a-z]a", 2);
        assertEquals("bar", e2.getMessage());

        CaptureElement e3 = buf.matchLast("[a-z]a", 1);
        assertNull(e3);

        CaptureElement e4 = buf.matchLast("o$", 2);
        assertEquals("foo", e4.getMessage());
    }
}
