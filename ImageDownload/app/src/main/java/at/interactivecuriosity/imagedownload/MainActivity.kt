package at.interactivecuriosity.imagedownload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg" // URL des herunterzuladenden Bildes
    private val fileName = "downloadedImage.jpg"

    private val receiver : BroadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val success = intent?.getBooleanExtra(IntentConstant.success,false)

            val filePath = intent?.getStringExtra(IntentConstant.imgPath)

            if(success == true && filePath != null){
                //Success:
                val file = File(filePath)
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)
                Toast.makeText(this@MainActivity, "Bild heruntergeladen", Toast.LENGTH_SHORT).show()
            }else{
                //Error:
                Toast.makeText(this@MainActivity, "Fehler beim Herunterladen", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)

        downloadButton.setOnClickListener {
            downloadImage(imageUrl, fileName)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }
    }

    private fun downloadImage(urlString: String, fileName: String) {
        val intent = Intent(this@MainActivity,DownloadIntentService::class.java)
        intent.putExtra(IntentConstant.url,urlString)
        intent.putExtra(IntentConstant.fileName,fileName)
        Toast.makeText(this,"Download wurde im Hintergrund gestartet",Toast.LENGTH_SHORT).show();
        startService(intent)

        val intentFilter = IntentFilter(IntentConstant.broadCastIntentName)
        registerReceiver(receiver,intentFilter)
    }

    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            runOnUiThread {
                imageView.setImageBitmap(null)
                Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
            }
        }

        unregisterReceiver(receiver)
    }
}
