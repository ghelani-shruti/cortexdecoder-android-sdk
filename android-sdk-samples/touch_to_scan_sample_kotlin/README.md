
# Touch To Scan Sample App

This application demonstrates the Touch-to-Scan functionality using the Cortex Decoder SDK. It is designed for scenarios where multiple barcodes are visible in the camera preview simultaneously, allowing the user to select and decode only the specific barcode they tap on the screen.

## Features Demonstrated
- Decodes only the barcode located at the user's touch coordinates.
- Bridges user touch input with the SDK’s decoding engine
- Barcode result display

## Prerequisites
Before running this sample, you will need:

- **SDK Binary and License Key**,  managed through the  [Brady Developer Portal](https://devportal.codecorp.com/).
- **Minimum SDK Version**: Android 5.0 (API Level 21)
- **Tools**: Android Studio with Gradle build system

## SDK Integration 
To integrate the Cortex Decoder SDK into your Android project, build and run:

### Step 1: Clone the Repository
```bash
git clone https://github.com/Brady-Scanning-and-Vision-SDKs/cortexdecoder-android-sdk.git

cd cortexdecoder-android-sdk/android-sdk-samples/touch_to_scan_sample_kotlin
```

### Step 2: Add SDK to Your Project
* Download the Cortex Decoder SDK from the [Brady Developer Portal](https://devportal.codecorp.com/).
* Copy the SDK `.aar` files to the `libs/` directory in your app module.
* In your app's `build.gradle.kts`, add the following dependency:

```gradle
dependencies {
       implementation(files("libs/CortexDecoderLibrary.aar"))
}
```

### Step 3: Add License Key
Add your license key to the application. Obtain your license key from the [Brady Developer Portal](https://devportal.codecorp.com/). You can initialize the SDK with your license key in your LicenseInfo.kt. *(NOTE: No need to set customer id if using Standard version.)*

```Kotlin
const val CUSTOMER_ID = "" //ENTER-CUSTOMER-ID
   
const val LICENSE_KEY = "" //ENTER-YOUR-KEY
```

### Step 4: Build and Run
* Ensure you have library file and license keys added to the project
* Connect an Android device or start an emulator.
* Click **Run** or press `Shift + F10` to build and run the application.

### Step 5: Grant Permissions
When the app launches, grant camera permission when prompted.

## Sample App Usage
Place barcodes within the camera preview. Touch/tap the barcode you want to decode and decoded barcode data will be shown at the bottom text bar.

<!-- ## Troubleshooting
{{OPTIONAL SECTION}}
- {{List common issues and their solutions}} -->

## Contribution
Contributions are welcome. If you have a fix or an improvement for this sample application, please:
1. Fork this repository.
2. Create your feature branch.
3. Commit your changes.
4. Open a Pull Request against our `main` branch.

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
