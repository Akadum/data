mport android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.* // Import CoroutineScope, launch, Job, delay, isActive

class MainActivity : AppCompatActivity() {

    private lateinit var myTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    private var updateJob: Job? = null // ใช้ Job เพื่อควบคุม Coroutine (start/stop)
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ตรวจสอบว่าคุณมีไฟล์ layout ชื่อ activity_main.xml

        myTextView = findViewById(R.id.myTextView) // ID ของ TextView ใน layout
        startButton = findViewById(R.id.startButton) // ID ของ Button "Start"
        stopButton = findViewById(R.id.stopButton)   // ID ของ Button "Stop"

        // ตั้งค่าเริ่มต้น
        myTextView.text = "พร้อมเริ่มนับ..."

        startButton.setOnClickListener {
            startUpdatingTextView()
        }

        stopButton.setOnClickListener {
            stopUpdatingTextView()
        }
    }

    private fun startUpdatingTextView() {
        // ตรวจสอบว่า Coroutine ไม่ได้กำลังทำงานอยู่
        if (updateJob?.isActive != true) {
            counter = 0 // รีเซ็ตตัวนับทุกครั้งที่เริ่ม
            // Launch a new coroutine in the Main dispatcher (UI thread)
            updateJob = CoroutineScope(Dispatchers.Main).launch {
                // The 'while(true)' equivalent in a cancellable way
                while (isActive) { // isActive เป็น extension property ของ CoroutineScope
                                  // จะเป็น false เมื่อ Coroutine ถูกยกเลิก
                    myTextView.text = "นับได้: ${counter++}"
                    delay(1000) // หน่วงเวลา 1000 มิลลิวินาที (1 วินาที)
                }
            }
        }
    }

    private fun stopUpdatingTextView() {
        updateJob?.cancel() // ยกเลิก Coroutine
        myTextView.text = "หยุดแล้ว"
    }

    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel() // ตรวจสอบให้แน่ใจว่าได้ยกเลิก Coroutine เมื่อ Activity ถูกทำลาย
    }
}

------------------------------------- count script is OK ---------------------------------------------
import kotlinx.coroutines.*
import android.widget.TextView

class MyCoroutineUpdater(private val myTextView: TextView) {

    private var job: Job? = null
    private var counter = 0

    fun startUpdating() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) { // ตรวจสอบว่า Coroutine ยังทำงานอยู่หรือไม่
                counter++
                myTextView.text = "นับได้: $counter"
                delay(1000) // รอ 1 วินาที
            }
        }
    }

    fun stopUpdating() {
        job?.cancel() // ยกเลิก Coroutine
    }
}

// วิธีเรียกใช้ใน Activity/Fragment (Kotlin):
// val updater = MyCoroutineUpdater(findViewById(R.id.myTextView))
// updater.startUpdating()
// เมื่อต้องการหยุด: updater.stopUpdating()


----------- file main_activity.tk ------------
mybutton1 = findViewById(R.id.button6)
        mybutton1.setOnClickListener(){
            var test_w = telnet()
            test_w.run()
        }


----------- file telnet.tk -------------
class telnet {
    fun run() {
        println("hello")
    }

}

------------------------------------telnet android-------------------------------------------
package com.example.test_app2
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.Socket
import java.net.SocketPermission
import java.nio.charset.Charset
//private socket = var socket: Socket? = null
class telnet {
    private val hostname = "192.168.67.194" // เปลี่ยนเป็น IP หรือ hostname ของ Telnet Server ของคุณ
    private val port = 23             // พอร์ต Telnet มาตรฐาน

    fun RED_ON():String{
        var stat = "LED ON"
        GlobalScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            var reader: BufferedReader? = null
            var writer: BufferedWriter? = null

            socket = Socket(hostname, port)
            println("Connecting to $hostname:$port...")
            writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")))
            writer.write("r" + "\r\n")
            writer.flush()
            writer?.close()
        }
        return stat

    }

    fun RED_OFF():String {
        var stat = "LED OFF"
        GlobalScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            var reader: BufferedReader? = null
            var writer: BufferedWriter? = null
            socket = Socket(hostname, port)
            println("Connecting to $hostname:$port...")
            writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")))
            writer.write("f" + "\r\n")
            writer.flush()
            writer?.close()
        }
        return stat
    }

}

------------------------------------telnet funtion 2-------------------------------------------
 // funtion ส่งค่า para telnet
fun LED_OFF(callback: (result: String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            var reader: BufferedReader? = null
            var writer: BufferedWriter? = null
            var responseFromServer = "LED OFF" // Default error message

            try {
                println("Connecting to $hostname:$port...")
                socket = Socket(hostname, port)
                writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")))
                reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")))

                writer.write("f"+ "\r\n") // No need to concatenate with ""
                writer.flush()e

            } catch (e: IOException) {
                e.printStackTrace()
                //responseFromServer = "Error: ${e.message}"
                responseFromServer = "ERROR connect"
            } finally {
                try {
                    writer?.close()
                    reader?.close()
                    socket?.close()
                } catch (e: IOException) {
                    //e.printStackTrace()
                    responseFromServer = "ERROR connect"
                }
            }
            // Invoke the callback on the Main thread if you need to update UI
            // For simplicity, this example invokes it directly from the IO thread.
            // If updating UI, switch context: launch(Dispatchers.Main) { callback(responseFromServer) }
            callback(responseFromServer)
        }
    }

    // การใช่ funtion USE CALLBACK
    mybutton1 = findViewById(R.id.button7)
        mybutton1.setOnClickListener(){
            var test_w = telnet()
            var ck = test_w.LED_OFF {
                result ->
                show1 = findViewById(R.id.textView2)
                show1.text = "$result"
            }
        }
    

--------------------------------PUSH BUTTON--------------------------------------------
package com.your.package.name // เปลี่ยนเป็น package name ของคุณ

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var myButton: Button
    private lateinit var statusTextView: TextView

    @SuppressLint("ClickableViewAccessibility") // ยับยั้งคำเตือนเรื่อง Accessibility สำหรับ onTouch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myButton = findViewById(R.id.myButton)
        statusTextView = findViewById(R.id.statusTextView)

        myButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // เมื่อกดปุ่มลง
                    statusTextView.text = "สถานะ: ติด"
                    true // คืนค่า true เพื่อบอกว่าเราจัดการ event นี้แล้ว
                }
                MotionEvent.ACTION_UP -> {
                    // เมื่อปล่อยปุ่ม
                    statusTextView.text = "สถานะ: ดับ"
                    true // คืนค่า true เพื่อบอกว่าเราจัดการ event นี้แล้ว
                }
                else -> false // สำหรับ event อื่นๆ ให้ส่งต่อ
            }
        }
    }
}

--------------------------------Read write file txt--------------------------------------------
import java.io.File
import android.content.Context // ใน Android คุณต้องใช้ Context เพื่อเข้าถึง storage

fun writeToFile(context: Context, fileName: String, content: String) {
    try {
        // สำหรับ Internal Storage (เข้าถึงได้เฉพาะแอปของคุณ)
        val file = File(context.filesDir, fileName)
        file.writeText(content) // เขียนทับเนื้อหาเดิม
        
        // หากต้องการเพิ่มเนื้อหาต่อท้าย
        // file.appendText(content)

        println("เขียนไฟล์ $fileName สำเร็จแล้ว")
    } catch (e: Exception) {
        e.printStackTrace()
        println("เกิดข้อผิดพลาดในการเขียนไฟล์: ${e.message}")
    }
}

import java.io.File
import android.content.Context

fun readFromFile(context: Context, fileName: String): String {
    try {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val content = file.readText()
            println("อ่านไฟล์ $fileName สำเร็จแล้ว: $content")
            return content
        } else {
            println("ไฟล์ $fileName ไม่พบ")
            return ""
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("เกิดข้อผิดพลาดในการอ่านไฟล์: ${e.message}")
        return ""
    }
}

//-------------------------------- แจ้งเตือน -----------------------------------
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val showAlertDialogButton: Button = findViewById(R.id.showAlertDialogButton)
        showAlertDialogButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            // กำหนดหัวข้อ
            builder.setTitle("แจ้งเตือน")

            // กำหนดข้อความ
            builder.setMessage("คุณต้องการดำเนินการต่อหรือไม่?")

            // กำหนดปุ่ม Positive (ปุ่มยืนยัน)
            builder.setPositiveButton("ใช่") { dialog, which ->
                // โค้ดที่ทำงานเมื่อผู้ใช้กดปุ่ม "ใช่"
                Toast.makeText(applicationContext, "คุณกด ใช่", Toast.LENGTH_SHORT).show()
            }

            // กำหนดปุ่ม Negative (ปุ่มยกเลิก)
            builder.setNegativeButton("ไม่") { dialog, which ->
                // โค้ดที่ทำงานเมื่อผู้ใช้กดปุ่ม "ไม่"
                Toast.makeText(applicationContext, "คุณกด ไม่", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // ปิด Dialog
            }

            // กำหนดให้ Dialog ไม่สามารถปิดได้เมื่อคลิกนอกพื้นที่ Dialog (เป็นทางเลือก)
            builder.setCancelable(false)

            // สร้างและแสดง Dialog
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }
}

//-------------------------------- for -----------------------------------
for(i in 1..4) { // อ่านจนกว่าจะไม่มีข้อมูลค้างอยู่ในบัฟเฟอร์
    print(i)
    if(i == 2){
        break
    }
}

//-------------------------------- button background colour -----------------------------------
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
private lateinit var myButton: Button
private lateinit var textbutton: Button
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myButton: Button = findViewById(R.id.myButton)

        // Set background color to red using an RGB integer
        myButton.setBackgroundColor(Color.RED)

        textbutton = findViewById(R.id.button1)
        textbutton.setTextColor(Color.GREEN)

        // Or using an ARGB integer (alpha, red, green, blue)
        // myButton.setBackgroundColor(Color.parseColor("#FF0000")) // For red
        // myButton.setBackgroundColor(0xFFFF0000) // For red (hex ARGB)
    }
}