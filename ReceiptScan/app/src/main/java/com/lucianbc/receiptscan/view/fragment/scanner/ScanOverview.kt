package com.lucianbc.receiptscan.view.fragment.scanner


import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.utils.logd
import kotlinx.android.synthetic.main.fragment_scan_overview.*

class ScanOverview : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scan_image.setImageBitmap(argBitmap())
    }

    private fun argBitmap(): Bitmap? {
        return arguments!!.getParcelable(IMAGE_KEY)
    }

    companion object {
        fun newInstance() = ScanOverview()
        const val IMAGE_KEY = "image"
    }
}
