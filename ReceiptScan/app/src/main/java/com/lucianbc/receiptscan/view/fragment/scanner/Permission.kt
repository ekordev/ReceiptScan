package com.lucianbc.receiptscan.view.fragment.scanner

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.utils.logd
import com.lucianbc.receiptscan.view.activity.ScannerActivity
import com.lucianbc.receiptscan.viewmodel.ScannerViewModel
import kotlinx.android.synthetic.main.fragment_scanner_permission.*
import pub.devrel.easypermissions.EasyPermissions

class Permission : Fragment() {

    companion object {
        fun newInstance() = Permission()
    }

    private lateinit var viewModel: ScannerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner_permission, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScannerViewModel::class.java)
        scanner_permission_btn.setOnClickListener(requestPermission)
    }

    private val requestPermission = View.OnClickListener {
        logd("Requesting permission")
        EasyPermissions.requestPermissions(
            activity!!,
            getString(R.string.request_camera_msg),
            ScannerActivity.REQUEST_CAMERA_PERMISSION,
            Manifest.permission.CAMERA
        )
    }
}
