package imb.ridiqirici.plugin.cordova.pc700print;

import org.apache.cordova.CordovaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.zkc.helper.printer.PrintService;
import com.zkc.helper.printer.PrinterClass;
import com.zkc.pc700.activity.MainActivity;
import com.zkc.pc700.helper.PrinterClassSerialPort;

public class PrintPc700 extends CordovaPlugin{
	public static final String PRINT_TEXT = "printText";
	public static final String PRINT_IMG = "printImg";
	protected static final String TAG = "Pc700PrintPlugin";
	private CallbackContext mesazhi;
    private boolean veprimiKryer;
    static PrinterClassSerialPort printerClass = null;
    
	Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PrinterClass.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				Log.i(TAG, "readBuf:" + readBuf[0]);
				if (readBuf[0] == 0x13) {
					PrintService.isFUll = true;
				} else if (readBuf[0] == 0x11) {
					PrintService.isFUll = false;
				} else {
					String readMessage = new String(readBuf, 0, msg.arg1);
					if (readMessage.contains("800"))// 80mm paper
					{
						PrintService.imageWidth = 72;
					} else if (readMessage.contains("580"))// 58mm paper
					{
						PrintService.imageWidth = 48;
					}else {
						
					}
				}
				break;
			case PrinterClass.MESSAGE_STATE_CHANGE:// 蓝牙连接状
				switch (msg.arg1) {
				case PrinterClass.STATE_CONNECTED:// 已经连接
					break;
				case PrinterClass.STATE_CONNECTING:// 正在连接
					break;
				case PrinterClass.STATE_LISTEN:
				case PrinterClass.STATE_NONE:
					break;
				case PrinterClass.SUCCESS_CONNECT:
					printerClass.write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
					break;
				case PrinterClass.FAILED_CONNECT:
					break;
				case PrinterClass.LOSE_CONNECT:
				}
				break;
			case PrinterClass.MESSAGE_WRITE:

				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.mesazhi = callbackContext;
		this.veprimiKryer = true;
		
        if (PRINT_TEXT.equals(action)) {
        	JSONArray obj = args.optJSONArray(0);
            int nderprejrja = args.optInt(1);
            if (obj != null) {
                this.printoTekstin(obj, nderprejrja);
            } else {
                this.veprimiKryer = false;
                this.mesazhi.error("Perdoruesi nuk ka specifikuar te dhena per tu printuar");
                Log.d(TAG, "Ndodhi nje JSON Exception ");
            }
        } else if (PRINT_IMG.equals(action)) {
            //TODO
        } else {
        	this.veprimiKryer = false;
        	this.mesazhi.error("Veprim i pavlefshem!");
            Log.d(TAG, "Veprim i pavlefshem : Eshte kaluar veprimi " + action + "!");
        }
        return veprimiKryer;
    }
        
	public boolean printoTekstin(JSONArray arObj, int cutLines) {
		String str = "Pershendetje";
		boolean uPrintua = false;
		try {
			printerClass = new PrinterClassSerialPort(mhandler);
			if (printerClass.open())
			{
				uPrintua = printerClass.printText(str);
				printerClass.close();
			}
			return uPrintua;
		} catch (Exception e) {
			this.veprimiKryer = false;
        	this.mesazhi.error("Gabim gjate printimit te tekstit! " + e.getMessage() + " " + e.toString() + " " + uPrintua);
			Log.e(TAG, e.getMessage());
			return this.veprimiKryer;
		}
    }
}
