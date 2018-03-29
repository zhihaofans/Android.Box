package com.zhihaofans.androidbox.view

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import kotlinx.android.synthetic.main.activity_setting.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import org.jetbrains.anko.toast


class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this@SettingActivity.title = getString(R.string.text_setting)
        var hasPermission = false
        val path = Environment.getExternalStorageDirectory().toString() + "/httpbin.png"
        val downUrl = "http://httpbin.org/image/png"
        HiPermission.create(this@SettingActivity)
                .checkMutiPermission(object : PermissionCallback {
                    override fun onClose() {
                        Logger.i("onClose")
                        toast("用户关闭权限申请")
                        hasPermission = false
                    }

                    override fun onFinish() {
                        toast("所有权限申请完成")
                        hasPermission = true

                    }

                    override fun onDeny(permission: String, position: Int) {
                        Logger.d("onDeny")
                        toast("权限申请失败")
                        hasPermission = false
                    }

                    override fun onGuarantee(permission: String, position: Int) {
                        Logger.d("onGuarantee")
                    }
                })
        fab_save.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            if (hasPermission) {
                FileDownloader.getImpl().create(downUrl)
                        .setPath(path)
                        .setListener(object : FileDownloadListener() {
                            override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                                Logger.d("FileDownloader\npending\n$downUrl\nBytes$soFarBytes/$totalBytes")
                            }

                            override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                                Logger.d("FileDownloader\nconnected\n$downUrl\nBytes:$soFarBytes/$totalBytes")
                            }

                            override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                                Logger.d("FileDownloader\nconnected\n$downUrl\nBytes:$soFarBytes/$totalBytes")
                            }

                            override fun blockComplete(task: BaseDownloadTask?) {
                                Logger.d("FileDownloader\nblockComplete\n$downUrl")
                            }

                            override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                                Logger.d("FileDownloader\nretry\n$downUrl\nBytes:$soFarBytes\nTimes:$retryingTimes")
                            }

                            override fun completed(task: BaseDownloadTask) {
                                Logger.d("FileDownloader\ncompleted\n$downUrl\n$path")
                            }

                            override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                                Logger.d("FileDownloader\npaused\n$downUrl\nBytes:$soFarBytes/$totalBytes")
                            }

                            override fun error(task: BaseDownloadTask, e: Throwable) {
                                Logger.e("FileDownloader\nerror\n$downUrl\n${e.message}")
                                e.printStackTrace()
                            }

                            override fun warn(task: BaseDownloadTask) {
                                Logger.w("FileDownloader\nwarn\n$downUrl\n$path")

                            }
                        }).start()
            } else {
                Snackbar.make(view, "缺少权限", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.home -> finish()
        }
        return true
    }
}
