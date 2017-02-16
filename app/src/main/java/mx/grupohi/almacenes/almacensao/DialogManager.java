package mx.grupohi.almacenes.almacensao;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;

import com.bixolon.printer.BixolonPrinter;

import java.util.Set;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
@SuppressLint("NewApi")
public class DialogManager {

    private static final String[] CODE_PAGE_ITEMS = {
            "Page 0 437 (USA, Standard Europe)",
            "Page 1 Katakana",
            "Page 2 850 (Multilingual)",
            "Page 3 860 (Portuguese)",
            "Page 4 863 (Canadian-French)",
            "Page 5 865 (Nordic)",
            "Page 16 1252 (Latin I)",
            "Page 17 866 (Cyrillic #2)",
            "Page 18 852 (Latin 2)",
            "Page 19 858 (Euro)",
            "Page 21 862 (Hebrew DOS code)",
            "Page 22 864 (Arabic)",
            "Page 23 Thai42",
            "Page 24 1253 (Greek)",
            "Page 25 1254 (Turkish)",
            "Page 26 1257 (Baltic)",
            "Page 27 Farsi",
            "Page 28 1251 (Cyrillic)",
            "Page 29 737 (Greek)",
            "Page 30 775 (Baltic)",
            "Page 31 Thai14",
            "Page 33 1255 (Hebrew New code)",
            "Page 34 Thai 11",
            "Page 35 Thai 18",
            "Page 36 855 (Cyrillic)",
            "Page 37 857 (Turkish)",
            "Page 38 928 (Greek)",
            "Page 39 Thai 16",
            "Page 40 1256 (Arabic)",
            "Page 41 1258 (Vietnam)",
            "Page 42 KHMER(Cambodia)",
            "Page 47 1250 (Czech)",
            "KS5601 (double byte font)",
            "BIG5 (double byte font)",
            "GB2312 (double byte font)",
            "SHIFT-JIS (double byte font)"
    };

    private static final String[] PRINTER_ID_ITEMS = {
            "Firmware version",
            "Manufacturer",
            "Printer model",
            "Code page"
    };

    private static final String[] PRINT_SPEED_ITEMS = {
            "High speed",
            "Medium speed",
            "Low Speed"
    };

    private static final String[] PRINT_DENSITY_ITEMS = {
            "Light density",
            "Default density",
            "Dark density"
    };

    private static final String[] PRINT_COLOR_ITEMS = {
            "Black",
            "Red"
    };

    public static void showBluetoothDialog(final Context context, final BixolonPrinter bixolonPrinterApi, final Set<BluetoothDevice> pairedDevices) {
        final String[] items = new String[pairedDevices.size()];
        int index = 0;
        for (BluetoothDevice device : pairedDevices) {
            items[index++] = device.getAddress();
        }

        new AlertDialog.Builder(context).setTitle("Impresoras Bluetooth Emparejadas")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        bixolonPrinterApi.connect(items[which]);
                    }
                }).show();
    }

}
