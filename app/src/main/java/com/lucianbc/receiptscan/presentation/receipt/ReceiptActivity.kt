package com.lucianbc.receiptscan.presentation.receipt

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.base.BaseActivity
import com.lucianbc.receiptscan.presentation.ShareOptionsSheet
import com.lucianbc.receiptscan.util.loge
import kotlinx.android.synthetic.main.fragment_receipt.*

class ReceiptActivity
    : BaseActivity<ReceiptViewModel>(ReceiptViewModel::class.java) {

    private val imageFrag by lazy { ImageFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        safeInit()
        setContentView(R.layout.activity_receipt)
        setupButtons()
    }

    private fun safeInit() {
        intent.extras?.apply {
            getLong(RECEIPT_ID).let { viewModel.init(it) }
        }
    }

    private fun setupButtons() {
        shareReceiptBtn.setOnClickListener {
            ShareOptionsSheet().apply {
                show(supportFragmentManager, ShareOptionsSheet.TAG)
                onTextOnly = { viewModel.exportText(textExporter) }
                onImageOnly = { viewModel.exportImage(imageExporter, shareFileErrorHandler) }
                onBoth = { viewModel.exportBoth(bothExporter, shareFileErrorHandler) }
            }
        }
        imageBtn.setOnClickListener(addFragment(IMAGE_FRAG_TAG, imageFrag))
    }

    private val textExporter = { text: String ->
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Receipt from ${viewModel.merchant.value}")
            type = "text/plain"
        }.let(::startActivity)
    }

    private val imageExporter = { imageUri: Uri ->
        Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "image/jpeg"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(imageUri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.let(::startActivity)
    }

    private val bothExporter = { text: String, imageUri: Uri ->
        Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "image/jpeg"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(imageUri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Receipt from ${viewModel.merchant.value}")
        }.let(::startActivity)
    }

    private val shareFileErrorHandler = { t: Throwable ->
        loge("Error exporting image", t)
        AlertDialog.Builder(this)
            .setTitle("Error!")
            .setMessage("Error when sharing the image")
            .setNegativeButton("Dismiss") { _, _ ->
                supportFragmentManager.apply {
                    findFragmentByTag(ShareOptionsSheet.TAG)?.let {
                        beginTransaction()
                            .remove(it)
                            .commit()
                    }
                }
            }
            .show()
        Unit
    }

    @SuppressLint("PrivateResource")
    private val inn = R.anim.mtrl_bottom_sheet_slide_in
    @SuppressLint("PrivateResource")
    private val out = R.anim.mtrl_bottom_sheet_slide_out

    private fun addFragment(tag: String, frag: Fragment): (View) -> Unit {
        return {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(inn, out, inn, out)
                .add(R.id.receiptContainer, frag, tag)
                .addToBackStack(tag)
                .commit()
        }
    }


    companion object {
        fun navIntent(
            context: Context,
            receiptId: Long
        ) = Intent (context, ReceiptActivity::class.java).apply {
            putExtra(RECEIPT_ID, receiptId)
        }

        private const val RECEIPT_ID = "RECEIPT_ID"
        private const val IMAGE_FRAG_TAG = "RECEIPT_IMAGE"
    }
}
