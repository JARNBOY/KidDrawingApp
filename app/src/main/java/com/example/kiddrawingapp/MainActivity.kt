package com.example.kiddrawingapp


import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get



class MainActivity : AppCompatActivity() {

    private var drawingView : DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null
    private var cameraResultLauncher : ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
                isGranted ->
                if (isGranted) {
                    Toast.makeText(this,"Permission granted for camera.",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Permission denied for camera.",Toast.LENGTH_LONG).show()
                }
        }
    private var cameraAndLocationResultLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {
                permissions ->
                permissions.entries.forEach {
                    val permissionName = it.key
                    val isGranted = it.value

                    if (isGranted) {
                        if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION) {
                            Toast.makeText(this,"Permission granted for location.",Toast.LENGTH_LONG).show()
                        } else if (permissionName == Manifest.permission.CAMERA) {
                            Toast.makeText(this,"Permission granted for camera.",Toast.LENGTH_LONG).show()
                        }
                    } else {
                        if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION) {
                            Toast.makeText(this,"Permission denied for location.",Toast.LENGTH_LONG).show()
                        } else if (permissionName == Manifest.permission.CAMERA) {
                            Toast.makeText(this,"Permission denied for camera.",Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColor = findViewById<LinearLayout>(R.id.ll_paint_color)
        mImageButtonCurrentPaint = linearLayoutPaintColor[1] as ImageButton
        mImageButtonCurrentPaint?.let {
            it.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
        }

        val ib_brush : ImageButton = findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        val ib_gallery : ImageButton = findViewById(R.id.ib_gallery)
        ib_gallery.setOnClickListener {
            checkOpenPermissionCamera()
        }
    }

    private fun showRationaleDialog(
        title: String,
        message: String
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") {dialog,_ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun checkOpenPermissionCamera() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M &&
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showRationaleDialog("Permission requires camera access","Camera cannot used because Camera access is denied")
        } else {
//            cameraResultLauncher.launch(Manifest.permission.CAMERA)
            cameraAndLocationResultLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }

    }

    private fun showBrushSizeChooserDialog() {
        var brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view: View) {
//        Toast.makeText(this,"paint clicked", Toast.LENGTH_SHORT).show()
        if (view != mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            //set draw current pressed color
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )

            //before set new current view in property mImageButtonCurrentPaint will clear set draw normal
            mImageButtonCurrentPaint?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pallet_normal)
                )
            }

            //then set new current view in property mImageButtonCurrentPaint
            mImageButtonCurrentPaint = view
        }
    }

}