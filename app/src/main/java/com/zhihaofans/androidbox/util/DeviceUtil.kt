package com.zhihaofans.androidbox.util

import dev.utils.app.DeviceUtils

/**

 * @author: zhihaofans

 * @date: 2019-02-28 19:36

 */
class DeviceUtil {
    companion object {
        fun isXiaomi() = DeviceUtils.getManufacturer().toLowerCase() == "xiaomi"
        fun isSamsung() = DeviceUtils.getManufacturer().toLowerCase() == "samsung"

    }
}