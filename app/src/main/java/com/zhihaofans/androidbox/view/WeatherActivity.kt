package com.zhihaofans.androidbox.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhihaofans.androidbox.R
import com.zhihaofans.androidbox.gson.WeatherNowGson
import com.zhihaofans.androidbox.gson.WeatherNowResultGson
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.content_weather.*
import okhttp3.*
import okhttp3.CacheControl
import java.io.IOException

class WeatherActivity : AppCompatActivity() {
    private val g = Gson()
    private val request = Request.Builder().get().cacheControl(CacheControl.Builder().noCache().build())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        setSupportActionBar(toolbar)
        loading()
        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            loading()
        }
    }

    fun loading() {
        //val loadingProgressBar: ProgressDialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Loading...")

        val client = OkHttpClient()
        val url = "https://api.seniverse.com/v3/weather/now.json?key=y0rkk7s0r2lxzfbi&location=ip&language=zh-Hans"
        request.url(url)
        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //loadingProgressBar.dismiss()
                runOnUiThread {
                    Snackbar.make(coordinatorLayout_weather, "获取信息失败", Snackbar.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body()
                var responseStr = ""
                if (resBody != null) {
                    responseStr = resBody.string()
                    val weatherNowGson: WeatherNowGson = g.fromJson(responseStr, WeatherNowGson::class.java)
                    if (weatherNowGson.results == null || weatherNowGson.results.size == 0) {
                        Logger.e("Get weather error\n$url\nCode:${weatherNowGson.status_code}\nMessage:${weatherNowGson.status}")
                        runOnUiThread {
                            Snackbar.make(coordinatorLayout_weather, weatherNowGson.status, Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        val weatherResult: WeatherNowResultGson = weatherNowGson.results[0]
                        runOnUiThread {
                            listView_weather.adapter = ArrayAdapter<String>(this@WeatherActivity, android.R.layout.simple_list_item_1, mutableListOf(
                                    weatherResult.location.name,
                                    weatherResult.now.text,
                                    weatherResult.now.temperature + " 度"
                            ))
                        }
                    }
                } else {
                    runOnUiThread {
                        Snackbar.make(coordinatorLayout_weather, "空白数据", Snackbar.LENGTH_SHORT).show()
                    }
                }
                Logger.d(responseStr)
                //loadingProgressBar.dismiss()
                runOnUiThread {
                }
            }
        })
    }
}
