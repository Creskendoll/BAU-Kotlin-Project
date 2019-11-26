package com.kenansoylu.bauproject.misc

import android.text.format.Time

class Helpers {
    companion object {
        // https://stackoverflow.com/questions/17430477/is-it-possible-to-add-a-timer-to-the-actionbar-on-android
        fun secondsToString(improperSeconds: Int): String? { //Seconds must be fewer than are in a day
            val secConverter = Time()
            secConverter.hour = 0
            secConverter.minute = 0
            secConverter.second = 0
            secConverter.second = improperSeconds
            secConverter.normalize(true)
//            var hours: String = java.lang.String.valueOf(secConverter.hour)
            val minutes: String = java.lang.String.valueOf(secConverter.minute)
            var seconds: String = java.lang.String.valueOf(secConverter.second)
            if (seconds.length < 2) {
                seconds = "0$seconds"
            }
//            if (minutes.length < 2) {
//                minutes = "0$minutes"
//            }
//            if (hours.length < 2) {
//                hours = "0$hours"
//            }
            return "$minutes:$seconds"
        }
    }
}