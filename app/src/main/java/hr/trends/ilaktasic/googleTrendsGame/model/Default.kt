package hr.trends.ilaktasic.googleTrendsGame.model

class Default {
    var timelineData: Array<TimelineData>? = null

    var averages: Array<Int>? = null

    override fun toString(): String {
        return "ClassPojo [timelineData = $timelineData, averages = $averages]"
    }
}