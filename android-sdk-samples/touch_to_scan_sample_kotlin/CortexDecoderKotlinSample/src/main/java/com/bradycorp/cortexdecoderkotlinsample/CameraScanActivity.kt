package com.bradycorp.cortexdecoderkotlinsample

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.codecorp.CDCamera
import com.codecorp.CDDecoder
import com.codecorp.CDResult
import android.os.CountDownTimer
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * CameraScanActivity demonstrates the touch-to-scan feature for barcode decoding.
 * Users can tap anywhere on the screen to initiate scanning.
 * The ROI expands progressively until a barcode is decoded.
 * After successful decoding, the camera preview remains active for subsequent scans.
 */
class CameraScanActivity : AppCompatActivity() {

    // Request code for camera permission
    private val cameraPermissionRequestCode = 1001

    // Timer for expanding the ROI during scanning
    private var mScaleROICountDownTimer: CountDownTimer? = null

    /**
     * Handles the result of camera permission request.
     * If granted, proceeds; otherwise, shows a rationale dialog.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted - no action needed here
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        // Show rationale for camera permission
                        AlertDialog.Builder(this)
                            .setTitle("Camera Permission Required")
                            .setMessage("Please allow camera permissions.")
                            .setPositiveButton("OK") { _, _ ->
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
                            }
                            .setNegativeButton("Cancel") { _, _ ->
                                // Permission request cancelled by the user
                            }
                            .create()
                            .show()
                    } else {
                        // Request permission again
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
                    }
                }
                return
            }
        }
    }

    /**
     * Initializes the activity, checks camera permission, and sets up touch listeners.
     */
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
        // Check and request camera permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
        }

        // Set up touch listeners for initiating scans
        setOnClicks()
    }

    /**
     * Resumes the activity: configures decode settings, starts camera preview.
     * Decoding is disabled until user taps to scan.
     */
    override fun onResume() {
        super.onResume()
        configDecodeSettings()
        // Disable decoding initially - wait for user tap
        CDDecoder.shared.decoding = false
        // Start camera preview
        startCameraPreview()
    }

    /**
     * Pauses the activity: stops decoding, destroys camera preview, cancels any ongoing ROI expansion.
     */
    override fun onPause() {
        super.onPause()
        // Disable decoding
        CDDecoder.shared.decoding = false
        // Stop camera preview
        stopCameraPreview()
        // Cancel any ongoing ROI expansion
        mScaleROICountDownTimer?.cancel()
    }

    /**
     * Starting Camera and preview and updating UI
     * [CDCamera.startCamera]
     * [CDCamera.startPreview]
     */
    private fun startCameraPreview() {
        // Start camera with decode callback
        CDCamera.shared.startCamera(this::onDecode)
        // Add camera preview to the frame layout
        val mCameraFrame = findViewById<View>(R.id.activity_camera_scan_frame) as RelativeLayout
        mCameraFrame.addView(CDCamera.shared.startPreview(), 0)
    }

    /**
     * Stopping camera and preview and updating UI
     * [CDCamera.stopPreview]
     * [CDCamera.stopCamera]
     */
    private fun stopCameraPreview() {
        // Stop preview
        CDCamera.shared.stopPreview()
        // Remove preview from UI
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
    private fun configDecodeSettings() {
        // Enable barcode highlighting
        CDCamera.shared.setHighLightBarcodes(true)
        // Set to decode exactly 1 barcode
        CDDecoder.shared.setBarcodesToDecode(1, true)
        // Decoding is enabled only on user tap
    }

    /**
     * onDecode callback listener implementation.
     * Listens to callback result from [CDCamera.startCamera]
     */
    private fun onDecode(cdResults: Array<CDResult>) {
        if (cdResults.size == 1) {
            // Display the decode result on UI thread
            runOnUiThread {
                showBarcodeResult(cdResults[0])
            }
        }

        // Check if decoding was successful
        if (cdResults[0].status == CDResult.CDDecodeStatus.success || cdResults[0].status == CDResult.CDDecodeStatus.decodedQRConfigCode) {
            runOnUiThread {
                // Stop decoding but keep camera preview running
                CDDecoder.shared.decoding = false
                // Cancel ROI expansion timer
                mScaleROICountDownTimer?.cancel()
            }
        }
    }

    /**
     * Displays the decoded barcode result in the UI.
     */
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

    /**
     * Sets up touch listener for initiating scans with expanding ROI.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClicks() {
        findViewById<View>(R.id.activity_camera_scan_frame).setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val x = event.x.toInt()
                val y = event.y.toInt()
                // Start video capturing and decoding
                CDCamera.shared.videoCapturing = true
                CDDecoder.shared.decoding = true

                // Begin expanding ROI from tap point
                startROIExpansion(x, y)
            }
            true
        }
    }

    /**
     * Starts the ROI expansion timer from the specified tap coordinates.
     * ROI expands from 100x100 pixels outward every 20ms for 300ms total.
     *
     * For better performance with known barcode sizes, you can set a fixed ROI
     * (e.g., 300x300 pixels) instead of expanding. Simply replace the CountDownTimer
     * with a direct setRegionOfInterest call:
     * val regionRect = Rect(tapX - 150, tapY - 150, tapX + 150, tapY + 150)
     * CDDecoder.shared.setRegionOfInterest(regionRect, false, true)
     */
    private fun startROIExpansion(tapX: Int, tapY: Int) {
        mScaleROICountDownTimer = object : CountDownTimer(300, 20) {
            var count = 0
            override fun onTick(millisUntilFinished: Long) {
                // Calculate expanding ROI dimensions
                val left = tapX - 20 * count
                val top = tapY - 20 * count
                val right = left + 100 + 20 * count
                val bottom = top + 100 + 20 * count

                // Clamp ROI to screen boundaries
                val clampedLeft = left.coerceAtLeast(0)
                val clampedTop = top.coerceAtLeast(0)
                val clampedRight = right.coerceAtMost(findViewById<View>(R.id.activity_camera_scan_frame).width)
                val clampedBottom = bottom.coerceAtMost(findViewById<View>(R.id.activity_camera_scan_frame).height)

                // Set ROI for decoding
                val regionRect = Rect(clampedLeft, clampedTop, clampedRight, clampedBottom)
                CDDecoder.shared.setRegionOfInterest(regionRect, false, true)


                count++
            }

            override fun onFinish() {
                // Expansion complete - continue scanning if not yet decoded
            }
        }.start()
    }
}