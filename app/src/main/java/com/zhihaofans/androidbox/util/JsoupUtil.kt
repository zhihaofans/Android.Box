package com.zhihaofans.androidbox.util

import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * Created by zhihaofans on 2017/11/20.
 */
class JsoupUtil(inputDom: Document) {
    private val dom: Document? = inputDom

    fun img(cssQuery: String): String {
        return this.attr(cssQuery, "src")
    }

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

    fun html(cssQuery: String, index: Int = 0): String {
        val a: Elements = dom!!.select(cssQuery)
        if (a.isNotEmpty()) {
            if (a.size == 1) return a.html()
            val html = a[index].html()
            if (html.isNotEmpty()) {
                return html
            }
        }
        return ""
    }

    fun htmlorNull(cssQuery: String, index: Int = 0): String? {
        val a = dom!!.select(cssQuery)
        if (a.isNotEmpty()) {
            if (a.size == 1) return html(cssQuery)
            val html = a[index].html()
            if (html.isNotEmpty()) {
                return html
            }
        }
        return null
    }

    fun textorNull(cssQuery: String): String? {
        val a = dom!!.select(cssQuery)
        if (a.isNotEmpty()) {
            val text = a.html()
            if (text.isNotEmpty()) {
                return text
            }
        }
        return null
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

    fun body(): Elements? {
        val webBody = dom!!.select("html > body")
        return if (webBody.isEmpty()) {
            null
        } else {
            webBody
        }
    }

    fun link(cssQuery: String): String {
        val link = dom!!.select(cssQuery)
        return if (link.isEmpty()) {
            ""
        } else {
            link.attr("href") ?: ""
        }
    }
}
