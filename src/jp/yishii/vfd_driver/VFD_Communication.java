package jp.yishii.vfd_driver;

import java.io.UnsupportedEncodingException;

/*
 * Noritake ITRON's VFD Command creator class
 * for Android
 * 
 * Tested with Noritake ITRON's GU-256X64-3101
 * 
 * Coded by Yasuhiro ISHII
 * 
 * most of the APIs in this class are compatible with Noritake ITRON's C++ Control class for VC++/CLI.
 */

public class VFD_Communication {
	private static final String TAG = "VFD_Communication";

	private VFD_Driver mVFD_Driver;

	private static byte VAL_ESC = 0x1b;
	private static byte VAL_US = 0x1f;

	public VFD_Communication(VFD_Driver vd) {
		mVFD_Driver = vd;
	}

	/** 各種設定を初期状態にする
	 * 
	 */
	public void initialize() {
		byte[] cmd = new byte[2];
		cmd[0] = VAL_ESC;
		cmd[1] = 0x40;

		mVFD_Driver.write(cmd);
	}

	/** 表示輝度の設定
	 * 
	 * @param Percent : 輝度(%)
	 */
	public void brightness(int Percent) {
		byte[] cmd = new byte[3];

		if (Percent >= 100) {
			Percent = 100;
		}

		cmd[0] = VAL_US;
		cmd[1] = 0x58;
		cmd[2] = (byte) ((Percent * 0x8 / 100) + 0x10);

		mVFD_Driver.write(cmd);
	}

	/** カーソル位置を表示メモリの指定位置に移動する
	 * 
	 * @param X : カーソル位置(x)
	 * @param Y : カーソル位置(y)
	 */
	public void cursorSet(int X, int Y) {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x24;

		cmd[2] = (byte) (X % 256);
		cmd[3] = (byte) ((X / 256) % 256);
		cmd[4] = (byte) (Y % 256);
		cmd[5] = (byte) ((Y / 256) % 256);

		mVFD_Driver.write(cmd);
	}

	/** カーソルの表示ON/OFFを制御する
	 * 
	 * @param on カーソルのON/OFF指定。true=カーソルON、false=カーソルOFF
	 */
	
	public void cursorOn(boolean on) {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x43;
		if (on) {
			cmd[2] = 0x01;
		} else {
			cmd[2] = 0x00;
		}

		mVFD_Driver.write(cmd);
	}

	/** 画面モードの選択
	 * 
	 * @param a 0x00=表示画面モード、0x01=全画面モード
	 */
	
	public void screenModeSelect(int a) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x77;
		cmd[3] = 0x10;
		if (a == 0x00) {
			cmd[4] = 0x00;
		} else {
			cmd[4] = 0x01;
		}

		mVFD_Driver.write(cmd);
	}

	/** 国際文字セットの選択
	 * 
	 * @param n 文字セット
	 * 0x00 アメリカ
	 * 0x01 フランス
	 * 0x02 ドイツ
	 * 0x03 イギリス
	 * 0x04 デンマーク
	 * 0x05 スウェーデン
	 * 0x06 イタリア
	 * 0x07 スペイン
	 * 0x08 日本
	 * 0x09 ノルウェー
	 * 0x0A デンマーク
	 * 0x0B スペイン
	 * 0x0C ラテンアメリカ
	 * 0x0D 韓国
	 * 
	 */
	public void internationalFontSet(int n) {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_ESC;
		cmd[1] = 0x52;
		cmd[2] = (byte) n;

		mVFD_Driver.write(cmd);
	}

	/** キャラクタコード表の選択
	 * 
	 * @param n 文字種
	 * 00h	PC437（USA：Standard Europe）	
	 * 01h	カタカナ	
	 * 02h	PC850（Multilingual）	
	 * 03h	PC860（Portuguese）	
	 * 04h	PC863（Canadian-French）	
	 * 05h	PC865（Nordic）	
	 * 10h	WPC1252	
	 * 11h	PC866（Cyrillic #2）	
	 * 12h	PC852（Latin 2）	
	 * 13h	PC858	
	 * FFh	ユーザーテーブル	
	 *
	 */
	public void characterCodeType(int n) {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_ESC;
		cmd[1] = 0x74;
		cmd[2] = (byte) n;

		mVFD_Driver.write(cmd);
	}

	/** 表示モードをオーバライトモードに設定する
	 * 
	 */
	public void endOfLineMode_overWrite() {
		byte[] cmd = new byte[2];

		cmd[0] = VAL_US;
		cmd[1] = 0x01;

		mVFD_Driver.write(cmd);
	}

	/** 表示モードを縦スクロールモードに設定する
	 * 
	 */
	public void endOfLineMode_VScroll() {
		byte[] cmd = new byte[2];

		cmd[0] = VAL_US;
		cmd[1] = 0x02;

		mVFD_Driver.write(cmd);
	}

	/** 表示モードを横スクロールモードに設定する
	 * 
	 */
	public void endOfLineMode_HScroll() {
		byte[] cmd = new byte[2];

		cmd[0] = VAL_US;
		cmd[1] = 0x03;

		mVFD_Driver.write(cmd);
	}

	/** 横スクロールモードのスクロール速度指定
	 * 
	 * @param n 速度
	 * 0x00 即時表示
	 * 0x01 T[ms/2dots]
	 * 0x02〜0x1F (n-1)T [ms/dot]
	 */
	public void horizontalScrollSpeed(int n) {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x73;
		cmd[2] = (byte) n;

		mVFD_Driver.write(cmd);
	}

	/** 1バイトコード文字のフォントサイズ指定
	 * 
	 * @param m フォントサイズ
	 * 0x01 6×8ドット
	 * 0x02 8×16ドット
	 * 0x04 16×32ドット
	 */
	public void fontSize(int m) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x01;
		cmd[4] = (byte) m;

		mVFD_Driver.write(cmd);
	}

	/** 2バイト文字モードを指定または解除します
	 * 
	 * @param m
	 */
	public void specify2ByteCharaMode(boolean m) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x01;
		if (m) {
			cmd[4] = 1;
		} else {
			cmd[4] = 0;
		}

		mVFD_Driver.write(cmd);
	}

	/** 2バイト文字タイプを指定
	 * 
	 * @param m 文字タイプ
	 * 
	 * 0x00	日本語フォントを指定します		JIS X0208(SHIFT-JIS)	
	 * 0x01	韓国語フォントを指定します		KSC5601-87	
	 * 0x02	中国簡体語を指定します			GB2312-80	
	 * 03	中国繁体語を指定します			Big-5	
	 * 
	 */
	public void select2ByteCharType(int m) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x03;
		cmd[4] = (byte) m;

		mVFD_Driver.write(cmd);
	}

	/** キャラクタの表示倍率の指定
	 * 
	 * @param X X方向倍率
	 * @param Y Y方向倍率
	 */
	public void fontMagnify(int X, int Y) {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x40;
		cmd[4] = (byte) X;
		cmd[5] = (byte) Y;

		mVFD_Driver.write(cmd);
	}

	/** キャラクタのボールド表示の指定・解除を行う
	 * 
	 * @param bold ボールド指定
	 */
	public void characterBold(boolean bold) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x41;
		if (bold) {
			cmd[4] = 0x01;
		} else {
			cmd[4] = 0x00;
		}

		mVFD_Driver.write(cmd);
	}

	/** 指定時間分コマンド/データ処理を停止する
	 * 
	 * @param t_halfSec 時間(0.5s)
	 */
	public void wait(byte t_halfSec) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x01;
		cmd[4] = t_halfSec;

		mVFD_Driver.write(cmd);
	}

	/** 指定時間分コマンド/データ処理を停止する
	 * 
	 * @param t_halfSec 時間(14ms)
	 */
	public void shortWait(byte t_14mSec) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x02;
		cmd[4] = t_14mSec;

		mVFD_Driver.write(cmd);
	}

	/** 表示画面のシフトを指定回数分行う
	 * 
	 * @param width
	 * @param count
	 * @param speed
	 */
	public void scrollAction(int width, int count, int speed) {
		byte[] cmd = new byte[9];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x10;
		cmd[4] = (byte) (width % 256);
		cmd[5] = (byte) ((width / 256) % 256);
		cmd[6] = (byte) (count % 256);
		cmd[7] = (byte) ((count / 256) % 256);
		cmd[8] = (byte) speed;

		mVFD_Driver.write(cmd);
	}

	/** 表示画面のブリンクを行う
	 * 
	 * @param pattern
	 * @param onTime
	 * @param offtime
	 * @param numberOfRepeat
	 */
	public void displayBlinkAction(byte pattern, byte onTime, byte offtime,
			byte numberOfRepeat) {
		byte[] cmd = new byte[8];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x11;
		cmd[4] = pattern;
		cmd[5] = onTime;
		cmd[6] = offtime;
		cmd[7] = numberOfRepeat;

		mVFD_Driver.write(cmd);
	}

	/** 表示エリアのカーテン表示を行う
	 * 
	 * @param direction
	 * @param speed
	 * @param pattern
	 */
	public void curtainAction(int direction, int speed, int pattern) {
		byte[] cmd = new byte[7];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x12;
		cmd[4] = (byte) direction;
		cmd[5] = (byte) speed;
		cmd[6] = (byte) pattern;

		mVFD_Driver.write(cmd);
	}

	/** 表示エリアの湧き出し表示を行う
	 * 
	 * @param direction
	 * @param speed
	 * @param patternAddress
	 */
	public void springAction(int direction, int speed, int patternAddress) {
		byte[] cmd = new byte[8];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x13;
		cmd[4] = (byte) direction;
		cmd[5] = (byte) speed;
		cmd[6] = (byte) (patternAddress % 256);
		cmd[7] = (byte) ((patternAddress / 256) % 256);

		mVFD_Driver.write(cmd);
	}

	/** 表示エリアの表示を行う(パターンはランダム)
	 * 
	 * @param speed
	 * @param patternAddress
	 */
	public void randomAction(int speed, int patternAddress) {
		byte[] cmd = new byte[7];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x14;
		cmd[4] = (byte) speed;
		cmd[5] = (byte) (patternAddress % 256);
		cmd[6] = (byte) ((patternAddress / 256) % 256);

		mVFD_Driver.write(cmd);
	}

	/** 表示用電源のON/OFF制御を行う
	 * 
	 * @param on
	 */
	public void displayPowerOn(boolean on) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x61;
		cmd[3] = 0x40;
		if (on) {
			cmd[4] = 0x01;
		} else {
			cmd[4] = 0x00;
		}

		mVFD_Driver.write(cmd);
	}

	/** 任意位置にドットをセット/リセットする
	 * 
	 * @param pen
	 * @param X
	 * @param Y
	 */
	public void dotPatternDraw(boolean pen, int X, int Y) {
		byte[] cmd = new byte[9];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x64;
		cmd[3] = 0x10;
		if (pen) {
			cmd[4] = 0x01;
		} else {
			cmd[4] = 0x00;
		}
		cmd[5] = (byte) (X % 256);
		cmd[6] = (byte) ((X / 256) % 256);
		cmd[7] = (byte) (Y % 256);
		cmd[8] = (byte) ((Y / 256) % 256);

		mVFD_Driver.write(cmd);
	}

	/** 指定された描画位置にライン/ボックス/ボックスフィルを行う
	 * 
	 * @param mode 0=ライン、1=ボックス、2=ボックスフィル
	 * @param pen
	 * @param X1
	 * @param Y1
	 * @param X2
	 * @param Y2
	 */
	public void lineBoxDraw(int mode, boolean pen, int X1, int Y1, int X2,
			int Y2) {
		byte[] cmd = new byte[14];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x64;
		cmd[3] = 0x11;
		cmd[4] = (byte) (mode % 256);
		if (pen) {
			cmd[5] = 0x01;
		} else {
			cmd[5] = 0x00;
		}
		cmd[6] = (byte) (X1 % 256);
		cmd[7] = (byte) ((X1 / 256) % 256);
		cmd[8] = (byte) (Y1 % 256);
		cmd[9] = (byte) ((Y1 / 256) % 256);

		cmd[10] = (byte) (X2 % 256);
		cmd[11] = (byte) ((X2 / 256) % 256);
		cmd[12] = (byte) (Y2 % 256);
		cmd[13] = (byte) ((Y2 / 256) % 256);

		mVFD_Driver.write(cmd);
	}

	/** カーソル位置にビットイメージデータを表示する
	 * 
	 * @param X
	 * @param Y
	 * @param d
	 */
	public void realTimeBitImageDisplay(int X, int Y, byte[] d) {
		if (d.length < (X * Y)) {
			// this action is same as NoritakeITRON's C++ Sample
			this.print("*Error*");
		} else {
			byte[] cmd = new byte[X * Y + 9];

			cmd[0] = VAL_US;
			cmd[1] = 0x28;
			cmd[2] = 0x66;
			cmd[3] = 0x11;
			cmd[4] = (byte) (X % 256);
			cmd[5] = (byte) ((X / 256) % 256);
			cmd[6] = (byte) (Y % 256);
			cmd[7] = (byte) ((Y / 256) % 256);
			cmd[8] = 1;
			System.arraycopy(d, 0, cmd, 9, X * Y);

			mVFD_Driver.write(cmd);
		}
	}

	/** 指定したビットイメージ(最大1024バイト)をRAM上に定義する
	 * 
	 * @param address
	 * @param size
	 * @param d
	 */
	public void ramBitImageDefinition(int address, int size, byte[] d) {
		byte[] cmd = new byte[d.length + 10];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x66;
		cmd[3] = 0x01;
		cmd[4] = (byte) (address % 0x100);
		cmd[5] = (byte) ((address / 0x100) % 0x100);
		cmd[6] = (byte) ((address / 0x10000) % 0x100);
		cmd[7] = (byte) (size % 0x100);
		cmd[8] = (byte) ((size / 0x100) % 0x100);
		cmd[9] = (byte) ((size / 0x10000) % 0x100);
		System.arraycopy(d, 0, cmd, 10, d.length);

		mVFD_Driver.write(cmd);
	}

	/** FROMにビットイメージを定義する
	 * 
	 * @param address
	 * @param size
	 * @param d
	 */
	public void FromBitImageDefinition(int address, int size, byte[] d) {
		byte[] cmd = new byte[d.length + 10];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x10;
		cmd[4] = (byte) (address % 0x100);
		cmd[5] = (byte) ((address / 0x100) % 0x100);
		cmd[6] = (byte) ((address / 0x10000) % 0x100);
		cmd[7] = (byte) (size % 0x100);
		cmd[8] = (byte) ((size / 0x100) % 0x100);
		cmd[9] = (byte) ((size / 0x10000) % 0x100);
		System.arraycopy(d, 0, cmd, 10, d.length);

		mVFD_Driver.write(cmd);
	}

	/** RAMまたはFROM内のイメージをカーソル位置に展開する
	 * 
	 * @param memory
	 * @param address
	 * @param imageSizeY
	 * @param displaySizeX
	 * @param displaySizeY
	 */
	public void displayDownloadedImage(int memory, int address, int imageSizeY,
			int displaySizeX, int displaySizeY) {
		byte[] cmd = new byte[15];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x66;
		cmd[3] = 0x10;
		cmd[4] = (byte) memory;
		cmd[5] = (byte) (address % 0x100);
		cmd[6] = (byte) ((address / 0x100) % 0x100);
		cmd[7] = (byte) ((address / 0x10000) % 0x100);
		cmd[8] = (byte) (imageSizeY % 256);
		cmd[9] = (byte) ((imageSizeY / 256) % 256);
		cmd[10] = (byte) (displaySizeX % 256);
		cmd[11] = (byte) ((displaySizeX / 256) % 256);
		cmd[12] = (byte) (displaySizeY % 256);
		cmd[13] = (byte) ((displaySizeY / 256) % 256);
		cmd[14] = 0x01;

		mVFD_Driver.write(cmd);
	}

	/** RAMまたはFROMの内容を右端からスクロール表示する
	 * 
	 * @param memory
	 * @param address
	 * @param imageSizeY
	 * @param displaySizeX
	 * @param displaySizeY
	 * @param speed
	 */
	public void displayDownloadedImage_scroll(int memory, int address,
			int imageSizeY, int displaySizeX, int displaySizeY, int speed) {
		byte[] cmd = new byte[16];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x66;
		cmd[3] = (byte) 0x90;
		cmd[4] = (byte) memory;
		cmd[5] = (byte) (address % 0x100);
		cmd[6] = (byte) ((address / 0x100) % 0x100);
		cmd[7] = (byte) ((address / 0x10000) % 0x100);
		cmd[8] = (byte) (imageSizeY % 256);
		cmd[9] = (byte) ((imageSizeY / 256) % 256);
		cmd[10] = (byte) (displaySizeX % 256);
		cmd[11] = (byte) ((displaySizeX / 256) % 256);
		cmd[12] = (byte) (displaySizeY % 256);
		cmd[13] = (byte) ((displaySizeY / 256) % 256);
		cmd[14] = 0x01;
		cmd[15] = (byte) speed;

		mVFD_Driver.write(cmd);
	}

	/** 横スクロール表示品位の設定
	 * 
	 * @param onSpeed true=表示品位優先、false=表示速度優先
	 */
	public void horizontalScrollQuality(boolean onSpeed) {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x6d;
		if (onSpeed) {
			cmd[2] = 0x00;
		} else {
			cmd[2] = 0x01;
		}

		mVFD_Driver.write(cmd);
	}

	/** リバース表示するか否かの設定
	 * 
	 * @param reverse
	 */
	public void specifyReverseDisplay(boolean reverse) {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x72;
		if (reverse) {
			cmd[2] = 0x00;
		} else {
			cmd[2] = 0x01;
		}

		mVFD_Driver.write(cmd);
	}

	/** 表示書き込み時の合成モードの指定
	 * 
	 */
	public void specifyWriteMixMode_none() {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x77;
		cmd[2] = 0x00;

		mVFD_Driver.write(cmd);
	}

	/** 表示書き込み時の合成モード(OR)の指定
	 * 
	 */
	public void specifyWriteMixMode_or() {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x77;
		cmd[2] = 0x01;

		mVFD_Driver.write(cmd);
	}

	/** 表示書き込み時の合成モード(AND)の指定
	 * 
	 */
	public void specifyWriteMixMode_and() {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x77;
		cmd[2] = 0x02;

		mVFD_Driver.write(cmd);
	}

	/** 表示書き込み時の合成モード(EX-OR)の指定
	 * 
	 */
	public void specifyWriteMixMode_exOr() {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_US;
		cmd[1] = 0x77;
		cmd[2] = 0x03;

		mVFD_Driver.write(cmd);
	}

	/** カレントウィンドウの選択
	 * 
	 * @param a カレントウィンドウNo
	 * 0x00 ベースウィンドウ
	 * 0x01 ユーザーウィンドウ1
	 * 0x02 ユーザーウィンドウ2
	 * 0x03 ユーザーウィンドウ3
	 * 0x04 ユーザーウィンドウ4
	 */
	public void currentWindowSelect(int a) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x77;
		cmd[3] = 0x01;
		cmd[4] = (byte) (a % 0x100);

		mVFD_Driver.write(cmd);
	}

	/** ユーザーウィンドウの定義・解除
	 * 
	 * @param number
	 * @param define
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	public void userWindowDinition(int number, boolean define, int left,
			int top, int width, int height) {
		byte[] cmd;

		if (define == false) {
			cmd = new byte[16]; // wrong size?

			cmd[0] = VAL_US;
			cmd[1] = 0x28;
			cmd[2] = 0x77;
			cmd[3] = 0x02;
			cmd[4] = (byte) number;
			cmd[5] = 0x00;

		} else {
			cmd = new byte[14];

			cmd[0] = VAL_US;
			cmd[1] = 0x28;
			cmd[2] = 0x77;
			cmd[3] = 0x02;
			cmd[4] = (byte) number;
			cmd[5] = 0x01;
			cmd[6] = (byte) (left % 256);
			cmd[7] = (byte) ((left / 256) % 256);
			cmd[8] = (byte) (top % 256);
			cmd[9] = (byte) ((top / 256) % 256);
			cmd[10] = (byte) (width % 256);
			cmd[11] = (byte) ((width / 256) % 256);
			cmd[12] = (byte) (height % 256);
			cmd[13] = (byte) ((height / 256) % 256);
		}

		mVFD_Driver.write(cmd);
	}

	/** ユーザーウィンドウの定義・解除
	 * 
	 * @param number
	 * @param define
	 */
	public void userWindowDinition(int number, boolean define) {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x77;
		cmd[3] = 0x02;
		cmd[4] = (byte) number;
		cmd[5] = 0x00;

		mVFD_Driver.write(cmd);
	}

	/** 6×8及び8×16のダウンロード文字有効・無効設定
	 * 
	 * @param enable true=有効、false=無効
	 */
	public void downloadCharacterSpecify(boolean enable) {
		byte[] cmd = new byte[3];

		cmd[0] = VAL_ESC;
		cmd[1] = 0x25;
		if (enable) {
			cmd[2] = 0x01;
		} else {
			cmd[2] = 0x00;
		}

		mVFD_Driver.write(cmd);
	}

	/** 6×8または8×16の1バイト系ダウンロード文字をRAMに定義する
	 * 
	 * @param characterType
	 * @param codeBeginWith
	 * @param codeEndWith
	 * @param d
	 */
	public void downloadCharacterDifinition(int characterType,
			byte codeBeginWith, byte codeEndWith, byte[] d) {
		byte[] cmd = new byte[d.length + 5];

		cmd[0] = VAL_ESC;
		cmd[1] = 0x26;
		cmd[2] = (byte) characterType;
		cmd[3] = codeBeginWith;
		cmd[4] = codeEndWith;
		System.arraycopy(d, 0, cmd, 5, d.length);

		mVFD_Driver.write(cmd);
	}

	/** 6×8または8×16ドットのダウンロード文字を抹消する
	 * 
	 * @param characterType
	 * @param code
	 */
	public void downloadedCharacterDelete(int characterType, byte code) {
		byte[] cmd = new byte[4];

		cmd[0] = VAL_ESC;
		cmd[1] = 0x3f;
		cmd[2] = (byte) characterType;
		cmd[3] = code;

		mVFD_Driver.write(cmd);
	}

	/** 16×16の2バイト系ダウンロード文字を定義する
	 * 
	 * @param code
	 * @param d
	 */
	public void downloadCharaDefinition_16x16dot(int code, byte[] d) {
		byte[] cmd = new byte[d.length + 6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x10;
		cmd[4] = (byte) ((code / 256) % 256);
		cmd[5] = (byte) (code % 256);
		System.arraycopy(d, 0, cmd, 6, d.length);

		mVFD_Driver.write(cmd);
	}

	/** 16×16の2バイト系ダウンロード文字の抹消
	 * 
	 * @param code
	 */
	public void downloadCharacterDelete_16x16dot(byte code) {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x11;
		cmd[4] = (byte) ((code / 256) % 256);
		cmd[5] = (byte) (code % 256);

		mVFD_Driver.write(cmd);
	}

	/** RAM上のダウンロード文字をFROMに保存する
	 * 
	 * @param FontType
	 */
	public void downloadedCharaSaveFromRamToFROM(int FontType) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x11;
		cmd[4] = (byte) FontType;

		mVFD_Driver.write(cmd);
	}

	/** FROMに保存されているダウンロード文字をRAMに読み込む
	 * 
	 * @param FontType
	 */
	public void downloadedCharaTranFromFROMToRam(int FontType) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x21;
		cmd[4] = (byte) FontType;

		mVFD_Driver.write(cmd);
	}

	/** ユーザーテーブルに各サイズの1バイトコードのユーザーフォントを定義
	 * 
	 * @param fontType
	 * @param pattern
	 */
	public void downloadFromUserFontDifinition(int fontType, byte[] pattern) {
		byte[] cmd = new byte[pattern.length + 5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x13;
		cmd[4] = (byte) fontType;
		System.arraycopy(pattern, 0, cmd, 5, pattern.length);

		mVFD_Driver.write(cmd);
	}

	/** ユーザー設定モードへの移行
	 * 
	 */
	public void userSetupModeStart() {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x01;
		cmd[4] = 'I';
		cmd[5] = 'N';

		mVFD_Driver.write(cmd);
	}

	/** ユーザー設定モードを終了し、ディスプレイモジュールソフトウェアをリセットする
	 * 
	 */
	public void userSetupModeEnd() {
		byte[] cmd = new byte[7];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x02;
		cmd[4] = 'O';
		cmd[5] = 'U';
		cmd[6] = 'T';

		mVFD_Driver.write(cmd);
	}

	/** 汎用ポートの入出力設定
	 * 
	 * @param portNumber
	 * @param output
	 */
	public void IOPortSetting(int portNumber, byte output) {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x70;
		cmd[3] = 0x01;
		cmd[4] = (byte) portNumber;
		cmd[5] = output;

		mVFD_Driver.write(cmd);
	}

	/** 出力ポートの出力値設定
	 * 
	 * @param portNumber
	 * @param pattern
	 */
	public void IOPortOut(int portNumber, byte pattern) {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x70;
		cmd[3] = 0x10;
		cmd[4] = (byte) portNumber;
		cmd[5] = pattern;

		mVFD_Driver.write(cmd);
	}

	/** 入力ポートの状態を返す
	 * 
	 * @param portNumber
	 * (未実装)
	 */
	public void IOPortInput(int portNumber) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x70;
		cmd[3] = 0x20;
		cmd[4] = (byte) portNumber;

		mVFD_Driver.write(cmd);
	}

	/** RAMマクロ及びRAMプログラムマクロの定義または消去を行う
	 * 
	 * @param macroCode
	 */
	public void macroRamMacroDifinition(byte[] macroCode) {
		byte[] cmd = new byte[macroCode.length + 4];

		cmd[0] = VAL_US;
		cmd[1] = 0x3a;
		cmd[2] = (byte) (macroCode.length % 256);
		cmd[3] = (byte) ((macroCode.length / 256) % 256);
		System.arraycopy(macroCode, 0, cmd, 4, macroCode.length);

		mVFD_Driver.write(cmd);
	}

	/** FROMマクロ及びFROMプログラムマクロの定義または消去を行う
	 * 
	 * @param macroNumber
	 * @param t1
	 * @param t2
	 * @param macroCode
	 */
	public void macroFromMacroDifinition(int macroNumber, byte t1, byte t2,
			byte[] macroCode) {
		byte[] cmd = new byte[macroCode.length + 9];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x12;
		cmd[4] = (byte) macroNumber;
		cmd[5] = (byte) (macroCode.length % 256);
		cmd[6] = (byte) ((macroCode.length / 256) % 256);
		cmd[7] = t1;
		cmd[8] = t2;

		System.arraycopy(macroCode, 0, cmd, 9, macroCode.length);

		mVFD_Driver.write(cmd);
	}

	/** マクロの繰り返し実行
	 * 
	 * @param macroNumberAssigned
	 * @param t1
	 * @param t2
	 */
	public void macroExecution(int macroNumberAssigned, byte t1, byte t2) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x5e;
		cmd[2] = (byte) macroNumberAssigned;
		cmd[3] = t1;
		cmd[4] = t2;

		mVFD_Driver.write(cmd);
	}

	/** メモリスイッチのセット
	 * 
	 * @param switchNumber
	 * @param content
	 */
	public void memorySwSet(int switchNumber, int content) {
		byte[] cmd = new byte[6];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x03;
		cmd[4] = (byte) switchNumber;
		cmd[5] = (byte) content;

		mVFD_Driver.write(cmd);
	}

	/** メモリスイッチ状態の読み出し(未実装)
	 * 
	 * @param switchNumber
	 */
	public void memorySwRead(int switchNumber) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x04;
		cmd[4] = (byte) switchNumber;

		mVFD_Driver.write(cmd);
	}

	/** 各種状態読み出し(未実装)
	 * 
	 * @param typeOfInfo
	 * @param startAddress
	 * @param dataLength
	 */
	public void displayStatusRead(int typeOfInfo, int startAddress,
			int dataLength) {
		byte[] cmd = new byte[7];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x40;
		cmd[4] = (byte) typeOfInfo;
		cmd[5] = (byte) startAddress;
		cmd[6] = (byte) dataLength;

		mVFD_Driver.write(cmd);
	}

	/** 各種状態読み出し(未実装)
	 * 
	 * @param typeOfInfo
	 */
	public void displayStatusRead(int typeOfInfo) {
		byte[] cmd = new byte[5];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x65;
		cmd[3] = 0x40;
		cmd[4] = (byte) typeOfInfo;

		mVFD_Driver.write(cmd);
	}

	/** 日本語の設定
	 * 
	 */
	public void setupAsianFont_Japanese() {
		byte[] cmd = new byte[15];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x01;
		cmd[4] = 0x02;
		cmd[5] = 0x1f;
		cmd[6] = 0x28;
		cmd[7] = 0x67;
		cmd[8] = 0x02;
		cmd[9] = 0x01;
		cmd[10] = 0x1f;
		cmd[11] = 0x28;
		cmd[12] = 0x67;
		cmd[13] = 0x03;
		cmd[14] = 0x00;

		mVFD_Driver.write(cmd);
	}

	/** 韓国語の設定
	 * 
	 */
	public void setupAsianFont_Korean() {
		byte[] cmd = new byte[15];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x01;
		cmd[4] = 0x02;
		cmd[5] = 0x1f;
		cmd[6] = 0x28;
		cmd[7] = 0x67;
		cmd[8] = 0x02;
		cmd[9] = 0x01;
		cmd[10] = 0x1f;
		cmd[11] = 0x28;
		cmd[12] = 0x67;
		cmd[13] = 0x03;
		cmd[14] = 0x01;

		mVFD_Driver.write(cmd);
	}

	/** 中国語(繁体)の設定
	 * 
	 */
	public void setupAsianFont_TradChinese() {
		byte[] cmd = new byte[15];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x01;
		cmd[4] = 0x02;
		cmd[5] = 0x1f;
		cmd[6] = 0x28;
		cmd[7] = 0x67;
		cmd[8] = 0x02;
		cmd[9] = 0x01;
		cmd[10] = 0x1f;
		cmd[11] = 0x28;
		cmd[12] = 0x67;
		cmd[13] = 0x03;
		cmd[14] = 0x03;

		mVFD_Driver.write(cmd);
	}

	/** 中国語(簡体)の設定
	 * 
	 */
	public void setupAsianFont_SimplChinese() {
		byte[] cmd = new byte[15];

		cmd[0] = VAL_US;
		cmd[1] = 0x28;
		cmd[2] = 0x67;
		cmd[3] = 0x01;
		cmd[4] = 0x02;
		cmd[5] = 0x1f;
		cmd[6] = 0x28;
		cmd[7] = 0x67;
		cmd[8] = 0x02;
		cmd[9] = 0x01;
		cmd[10] = 0x1f;
		cmd[11] = 0x28;
		cmd[12] = 0x67;
		cmd[13] = 0x03;
		cmd[14] = 0x02;

		mVFD_Driver.write(cmd);
	}

	/*
	 * 
	 */

	/** モジュールのオープン
	 * 
	 */
	public void open() {
		mVFD_Driver.Open();
	}

	/** モジュールのクローズ
	 * 
	 */
	public void close() {
		mVFD_Driver.Close();
	}

	/** 文字列の送信(1バイト系)
	 * 
	 * @param str
	 */
	public void print(String str) {
		mVFD_Driver.write(str.getBytes());
	}

	/** 文字列の送信(日本語・ユニコード)
	 * 
	 * @param str
	 */
	public void printJapanese(String str) {
		try {
			mVFD_Driver.write(str.getBytes("SJIS"));
		} catch (UnsupportedEncodingException e) {
		}
	}

}
