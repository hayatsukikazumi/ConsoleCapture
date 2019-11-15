package com.hayatsukikazumi.coc;

/**
 * 1行キャプチャのイベントリスナー。
 *
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * created on 2019/11/03
 * @author Hayatsukikazumi
 */
public interface CaptureListener {

    /**
     * 1行キャプチャ時に呼ばれる。
     * メッセージは最後の改行文字も含まれる（1行長さ制限のため途中で切れた場合を除く）
     * @param type ログのタイプ
     * @param startTime この行の出力開始時刻
     * @param message メッセージ
     */
    public void onCapture(Enum<?> type, long startTime, String message);
}
