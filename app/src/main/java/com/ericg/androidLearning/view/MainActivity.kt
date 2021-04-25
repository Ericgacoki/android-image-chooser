package com.ericg.androidLearning.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ericg.androidLearning.R
import com.ericg.androidLearning.databinding.ActivityMainBinding
import kotlin.math.sqrt
import androidx.databinding.DataBindingUtil as DB

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)

class MainActivity : AppCompatActivity(), View.OnTouchListener {
    private var matrix = Matrix()
    private var savedMatrix = Matrix()
    private var mode = NONE

    private var start = PointF()
    private var mid = PointF()
    private var oldDist = 1f

    private var uri: Uri? = null
    private var mainActivityBinding: ActivityMainBinding? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = DB.setContentView(this, R.layout.activity_main)
        mainActivityBinding?.btnSelectImage?.setOnClickListener {
            selectImage()
        }

       mainActivityBinding!!.imageView.setOnTouchListener(this)
    }

    private fun selectImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            startActivityForResult(this, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            uri = data.data
            val bitMap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            mainActivityBinding!!.imageView.apply {
                setImageBitmap(bitMap)
                clipToOutline = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fmWhatsApp -> {
                toast("Clicked Fm WhatsApp")
                // Navigate
            }
            R.id.restartApp -> {
                toast("Clicked Restart")
                // Navigate
            }
            R.id.messageANumber -> {
                toast("Clicked Restart")
                // Navigate
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toast(message: String? = "") {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val view = v as ImageView
        view.scaleType = ImageView.ScaleType.MATRIX
        val scale: Float
        dumpEvent(event)
        // Handle touch events here...
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN // first finger down only
            -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                Log.d(
                    TAG,
                    "mode=DRAG"
                ) // write to LogCat
                mode = DRAG
            }
            MotionEvent.ACTION_UP // first finger lifted
                ,
            MotionEvent.ACTION_POINTER_UP // second finger lifted
            -> {
                mode = NONE
                Log.d(TAG, "mode=NONE")
            }
            MotionEvent.ACTION_POINTER_DOWN // first and second finger down
            -> {
                oldDist = spacing(event)
                Log.d(TAG, "oldDist=$oldDist")
                if (oldDist > 5f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                    Log.d(TAG, "mode=ZOOM")
                }
            }
            MotionEvent.ACTION_MOVE ->
                if (mode == DRAG) {
                    matrix.set(savedMatrix)
                    matrix.postTranslate(
                        event.x - start.x,
                        event.y - start.y
                    ) // create the transformation in the matrix of points
                } else if (mode == ZOOM) {
                    // pinch zooming
                    val newDist = spacing(event)
                    Log.d(
                        TAG,
                        "newDist=$newDist"
                    )
                    if (newDist > 5f) {
                        matrix.set(savedMatrix)
                        scale = newDist / oldDist // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y)
                    }
                }
        }
        view.imageMatrix = matrix // display the transformation on screen
        return true // indicate event was handled
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    private fun dumpEvent(event: MotionEvent) {
        val names = arrayOf(
            "DOWN",
            "UP",
            "MOVE",
            "CANCEL",
            "OUTSIDE",
            "POINTER_DOWN",
            "POINTER_UP",
            "7?",
            "8?",
            "9?"
        )
        val sb = StringBuilder()
        val action = event.action
        val actionCode = action and MotionEvent.ACTION_MASK
        sb.append("event ACTION_").append(names[actionCode])
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
            sb.append(")")
        }
        sb.append("[")
        for (i in 0 until event.pointerCount) {
            sb.append("#").append(i)
            sb.append("(pid ").append(event.getPointerId(i))
            sb.append(")=").append(event.getX(i).toInt())
            sb.append(",").append(event.getY(i).toInt())
            if (i + 1 < event.pointerCount)
                sb.append(";")
        }
        sb.append("]")
        Log.d("Touch Events ---------", sb.toString())
    }

    companion object {
        const val TAG = "Touch"
        // internal val MIN_ZOOM = 1f
        // internal val MAX_ZOOM = 1f

        // The 3 states (events) which the user is trying to perform
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }
}