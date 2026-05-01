package com.bradycorp.sampleprojectwithjava;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// Imports for CortexDecoder SDK components
import com.codecorp.CDCamera;
import com.codecorp.CDDecoder;
import com.codecorp.CDLicense;
import com.codecorp.CDLicenseResult;
import com.codecorp.CDResult;
import com.codecorp.listeners.CDLicenseResultListener;

/**
 * Main activity for the barcode scanning sample app.
 * This activity demonstrates how to use the CortexDecoder SDK to scan barcodes using camera.
 */
public class MainActivity extends AppCompatActivity {
    // Enter customer ID for SDK license here
    private static final String CUSTOMERID = "";
    // Enter activation key for the SDK license here
    private static final String ACTIVATE_KEY = "";
    // Request code for camera permission
    private static final int cameraPermissionRequestCode = 1001;

    /**
     * Handles the result of permission requests.
     * Specifically checks for camera permission grant.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == cameraPermissionRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, can proceed with camera usage
            }
        }
    }

    /**
     * Called when the activity is first created.
     * Initializes the UI, SDK, requests camera permission, and sets up click listener for camera frame.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_scan);
        
        initCortexSDK();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    cameraPermissionRequestCode);

        }
        RelativeLayout cameraFrame = findViewById(R.id.activity_camera_scan_frame);
        cameraFrame.setOnClickListener(view -> {
            if(!CDCamera.shared.getVideoCapturing()){
//                CDCamera.shared.setTorch(CDCamera.CDTorch.auto); // Optional: Enable auto torch
                CDCamera.shared.setVideoCapturing(true);
                CDDecoder.shared.setDecoding(true);
            }
        });
    }

    /**
     * Starts the camera preview and adds it to the UI.
     * Also starts the camera with a decode callback.
     */
    private void startCameraPreview() {
        // start the camera
        CDCamera.shared.startCamera(this::onDecode);

        // Update RelativeLayout with Camera Preview
        RelativeLayout mCameraFrame = findViewById(R.id.activity_camera_scan_frame);

        // start the preview and add at index 0
        mCameraFrame.addView(CDCamera.shared.startPreview(), 0);


    }

    /**
     * Stops the camera preview, removes it from UI, and stops the camera.
     */
    private void stopCameraPreview() {
        // stop the preview
        CDCamera.shared.stopPreview();
        // remove from ui
        RelativeLayout mCameraFrame = findViewById(R.id.activity_camera_scan_frame);
        mCameraFrame.removeViewAt(0);
        mCameraFrame.invalidate();
        // stop the camera
        CDCamera.shared.stopCamera();
    }

    /**
     * Configures the decoding settings for the barcode scanner.
     * Enables highlights, sets to decode exactly one barcode, and turns decoding on.
     */
    private void configDecodeSettings() {
        // enable highlights
        CDCamera.shared.setHighLightBarcodes(true);
        // set barcodes to decode exactly 1
        CDDecoder.shared.setBarcodesToDecode(1, true);
        // Set decoding on
        CDDecoder.shared.setDecoding(true);
    }

    /**
     * Called when the activity is resumed.
     * Configures decode settings, enables decoding, and starts camera preview.
     */
    @Override
    protected void onResume() {
        super.onResume();
        configDecodeSettings();
        // Set decoding on
        CDDecoder.shared.setDecoding(true);
        // Start camera preview
        startCameraPreview();

    }

    /**
     * Called when the activity is paused.
     * Disables decoding and stops camera preview.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Set decoding off
        CDDecoder.shared.setDecoding(false);
        // Destroy camera preview
        stopCameraPreview();
    }

    /**
     * Callback method called when barcodes are decoded.
     * Displays the result on screen and stops video capturing if successful.
     */
    private void onDecode(CDResult[] cdResults) {
        // if result size is 1
        if(cdResults.length == 1){
            // print on screen
            runOnUiThread(() -> showBarcodeResult(cdResults[0]));
        }
        // cdResults is array of decoded items
        if (cdResults[0].status == CDResult.CDDecodeStatus.success || cdResults[0].status == CDResult.CDDecodeStatus.decodedQRConfigCode) {
            runOnUiThread(() -> {
                // stop video capturing if decoded
                CDCamera.shared.setVideoCapturing(false);
//                    CDDecoder.shared.setDecoding(true);
            });
        }
    }

    /**
     * Displays the decoded barcode result in the UI.
     * Shows status, data, symbology, and decode time.
     */
    @SuppressLint("SetTextI18n")
    private void showBarcodeResult(CDResult cdResult) {
        TextView textView = findViewById(R.id.activity_camera_scan_barcode_result);
        textView.setText(
                "Status: " + cdResult.status.name()+ "\n" +
                        "Barcode Data: " + cdResult.barcodeData+ "\n" +
                        "Symbology: " + cdResult.symbology + "\n" +
                        "Decode Time: " + cdResult.decodeTime + " ms"
        );
    }

    /**
     * Initializes the CortexDecoder SDK components.
     * Sets up the decoder and license, then activates the license.
     */
    private void initCortexSDK(){
        CDDecoder mCDDecoder = CDDecoder.shared;
        CDLicense mCDLicense = CDLicense.shared;

        mCDLicense.setCustomerID(CUSTOMERID);//for EDK only

        mCDLicense.activateLicense(ACTIVATE_KEY, cdLicenseResult -> {
            CDLicenseResult.CDLicenseStatus status = cdLicenseResult.status;
            if(status == CDLicenseResult.CDLicenseStatus.activated){
                Toast.makeText(getApplicationContext(), "License Valid", Toast.LENGTH_SHORT).show();

            }else if(status == CDLicenseResult.CDLicenseStatus.expired){
                Toast.makeText(getApplicationContext(), "Expired License", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getApplicationContext(), "License Invalid", Toast.LENGTH_SHORT).show();

            }
        });
    }
}