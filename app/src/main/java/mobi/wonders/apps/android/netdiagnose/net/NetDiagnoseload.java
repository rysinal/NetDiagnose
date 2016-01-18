package mobi.wonders.apps.android.netdiagnose.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import mobi.wonders.apps.android.netdiagnose.R;

/**
 * <p>
 * Title:CMS_[所属模块]_[标题]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * <p/>
 * NetDiagnoseload
 *
 * @author yuqing
 * @date 2016/1/18
 */
public class NetDiagnoseload implements NetDiagnose {

    private static NetDiagnoseload instance;

    private Activity mActivity;
    private String website;

    private final int DELAYTIME_LONG = 2000;
    private final int DELAYTIME_SHORT = 1000;

    //检测状态文本
    private TextView tv_phone_status, tv_phone_status_ping, tv_server_status;
    // 检测状态progressbar
    private ProgressBar pro_phone_dia, pro_phone_ping, pro_server_dia;
    //检测状态结果
    private ImageView iv_phone_error, iv_phone_success, iv_server_error, iv_server_success, iv_phone_error_ping, iv_server_success_ping;
    //最终检测结果
    private TextView tv_result;
    private LinearLayout ll_result;

    private static final int MSG_PHONE = 0;
    private static final int MSG_NET = 1;
    private static final int MSG_SERVER = 2;
    private static final String PING_WEBSITE = "https://www.baidu.com/";

    public NetDiagnoseload(Activity activity, String url) {
        this.mActivity = activity;
        this.website = url;
        startDiagnose();
    }

    @Override
    public View getDialogView() {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View diagnoseDialog = inflater.inflate(R.layout.dialogfortest, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity).setView(diagnoseDialog);
        dialogBuilder.setIcon(R.mipmap.net_status);
        dialogBuilder.setTitle("网络诊断");
        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        return diagnoseDialog;
    }

    @Override
    public void initialise() {
        View dialogView = getDialogView();

        //手机检测
        pro_phone_dia = (ProgressBar) dialogView.findViewById(R.id.pro_phone_dia);
        tv_phone_status = (TextView) dialogView.findViewById(R.id.tv_phone_status);
        iv_phone_error = (ImageView) dialogView.findViewById(R.id.iv_phone_error);
        iv_phone_success = (ImageView) dialogView.findViewById(R.id.iv_phone_success);

        //网络连通检测
        pro_phone_ping = (ProgressBar) dialogView.findViewById(R.id.pro_phone_ping);
        tv_phone_status_ping = (TextView) dialogView.findViewById(R.id.tv_phone_status_ping);
        iv_phone_error_ping = (ImageView) dialogView.findViewById(R.id.iv_phone_error_ping);
        iv_server_success_ping = (ImageView) dialogView.findViewById(R.id.iv_server_success_ping);

        //服务器检测
        pro_server_dia = (ProgressBar) dialogView.findViewById(R.id.pro_server_dia);
        tv_server_status = (TextView) dialogView.findViewById(R.id.tv_server_status);
        iv_server_error = (ImageView) dialogView.findViewById(R.id.iv_server_error);
        iv_server_success = (ImageView) dialogView.findViewById(R.id.iv_server_success);

        //检测结果
        ll_result = (LinearLayout) dialogView.findViewById(R.id.ll_result);
        tv_result = (TextView) dialogView.findViewById(R.id.tv_result);
    }

    @Override
    public void diagnosePhone() {
        startNetDiagnose("正在检查手机网络是否可用...", tv_phone_status);
        handler.postDelayed(phoneCheckTask, DELAYTIME_LONG);
    }

    @Override
    public void diagnoseNet() {
        startNetDiagnose("正在检查与网络连通性...", tv_phone_status_ping);
        handler.postDelayed(netCheckTask, DELAYTIME_SHORT);
    }

    @Override
    public void diagnoseServer() {
        startNetDiagnose("正在检测与服务器连通性...", tv_server_status);
        handler.postDelayed(serverCheckTask, DELAYTIME_SHORT);
    }

    /**
     * 开始诊断网络
     */
    @Override
    public void startDiagnose() {
        initialise();
        //检查手机网络
        diagnosePhone();
    }

    /**
     * 开始诊断
     *
     * @param stateMsg 当前诊断信息
     * @param tvStatus 当前诊断view
     */
    private void startNetDiagnose(String stateMsg, TextView tvStatus) {
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(stateMsg);
    }

    /**
     * 诊断结束
     *
     * @param progressbar 诊断进度
     * @param tvStatus    当前进度
     * @param isSuccess   是否连接成功
     * @param ivSuccess   成功图片
     * @param ivError     失败图片
     * @param result      诊断结果
     */
    private void endNetDiagnose(ProgressBar progressbar, TextView tvStatus, boolean isSuccess, ImageView ivSuccess, ImageView ivError, String result) {
        progressbar.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
        if (isSuccess) {
            ivSuccess.setVisibility(View.VISIBLE);
        } else {
            ivError.setVisibility(View.VISIBLE);
        }
        if (result != null) {
            ll_result.setVisibility(View.VISIBLE);
            tv_result.setText(result);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PHONE:
                    diagnoseNet();
                    break;
                case MSG_NET:
                    boolean status_ping = (boolean) msg.obj;
                    if (status_ping) { // 网络Ping通
                        endNetDiagnose(pro_phone_ping, tv_phone_status_ping, status_ping, iv_server_success_ping, iv_phone_error_ping, null);
                        diagnoseServer();
                    } else { // 网络PING失败
                        endNetDiagnose(pro_phone_ping, tv_phone_status_ping, status_ping, iv_server_success_ping, iv_phone_error_ping, "请检查网络是否能上网");
                        pro_server_dia.setVisibility(View.GONE);
                        iv_server_error.setVisibility(View.VISIBLE);
                    }

                    break;
                case MSG_SERVER:
                    boolean status = (boolean) msg.obj;
                    int statusCode = msg.arg1;
                    if (status && statusCode == 200) {
                        endNetDiagnose(pro_server_dia, tv_server_status, true, iv_server_success, iv_server_error, "诊断完成，网络通畅！");
                    } else {
                        tv_server_status.setVisibility(View.GONE);
                        ll_result.setVisibility(View.VISIBLE);
                        iv_server_error.setVisibility(View.VISIBLE);
                        switch (statusCode) {
                            //根据 code 自定义处理结果
                            case -1:
                                tv_result.setText("请检查服务器是否开启，或者服务器地址是否正确！");
                                break;
                            case 404:
                                tv_result.setText("404错误，请检查输入的URL");
                                break;
                            case 500:
                                tv_result.setText("500错误，服务器遇到错误，无法完成请求");
                                break;
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 本地配置是否可用检测
     */
    private Runnable phoneCheckTask = new Runnable() {
        @Override
        public void run() {
            boolean status = NetUtils.checkNetworkState(mActivity);
            if (status) { // 手机网络没问题
                endNetDiagnose(pro_phone_dia, tv_phone_status, true, iv_phone_success, iv_phone_error, null);

                //随后检测服务器
                Message message = Message.obtain();
                message.what = MSG_PHONE;
                handler.sendMessage(message);

            } else { // 手机网络有问题

                endNetDiagnose(pro_phone_dia, tv_phone_status, false, iv_phone_success, iv_phone_error, "请检查手机网络是否开启！");

                //网络连通错误提示
                pro_phone_ping.setVisibility(View.GONE);
                iv_phone_error_ping.setVisibility(View.VISIBLE);

                //服务器连通错误提示
                pro_server_dia.setVisibility(View.GONE);
                iv_server_error.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 网络连通检测任务
     */
    private Runnable netCheckTask = new Runnable() {
        @Override
        public void run() {
            NetUtils.getStatus(PING_WEBSITE, new NetCallBack() {
                @Override
                public void onResponse(boolean status, int statusCode) {
                    Message message = Message.obtain();
                    message.obj = status;
                    message.arg1 = statusCode;
                    message.what = MSG_NET;
                    handler.sendMessage(message);
                }
            });
        }
    };

    /**
     * 服务器是否可用检测
     */
    private Runnable serverCheckTask = new Runnable() {
        @Override
        public void run() {
            NetUtils.getStatus(website, new NetCallBack() {
                @Override
                public void onResponse(boolean status, int statusCode) {
                    Message message = Message.obtain();
                    message.obj = status;
                    message.arg1 = statusCode;
                    message.what = MSG_SERVER;
                    handler.sendMessage(message);
                }
            });
        }
    };
}
