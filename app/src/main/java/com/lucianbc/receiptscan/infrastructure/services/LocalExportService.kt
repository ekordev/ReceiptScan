package com.lucianbc.receiptscan.infrastructure.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.domain.export.ExportUseCase
import com.lucianbc.receiptscan.domain.export.LocalSession
import com.lucianbc.receiptscan.presentation.home.HomePagerAdapter
import com.lucianbc.receiptscan.presentation.home.MainActivity
import com.lucianbc.receiptscan.presentation.home.exports.CHANNEL_ID
import dagger.android.DaggerService
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LocalExportService : DaggerService() {

    private lateinit var localSession: LocalSession

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var useCase: ExportUseCase

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        localSession = intent?.extras?.getParcelable(SESSION_KEY)!!

        startForeground(1, notification())

        useCase.newExport(localSession)
            .subscribeOn(Schedulers.io())
            .subscribe {
                stopSelf()
                showFinishNotification()
            }
            .addTo(disposables)

        return START_NOT_STICKY
    }

    private fun notification(): Notification {
        val tapIntent = MainActivity.navIntent(this, HomePagerAdapter.EXPORT)
        val pendingIntent = PendingIntent.getActivity(this, 0, tapIntent, 0)

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("Exporting")
            .setContentText("Building your spreadsheet")
            .setProgress(0, 0, true)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun showFinishNotification() {
        val tapIntent = MainActivity.navIntent(this, HomePagerAdapter.EXPORT)
        val pendingIntent = PendingIntent.getActivity(this, 0, tapIntent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Finished")
            .setContentText("Your spreadsheet has been written. Tap to go to exports.")
            .setProgress(0, 0, true)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentIntent(pendingIntent)
            .build()
        with(NotificationManagerCompat.from(this)) {
            notify(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    companion object {
        fun intent(context: Context, localSession: LocalSession): Intent {
            return Intent(context, LocalExportService::class.java).apply {
                putExtra(SESSION_KEY, localSession)
            }
        }

        private const val SESSION_KEY = "SESSION_KEY"
    }
}