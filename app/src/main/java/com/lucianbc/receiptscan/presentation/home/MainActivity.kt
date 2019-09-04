package com.lucianbc.receiptscan.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.domain.export.CloudSession
import com.lucianbc.receiptscan.domain.export.LocalSession
import com.lucianbc.receiptscan.infrastructure.services.ExportService
import com.lucianbc.receiptscan.infrastructure.services.LocalExportService
import com.lucianbc.receiptscan.presentation.CategoryFragment
import com.lucianbc.receiptscan.presentation.CurrencyFragment
import com.lucianbc.receiptscan.presentation.Event
import com.lucianbc.receiptscan.presentation.home.exports.form.FormContainerFragment
import com.lucianbc.receiptscan.presentation.scanner.ScannerActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MainActivity :
    DaggerAppCompatActivity() {

    @Inject
    lateinit var eventBus: EventBus

    @SuppressLint("PrivateResource")
    private val inn = R.anim.mtrl_bottom_sheet_slide_in
    @SuppressLint("PrivateResource")
    private val out = R.anim.mtrl_bottom_sheet_slide_out

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readState(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdapter()
        initButtons()
    }

    private fun initAdapter() {
        val page = intent?.extras?.getString(PAGE_KEY)
                    ?.let(HomePagerAdapter.Page::valueOf)
                    ?: HomePagerAdapter.RECEIPTS

        supportFragmentManager.apply {
            HomePagerAdapter(this).also {
                pager.adapter = it
                pager.currentItem = page.position
            }
        }
    }

    private fun initButtons() {
        drafts_btn.setOnClickListener { setView(HomePagerAdapter.DRAFTS) }
        export_btn.setOnClickListener { setView(HomePagerAdapter.EXPORT) }
        receipts_btn.setOnClickListener { setView(HomePagerAdapter.RECEIPTS) }
        settings_btn.setOnClickListener { setView(HomePagerAdapter.SETTINGS) }
        scanner_button.setOnClickListener { goToScanner() }
    }

    private fun setView(page: HomePagerAdapter.Page) = pager.setCurrentItem(page.position, false)

    private fun goToScanner() {
        val intent = ScannerActivity.navIntent(this)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    @Subscribe
    fun onCurrencyTapped(event: Event.CurrencyTapped) {
        val frag = CurrencyFragment(event.callback)
        addFragment(CurrencyFragment.TAG, frag)
    }

    @Subscribe
    fun onCategoryTapped(event: Event.CategoryTapped) {
        val frag = CategoryFragment(event.callback)
        addFragment(CategoryFragment.TAG, frag)
    }

    private var lastLocalSession: LocalSession? = null

    @Subscribe
    fun onExportForm(event: Event.ExportForm) {
        val localExportWithPermission = { s: LocalSession ->
            lastLocalSession = s
            startLocalExportWithPermission()
        }

        val frag = FormContainerFragment(onCloudExport, localExportWithPermission)
        addFragment(FormContainerFragment.TAG, frag)
    }

    @AfterPermissionGranted(WRITE_STORAGE_PERMISSION_REQUEST)
    private fun startLocalExportWithPermission() {
        val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
        lastLocalSession?.let { s ->
            if (EasyPermissions.hasPermissions(this, perm))
                onLocalExport(s)
            else
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.export_storage_perm),
                    WRITE_STORAGE_PERMISSION_REQUEST,
                    perm
                )
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putParcelable(LAST_SESSION_KEY, lastLocalSession)
    }

    private fun readState(inState: Bundle?) {
        lastLocalSession = inState?.getParcelable(LAST_SESSION_KEY)
    }

    private val onCloudExport = { s: CloudSession ->
        ExportService.intent(this, s).let(::startService)
        supportFragmentManager.popBackStack()
    }

    private val onLocalExport = { s: LocalSession ->
        LocalExportService.intent(this, s).let(::startService)
        supportFragmentManager.popBackStack()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun addFragment(tag: String, frag: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(inn, out, inn, out)
            .add(R.id.homeFrame, frag, tag)
            .addToBackStack(tag)
            .commit()
    }

    companion object {
        fun navIntent(context: Context, page: HomePagerAdapter.Page) =
            navIntent(context).apply {
                putExtra(PAGE_KEY, page.toString())
            }

        private fun navIntent(context: Context) = Intent(context, MainActivity::class.java)

        private const val PAGE_KEY = "PAGE"

        private const val WRITE_STORAGE_PERMISSION_REQUEST = 105
        private const val LAST_SESSION_KEY = "LOCAL_SESSION"
    }
}
