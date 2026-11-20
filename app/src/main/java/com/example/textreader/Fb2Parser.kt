package com.example.textreader

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

/**
 * Парсер формата FB2 (это XML-формат).
 * Извлекает текст из тегов <p> и <title>.
 */
object Fb2Parser {

    fun parse(inputStream: InputStream): List<String> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)

        val paragraphs = mutableListOf<String>()
        var currentTag = ""
        val sb = StringBuilder()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                }
                XmlPullParser.TEXT -> {
                    if (currentTag == "p" || currentTag == "title") {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            sb.append(text).append(" ")
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "p" || parser.name == "title") {
                        if (sb.isNotEmpty()) {
                            paragraphs.add(sb.toString().trim())
                            sb.clear()
                        }
                    }
                    currentTag = ""
                }
            }
            eventType = parser.next()
        }
        return paragraphs
    }
}