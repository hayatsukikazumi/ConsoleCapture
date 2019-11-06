package com.hayatsukikazumi.coc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 書き込んだ内容を、1行ごとにキャプチャーするOutputStream。 スレッドセーフではない。
 *
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * @author Hayatsukikazumi
 * @date 2019/11/03
 */
public class CaptureOutputStream extends ByteArrayOutputStream {

    private static final int BUF_SIZE = 2048;
    private final byte[] newLine;

    private CaptureListener listener;

    private long startTime;
    private Enum<?> type;

    /**
     * コンストラクタ。
     * @param typ このストリームのタイプ
     * @param lsnr キャプチャー時に呼び出されるlistener
     */
    public CaptureOutputStream(Enum<?> typ, CaptureListener lsnr) {
        super(BUF_SIZE);
        startTime = Long.MIN_VALUE;
        type = typ;
        listener = lsnr;
        newLine = System.lineSeparator().getBytes();
    }

    @Override
    public void flush() throws IOException {
        if (onFlushed(false)) {
            reset();
        }
    }

    @Override
    public void close() throws IOException {
        onFlushed(true);
        reset();
    }

    /**
     * flush、closeが呼び出された時の処理
     * @param forced 強制キャプチャする？
     * @return 全部をキャプチャに吐き出したらtrue
     */
    private boolean onFlushed(boolean forced) {

        if (startTime == Long.MIN_VALUE) {
            startTime = System.currentTimeMillis();
        }

        int len = size();
        if (len == 0 || (!forced && len < BUF_SIZE && !endsWithNewLine(buf, len))) return false;

        if (listener != null) {
            listener.onCapture(type, startTime, new String(buf, 0, len));
        }

        startTime = Long.MIN_VALUE;

        return true;
    }

    /**
     * 改行文字で終わるかを返す。
     * @param buf
     * @param len 最初から何バイト目まで見るか
     * @return
     */
    private boolean endsWithNewLine(byte[] buf, int len) {
        if (len < this.newLine.length) return false;

        int pos = len - this.newLine.length;
        for (int i = 0; i < this.newLine.length; i++) {
            if (buf[pos++] != this.newLine[i]) return false;
        }

        return true;
    }
}
