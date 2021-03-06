package common;

import android.app.Application;

import common.netstate.NetChangeObserver;
import common.netstate.NetStateReceiver;
import common.netstate.NetworkUtils;
import common.utils.ToastUtils;

/**
 * 描述:
 * Created by mjd on 2017/1/16.
 */

public class CommonApplication extends Application {

    public NetworkUtils.NetworkType mNetType;
    private NetStateReceiver netStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        initNetChangeReceiver();

    }

    /**
     * 应用全局的网络变化处理
     */
    private void initNetChangeReceiver() {

        //获取当前网络类型
        mNetType = NetworkUtils.getNetworkType(this);

        //定义网络状态的广播接受者
        netStateReceiver = NetStateReceiver.getReceiver();

        //给广播接受者注册一个观察者
        netStateReceiver.registerObserver(netChangeObserver);

        //注册网络变化广播
        NetworkUtils.registerNetStateReceiver(this, netStateReceiver);

    }

    private NetChangeObserver netChangeObserver = new NetChangeObserver() {

        @Override
        public void onConnect(NetworkUtils.NetworkType type) {
            CommonApplication.this.onNetConnect(type);
        }

        @Override
        public void onDisConnect() {
            CommonApplication.this.onNetDisConnect();
        }
    };

    public void onNetDisConnect() {
        ToastUtils.showShort(this, "网络已断开,请检查网络设置");
        mNetType = NetworkUtils.NetworkType.NETWORK_NONE;
    }

    public void onNetConnect(NetworkUtils.NetworkType type) {
        if (type == mNetType) return; //net not change
        switch (type) {
            case NETWORK_WIFI:
                ToastUtils.showLong(this, "已切换到 WIFI 网络");
                break;
            case NETWORK_MOBILE:
                ToastUtils.showLong(this, "已切换到 2G/3G/4G 网络");
                break;
        }
        mNetType = type;
    }

    //释放广播接受者(建议在 最后一个 Activity 退出前调用)
    public void destroyReceiver() {
        //移除里面的观察者
        netStateReceiver.removeObserver(netChangeObserver);
        //解注册广播接受者,
        NetworkUtils.unRegisterNetStateReceiver(this, netStateReceiver);
    }

}
