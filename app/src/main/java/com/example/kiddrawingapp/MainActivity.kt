package com.example.kiddrawingapp


import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
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
    private var openGalleryLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result ->

            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageBackground:ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageURI(result.data?.data)
            }
        }
    private var cameraAndLocationAndStorageResultLauncher : ActivityResultLauncher<Array<String>> =
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
                        } else if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                            Toast.makeText(this,"Permission granted for READ_EXTERNAL_STORAGE.",Toast.LENGTH_LONG).show()
                            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            openGalleryLauncher.launch(pickIntent)
                        }
                    } else {
                        if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION) {
                            Toast.makeText(this,"Permission denied for location.",Toast.LENGTH_LONG).show()
                        } else if (permissionName == Manifest.permission.CAMERA) {
                            Toast.makeText(this,"Permission denied for camera.",Toast.LENGTH_LONG).show()
                        } else if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                            Toast.makeText(this,"Permission denied for READ_EXTERNAL_STORAGE.",Toast.LENGTH_LONG).show()
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
            checkOpenAllPermissions()
        }

        val ib_undo : ImageButton = findViewById(R.id.ib_undo)
        ib_undo.setOnClickListener {
            drawingView?.onClickUndo()
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

    private fun checkOpenAllPermissions() {
//        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M &&
//            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//            showRationaleDialog("Permission requires camera access","Camera cannot used because Camera access is denied")
//        } else {
//            cameraResultLauncher.launch(Manifest.permission.CAMERA)
//        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationaleDialog("Kids Drawing App","Kids Drawing App need access your External Storage, Camera and Location")
        } else {
            cameraAndLocationAndStorageResultLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
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