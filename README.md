# ConsoleCapture

## 何のソフトなの？

このプログラムは、 コンソール出力(System.out, System.err)を文字列に
キャプチャするためのライブラリです。

Junit等でコンソール出力をログで目視する代わりに作成してみました。

src/test/java/com/hayatsukikazumi/coc/SampleTest.java に大まかな使い方
のイメージを示しました。

## 動作条件

Java（バージョン1.7以降の実行環境）をインストールしたマシン
（Pure JavaなのでOSは問いません）

## 使用方法（JUnit 4で使う場合の例）
* beforeClass() メソッド内
    1. ConsoleCapture.init() を実行

```
    @BeforeClass
    public static void beforeClass() {
        ConsoleCapture.init();
    }
```

* tearDown() メソッド内
    1. ConsoleCapture#stop() を実行（テストメソッド途中の例外などを考慮）

```
    @After
    public void tearDown() throws Exception {
        ConsoleCapture.getInstance().stop();
    }
```

* 各テストメソッド内
    1. CaptureBuffer のインスタンスを生成
    2. 1.で生成したインスタンスを引数に、ConsoleCapture#start() を実行し、キャプチャ開始
    3. コンソール出力のあるテスト対象処理を実行
    4. ConsoleCapture#stop() を実行し、キャプチャ終了
    5. CaptureBuffer クラスの各メソッドを実行し、実行結果を評価

```
    @Test
    public void testSameBuffer() {
        // 1.
        CaptureBuffer buf = new CaptureBuffer();
        // 2.
        ConsoleCapture.getInstance().start(buf, buf);
        // 3.
        System.out.println("Output");
        System.err.println("Error Output");
        // 4.
        ConsoleCapture.getInstance().stop();
        // 5.
        assertEquals(2, buf.size());
        assertNotNull(buf.find("Out"));
        assertNotNull(buf.find("Err"));
        assertNull(buf.find("Print"));
    }
```

## 変更履歴

* 2019/11/03 Ver. 1.0.0を公開
* 2019/11/07 Ver. 1.0.1を公開
    * ConsoleCapture#start(CaptureBuffer, CaptureBuffer, boolean) メソッド追加
    * コメント修正
* 2019/11/09 Ver. 1.0.2を公開
    * CaptureBuffer#getList() の実際の戻り値を ArrayList に変更
* 2019/11/16 Ver. 1.0.3を公開
    * コメント修正、リファクタリング
* 2020/08/29 Ver. 1.0.4を公開
    * ConsoleCapture#init() メソッド追加

--------
Copyright(c) はやつきかづみ（早月加積） 2019-2020

zzzszoo200@gmail.com

