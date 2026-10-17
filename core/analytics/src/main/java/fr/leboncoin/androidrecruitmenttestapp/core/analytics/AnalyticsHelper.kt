package fr.leboncoin.androidrecruitmenttestapp.core.analytics

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    private val prefs by lazy {
        context.getSharedPreferences(ANALYTICS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun trackSelection(itemId: String) {
        prefs.edit { putString(SELECTED_ITEM_KEY, itemId) }
        // Simulate some analytics logging
        println("Analytics: User selected item - $itemId")
    }

    fun trackScreenView(screenName: String) {
        // Simulate some analytics logging
        println("Analytics: Screen viewed - $screenName")
    }
}

private const val ANALYTICS_SHARED_PREFS = "analytics_prefs"
private const val SELECTED_ITEM_KEY = "selected_item"