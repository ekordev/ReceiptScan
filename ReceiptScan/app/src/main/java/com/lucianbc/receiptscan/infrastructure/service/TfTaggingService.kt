package com.lucianbc.receiptscan.infrastructure.service

import android.content.Context
import com.lucianbc.receiptscan.domain.model.Annotations
import com.lucianbc.receiptscan.domain.model.OcrElements
import com.lucianbc.receiptscan.domain.model.Tag
import com.lucianbc.receiptscan.domain.model.toAnnotation
import com.lucianbc.receiptscan.domain.service.TaggingService
import com.lucianbc.receiptscan.infrastructure.dao.Converters
import com.lucianbc.receiptscan.util.Just
import com.lucianbc.receiptscan.util.None
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class TfTaggingService @Inject constructor(
    private val context: Context
) : TaggingService {
    override fun tag(elements: OcrElements): Annotations {
        return when (val samples = transform(elements.toList())) {
            is None -> emptySequence()
            is Just -> {
                val tags = classify(samples.value)
                val result = elements
                    .zip(tags.asSequence())
                    .map { it.first.toAnnotation(it.second) }
                result
            }
        }
    }

    private fun classify(data: List<Pair<ByteBuffer, Array<FloatArray>>>): List<Tag> {
        val tfModel = loadModelFile(context)
        val tflite = Interpreter(tfModel, Interpreter.Options())

        return data.map { element ->
            tflite.run(element.first, element.second)
            val tagOrdinal = element.second[0].indexOf(element.second[0].max()!!)
            Converters.toTag(tagOrdinal)
        }
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("model_v1.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}