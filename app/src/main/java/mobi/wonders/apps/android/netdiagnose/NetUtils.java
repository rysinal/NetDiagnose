package mobi.wonders.apps.android.netdiagnose;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * Title:CMS_[所属模块]_[标题]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * <p>
 * NetUtils
 *
 * @author yuqing
 * @date 2016/1/13
 */
public class NetUtils {

    private static ConnectivityManager mManager;

    public static final int CMWAP = 0;
    public static final int CMNET = 1;
    public static final int WIFI = 2;

    public static boolean connect = false;
    public static int netCode = -1;

    /**
     * 检测与服务器是否连接正常
     *
     * @param url
     * @param callBack
     */
    public static void getStatus(final String url, final NetCallBack callBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL u = new URL(url);
                    try {
                        HttpURLConnection uConnection = (HttpURLConnection) u.openConnection();
                        try {
                            uConnection.setReadTimeout(5000);
                            uConnection.setConnectTimeout(10000);
                            uConnection.connect();
                            netCode = uConnection.getResponseCode();
                            System.out.println("responseCode:" + netCode);
                            connect = true;
                            InputStream is = uConnection.getInputStream();
                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            StringBuilder sb = new StringBuilder();
                            while (br.read() != -1) {
                                sb.append(br.readLine());
                            }
                            String content = new String(sb);
                            content = new String(content.getBytes("GBK"), "ISO-8859-1");
                            System.out.println("response:" + content);
                            br.close();
                        } catch (Exception e) {
                            connect = false;
                            e.printStackTrace();
                            System.out.println("connect failed");
                        }

                    } catch (IOException e) {
                        System.out.println("build failed");
                        e.printStackTrace();
                    }

                } catch (MalformedURLException e) {
                    System.out.println("build url failed");
                    e.printStackTrace();
                }
                callBack.onResponse(NetUtils.connect, netCode);
                netCode = -1;
                if (NetUtils.connect) {
                    System.out.println("connect success");
                } else {
                    System.out.println("connect error");
                }
            }
        }.start();

    }

    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean checkNetworkState(Context context) {
        boolean flag = false;
        //得到网络连接信息
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (mManager.getActiveNetworkInfo() != null) {
            flag = mManager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork(context);
        }
        return flag;
    }

    /**
     * 拿到网络类型
     *
     * @return
     */
    public static int getAPNType(Context context) {
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int netType = -1;
        NetworkInfo networkInfo = mManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            Log.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is " + networkInfo.getExtraInfo());
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = CMNET;
                Log.d("网络连接类型", "======>CMNET");
            } else {
                netType = CMWAP;
                Log.d("网络连接类型", "======>CMWAP");
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = WIFI;
            Log.d("网络连接类型", "======>WIFI");
        }
        return netType;
    }

    /**
     * 网络未连接时，调用设置方法
     */
    public static void setNetwork(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.net_tip);
        builder.setTitle("网络提示信息");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

}
