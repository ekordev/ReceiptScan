package com.lucianbc.receiptscan.presentation.home.exports.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.base.BaseFragment
import com.lucianbc.receiptscan.databinding.FragmentFormContainerBinding
import com.lucianbc.receiptscan.domain.export.SessionException
import com.lucianbc.receiptscan.domain.export.CloudSession
import com.lucianbc.receiptscan.domain.export.LocalSession
import kotlinx.android.synthetic.main.fragment_form_container.*


class FormContainerFragment(
    private val cloudCallback: (CloudSession) -> Unit,
    private val localCallback: (LocalSession) -> Unit
)
    : BaseFragment<FormViewModel>(FormViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initParentViewModel()
        return setupBinding(inflater, container)?.root
    }

    private fun setupBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFormContainerBinding? {
        val binding = DataBindingUtil.inflate<FragmentFormContainerBinding>(
            inflater,
            R.layout.fragment_form_container,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = OptionsAdapter(fragmentManager!!, ::advancePager, ::triggerLocal)
        exportOptionsPager.offscreenPageLimit = adapter.count
        exportOptionsPager.adapter = adapter

        exportOptionsPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.option.value =
                    (if (position == adapter.count- 1) FormViewModel.Option.Check else FormViewModel.Option.Next)
            }
        })

        wormDotsIndicator.setViewPager(exportOptionsPager)

        actionButton.setOnClickListener {
            if (viewModel.option.value == FormViewModel.Option.Next)
                advancePager()
            else {
                try {
                    viewModel.validateCloudInput().let(cloudCallback::invoke)
                } catch (e: SessionException) {
                    Toast.makeText(activity!!, e.error.name, Toast.LENGTH_SHORT).show()
                }
            }
        }
        closeFormBtn.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    private fun advancePager() {
        exportOptionsPager.currentItem = exportOptionsPager.currentItem + 1
    }

    private fun triggerLocal() {
        viewModel.validateLocalInput().let(localCallback)
    }

    companion object {
        const val TAG = "EXPORT_FORM_CONTAINER"
    }
}
