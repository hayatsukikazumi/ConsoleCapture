package com.hayatsukikazumi.coc;
/*
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * created on 2019/11/03
 */
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ConsoleCaptureを使ったJunitテストケースのサンプル。
 *
 * @author Hayatsukikazumi
 */
public class SampleTest {

    @BeforeClass
    public static void beforeClass() {
        ConsoleCapture.init();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        // assert途中でエラー終了した場合を想定し、キャプチャ終了メソッドを呼ぶ
        ConsoleCapture.getInstance().stop();
    }

    @Test
    public void testSameBuffer() {

        // キャプチャ内容を保持
        CaptureBuffer buf = new CaptureBuffer();

        // キャプチャ開始
        ConsoleCapture.getInstance().start(buf, buf);

        // コンソール出力のある処理
        System.out.println("Output");
        System.err.println("Error Output");

        // キャプチャ終了
        ConsoleCapture.getInstance().stop();

        // 評価
        assertEquals(2, buf.size());
        assertNotNull(buf.find("Out"));
        assertNotNull(buf.find("Err"));
        assertNull(buf.find("Print"));
    }

    @Test
    public void testDifferentBuffers() {

        // キャプチャ内容を保持
        CaptureBuffer buf1 = new CaptureBuffer();
        CaptureBuffer buf2 = new CaptureBuffer();

        // キャプチャ開始
        ConsoleCapture.getInstance().start(buf1, buf2);

        // コンソール出力のある処理
        System.out.println("Output");
        System.err.println("Error Output");

        // キャプチャ終了
        ConsoleCapture.getInstance().stop();

        // 評価
        assertNotNull(buf1.find("Out"));
        assertNull(buf1.find("Err"));

        assertNotNull(buf2.find("Out"));
        assertNotNull(buf2.find("Err"));
    }

    @Test
    public void testPOutputToOriginalConsole() {

        // キャプチャ内容を保持
        CaptureBuffer buf1 = new CaptureBuffer();

        // キャプチャ開始
        ConsoleCapture.getInstance().start(buf1, buf1, false);

        // コンソール出力のある処理
        System.out.println("Output test2");
        System.err.println("Error Output test2");

        // 元のコンソールにも出力する
        ConsoleCapture.getInstance().redirectToOriginal(true);

        // コンソール出力のある処理
        System.out.println("Output to console");
        System.err.println("Error Output to console");

        // キャプチャ終了
        ConsoleCapture.getInstance().stop();

        // 評価
        assertEquals(4, buf1.size());
        assertEquals(ConsoleCapture.Type.ERR, buf1.find("Error").getType());

        // キャプチャ内容を出力
        for (CaptureElement el : buf1.getList()) {
            System.out.println(el);
        }
    }
}
