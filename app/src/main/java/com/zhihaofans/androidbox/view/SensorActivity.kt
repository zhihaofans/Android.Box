package com.zhihaofans.androidbox.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.util.ToastUtil
import io.zhihao.library.android.kotlinEx.init
import kotlinx.android.synthetic.main.activity_sensor.*
import kotlinx.android.synthetic.main.content_sensor.*


class SensorActivity : AppCompatActivity(), SensorEventListener {

    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Stop?", Snackbar.LENGTH_LONG)
                    .setAction("Yes") {
                        mSensorManager?.unregisterListener(this)
                        mSensor = null
                        ToastUtil.success()
                    }.show()
        }
        try {
            mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensorName = "温度"
            val sensorId = Sensor.TYPE_AMBIENT_TEMPERATURE
            if (mSensorManager == null) {
                ToastUtil.error("获取传感器系统服务失败")
            } else {
                mSensor = mSensorManager!!.getDefaultSensor(sensorId)
                if (mSensor == null) {
                    ToastUtil.error("不支持${sensorName}传感器")
                } else {
                    if (mSensorManager!!.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI)) {
                        ToastUtil.success("注册${sensorName}传感器成功")
                    } else {
                        ToastUtil.error("注册${sensorName}传感器失败")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.error("初始化传感器失败")
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        // 取消监听
        mSensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // 当传感器精度发生改变时回调该方法
    }

    override fun onSensorChanged(event: SensorEvent) {
        // 当传感器的值改变的时候回调该方法

        val values = event.values
        // 获取传感器类型
        val sb = mutableListOf<String>()
        this@SensorActivity.title = event.sensor.stringType
        sb.addAll(values.map {
            it.toString()
        }.toList())
        listView_sensor.init(this, sb)
    }

}
