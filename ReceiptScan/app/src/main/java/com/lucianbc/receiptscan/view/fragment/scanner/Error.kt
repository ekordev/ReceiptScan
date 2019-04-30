package com.lucianbc.receiptscan.view.fragment.scanner


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.utils.logd
import com.lucianbc.receiptscan.viewmodel.scanner.ActivityViewModel

class Error : Fragment() {

    companion object {
        fun newInstance() = Error()
    }

    private lateinit var viewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner_error, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ActivityViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        logd("Error destoryed")
    }
}
