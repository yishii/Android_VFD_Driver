package jp.yishii.vfdrivertest;

import java.io.UnsupportedEncodingException;

import jp.yishii.vfd_driver.VFD_Communication;
import jp.yishii.vfd_driver.VFD_Driver;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class VFDriverTestActivity extends Activity implements Runnable {
	private static final String TAG = "VFDriverTestActivity";

	private VFD_Driver mVFD_Driver;
	private VFD_Communication mVFD_Communication;

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
						mVFD_Communication.printJapanese("日曜エレクトロニクス(日エレ)");
						mVFD_Communication.fontSize(0x01);
						mVFD_Communication.cursorSet(0, 7);
						mVFD_Communication.print("https://github.com/yishii");
						mVFD_Communication.cursorSet(0, 6);
						mVFD_Communication
								.print("http://projectc3.seesaa.net/");

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
