package com.lucianbc.receiptscan.presentation.home.exports.form


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_local_or_cloud.*

class LocalOrCloudFragment(
    private val onLocalOption: () -> Unit,
    private val onCloudOption: () -> Unit
) : BaseFragment<FormViewModel>(FormViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return inflater.inflate(
            R.layout.fragment_local_or_cloud,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cloudOption.setOnClickListener { onCloudOption() }
        localOption.setOnClickListener { onLocalOption() }
    }
}
