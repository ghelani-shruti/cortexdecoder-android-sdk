package com.bradycorp.cortexdecoderkotlinsample

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.codecorp.CDCamera
import com.codecorp.CDDecoder
import com.codecorp.CDResult

class CameraScanActivity : AppCompatActivity() {

    private val cameraPermissionRequestCode = 1001
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        // User has previously denied the permission request, show a rationale for why the permission is needed
                        // You can use an AlertDialog to display a message to the user explaining why the permission is needed
                        AlertDialog.Builder(this)
                            .setTitle("Camera Permission Required")
                            .setMessage("Please allow camera permissions.")
                            .setPositiveButton("OK") { _, _ ->
                                // Request the permission again
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
                            }
                            .setNegativeButton("Cancel") { _, _ ->
                                // Permission request cancelled by the user
                            }
                            .create()
                            .show()
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
                    }
                }
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scan)

        //Explicitly handle window insets to position the barcode result above the navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_camera_scan_barcode_result)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = systemBars.bottom
            }
            insets
        }

        // camera permission condition
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
        }
        // setting on clicks for decoding
        setOnClicks()
    }
    override fun onResume() {
        super.onResume()
        configDecodeSettings()
        // Set decoding on
        CDDecoder.shared.decoding = true
        // Start camera preview
        startCameraPreview()

        // Set the ROI after layout is ready
        findViewById<View>(R.id.activity_camera_scan_frame).post {
            setupROI()
        }
    }

    override fun onPause() {
        super.onPause()
        // Set decoding off
        CDDecoder.shared.decoding = false
        // Destroy camera preview
        stopCameraPreview()
    }

    /**
     * Starting Camera and preview and updating UI
     * [CDCamera.startCamera]
     * [CDCamera.startPreview]
     */
    private fun startCameraPreview(){
        // start the camera
        CDCamera.shared.startCamera(this::onDecode)
        // Update RelativeLayout with Camera Preview
        val mCameraFrame = findViewById<View>(R.id.activity_camera_scan_frame) as RelativeLayout
        // start the preview and add at index 0
        mCameraFrame.addView(CDCamera.shared.startPreview(), 0)
    }

    /**
     * Stopping camera and preview and updating UI
     * [CDCamera.stopPreview]
     * [CDCamera.stopCamera]
     */
    private fun stopCameraPreview(){
        // stop the preview
        CDCamera.shared.stopPreview()
        // remove from ui
        val mCameraFrame = findViewById<View>(R.id.activity_camera_scan_frame) as RelativeLayout
        mCameraFrame.removeViewAt(0)
        mCameraFrame.invalidate()
        // stop the camera
        CDCamera.shared.stopCamera()
    }

    /**
     * Configuring Decode Settings
     * Enabling Highlights[CDCamera.setHighLightBarcodes]
     * Setting barcodes to decode[CDDecoder.setBarcodesToDecode]
     * Enabling Decoding [CDDecoder.setDecoding]
     */
    private fun configDecodeSettings(){
        // enable highlights
        CDCamera.shared.setHighLightBarcodes(true)
        // set barcodes to decode exactly 1
        CDDecoder.shared.setBarcodesToDecode(1, true)
        // Set decoding on
        CDDecoder.shared.decoding = true
    }

    /**
     * onDecode callback listener implementation.
     * Listens to callback result from [CDCamera.startCamera]
     */
    private fun onDecode(cdResults: Array<CDResult>) {
        // if result size is 1
        if(cdResults.size == 1){
            // print on screen
            runOnUiThread{
                showBarcodeResult(cdResults[0])
            }
        }

        // cdResults is array of decoded items
        if (cdResults[0].status == CDResult.CDDecodeStatus.success || cdResults[0].status == CDResult.CDDecodeStatus.decodedQRConfigCode) {
            runOnUiThread {
                // stop video capturing if decoded
                CDCamera.shared.videoCapturing = false
            }
        }
    }

    private lateinit var overlayView: ScannerOverlayView


    /**
     * Set region of interest with [CDDecoder.setRegionOfInterest] to set ROI on
     * specified rect, ensuring to decode inside the rect and giving rect values
     * based on preview view, not image pixels.
     *
     * Parameter 1 (regionRect): The screen-based coordinates.
     * Parameter 2 (ensureCorners):
     * - true: Forces the decoder to ignore data outside the box.
     * - false: Allows decoding of barcodes partially overlapping the ROI edges.
     * Parameter 3 (screenBased):
     * - true: Input coordinates are treated as UI Screen Pixels. The SDK automatically
     *          scales them to match the camera resolution.
     * - false: Input coordinates are treated as raw Image Pixels. The user must
     *          manually scale coordinates if the camera resolution differs from the screen.
     */
    private fun setupROI() {
        overlayView = findViewById(R.id.roi_overlay)

        // Listen for manual changes from the user
        overlayView.onRoiChanged = { rectF ->
            // Convert the UI Float Rect to an SDK-compatible Integer Rect
            val regionRect = Rect(
                rectF.left.toInt(),
                rectF.top.toInt(),
                rectF.right.toInt(),
                rectF.bottom.toInt()
            )
            // Set ROI
            CDDecoder.shared.setRegionOfInterest(regionRect, true, true)
        }
    }

    // UI update methods
    @SuppressLint("SetTextI18n")
    private fun showBarcodeResult(cdResult: CDResult) {
        findViewById<AppCompatTextView>(R.id.activity_camera_scan_barcode_result).text =
            """
            Status: ${cdResult.status.name}
            Barcode Data: ${cdResult.barcodeData}
            Symbology: ${cdResult.symbology}
            Decode Time: ${cdResult.decodeTime} ms        
            """.trimIndent()
    }

    private fun setOnClicks(){
        // frame on click listener
        findViewById<View>(R.id.activity_camera_scan_frame).setOnClickListener {
            if(!CDCamera.shared.videoCapturing){
                // start camera again
                CDCamera.shared.videoCapturing = true
                // start decoding again
                CDDecoder.shared.decoding = true
            }
        }

    }
}