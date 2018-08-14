package com.zhihaofans.androidbox.util

import org.jsoup.nodes.Document

/**
 * Created by zhihaofans on 2017/11/20.
 */
class JsoupUtil(inputDom: Document) {
    private val dom: Document? = inputDom

    fun attr(cssQuery: String, attrName: String): String {
        val a = dom!!.select(cssQuery)
        if (a.isNotEmpty()) {
            val attr = a.attr(attrName)
            if (attr.isNotEmpty()) {
                return attr
            }
        }
        return ""
    }

    fun title(): String {
        return this.text("head > title")
    }

    fun html(cssQuery: String): String {
        val a = dom!!.select(cssQuery)
        if (a.isNotEmpty()) {
            val html = a.html()
            if (html.isNotEmpty()) {
                return html
            }
        }
        return ""
    }

    fun html(cssQuery: String, index: Int): String {
        val a = dom!!.select(cssQuery)
        if (a.isNotEmpty()) {
            if (a.size == 1) return html(cssQuery)
            val html = a[index].html()
            if (html.isNotEmpty()) {
                return html
            }
        }
        return ""
    }

    fun text(cssQuery: String): String {
        val a = dom!!.select(cssQuery)
        if (a.isNotEmpty()) {
            val text = a.html()
            if (text.isNotEmpty()) {
                return text
            }
        }
        return ""
    }
}
