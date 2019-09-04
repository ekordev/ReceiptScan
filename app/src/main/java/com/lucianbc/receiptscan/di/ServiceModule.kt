package com.lucianbc.receiptscan.di

import com.lucianbc.receiptscan.infrastructure.services.NotificationService
import com.lucianbc.receiptscan.infrastructure.services.ExportService
import com.lucianbc.receiptscan.infrastructure.services.LocalExportService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract fun contributeExportService(): ExportService

    @ContributesAndroidInjector
    abstract fun contributeNotificationService(): NotificationService

    @ContributesAndroidInjector
    abstract fun contributeLocalExportService(): LocalExportService
}