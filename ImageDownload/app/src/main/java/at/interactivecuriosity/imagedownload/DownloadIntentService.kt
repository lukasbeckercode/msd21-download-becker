package at.interactivecuriosity.imagedownload

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.net.URL

class DownloadIntentService : IntentService {
    constructor(): super("DownloadIntentService")
    override fun onHandleIntent(intent: Intent?) {
        val urlString = intent?.getStringExtra(IntentConstant.url)
        val fileName = intent?.getStringExtra(IntentConstant.fileName)

        var success = false;
        var file:File? = null ;
        try {
            if(urlString == null || fileName == null){
                throw IllegalArgumentException("Missing Parameters!")
            }
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            file = File(getExternalFilesDir(null), fileName)
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            success = true

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val broadcastIntent = Intent(IntentConstant.broadCastIntentName)
        broadcastIntent.putExtra(IntentConstant.success, success)
        if(success){
            broadcastIntent.putExtra(IntentConstant.imgPath, file?.absolutePath)
        }


        sendBroadcast(broadcastIntent)
    }
}