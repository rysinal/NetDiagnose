package mobi.wonders.apps.android.netdiagnose.net;

import android.view.View;

/**
 * <p>
 * Title:CMS_[所属模块]_[标题]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * <p/>
 * NetDiagnose
 *
 * @author yuqing
 * @date 2016/1/18
 */
public interface NetDiagnose {

    /**
     *  自定义布局
     * @return
     */
    View getDialogView();

    /**
     *  初始化布局
     */
    void initialise();

    /**
     * 本地检测
     */
    void diagnosePhone();

    /**
     * 网络检测
     */
    void diagnoseNet();

    /**
     * 服务器检测
     */
    void diagnoseServer();

    /**
     * 开始检测
     */
    void startDiagnose();
}
