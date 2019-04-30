package com.lucianbc.receiptscan.view.fragment.scanner

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.utils.logd
import com.lucianbc.receiptscan.viewmodel.scanner.ActivityViewModel
import com.otaliastudios.cameraview.Size
import com.otaliastudios.cameraview.SizeSelector
import kotlinx.android.synthetic.main.scanner_fragment.*
import com.lucianbc.receiptscan.databinding.ScannerFragmentBinding
import com.lucianbc.receiptscan.viewmodel.scanner.ScannerViewModel
import com.otaliastudios.cameraview.Flash

class Scanner : Fragment() {
    companion object {
        fun newInstance() = Scanner()
    }

    private lateinit var activityViewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<ScannerFragmentBinding>(
            inflater,
            R.layout.scanner_fragment,
            container,
            false)
        val vm = ViewModelProviders.of(this).get(ScannerViewModel::class.java)

        binding.viewModel = vm
        binding.lifecycleOwner = this

        observe(vm)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanner.setLifecycleOwner(viewLifecycleOwner)
        scanner.setPreviewStreamSize(object: SizeSelector {
            override fun select(source: MutableList<Size>): MutableList<Size> {
                logd(source.toString())
                return source
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activityViewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
    }

    private fun observe(vm: ScannerViewModel) {
        vm.flash.observe(this, Observer {
            if (it) scanner.flash = Flash.TORCH
            else scanner.flash = Flash.OFF
        })
    }
}

@BindingAdapter("android:src")
fun ImageButton.bindSource(isOn: Boolean) {
    if (isOn)
        this.setImageResource(R.drawable.ic_flash_off_white_24dp)
    else
        this.setImageResource(R.drawable.ic_flash_on_white_24dp)
}