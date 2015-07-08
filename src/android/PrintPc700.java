package imb.ridiqirici.plugin.cordova.pc700print;

import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import com.zkc.pc700.helper.PrinterClassSerialPort;

public class PrintPc700 extends CordovaPlugin{
	public static final String PRINT_TEXT = "printText";
	public static final String PRINT_IMG = "printImg";
	protected static final String TAG = "Pc700PrintPlugin";
	private CallbackContext mesazhi;
    private boolean veprimiKryer;
    static PrinterClassSerialPort printerClass = null;
    
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
		try {
			return printerClass.printText(str);
		} catch (Exception e) {
			this.veprimiKryer = false;
        	this.mesazhi.error("Gabim gjate printimit te tekstit!");
			Log.e(TAG, e.getMessage());
			return this.veprimiKryer;
		}
    }
}
