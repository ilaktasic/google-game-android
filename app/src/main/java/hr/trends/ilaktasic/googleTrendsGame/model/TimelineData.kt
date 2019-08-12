package hr.trends.ilaktasic.googleTrendsGame.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class TimelineData {
    var formattedAxisTime: String? = null

    var hasData: Array<String>? = null

    var formattedValue: Array<String>? = null

    var formattedTime: String? = null

    var time: String? = null

    var value: Array<Int>? = null

    var isPartial: Boolean? = null

    override fun toString(): String {
        return "ClassPojo [formattedAxisTime = $formattedAxisTime, hasData = $hasData, formattedValue = $formattedValue, formattedTime = $formattedTime, time = $time, value = $value, isPartial = $isPartial]"
    }
}