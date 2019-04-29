package com.lucianbc.receiptscan.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.utils.logd
import com.lucianbc.receiptscan.view.fragment.scanner.Scanner
import com.lucianbc.receiptscan.view.fragment.scanner.Error
import com.lucianbc.receiptscan.view.fragment.scanner.Permission
import com.lucianbc.receiptscan.viewmodel.ScannerViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class ScannerActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val stateObserver = Observer<ScannerViewModel.State> {
        when (it) {
            ScannerViewModel.State.NoPermission -> loadPermissionMessage()
            ScannerViewModel.State.Allowed -> loadScannerFragment()
            ScannerViewModel.State.Error -> loadErrorMessage()
        }
    }

    private lateinit var viewModel: ScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java)
        logd("ViewModel in activity: $viewModel")
        observeState(viewModel)
        checkCameraPermissions()
    }

    private fun observeState(viewModel: ScannerViewModel) {
        viewModel.state.observe(this, stateObserver)
    }

    @AfterPermissionGranted(REQUEST_CAMERA_PERMISSION)
    private fun checkCameraPermissions() {
        logd("Checking permission")
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA))
            viewModel.state.value = ScannerViewModel.State.Allowed
        else
            viewModel.state.value = ScannerViewModel.State.NoPermission
    }

    private fun loadScannerFragment() {
        replaceFragment(Scanner.newInstance())
    }

    private fun loadPermissionMessage() {
        replaceFragment(Permission.newInstance())
    }

    private fun loadErrorMessage() {
        replaceFragment(Error.newInstance())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkCameraPermissions()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.scanner_container, fragment)
            .commit()
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 100
    }
}
