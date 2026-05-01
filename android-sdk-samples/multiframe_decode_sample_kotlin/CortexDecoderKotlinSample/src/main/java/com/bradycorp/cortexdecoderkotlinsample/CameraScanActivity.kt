package com.bradycorp.cortexdecoderkotlinsample

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
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

/**
 * CameraScanActivity demonstrates camera-based barcode scanning functionality.
 * This activity handles camera permission requests, initializes the camera preview,
 * and configures decoding settings for both single and multi-frame scanning.
 * In multi-frame mode, it scans a predefined number of barcodes consecutively and displays
 * all results in a dialog upon completion. In single-frame mode, it scans one barcode
 * at a time. The UI displays the decoded barcode results.
 */

class CameraScanActivity : AppCompatActivity() {

    // Request code for camera permission
    private val cameraPermissionRequestCode = 1001

    // Multi-frame decoding configuration
    private val isMultiFrameEnabled = true
    // Set Barcodes to decode to a number greater than 1 for multiframe decoding
    private val multiFrameBarcodeTarget = 4
    private var multiFrameCurrentCount = 0
    private val decodedBarcodeResults = linkedMapOf<String, String>()
    private var hasShownResultDialog = false

    // Handles the result of camera permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted - no action needed here
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    override fun onResume() {
        super.onResume()
        resetScanSessionState()
        // Configure decode settings
        configDecodeSettings()
        // Set decoding on
        CDDecoder.shared.decoding = true
        // Start camera preview
        startCameraPreview()
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
        // Start the camera
        CDCamera.shared.startCamera(this::onDecode)
        // Update RelativeLayout with Camera Preview
        val mCameraFrame = findViewById<View>(R.id.activity_camera_scan_frame) as RelativeLayout
        // Start the preview and add at index 0
        mCameraFrame.addView(CDCamera.shared.startPreview(), 0)
    }

    /**
     * Stopping camera and preview and updating UI
     * [CDCamera.stopPreview]
     * [CDCamera.stopCamera]
     */
    private fun stopCameraPreview(){
        // Stop the preview
        CDCamera.shared.stopPreview()
        // Remove from ui
        val mCameraFrame = findViewById<View>(R.id.activity_camera_scan_frame) as RelativeLayout
        mCameraFrame.removeViewAt(0)
        mCameraFrame.invalidate()
        // Stop the camera
        CDCamera.shared.stopCamera()
    }

    /**
     * Configuring Decode Settings
     * Setting barcodes to decode[CDDecoder.setBarcodesToDecode]
     * Enabling Decoding [CDDecoder.setDecoding]
     */
    private fun configDecodeSettings(){
        if (isMultiFrameEnabled) {
            updateMultiFrameProgressText(0)
            CDDecoder.shared.setMultiFrameDecoding(true) { count ->
                multiFrameCurrentCount = count
                runOnUiThread {
                    updateMultiFrameProgressText(count)
                }
            }
            // Multi-frame mode requires exactlyNBarcodes = false.
            CDDecoder.shared.setBarcodesToDecode(multiFrameBarcodeTarget, false)
        } else {
            // Single-frame behavior
            CDDecoder.shared.setBarcodesToDecode(1, true)
            CDDecoder.shared.setMultiFrameDecoding(false) { }
        }

        // Set decoding on
        CDDecoder.shared.decoding = true
    }

    /**
     * onDecode callback listener implementation.
     * Listens to callback result from [CDCamera.startCamera]
     */
    private fun onDecode(cdResults: Array<CDResult>) {
        if (cdResults.isEmpty()) return

        cdResults.forEach { result ->
            if (result.status == CDResult.CDDecodeStatus.success || result.status == CDResult.CDDecodeStatus.decodedQRConfigCode) {
                if (result.barcodeData.isNotBlank()) {
                    decodedBarcodeResults[result.barcodeData] = result.symbology.toString()
                }
            }
        }

        if (!isMultiFrameEnabled) {
            if (cdResults.size == 1) {
                runOnUiThread {
                    showBarcodeResult(cdResults[0])
                }
            } else {
                val merged = cdResults.joinToString("\n\n") { result ->
                    """
                    Status: ${result.status.name}
                    Barcode Data: ${result.barcodeData}
                    Symbology: ${result.symbology}
                    Decode Time: ${result.decodeTime} ms
                    """.trimIndent()
                }
                runOnUiThread {
                    findViewById<AppCompatTextView>(R.id.activity_camera_scan_barcode_result).text = merged
                }
            }
        }

        val hasSuccess = cdResults.any {
            it.status == CDResult.CDDecodeStatus.success || it.status == CDResult.CDDecodeStatus.decodedQRConfigCode
        }

        if (hasSuccess) {
            runOnUiThread {
                val isComplete = multiFrameCurrentCount >= multiFrameBarcodeTarget || decodedBarcodeResults.size >= multiFrameBarcodeTarget
                if (!isMultiFrameEnabled || isComplete) {
                    CDCamera.shared.videoCapturing = false
                    if (isMultiFrameEnabled && !hasShownResultDialog) {
                        hasShownResultDialog = true
                        showResultDialog()
                    }
                }
            }
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
        // Frame on click listener
        findViewById<View>(R.id.activity_camera_scan_frame).setOnClickListener {
            if(!CDCamera.shared.videoCapturing){
                resetScanSessionState()
                configDecodeSettings()
                // Start camera again
                CDCamera.shared.videoCapturing = true
                // Start decoding again
                CDDecoder.shared.decoding = true
            }
        }

    }

    private fun resetScanSessionState() {
        multiFrameCurrentCount = 0
        decodedBarcodeResults.clear()
        hasShownResultDialog = false
        if (isMultiFrameEnabled) {
            updateMultiFrameProgressText(0)
        }
    }

    private fun updateMultiFrameProgressText(count: Int) {
        findViewById<AppCompatTextView>(R.id.activity_camera_scan_barcode_result).text =
            "Barcode Scanned: $count/$multiFrameBarcodeTarget"
    }

    private fun showResultDialog() {
        val message = if (decodedBarcodeResults.isEmpty()) {
            "No barcode data found."
        } else {
            decodedBarcodeResults.entries.mapIndexed { index, entry ->
                val symbology = if (entry.value.isBlank()) "Unknown" else entry.value
                "${index + 1}. Barcode Data: ${entry.key}\nSymbology: $symbology"
            }.joinToString("\n\n")
        }

        AlertDialog.Builder(this)
            .setTitle("Scan Result")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}