package jp.yishii.vfdrivertest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jp.yishii.vfd_driver.VFD_Communication;
import jp.yishii.vfd_driver.VFD_Driver;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.Button;

public class VFDriverTestActivity extends Activity implements Runnable {
	private static final String TAG = "VFDriverTestActivity";

	private VFD_Driver mVFD_Driver;
	private VFD_Communication mVFD_Communication;

	private WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mVFD_Driver = new VFD_Driver(
				(UsbManager) getSystemService(Context.USB_SERVICE));
		mVFD_Communication = new VFD_Communication(mVFD_Driver);

		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		mWebView = (WebView) findViewById(R.id.webview1);
		mWebView.setPictureListener(new MyPictureListener());
		mWebView.setWebViewClient(new WebViewClient());

		((Button) findViewById(R.id.button1))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						mVFD_Communication.initialize();
						mVFD_Communication.setupAsianFont_Japanese();

						mVFD_Communication.fontSize(0x04);
						mVFD_Communication.cursorSet(0, 0);
						mVFD_Communication.printJapanese("文字列出力テスト");
						mVFD_Communication.fontSize(0x02);
						mVFD_Communication.cursorSet(0, 4);
						mVFD_Communication.characterBold(true);
						mVFD_Communication.printJapanese("日曜エレクトロニクス");
						mVFD_Communication.characterBold(false);
						mVFD_Communication.printJapanese("(日エレ)");
						mVFD_Communication.fontSize(0x01);
						mVFD_Communication.cursorSet(0, 7);
						mVFD_Communication.print("https://github.com/yishii");
						mVFD_Communication.cursorSet(0, 6);
						mVFD_Communication
								.print("http://projectc3.seesaa.net/");

						mVFD_Communication.shortWait((byte) 100);

						mVFD_Communication.scrollAction(16, 256, 1);

						{
							int i;
							int j;
							for (j = 0; j < 3; j++) {
								for (i = 100; i >= 0; i -= 2) {
									mVFD_Communication.brightness(i);
									mVFD_Communication.shortWait((byte) 1);
								}
								for (i = 0; i <= 100; i += 2) {
									mVFD_Communication.brightness(i);
									mVFD_Communication.shortWait((byte) 1);
								}
							}
						}
					}
				});

		((Button) findViewById(R.id.button2))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int i;
						mVFD_Communication.initialize();
						mVFD_Communication.setupAsianFont_Japanese();

						for (i = 0; i < 16; i++) {
							mVFD_Communication.lineBoxDraw(0, true, 0, i * 4,
									i * 16, 63);
						}

						mVFD_Communication.fontSize(0x02);
						mVFD_Communication.cursorSet(14, 0);
						mVFD_Communication.printJapanese("ノリタケ伊勢電子製VFDパネル用");
						mVFD_Communication.cursorSet(118, 2);
						mVFD_Communication.printJapanese("制御ライブラリの");
						mVFD_Communication.cursorSet(165, 4);
						mVFD_Communication.printJapanese("テストです");

					}
				});

		
		((Button) findViewById(R.id.button3))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						mVFD_Communication.initialize();

//						mWebView.loadUrl("http://strawberry-linux.com/");
						//mWebView.loadUrl("http://projectc3.seesaa.net/");
						mWebView.loadUrl("http://akizukidenshi.com/catalog/default.aspx");
						

					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");

		mVFD_Communication.open();

		// Thread t = new Thread(this);
		// t.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mVFD_Driver.Close();
	}

	@SuppressWarnings("deprecation")
	private class MyPictureListener implements PictureListener {

		@Override
		@Deprecated
		public void onNewPicture(WebView view, Picture picture) {
			Log.d(TAG, "PictureListener#onNewPicture");

			Picture pic = view.capturePicture();

			Bitmap bmp = Bitmap.createBitmap(pic.getWidth(), pic.getHeight(),
					Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bmp);
			pic.draw(canvas);

			int[] pixels = new int[256 * 64];
			int i;

			bmp.getPixels(pixels, 0, 256,0, 0, 256, 64);

			for (int k=0;k<8;k++){
				byte[] byteBuff = new byte[256];

				for (int x = 0; x < 256; x++) {
					
					// convert bitmap matrix to VFD image format
					for (i=0;i<8;i++){
						byteBuff[x] |= (((pixels[x + 256*i + 256*8*k] & 0xff00) >> 8) > 0x7f ? 1 : 0) << 7-i;
					}
				}
	
				mVFD_Communication.cursorSet(0, k);
				mVFD_Communication.realTimeBitImageDisplay(256, 1, byteBuff);
			}
		}

	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "in thread...");
		}
	}

	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				mVFD_Driver.usbAttached(intent);
				mVFD_Driver.Open();

			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				mVFD_Driver.usbDetached(intent);
				mVFD_Driver.Close();
			}
		}
	};

}
