package com.hayatsukikazumi.coc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * ConsoleCaptureを使ったJunitテストケースのサンプル。
 * @author Hayatsukikazumi
 */
public class SampleTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        // assert途中でエラー終了した場合を想定し、キャプチャ終了メソッドを呼ぶ
        ConsoleCapture.getInstance().stop();
    }

    @Test
    public void test1() {

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
    public void test2() {

        // キャプチャ内容を保持
        CaptureBuffer buf1 = new CaptureBuffer();

        // キャプチャ開始
        ConsoleCapture cap = ConsoleCapture.getInstance();
        cap.redirectToOriginel(false); // 標準出力に出さない
        cap.start(buf1, buf1);

        // コンソール出力のある処理
        System.out.println("Output");
        System.err.println("Error Output");

        cap.stop();

        // 評価
        assertEquals(2, buf1.size());

        // キャプチャ内容を出力
        for (CaptureElement el : buf1.getList()) {
            System.out.println(el);
        }
    }
}
