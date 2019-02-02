package com.zhihaofans.androidbox.util

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

/**
 * Created by zhihaofans on 2019/1/25.
 */
class RandomUtil {
    companion object {
        fun getInt(min: Int, max: Int): Int? {
            return try {
                Random.nextInt(min, max)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getInt2(min: Int, max: Int): Int? {
            return try {
                ThreadLocalRandom.current().nextInt(min, max)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getInt3(min: Int, max: Int): Int? {
            return try {
                SplittableRandom().nextInt(min, max)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        }

        fun getStringItems(itemList: List<String>, length: Int): List<String>? {
            return if (length > itemList.size) {
                null
            } else {
                //TODO:getStringItems()
                null
            }
        }
    }
}