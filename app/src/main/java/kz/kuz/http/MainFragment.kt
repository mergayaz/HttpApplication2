package kz.kuz.http

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainFragment() : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.toolbar_title)
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val executorService = Executors.newSingleThreadExecutor()
        executorService.execute(Runnable
        {
            var url: URL? = null
            try {
                url = URL("https://kuz.kz/astana.txt")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            var connection: HttpURLConnection? = null
            try {
                connection = url?.openConnection() as HttpURLConnection
                // объект HttpURLConnection представляет подключение, но связь с конечной точкой
                // будет установлена только после вызова getInputStream()
                // или getOutputStream() для POST-вызовов
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val out = ByteArrayOutputStream()
            // ByteArrayOutputStream - класс, работающий с поступившими байтами
            var `in`: InputStream? = null
            // InputStream - класс, работающий с байтами по мере их доступности
            try {
                `in` = connection?.inputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                if (connection?.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException(connection?.responseMessage +
                            ": with https://kuz.kz/astana.txt")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var bytesRead = 0
            val buffer = ByteArray(1024)
            while (true) {
                try {
                    if ((`in`!!.read(buffer).also { bytesRead = it }) <= 0) break
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                out.write(buffer, 0, bytesRead)
            }
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            connection?.disconnect()
            val urlBytes = out.toByteArray()
            val urlString = String(urlBytes)
            Log.e("data from site", urlString)
        })
        executorService.shutdown()
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return view
    }
}