package com.hayatsukikazumi.coc;

import java.io.PrintStream;

/**
 * コンソール出力をキャプチャする。
 *
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * created on 2019/11/03
 * @author Hayatsukikazumi
 */
public class ConsoleCapture {

    /**
     * キャプチャ出力の型
     */
    public enum Type {
        OUT, ERR
    }

    private static final String CAPTURE_MARK = "[C]";

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private CaptureOutputStream newOut = null;
    private CaptureOutputStream newErr = null;

    private CaptureListener lsnrOut = new OutListener();
    private CaptureListener lsnrErr = new ErrListener();

    private CaptureBuffer bufOut = null;
    private CaptureBuffer bufErr = null;

    private boolean inCapture = false;

    private boolean redirect = true;

    /**
     * デフォルトコンストラクタは使用禁止
     */
    @SuppressWarnings("unused")
    private ConsoleCapture() {
    }

    /**
     * このクラスのインスタンスを得る。
     * @return このクラスのSingletonインスタンス
     */
    public static ConsoleCapture getInstance() {
        return ConsoleCaptureHolder.TheInstance;
    }

    /**
     * キャプチャを開始する。
     * 2つの引数に同じCaptureBufferのインスタンスを指定してもよい。
     * 引数にnullを指定した場合、その出力はキャプチャしない。
     * 既に開始済みの場合は無視される。
     * （キャプチャを終了しないとCaptureBufferを変更できない）
     * 元の標準出力への出力は、初回起動時はtrue（出力する）で、それ以降は直前の状態を記憶する。
     * @param outBuf 標準出力用CaptureBuffer
     * @param errBuf エラー出力用CaptureBuffer
     */
    public synchronized void start(CaptureBuffer outBuf, CaptureBuffer errBuf) {
        if (!inCapture) {
            bufOut = outBuf;
            bufErr = errBuf;

            inCapture = true;
            newOut = new CaptureOutputStream(Type.OUT, lsnrOut);
            newErr = new CaptureOutputStream(Type.ERR, lsnrErr);

            System.setOut(new TSPrintStream(newOut));
            System.setErr(new TSPrintStream(newErr));
        }
    }

    /**
     * キャプチャを開始する。
     * 2つの引数に同じCaptureBufferのインスタンスを指定してもよい。
     * 引数にnullを指定した場合、その出力はキャプチャしない。
     * 既に開始済みの場合は無視される。
     * （キャプチャを終了しないとCaptureBufferを変更できない）
     * @param outBuf 標準出力用CaptureBuffer
     * @param errBuf エラー出力用CaptureBuffer
     * @param oto 元の標準出力にも出力する場合はtrue
     * @since 1.0.1
     */
    public synchronized void start(CaptureBuffer outBuf, CaptureBuffer errBuf, boolean oto) {
        redirect = oto;
        start(outBuf, errBuf);
    }

    /**
     * キャプチャを終了する。
     * 既に終了済みの場合は何もしない。
     */
    public synchronized void stop() {
        if (inCapture) {
            System.setOut(originalOut);
            System.setErr(originalErr);
            inCapture = false;

            try {
                newOut.close();
            } catch (Exception e) {
                // do nothing
            }

            try {
                newErr.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    /**
     * 元の標準出力にも出力するかを設定する。
     * @param oto 元の標準出力にも出力する場合はtrue
     */
    public void redirectToOriginal(boolean oto) {
        redirect = oto;
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }

    /**
     * 標準出力キャプチャのリスナー。
     */
    private class OutListener implements CaptureListener {
        public void onCapture(Enum<?> type, long startTime, String message) {
            if (bufOut != null) {
                bufOut.add(type, startTime, message);
            }
            if (redirect) {
                originalOut.print(CAPTURE_MARK);
                originalOut.print(message);
            }
        }
    }

    /**
     * 標準エラー出力キャプチャのリスナー。
     */
    private class ErrListener implements CaptureListener {
        public void onCapture(Enum<?> type, long startTime, String message) {
            if (bufErr != null) {
                bufErr.add(type, startTime, message);
            }
            if (redirect) {
                originalErr.print(CAPTURE_MARK);
                originalErr.print(message);
            }
        }
    }

    /**
     * Singleton instanceを保持する。
     */
    private static class ConsoleCaptureHolder {
        private static final ConsoleCapture TheInstance = new ConsoleCapture();
    }
}
