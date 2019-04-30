package com.lucianbc.receiptscan.common.service

import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.lucianbc.receiptscan.utils.logd
import com.lucianbc.receiptscan.view.fragment.scanner.BoxedData
import com.lucianbc.receiptscan.view.fragment.scanner.Element
import com.otaliastudios.cameraview.Frame

typealias Callback = (Iterable<Element>) -> Unit

class ScannerProcessor {

    private val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

    fun process(image: Bitmap, onSuccess: Callback) {
        val visionImg = FirebaseVisionImage.fromBitmap(image)
        process(visionImg, onSuccess)
    }

    fun process(image: Frame, onSuccess: Callback) {
        val md = FirebaseVisionImageMetadata.Builder()
            .setFormat(image.format)
            .setRotation(rotation(image.rotation))
            .setHeight(image.size.height)
            .setWidth(image.size.width)
            .build()
        val visionImg = FirebaseVisionImage.fromByteArray(image.data, md)
        process(visionImg, onSuccess)
    }

    private fun process(visionImg: FirebaseVisionImage, onSuccess: Callback) {
        detector.processImage(visionImg)
            .addOnSuccessListener {
                val lines = it.textBlocks
                    .flatMap { t -> t.lines }
                    .map { l -> BoxedData(l.boundingBox!!, l.text) }
                onSuccess(lines)
            }
            .addOnFailureListener { logd("Error when recognizing the image") }
    }

    private fun rotation(rotation: Int): Int {
        return when (rotation) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> FirebaseVisionImageMetadata.ROTATION_0
        }
    }
}