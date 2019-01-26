package com.zhihaofans.androidbox.util

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

/**
 * Created by zhihaofans on 2019/1/25.
 */
class RandomUtil {
    companion object {
        fun number(min: Int, max: Int): Int {
            return Random.nextInt(min, max)
        }

        fun number2(min: Int, max: Int): Int {
            return ThreadLocalRandom.current().nextInt(min, max)
        }

        fun number3(min: Int, max: Int): Int {
            return SplittableRandom().nextInt(min, max)
        }
    }
}