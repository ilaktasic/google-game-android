package hr.trends.ilaktasic.googleTrendsGame.model

class Default {
    var timelineData: List<TimelineData>? = null

    var averages: List<Int>? = null

    override fun toString(): String {
        return "ClassPojo [timelineData = $timelineData, averages = $averages]"
    }
}