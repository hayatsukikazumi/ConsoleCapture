package com.hayatsukikazumi.coc;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * キャプチャ結果のログを保持するバッファ。
 * @author Hayatsukikazumi
 * @date 2019/11/03
 */
public class CaptureBuffer {

    private AtomicInteger lineNumber = new AtomicInteger();
    private int bufSize;
    private List<CaptureElement> captureList = Collections.synchronizedList(new LinkedList<CaptureElement>());

    /**
     * コンストラクタ。
     * デフォルトの最大保持行数(1024)
     */
    public CaptureBuffer() {
        this(1024);
    }

    /**
     * コンストラクタ。
     * @param max 最大保持行数
     */
    public CaptureBuffer(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Cannot set negative value.");
        }

        bufSize = max;
    }

    /**
     * 結果を追加する。
     * @param type ログのタイプ
     * @param startTime 出力開始時刻
     * @param message メッセージ
     */
    public void add(Enum<?> type, long startTime, String message) {

        this.captureList.add(new CaptureElement(
                lineNumber.getAndIncrement(), type, startTime, message));

        if (bufSize < captureList.size()) {
            synchronized (captureList) {
                Iterator<CaptureElement> it = captureList.iterator();
                it.next();
                it.remove();
            }
        }
    }

    /**
     * 現在保持しているログのサイズを返す。
     * @return 保持しているログのサイズ
     */
    public int size() {
        return captureList.size();
    }

    /**
     * 現在の行数を返す。
     * @return 現在の行数（＝何行出力したか）
     */
    public int getCurrentLineNumber() {
        return lineNumber.get();
    }

    /**
     * 保持しているログのコピーを返す。
     * @return 保持しているログのコピー
     */
    public List<CaptureElement> getList() {
        return new LinkedList<CaptureElement>(captureList);
    }

    /**
     * 保持しているログのコピーを返し、ログをクリアする。
     * @return 保持しているログのコピー
     */
    public List<CaptureElement> getListAndClear() {
        List<CaptureElement> retList = new LinkedList<CaptureElement>();
        synchronized (captureList) {
            Iterator<CaptureElement> it = captureList.iterator();
            while (it.hasNext()) {
                retList.add(it.next());
                it.remove();
            }
        }

        return retList;
    }

    /**
     * ログのクリア。行数は0に戻さない。
     */
    public void clear() {
        captureList.clear();
    }

    /**
     * ログのクリア。行数も0に戻す。
     */
    public void allClear() {
        synchronized (captureList) {
            lineNumber.set(0);
            captureList.clear();
        }
    }

    /**
     * 保持しているログから検索文字列のある最初の結果を返す。
     * @param needle 検索文字列
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement find(String needle) {
        List<CaptureElement> haystack = getList();
        return find(needle, haystack, Integer.MIN_VALUE);
    }

    /**
     * 保持しているログから検索文字列のある最初の結果を返す。
     * @param needle 検索文字列
     * @param lineNum 検索を開始するCaptureElementの行番号
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement find(String needle, int lineNum) {
        List<CaptureElement> haystack = getList();
        return find(needle, haystack, lineNum);
    }

    /**
     * ログから検索文字列のある最初の結果を返す。
     * @param needle 検索文字列
     * @param haystack ログ
     * @param lineNum 検索を開始するCaptureElementの行番号（Integer.MIN_VALUE=最初から検索）
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    private static CaptureElement find(String needle, List<CaptureElement> haystack, int lineNum) {
        for (CaptureElement elem : haystack) {
            if (elem.getLineNumber() < lineNum) continue;
            if (elem.getMessage().indexOf(needle) != -1) return elem;
        }

        return null;
    }

    /**
     * 保持しているログから検索文字列のある最後の結果を返す。
     * @param needle 検索文字列
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement findLast(String needle) {
        List<CaptureElement> haystack = getList();
        return findLast(needle, haystack, Integer.MAX_VALUE);
    }

    /**
     * 保持しているログから検索文字列のある最後の結果を返す。
     * @param needle 検索文字列
     * @param lineNum 検索を開始するCaptureElementの行番号
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement findLast(String needle, int lineNum) {
        List<CaptureElement> haystack = getList();
        return findLast(needle, haystack, lineNum);
    }

    /**
     * ログから検索文字列のある最後の結果を返す。
     * @param needle 検索文字列
     * @param haystack ログ
     * @param lineNum 検索を開始するCaptureElementの行番号（Integer.MAX_VALUE=最初から検索）
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    private static CaptureElement findLast(String needle, List<CaptureElement> haystack, int lineNum) {
        for (ListIterator<CaptureElement> it = haystack.listIterator(haystack.size()); it.hasPrevious();) {
            CaptureElement elem = it.previous();
            if (elem.getLineNumber() > lineNum) continue;
            if (elem.getMessage().indexOf(needle) != -1) return elem;
        }

        return null;
    }

    /**
     * 保持しているログから検索文字列の正規表現に一致する最初の結果を返す。
     * @param pattern 正規表現
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement match(String pattern) {
        List<CaptureElement> haystack = getList();
        return match(pattern, haystack, Integer.MIN_VALUE);
    }

    /**
     * 保持しているログから検索文字列の正規表現に一致する最初の結果を返す。
     * @param needle 検索文字列
     * @param lineNum 検索を開始するCaptureElementの行番号
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement match(String needle, int lineNum) {
        List<CaptureElement> haystack = getList();
        return match(needle, haystack, lineNum);
    }

    /**
     * ログから検索文字列の正規表現に一致する最初の結果を返す。
     * @param pattern 正規表現
     * @param haystack ログ
     * @param lineNum 検索を開始するCaptureElementの行番号（Integer.MIN_VALUE=最初から検索）
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    private static CaptureElement match(String pattern, List<CaptureElement> haystack, int lineNum) {

        Pattern p = Pattern.compile(pattern);

        for (CaptureElement elem : haystack) {
            if (elem.getLineNumber() < lineNum) continue;
            Matcher m = p.matcher(elem.getMessage());
            if (m.find()) return elem;
        }

        return null;
    }

    /**
     * 保持しているログから検索文字列の正規表現に一致する最後の結果を返す。
     * @param pattern 正規表現
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement matchLast(String pattern) {
        List<CaptureElement> haystack = getList();
        return matchLast(pattern, haystack, Integer.MAX_VALUE);
    }

    /**
     * 保持しているログから検索文字列の正規表現に一致する最後の結果を返す。
     * @param needle 検索文字列
     * @param lineNum 検索を開始するCaptureElementの行番号
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    public CaptureElement matchLast(String needle, int lineNum) {
        List<CaptureElement> haystack = getList();
        return matchLast(needle, haystack, lineNum);
    }


    /**
     * ログから検索文字列の正規表現に一致する最後の結果を返す。
     * @param pattern 正規表現
     * @param haystack ログ
     * @param lineNum 検索を開始するCaptureElementの行番号（Integer.MAX_VALUE=最後から検索）
     * @return 当該文字列が存在するキャプチャ結果。ヒットしない場合はnull
     */
    private static CaptureElement matchLast(String pattern, List<CaptureElement> haystack, int lineNum) {

        Pattern p = Pattern.compile(pattern);

        for (ListIterator<CaptureElement> it = haystack.listIterator(haystack.size()); it.hasPrevious();) {
            CaptureElement elem = it.previous();
            if (elem.getLineNumber() > lineNum) continue;
            Matcher m = p.matcher(elem.getMessage());
            if (m.find()) return elem;
        }

        return null;
    }
}
