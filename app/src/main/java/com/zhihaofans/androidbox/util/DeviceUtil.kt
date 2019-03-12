package com.zhihaofans.androidbox.util

import dev.utils.app.DeviceUtils


/**

 * @author: zhihaofans

 * @date: 2019-02-28 19:36

 */
class DeviceUtil {
    companion object {
        fun isXiaomi(): Boolean = DeviceUtils.getManufacturer().toLowerCase() == "xiaomi"
        fun isSamsung(): Boolean = DeviceUtils.getManufacturer().toLowerCase() == "samsung"
    }
}