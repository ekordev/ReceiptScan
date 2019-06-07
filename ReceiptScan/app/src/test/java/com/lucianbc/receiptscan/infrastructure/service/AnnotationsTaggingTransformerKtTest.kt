package com.lucianbc.receiptscan.infrastructure.service

import com.lucianbc.receiptscan.domain.model.OcrElement
import com.lucianbc.receiptscan.util.Just
import org.junit.Test

import org.junit.Assert.*

class AnnotationsTaggingTransformerKtTest {

    @Test
    fun transform() {
        val something = listOf(OcrElement("sc", 24, 55, 313, 101))
        val result = transform(something)
        assertTrue(result is Just)
    }

    @Test
    fun createTextFeatures() {
        val subject = "asdfg"
        val result = createTextFeatures(subject)
        assertEquals(result.size, 1849)
    }
}