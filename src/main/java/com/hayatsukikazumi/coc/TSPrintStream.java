package com.hayatsukikazumi.coc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * スレッドセーフなPrintStream。
 *
 * https://github.com/hayatsukikazumi/ConsoleCapture
 * @author Hayatsukikazumi
 * @date 2019/11/03
 */
public class TSPrintStream extends PrintStream {

    public TSPrintStream(OutputStream o) {
        super(o, true); //flushする
    }

    @Override
    public synchronized void flush() {
        super.flush();
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public synchronized void write(int b) {
        super.write(b);
    }

    @Override
    public synchronized void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
    }

    @Override
    public synchronized void print(boolean b) {
        super.print(b);
    }

    @Override
    public synchronized void print(char c) {
        super.print(c);
    }

    @Override
    public synchronized void print(int i) {
        super.print(i);
    }

    @Override
    public synchronized void print(long l) {
        super.print(l);
    }

    @Override
    public synchronized void print(float f) {
        super.print(f);
    }

    @Override
    public synchronized void print(double d) {
        super.print(d);
    }

    @Override
    public synchronized void print(char[] s) {
        super.print(s);
    }

    @Override
    public synchronized void print(String s) {
        super.print(s);
    }

    @Override
    public synchronized void print(Object obj) {
        super.print(obj);
    }

    @Override
    public synchronized void println() {
        super.println();
    }

    @Override
    public synchronized void println(boolean x) {
        super.println(x);
    }

    @Override
    public synchronized void println(char x) {
        super.println(x);
    }

    @Override
    public synchronized void println(int x) {
        super.println(x);
    }

    @Override
    public synchronized void println(long x) {
        super.println(x);
    }

    @Override
    public synchronized void println(float x) {
        super.println(x);
    }

    @Override
    public synchronized void println(double x) {
        super.println(x);
    }

    @Override
    public synchronized void println(char[] x) {
        super.println(x);
    }

    @Override
    public synchronized void println(String x) {
        super.println(x);
    }

    @Override
    public synchronized void println(Object x) {
        super.println(x);
    }

    @Override
    public synchronized PrintStream printf(String format, Object... args) {
        return super.printf(format, args);
    }

    @Override
    public synchronized PrintStream printf(Locale l, String format, Object... args) {
        return super.printf(l, format, args);
    }

    @Override
    public synchronized PrintStream format(String format, Object... args) {
        return super.format(format, args);
    }

    @Override
    public synchronized PrintStream format(Locale l, String format, Object... args) {
        return super.format(l, format, args);
    }

    @Override
    public synchronized PrintStream append(CharSequence csq) {
        return super.append(csq);
    }

    @Override
    public synchronized PrintStream append(CharSequence csq, int start, int end) {
        return super.append(csq, start, end);
    }

    @Override
    public synchronized PrintStream append(char c) {
        return super.append(c);
    }

    @Override
    public synchronized void write(byte[] b) throws IOException {
        super.write(b);
    }

}
