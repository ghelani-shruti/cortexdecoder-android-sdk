# CortexDecoder Android SDK - Samples & Reference

[![Platform](https://img.shields.io/badge/Platform-%20Android%20-blue.svg)]()
[![SDK Version](https://img.shields.io/badge/SDK-v4.13.0-success.svg)]()
[![API Docs](https://img.shields.io/badge/API_Docs-NexGen-blueviolet.svg)](https://nexgen-docs.netlify.app/)
[![Support](https://img.shields.io/badge/Support-Brady_Enterprise-orange.svg)](mailto:software.support@codecorp.com)

**Enterprise-grade barcode scanning, data capture, and parsing for Android**

This repository contains ready-to-compile sample applications demonstrating how to integrate the CortexDecoder SDK into high-speed retail, rugged industrial, or government verification workflows.

> **Note:** This repository contains sample code only. Download the SDK binary and license keys from the [Brady Developer Portal](https://devportal.codecorp.com/).

<!-- {{Please record a 3-second gif of one of your best sample apps and insert below}}
<div align="center">
  <img src="demo.gif" alt="Preview of the {{INSERT SAMPLE APP NAME}} sample" width="600"/>
</div> -->



## Enterprise Barcode Scanning Features

* **Enterprise Barcode Scanning:** Sub-millisecond decoding latency for 40+ symbologies (1D, 2D, and postal codes).
* **Live Camera & Static Image Decoding:** Process continuous video streams or decode discrete bitmaps, gallery assets, and file uploads.
* **Industrial DPM & Multi-Code Tracking:** Reliably read low-contrast Direct Part Marking (laser, dot peen, chemical) and decode multiple barcodes simultaneously.
* **Driver's License, GS1 & UDI Data Parsing:** Instantly extract and format data from driver's licenses and healthcare/logistics formats.
* **Secure Offline Processing & ROI:** Utilize 100% on-device decoding for complete data privacy, and restrict scanning to an Active Region of Interest (ROI) for maximum precision.



## System Requirements
- **Supported Languages**: Java 8+, Kotlin 1.4+
- **Device/OS Compatibility**:  Android API 21+ (Android 5.0 and higher)
- **Minimum SDK Version**: API 21+
<!-- - **Build Tools**:  Android SDK Build Tools, Android Gradle Plugin  -->




## Quick Start Guide
These core APIs are required for any CortexDecoder integration. If you simply want to compile the sample code in this repository, skip to [Running the Sample Apps](#running-the-sample-apps).

### 1. Getting the License
Before initializing the SDK, generate your active development or production license key via the [Brady Developer Portal](https://devportal.codecorp.com/).

There are two flexible licensing models to suit your deployment environment: Enterprise and Standard.
### 2. Apply the License

### Enterprise License (Offline Activation)


```JAVA
// set customer id
CDLicense.shared.setCustomerID("Customer_ID");

// activate license
CDLicense.shared.activateLicense("License_Key", this::onActivationResult);

// callback listener of license activation
void onActivationResult(@NonNull CDLicenseResult cdLicenseResult) {
    // cdLicenseResult is an instance of CDLicenseResult class which shares details of license activation.
    if(cdLicenseResult.status == CDLicenseResult.CDLicenseStatus.activated){
        // proceed...
    }
}
```
### Standard License (Online Activation)

    
```JAVA
// activate license
CDLicense.shared.activateLicense("License_Key", this::onActivationResult);

// callback listener of license activation
void onActivationResult(@NonNull CDLicenseResult cdLicenseResult) {
    // cdLicenseResult is an instance of CDLicenseResult class which shares details of license activation.
    if(cdLicenseResult.status == CDLicenseResult.CDLicenseStatus.activated){
        // proceed...
    }
}
```
### Standard License (Offline Activation)
This requires a manual handshake using .C2V and .V2C files.

**Step A: Generate Device ID (.C2V):**       
* Generate the unique device identification file on your Android device
```JAVA
CDLicense.shared.generateDeviceID(this::onGenerate);
```
**Step B: Retrieve License (.V2C) via Sentinel Portal**

* Login to the [Sentinel EMS Customer Portal](https://gemalto.2drevolution.com/ems/customerLogin.html) using your Product Key.

* Click the Offline Activation tab (top right).

* In the pop-up, under Upload C2V, upload the .c2v stored in your internal storage of the device.

* Click Generate to create your .V2C license file.

* Download the .V2C file and copy it to your device's internal storage.

**Step C: Activate license by loading V2C file within your app**

```JAVA
CDLicense.shared.loadLicenseFile(filePath, this::onActivationResult);

// callback listener of license activation
void onActivationResult(@NonNull CDLicenseResult cdLicenseResult) {
    // cdLicenseResult is an instance of CDLicenseResult class which shares details of license activation.
    if(cdLicenseResult.status == CDLicenseResult.CDLicenseStatus.activated){
        // proceed...
    }
}
```

### 3. Configure Permissions
```JAVA
<uses-permission android:name="android.permission.CAMERA" />
```

### 4. Initialize the Camera
```JAVA
// Initialize the camera with a decode callback
CDCamera.shared.startCamera(this::onDecode);

// Locate your RelativeLayout for the Camera Preview
RelativeLayout mCameraFrame = findViewById(R.id.activity_camera_scan_frame);

// Start the preview and add it to the layout at index 0
mCameraFrame.addView(CDCamera.shared.startPreview(), 0);
```

### 5. Start Decoding
```JAVA
// Enable the decoding process
CDDecoder.shared.setDecoding(true);

// Enable camera video capturing
CDCamera.shared.setVideoCapturing(true);
```

### 6. Configure Symbologies
By default, Symbologies are enabled based on the license.
```JAVA
// Enable Code128 
CDSymbology.Code128.setCode128(true);

// Get Code128 in boolean
CDSymbology.Code128.getCode128();
```



## Advanced Features

### Region of Interest (ROI)

Restrict decoding to a specific screen area to optimize performance and prevent accidental scans.

```JAVA
// To set ROI on specified rect give rect values based on preview view not image pixels
CDDecoder.shared.setRegionOfInterest(specifiedRectHere, true, true);
```

### Highlight Barcodes

Draw a visual bounding box on decoded barcodes in real-time to provide immediate user feedback.

```JAVA
 CDCamera.shared.setHighLightBarcodes(true)
```



## Running the Sample Apps

To see these APIs in action, follow these steps to build and run the sample applications included in this repository.

1.  **Clone the Project**

    ```bash
    git clone https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk.git
    cd android-sdk-samples
    ```

2.  **Add the SDK Binary**

    Download the Android SDK from the [Brady Developer Portal](https://devportal.codecorp.com/) and copy the framework/library files into the project's directory.

3.  **Configure Licensing**

    Open `LicenseInfo` and enter your license key from the [Brady Developer Portal](https://devportal.codecorp.com/). 

4.  **Build and Run**

    Open your preferred sample app directory and follow its localized README for specific execution commands.



### Included Samples
| Sample App | Best For 
| --- | --- |
| [camera_scan_sample_java]( https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk/tree/main/android-sdk-samples/camera_scan_sample_java) | Real-time scanning via device camera | 
| [camera_scan_sample_kotlin](https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk/tree/main/android-sdk-samples/camera_scan_sample_kotlin) | Real-time scanning via device camera |
| [image_scan_sample_kotlin](https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk/tree/main/android-sdk-samples/image_scan_sample_kotlin) | Decoding from static file uploads |
| [multiframe_decode_sample_kotlin](https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk/tree/main/android-sdk-samples/multiframe_decode_sample_kotlin) | Scanning multiple unique barcodes using multiple frames | |
| [region_of_interest_sample_kotlin](https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk/tree/main/android-sdk-samples/region_of_interest_sample_kotlin) | Decoding only within a custom-defined Region of Interest |
| [touch_to_scan_sample_kotlin](https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk/tree/main/android-sdk-samples/touch_to_scan_sample_kotlin) | On-demand decoding via screen-touch interaction |




<!-- ## Common Issues & Troubleshooting
{{OPTIONAL SECTION}}
- {{List common issues and their solutions}} -->



## Support & Resources
- **Sample App Issues**: Report bugs related to the sample code via [GitHub Issues](https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk/issues).

- **SDK Product Support**: For inquiries regarding decoding performance, engine behavior, or licensing, contact *software.support@codecorp.com*.

- **API Documentation** - [NexGen Docs](https://nexgen-docs.netlify.app/)
- **Licensing & Binaries** - [Brady Developer Portal](https://devportal.codecorp.com/)
- **Main Website** - [CodeCorp by Brady](https://www.codecorp.com)




---



© 2026 Brady Worldwide, Inc. All rights reserved

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
