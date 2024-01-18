package com.chtibizoux.adeapp.data.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class ResourcesParser {
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): ResourceTree {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readTree(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTree(parser: XmlPullParser): ResourceTree {
        val categories = mutableListOf<Category>()
        parser.require(XmlPullParser.START_TAG, ns, "tree")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "category") {
                categories.add(readCategory(parser))
            } else {
                skip(parser)
            }
        }
        return ResourceTree(categories)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCategory(parser: XmlPullParser): Category {
        parser.require(XmlPullParser.START_TAG, ns, "category")
        val name = parser.getAttributeValue(ns, "category")
        val resources = mutableListOf<Resource>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "branch" || parser.name == "leaf") {
                resources.add(readResource(parser))
            } else {
                skip(parser)
            }
        }
        return Category(name, resources)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readResource(parser: XmlPullParser): Resource {
//        parser.require(XmlPullParser.START_TAG, ns, "branch")
//        parser.require(XmlPullParser.START_TAG, ns, "leaf")
        val id = parser.getAttributeValue(ns, "id").toInt()
        val name = parser.getAttributeValue(ns, "name")
        val resources = mutableListOf<Resource>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "branch" || parser.name == "leaf") {
                resources.add(readResource(parser))
            } else {
                skip(parser)
            }
        }
        return Resource(id, name, resources)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}