package hr.trends.ilaktasic.googleTrendsGame.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Player(
        val name: String?,
        var points: Int = 0,
        var roundPoints: Map<Long,Int> = mutableMapOf(),
        var phraseToGoogle: String? = null) : Parcelable {


        fun latestResult(): Int {
            return roundPoints.entries.maxBy { it.key }?.value ?: 0
        }
}