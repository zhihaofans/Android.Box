package com.zhihaofans.androidbox.database

import com.haoge.easyandroid.easy.PreferenceRename
import com.haoge.easyandroid.easy.PreferenceSupport

/**
 * Created by zhihaofans on 2018/7/6.
 */
@PreferenceRename("android_box")
class SaveDataSP : PreferenceSupport() {
    var server_chan_key: String = ""
    var news_box_last_site_id: String = ""
    var news_box_last_site_channel_id: String = ""
}

@PreferenceRename("android_box_setting")
class SettingSP : PreferenceSupport() {
    var browser_use_chrome_custom_tabs: Boolean = true
    var image_url_open_with_builtin_viewer: Boolean = true
}