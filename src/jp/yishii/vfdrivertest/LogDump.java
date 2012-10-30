/*
 * Android logcat dumplist output class
 * 
 * Created by Yasuhiro ISHII
 * 
 * This software is distributed under the Apache 2.0 License.
 * For latest code,please check at https://github.com/yishii/AndroidLogDump
 */

package jp.yishii.vfdrivertest;

import android.util.Log;

public class LogDump {

	/**
	 * Send a DEBUG log message.
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 */
	public static void d(String TAG, byte[] ary) {
		dumpLog(Log.DEBUG, TAG, ary, -1, true);
	}

	/**
	 * Send a DEBUG log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 * @param size	The count of the bytes that you would like to logged from the beginning
	 */
	public static void d(String TAG, byte[] ary, int size) {
		dumpLog(Log.DEBUG, TAG, ary, size, true);
	}

	/**
	 * Send a DEBUG log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 * @param size	The count of the bytes that you would like to logged from the beginning
	 * @param ascii	Used to specify whether to add the ASCII part or not.
	 */
	public static void d(String TAG, byte[] ary, int size, boolean ascii) {
		dumpLog(Log.DEBUG, TAG, ary, size, ascii);
	}

	/**
	 * Send a INFO log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 */
	public static void i(String TAG, byte[] ary) {
		dumpLog(Log.INFO, TAG, ary, -1, true);
	}

	/**
	 * Send a INFO log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 * @param size	The count of the bytes that you would like to logged from the beginning
	 */
	public static void i(String TAG, byte[] ary, int size) {
		dumpLog(Log.INFO, TAG, ary, size, true);
	}

	/**
	 * Send a INFO log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 * @param size	The count of the bytes that you would like to logged from the beginning
	 * @param ascii	Used to specify whether to add the ASCII part or not.
	 */
	public static void i(String TAG, byte[] ary, int size, boolean ascii) {
		dumpLog(Log.INFO, TAG, ary, size, ascii);
	}

	/**
	 * Send a VERBOSE log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 */
	public static void v(String TAG, byte[] ary) {
		dumpLog(Log.VERBOSE, TAG, ary, -1, true);
	}

	/**
	 * Send a VERBOSE log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 * @param size	The count of the bytes that you would like to logged from the beginning
	 */
	public static void v(String TAG, byte[] ary, int size) {
		dumpLog(Log.VERBOSE, TAG, ary, size, true);
	}

	/**
	 * Send a VERBOSE log message
	 * @param TAG	Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param ary	The data you would like logged in dump-format.
	 * @param size	The count of the bytes that you would like to logged from the beginning
	 * @param ascii	Used to specify whether to add the ASCII part or not.
	 */
	public static void v(String TAG, byte[] ary, int size, boolean ascii) {
		dumpLog(Log.VERBOSE, TAG, ary, size, ascii);
	}

	private static void dumpLog(int priority, String TAG, byte[] ary, int size,
			boolean ascii) {
		int col;
		int row;
		int dumpsize = ary.length;

		if ((size != -1) && (dumpsize > size)) {
			dumpsize = size;
		}
		String oneline;

		if (dumpsize == 0) {
			Log.println(priority, TAG, "No dump data");
			return;
		}

		for (row = 0; row <= dumpsize / 16; row++) {
			oneline = String.format("%08X : ", row * 16);
			for (col = 0; col <= 15; col++) {
				int bytepos = row * 16 + col;

				if (col != 0) {
					oneline += ",";
				}

				if (bytepos < dumpsize) {
					oneline += String.format("%02X", ary[bytepos]);
				} else {
					oneline += "--";
				}
			}

			if (ascii) {
				oneline += "  ";

				for (col = 0; col <= 15; col++) {
					int bytepos = row * 16 + col;

					if (bytepos < dumpsize) {
						byte b = ary[bytepos];
						if (b >= 0x20 && b <= 0x7e) {
							oneline += String.format("%c", b);
						} else {
							oneline += ".";
						}
					} else {
						oneline += " ";
					}
				}
			}

			Log.println(priority, TAG, oneline);
		}
	}
}
