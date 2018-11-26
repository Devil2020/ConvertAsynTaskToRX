package com.example.mohammedmorsemorsefcis.reactiveprogrammingfulldemo

import android.os.AsyncTask
import android.view.View
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class AsynTaskProcess : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String {
        val Data=StringBuilder()
        val uri = URL("http://api.themoviedb.org/3/movie/top_rated?api_key=107ed75bf9e25ec06bfe9fd33d042579")
        val connection = uri.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "GET"
        val stream = connection.inputStream
        val reader = InputStreamReader(stream)
        val scanner = Scanner(reader)
        while (scanner.hasNext()) {
            Data.append(scanner.nextLine())
        }
        return Data.toString()
    }
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        EventBus.getDefault().postSticky(result)
    }
}