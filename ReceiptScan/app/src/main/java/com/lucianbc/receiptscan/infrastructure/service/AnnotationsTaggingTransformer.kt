package com.lucianbc.receiptscan.infrastructure.service

import com.lucianbc.receiptscan.domain.model.OcrElement
import com.lucianbc.receiptscan.domain.service.ThresholdBetweenNeighbors
import com.lucianbc.receiptscan.util.Just
import com.lucianbc.receiptscan.util.None
import com.lucianbc.receiptscan.util.Optional
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun transform(elements: List<OcrElement>): Optional<MutableList<Pair<ByteBuffer, Array<FloatArray>>>> {
    if (elements.isEmpty()) return None
    val (top, bottom, left, right) = boundaries(elements)
    val width = right - left + 1
    val height = bottom - top + 1

    val lines = lineUnificationStrategy.unify(elements)

    val receiptFeatures = mutableListOf<Pair<ByteBuffer, Array<FloatArray>>>()

    for (line_ix in 0 until lines.size)
        for (element_ix in 0 until lines[line_ix].size) {
            val element = lines[line_ix][element_ix]
            val selfFeatures = createTextFeatures(element.text)

            val lineHintFeatures =
                when {
                    line_ix -1 >= 0 -> createTextFeatures(lines[line_ix - 1])
                    else -> createTextFeatures("")
                }

            val columnHintFeatures =
                when {
                    element_ix - 1 >= 0 -> createTextFeatures(lines[line_ix][element_ix - 1].text)
                    element_ix + 1 < lines[line_ix].size -> createTextFeatures(
                        lines[line_ix][element_ix + 1].text
                    )
                    else -> createTextFeatures("")
                }

            val topDistance = (element.top - top).toFloat() / height
            val leftDistance = (element.left - left).toFloat() / width

            val featuresBytes = ByteBuffer.allocateDirect(
                1 //1 dimension
                        * (selfFeatures.size + lineHintFeatures.size + columnHintFeatures.size + 2) // nr features
                        * 1 // 1 row
                        * 4 // 4 bytes per float
            )
            featuresBytes.order(ByteOrder.nativeOrder())
            selfFeatures.forEach { featuresBytes.putFloat(it) }
            lineHintFeatures.forEach { featuresBytes.putFloat(it) }
            columnHintFeatures.forEach { featuresBytes.putFloat(it) }
            featuresBytes.putFloat(topDistance)
            featuresBytes.putFloat(leftDistance)

            val resultSpace = Array(1) {FloatArray(6)}

            receiptFeatures.add(featuresBytes to resultSpace)
        }

    return Just(receiptFeatures)
}

private fun createTextFeatures(line: List<OcrElement>) =
    createTextFeatures(line.joinToString(" ") { it.text })

fun createTextFeatures(text: String): FloatArray {
    val counts = text
        .filter { it in ALPHABET }
        .zipWithNext { a, b -> String(charArrayOf(a, b)) }
        .groupingBy { it }
        .eachCount()

    val features = FloatArray(FEATURES.size)
    for (i in 0 until FEATURES.size)
        features[i] = counts[FEATURES[i]]?.toFloat() ?: 0F
    return features
}

private val lineUnificationStrategy = ThresholdBetweenNeighbors()

private fun boundaries(elements: List<OcrElement>): Boundaries {
    var top = -1
    var bottom = -1
    var left = -1
    var right = -1
    for (a in elements) {
        if (a.top < top) top = a.top
        if (a.left < left) left = a.left
        if (a.top > bottom) bottom = a.top
        if (a.left > right) right = a.left
    }
    return Boundaries(top, bottom, left, right)
}

private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz.,/- %:0123456789"
private val FEATURES = ALPHABET.bigrams()

private data class Boundaries(val top: Int, val bottom: Int, val left: Int, val right: Int)

private fun String.bigrams(): List<String> {
    val bigrams = mutableListOf<String>()
    for (x in this)
        for (y in this)
            bigrams.add(String(charArrayOf(x, y)))
    return bigrams
}