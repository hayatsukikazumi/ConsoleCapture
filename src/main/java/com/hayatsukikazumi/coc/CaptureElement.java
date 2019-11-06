package com.hayatsukikazumi.coc;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * イミュータブルな1行のキャプチャ結果。
 *
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * @author Hayatsukikazumi
 * @date 2019/11/03
 */
public class CaptureElement implements Serializable {

    private static final long serialVersionUID = -4192858923818207578L;
    private static final String DATE_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String SEP = "\t";

    private final int lineNumber;
    private final Enum<?> type;
    private final long startTime;
    private final long endTime = System.currentTimeMillis();
    private final String message;

    /**
     * コンストラクタ。
     * @param lineNum 行番号
     * @param typ ログのタイプ
     * @param stt この行の出力開始時刻
     * @param msg メッセージ（nullの場合は空文字で保持）
     */
    protected CaptureElement(int lineNum, Enum<?> typ, long stt, String msg) {
        lineNumber = lineNum;
        type = typ;
        startTime = stt;
        message = (msg == null) ? "" : msg;
    }

    /**
     * 行番号を取得する。
     * @return 行番号
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * ログのタイプを取得する。
     * @return ログのタイプ
     */
    public Enum<?> getType() {
        return type;
    }

    /**
     * 出力開始時刻を取得する。
     * @return 出力開始時刻
     */
    public Date getStartTime() {
        return new Date(startTime);
    }

    /**
     * 出力終了時刻を取得する。
     * @return 出力終了時刻
     */
    public Date getEndTime() {
        return new Date(endTime);
    }

    /**
     * メッセージを取得する。
     * @return メッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * 文字列表現を返す。
     * @return 行番号、タイプ、開始時刻、終了時刻、メッセージをタブで区切った文字列
     */
    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat(DATE_FMT);
        StringBuilder sb = new StringBuilder();

        sb.append(lineNumber);
        sb.append(SEP);
        sb.append(type);
        sb.append(SEP);
        sb.append(df.format(getStartTime()));
        sb.append(SEP);
        sb.append(df.format(getEndTime()));
        sb.append(SEP);
        sb.append(message);

        return sb.toString();
    }
}
