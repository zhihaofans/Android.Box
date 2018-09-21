package com.zhihaofans.androidbox.database

import com.haoge.easyandroid.easy.PreferenceRename
import com.haoge.easyandroid.easy.PreferenceSupport

/**
 * Created by zhihaofans on 2018/9/20.
 */
@PreferenceRename("android_box_experss")
class ExpressData : PreferenceSupport() {
    var expressListJson: String = ""
}
