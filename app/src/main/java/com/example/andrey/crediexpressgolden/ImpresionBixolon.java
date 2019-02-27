package com.example.andrey.crediexpressgolden;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.android.library.BxlService;

public class ImpresionBixolon extends Activity {
    private static final String TAG = "Crediexpress Golden";
    static final private int PrintText_ID = Menu.FIRST + 1;
    static final private int PrintDownloadedImage_ID = Menu.FIRST + 7;
    static final private int Connect_ID = Menu.FIRST + 12;
    static final private int Disconnect_ID = Menu.FIRST + 13;
    static BxlService mBxlService = null;
    private boolean conn = false;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private static final int Samsung_GalaxyS = 1;
    private static final int HTC_Desire = 2;
    private int DeviceMoldel = Samsung_GalaxyS;
    String ReciboDia0 = "NO HAY DATOS DISPONIBLES";



    public ImpresionBixolon() {
        Log.e(TAG, "+++ ON Contructor +++");
        String DeviceModeName = "HTC Desire";
        if (DeviceModeName.equals(Build.MODEL)) {
            DeviceMoldel = HTC_Desire;
            Log.i(TAG, Build.MODEL + " " + DeviceModeName.equals(Build.MODEL));
        } else {
            DeviceMoldel = Samsung_GalaxyS;
            Log.i(TAG, Build.MODEL + " " + DeviceModeName.equals(Build.MODEL));
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_imprime);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        super.onCreate(savedInstanceState);
        CheckGC("onCreate_Start");

        TextView t = (TextView) findViewById(R.id.text);
        registerForContextMenu(t);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();
        CheckGC("onCreate_End");

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

    }

    @Override
    public void onStart() {
        super.onStart();
        CheckGC("onStart_Start");
        TextView t = (TextView) findViewById(R.id.text);
        t.setText("");
        t.append("Crediexpress Golden\n\n");
        t.append("Mantenga pulsada la pantalla\n");
        Log.e(TAG, "++ ON START ++");
        CheckGC("onStart_End");
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "+ ON RESUME +");
        CheckGC("onResume_End");
    }
    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.e(TAG, "- ON PAUSE -");
        CheckGC("onPause_End");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "-- ON STOP --");
        CheckGC("onStop_End");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "--- ON DESTROY ---");
        super.onDestroy();
        if (mBxlService != null) {
            mBxlService.Disconnect();
            mBxlService = null;
        }
        wl.release();
        if (DeviceMoldel == HTC_Desire) {
            Runtime.getRuntime().exit(0);
        }
        CheckGC("onDestroy_End");
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        CheckGC("onCreateContextMenu_Start");
        if (conn == false) {
            menu.add(0, Connect_ID, 0, "Conectar");
        } else {
            menu.add(0, Disconnect_ID, 0, "Desconectar");
            menu.add(0, PrintText_ID, 0, "Imprimir factura");


        }
        CheckGC("onCreateContextMenu_End");
    }

    public boolean onContextItemSelected(MenuItem item) {
        int returnValue = BxlService.BXL_SUCCESS;
        TextView t = (TextView) findViewById(R.id.text);

            switch (item.getItemId()) {
            case Connect_ID:
                CheckGC("Conexión iniciada - Crediexpress Golden");
                mBxlService = new BxlService();
                t.setText("");
                t.append("Conectado - Crediexpress Golden\n");
                if (mBxlService.Connect() == 0) {
                    t.append("Hecho - Crediexpress Golden\n");
                    conn = true;
                } else {
                    t.append("Fallo - Crediexpress Golden\n\n");
                    Toast.makeText(ImpresionBixolon.this, "Por favor verifique que la impresora este encendida y este conectada a su dispositivo bluetooth - Crediexpress Golden" ,
                            Toast.LENGTH_SHORT).show();
                    conn = false;
                }
                t.append("Mantenga pulsado\n");
                CheckGC("Connect_End");
                return true;

            case Disconnect_ID:
                CheckGC("Desconexión iniciada - Crediexpress Golden");
                t.setText("");
                t.append("Desconectado - Crediexpress Golden\n");
                mBxlService.Disconnect();
                mBxlService = null;
                t.append("Listo\n\n");
                t.append("Mantenga pulsado\n");
                conn = false;
                // defense code for HTC Desire because of reconnect' fail
                // finish();
                CheckGC("Disconnect_End");
                return true;

            case PrintText_ID:
                CheckGC("PrintText_Start");
                t.setText("");
                t.append("IMPRESO - Crediexpress Golden\n");
                ReciboDia0="\n"+getIntent().getExtras().getString("RECIBO");
                returnValue = mBxlService.GetStatus();
                if (returnValue == BxlService.BXL_SUCCESS) {

                    mBxlService.PrintDownloadedImage("http://crediexpressgolden.com.co/img/crediexpressgoldenblack.png",
                            getFilesDir().getAbsolutePath(),
                            BxlService.BXL_WIDTH_FULL,
                            BxlService.BXL_ALIGNMENT_CENTER, 50);
                    mBxlService.PrintText(ReciboDia0,
                            BxlService.BXL_ALIGNMENT_LEFT,
                            BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                    returnValue = mBxlService.LineFeed(5, true);
                }

                if (returnValue == BxlService.BXL_SUCCESS) {
                    t.append("Listo\n\n");
                } else {
                    t.append("Fail\n\n");
                    String tem_buffer = new String();
                    tem_buffer = "ERROR [" + returnValue + "]";
                    t.append(tem_buffer.subSequence(0, tem_buffer.getBytes().length));
                }

                t.append("\nMantenga pulsada la pantalla\n");
                CheckGC("PrintText_End");
                return true;

            case PrintDownloadedImage_ID:
                CheckGC("PrintDownloadedImage_Start");
                t.setText("");
                t.append("Imprimiendo factura con logo - Crediexpress Golden\n");
                returnValue = mBxlService.PrintDownloadedImage("http://crediexpressgolden.com.co/img/crediexpressgoldenblack.png",
                        getFilesDir().getAbsolutePath(),
                        BxlService.BXL_WIDTH_FULL,
                        BxlService.BXL_ALIGNMENT_CENTER, 50);
                if (returnValue == BxlService.BXL_SUCCESS) {
                    returnValue = mBxlService.LineFeed(5, true);
                }

                if (returnValue == BxlService.BXL_SUCCESS) {
                    returnValue = mBxlService.LineFeed(2);
                    t.append("Success\n\n");
                } else {
                    t.append("Fail\n\n");
                    String tem_buffer = new String();
                    tem_buffer = "ERROR [" + returnValue + "]";
                    t.append(tem_buffer.subSequence(0, tem_buffer.getBytes().length));
                    tem_buffer = null;
                }
                t.append("\nMantenga pulsada la pantalla\n");
                CheckGC("PrintDownloadedImage_End");
                return true;

        }

        return super.onContextItemSelected(item);

    }

    void CheckGC(String FunctionName) {
        long VmfreeMemory = Runtime.getRuntime().freeMemory();
        long VmmaxMemory = Runtime.getRuntime().maxMemory();
        long VmtotalMemory = Runtime.getRuntime().totalMemory();
        long Memorypercentage = ((VmtotalMemory - VmfreeMemory) * 100)
                / VmtotalMemory;

        Log.i(TAG, FunctionName + "Before Memorypercentage" + Memorypercentage
                + "% VmtotalMemory[" + VmtotalMemory + "] " + "VmfreeMemory["
                + VmfreeMemory + "] " + "VmmaxMemory[" + VmmaxMemory + "] ");
        System.runFinalization();
        System.gc();
        VmfreeMemory = Runtime.getRuntime().freeMemory();
        VmmaxMemory = Runtime.getRuntime().maxMemory();
        VmtotalMemory = Runtime.getRuntime().totalMemory();
        Memorypercentage = ((VmtotalMemory - VmfreeMemory) * 100)
                / VmtotalMemory;

        Log.i(TAG, FunctionName + "_After Memorypercentage" + Memorypercentage
                + "% VmtotalMemory[" + VmtotalMemory + "] " + "VmfreeMemory["
                + VmfreeMemory + "] " + "VmmaxMemory[" + VmmaxMemory + "] ");
    }



    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            StringBuffer buffer = new StringBuffer();
            if (msg.what == BxlService.BXL_STS_COVEROPEN) {
                buffer.append("Cover is open.\n");
            } else if (msg.what == BxlService.BXL_STS_DRAWER_LOW) {
                buffer.append("Drawer kick-out connector pin 3 is LOW.\n");
            }
			 else if (msg.what == BxlService.BXL_STS_MECHANICAL_ERROR) {
                buffer.append("Mechanical error.\n");
            } else if (msg.what == BxlService.BXL_STS_AUTO_CUTTER_ERROR) {
                buffer.append("Auto cutter error occurred.\n");
            } else if (msg.what == BxlService.BXL_STS_ERROR) {
                buffer.append("Unrecoverable error.\n");
            } else if (msg.what == BxlService.BXL_STS_PAPER_NEAR_END) {
                buffer.append("Paper near end sensor: paper near end.\n");
            } else if (msg.what == BxlService.BXL_STS_NO_PAPER) {
                buffer.append("Paper end sensor: no paper present.\n");
            }

            if (buffer.length() > 0) {
                Toast.makeText((Activity)msg.obj, buffer.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
