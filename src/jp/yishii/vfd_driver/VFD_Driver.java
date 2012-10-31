package jp.yishii.vfd_driver;

/*
 * Noritake ITRON VFD Communication driver
 * 
 * Copyright(C) 2012 Yasuhiro ISHII
 * 
 * Special thanks to @ksksue's FTDriver code.USB Host control part of this code is based on it.
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class VFD_Driver {
	private static final String TAG = "VFD_Driver";

	public static final int READBUF_SIZE = 4096;
	public static final int WRITEBUF_SIZE = 4096;

	// Noritake ITRON's USB VID/PID
	private static final int DEVICE_VENDOR_ID = 0x0eda;
	private static final int DEVICE_PRODUCT_ID = 0x1000;

	private static final int DEVICE_OUT_EPNUM = 0;
	private static final int DEVICE_IN_EPNUM = 1;
	
	private static final int USB_IN_PACKET_SIZE = 64;

	private UsbManager mUsbManager;
	private UsbDevice mUsbDevice;
	private UsbDeviceConnection mUsbDeviceConnection;
	private UsbInterface[] mUsbInterface = new UsbInterface[1];

	private UsbEndpoint[] mUsbEndpoint_IN;
	private UsbEndpoint[] mUsbEndpoint_OUT;

	private byte[] mReadbuf = new byte[READBUF_SIZE];
	private int mReadbufRemain;
	private int mReadbufOffset;

	private PendingIntent mPermissionIntent;

	private boolean driverOpened = false;
	private boolean deviceAttached = false;

	public VFD_Driver(UsbManager manager) {
		mUsbManager = manager;
	}

	public boolean usbAttached(Intent intent) {
		Log.d(TAG,"usbAttached");

		UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		return getUsbInterfaces(device);
	}
	public void usbDetached(Intent intent) {
		Log.d(TAG,"usbDetached");

		deviceAttached = false;
		UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		String deviceName = device.getDeviceName();
		if (mUsbDevice != null && mUsbDevice.equals(deviceName)) {
			Log.d(TAG, "USB interface removed");
			setUSBInterface(null, null);
		}
	}

    public void setPermissionIntent(PendingIntent pi) {
    	mPermissionIntent = pi;
    }

	public boolean Open() {
		boolean ret = false;
		
		Log.d(TAG,"Open");

		if (driverOpened == false) {

			mUsbEndpoint_IN = new UsbEndpoint[1];
			mUsbEndpoint_OUT = new UsbEndpoint[1];

			for (UsbDevice dev : mUsbManager.getDeviceList().values()) {
				Log.d(TAG,"Open : Loop dev=" + dev);
				getPermission(dev);
				if (!mUsbManager.hasPermission(dev)) {
					Log.d(TAG,"Open : there is no permission for accessing to USB device");
					return (false);
				}
	    		if(getUsbInterfaces(dev)) {
	    			break;
	    		}
			}
			
			driverOpened = true;
			Log.d(TAG,"Open : successfully opened");
			ret = true;
		} else {
			Log.d(TAG,"Open : driver already opened");
		}

		return ret;
	}

	public boolean Close() {
		boolean ret = false;

		if (driverOpened == true) {
			driverOpened = false;
			ret = true;
		}
		deviceAttached = false;

		return ret;
	}

	
	public void getPermission(UsbDevice device) {

		if (device != null && mPermissionIntent != null) {
			if (!mUsbManager.hasPermission(device)) {
				mUsbManager.requestPermission(device, mPermissionIntent);
			}
		}
	}

	private boolean getUsbInterfaces(UsbDevice device) {
		boolean ret = false;
		UsbInterface[] intf;
		UsbEndpoint epIn = null;
		UsbEndpoint epOut = null;

		Log.d(TAG,"getUsbInterfaces");
		
		if ((device.getVendorId() == DEVICE_VENDOR_ID)
				&& (device.getProductId() == DEVICE_PRODUCT_ID)) {
			Log.d(TAG, "Found VFD Panel by NoritakeITRON");

			intf = findUSBInterfaceByVIDPID(device,DEVICE_VENDOR_ID,DEVICE_PRODUCT_ID);
			setUSBInterface(device,intf[0]);

			/*
				Log.d(TAG,"getEndpointCount = " + intf[0].getEndpointCount());
				for(int i=0;i<intf[0].getEndpointCount();i++){
					Log.d(TAG,"IF " + i + " Type=" + intf[0].getEndpoint(i).getType());
				}
			*/
			
			epOut = intf[0].getEndpoint(DEVICE_OUT_EPNUM);
			epIn = intf[0].getEndpoint(DEVICE_IN_EPNUM);
			
			if((epOut != null) && (epIn != null)){
				mUsbEndpoint_IN[0] = epIn;
				mUsbEndpoint_OUT[0] = epOut;
				deviceAttached = true;
				ret = true;
				Log.d(TAG,"getUsbInterfaces : deviceAttached = true");
			}
		}

		return (ret);
	}

	// Sets the current USB device and interface
	private boolean setUSBInterface(UsbDevice device, UsbInterface intf) {
		if (mUsbDeviceConnection != null) {
			if (mUsbInterface[0] != null) {
				mUsbDeviceConnection.releaseInterface(mUsbInterface[0]);
				mUsbInterface[0] = null;
			}
			mUsbDeviceConnection.close();
			mUsbDevice = null;
			mUsbDeviceConnection = null;
		}

		if (device != null && intf != null) {
			UsbDeviceConnection connection = mUsbManager.openDevice(device);
			if (connection != null) {
				if (connection.claimInterface(intf, false)) {

					if ((device.getVendorId() == DEVICE_VENDOR_ID)
							&& (device.getProductId() == DEVICE_PRODUCT_ID)) {
						mUsbDevice = device;
						mUsbDeviceConnection = connection;
						mUsbInterface[0] = intf;
						return true;
					}
				}
			} else {
				connection.close();
			}
		}

		return false;
	}

    // TODO: BUG : sometimes miss data transfer
    public int read(byte[] buf) {

		if(!deviceAttached){
			Log.d(TAG,"Device is not attached");
			return(-1);
		}
    	
    	if (buf.length <= mReadbufRemain) {
//        	System.arraycopy(mReadbuf, mReadbufOffset, buf, 0, buf.length);
        	for (int i=0; i<buf.length; i++ ) {
        		buf[i] = mReadbuf[mReadbufOffset++];
        	}
            mReadbufRemain -= buf.length;
        	return buf.length;
        }
        int ofst = 0;
        int needlen = buf.length;
        if (mReadbufRemain>0) {
            needlen -= mReadbufRemain;
            System.arraycopy(mReadbuf, mReadbufOffset, buf, ofst, mReadbufRemain);
//            for (; mReadbufRemain>0 ; mReadbufRemain-- ) {
//            	buf[ofst++] = mReadbuf[mReadbufOffset++];
//            }
        }
        int len = mUsbDeviceConnection.bulkTransfer(mUsbEndpoint_IN[0], mReadbuf, mReadbuf.length,
                0); // RX
        int blocks = len / USB_IN_PACKET_SIZE;
        int remain = len % USB_IN_PACKET_SIZE;
        if (remain>0) {
            blocks++;
        }
        mReadbufRemain = len - (2*blocks);
        int rbufindex = 0;
        for (int block=0; block<blocks; block++) {
            int blockofst = block*USB_IN_PACKET_SIZE;
//            System.arraycopy(mReadbuf, blockofst+2, mReadbuf, rbufindex+1, mPacketSize-2);
            for (int i=2; i<USB_IN_PACKET_SIZE ; i++ ) {
            	mReadbuf[rbufindex++] = mReadbuf[blockofst+i];
            }
        }
        
        mReadbufOffset = 0;
        
        for (;(mReadbufRemain>0) && (needlen>0);mReadbufRemain--,needlen--) {
            buf[ofst++] = mReadbuf[mReadbufOffset++];            
        }
        return ofst;
    }

    /** Writes 1byte Binary Data
     * 
     * @param buf : write buffer
     * @return written length
     */
    public int write(byte[] buf) {
    	return write(buf,buf.length);
    }
    	
    /** Writes n byte Binary Data to n channel
     * 
     * @param buf : write buffer
     * @param length : write length
     * @param channel : write channel
     * @return written length
     */
    public int write(byte[] buf, int length) {
    	int offset = 0;
    	int actual_length;
		byte[] write_buf = new byte[WRITEBUF_SIZE];
		
		if(!deviceAttached){
			Log.d(TAG,"Device is not attached");
			return(-1);
		}
    	
    	while(offset < length) {
    		int write_size = WRITEBUF_SIZE;
    		
    		if(offset+write_size > length) {
    			write_size = length-offset;
    		}
    		System.arraycopy(buf, offset, write_buf, 0, write_size);
    		
    		actual_length = mUsbDeviceConnection.bulkTransfer(mUsbEndpoint_OUT[0], write_buf, write_size, 0);

    		// Log.d(TAG,"write : actual_length = " + actual_length);
    		
    		if(actual_length<0) {
    			return -1;
    		}
    		offset += actual_length;
    	}
    	
		return offset;
    }

	public boolean isDeviceAttached(){
		return(deviceAttached);
	}
    
    private UsbInterface[] findUSBInterfaceByVIDPID(UsbDevice device,int vid, int pid) {
        UsbInterface[] retIntf = new UsbInterface[1];
        int j=0;
        int count = device.getInterfaceCount();

        Log.d(TAG,"findUSBInterfaceByVIDPID : count = "+count);
        
        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (device.getVendorId() == vid && device.getProductId() == pid) {
            	retIntf[j]=intf;
            	++j;
              }
        }
        return retIntf;
    }
    
}
