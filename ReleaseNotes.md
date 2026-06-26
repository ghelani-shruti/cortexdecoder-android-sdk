---
title: Android SDK
---

# Android SDK Release Notes

---

## Version 4.13.0

### New Features
- None

### Improvements & Fixes
- None

### API Changes
- New API: `refreshPreviewView()` refreshes the camera preview surface for cross-platform environments where views may remount.
- New API: `setCameraWithRefreshView(CDCamera.CDCameraLens cdCameraLens)` switches the active camera lens for cross-platform environments.

---

## Version 4.12.0

### New Features
- None

### Improvements & Fixes
- Updated decoder library to v25.2.2, that improved damaged QR code reading.

### API Changes
- None

---
## Version 4.11.0

### New Features
- None

### Improvements & Fixes
- Updated decoder library to v25.1.14, that improved Code11 decoding.

### API Changes
- None

---

## Version 4.10.0

### New Features
- None

### Improvements & Fixes
- Performance improvement for Camera on Region Of Interest feature. 

### API Changes
- None

---

## Version 4.9.0

### New Features
- New API: `setPinchToZoom(Boolean enable)` provides way to enable/disable pinch to zoom functionality
- New API: `getPinchToZoom()` returns current setting as boolean if pinch to zoom is enabled or not.


### Improvements & Fixes
- Bugfixes

### API Changes
- None

---
## Version 4.8.1

### New Features
- Symbol Modifier: CDResult now returns symbolModifier value for a decoded barcode

### Improvements & Fixes
- Updated isTorchSupported API to work before starting a camera


### API Changes
- None

---

## Version 4.8.0

### New Features
- Polarity :  CDResult now returns polarity value for DataMartix, QRCode and AztecCode barcodes.
-	Mirror:  CDResult now returns Boolean mirror value for DataMartix, QRCode and AztecCode barcodes.
-	ECC:  CDResult now return String as Error Code Correction levels for all 2D barcodes.
-	Rows and Columns:  CDResult now returns rows and columns as int for all 2D barcodes.

### Improvements & Fixes
- Updated decoder library to v25.1.6
- GS1 Stacked can now differentiate GS1StackedOmniDirectional and GS1StackedTruncated barcodes.


### API Changes
- None

---

## Version 4.7.1

### New Features
- None

### Improvements & Fixes
- Bugfixes

### API Changes
- None

---

## Version 4.7.0

### New Features
- Support of default Pinch To Zoom functionality on camera preview.
- Added Auto Torch functionality to improve scanning in dark environment.

### Improvements & Fixes
- Enabled 16KB page size support for native .so libraries for broader device compatibility.
- Updated decoder library to v24.1.28.

### API Changes
- API `setTorch(CDCamera.CDTorch torch)` now accepts a third Enum value: `AUTO`, in addition to the existing `ON` and `OFF` options.

---

## Version 4.6.0

### New Features
- None

### Improvements & Fixes
- Updated decoder library to v24.1.24.

### API Changes
- None

---

## Version 4.5.0

### New Features
- None

### Improvements & Fixes
- Internal improvements to camera lifecycle for better performance.

### API Changes
- None

---

## Version 4.4.0

### New Features
- Support of ultraWideAngle camera lens for supported devices. API: `setCamera(CDCameraLens cdCameraLens)`.

### Improvements & Fixes
- None 

### API Changes
- API `setCamera(CDCameraLens cdCameraLens)` now takes `cdCameraLens` as parameter to set available and supported lens like `wideAngle` and `ultraWideAngle`.

---

## Version 4.3.1

### New Features
- None

### Improvements & Fixes
- Updated decoder library to v23.2.8.

### API Changes
- None

---

## Version 4.3.0

### New Features
- New API: `CDDecoder#decode(ImageProxy imageProxy, CDImageDecodeListner decodeListener)` that supports decoding of barcode from imagePorxy.

### Improvements & Fixes
- None

### API Changes
- None

---

## Version 4.2.0

### New Features
- Added functionality to support Region of Interest (ROI) with Pick List Mode, to define a decoding range within a specific area.

### Improvements & Fixes
- Update the license expiration status when error code 908 is encountered.

### API Changes
- None

---

## Version 4.1.4

### New Features
- None

### Improvements & Fixes
- Updated decoder library to v23.1.4.

### API Changes
- None

---

## Version 4.1.3

### New Features
- New Property: `CDPreProcessorType#lowPass2` reduces noise from image.

### Improvements & Fixes
- None

### API Changes
- None

---

## Version 4.1.2

### New Features
- New Property: `CDResult#imageWidth` represents the decoded barcode image width when API `setDecodedImageRetrieval` is enabled.
- New Property: `CDResult#imageHeight` represents the decoded barcode image width when API `setDecodedImageRetrieval` is enabled.
- New API: `CDResult#getImageBitmap()` returns Android Bitmap object of decoded barcode image when API `setDecodedImageRetrieval` is enabled.

### Improvements & Fixes
- None

### API Changes
- None

---

## Version 4.1.1

### New Features
- None

### Improvements & Fixes
- Bug fixed to retain region of interest when switching camera or resolutions.

### API Changes
- None

---

## Version 4.1.0

### New Features
- New API: `setDecodedImageRetrieval(boolean enable)` provides a way to enable retrieving image of a successfully decoded barcode as part of `CDResult`.
- New API: `getDecodedImageRetrieval()` returns current setting as boolean if image retrieval is enabled or not.
- New Property: `CDResult#image` represents grayscale bytebuffer of decoded barcode image when API `setDecodedImageRetrieval` is enabled.

### Improvements & Fixes
- Decoder Library is updated to 22_2_9.
- Target SDK updated to API 33.
- Saved images using `setImageSaving` shall now annotate preview based ROI params at top.

### API Changes
- `CDSymbology.stringFromSymbologyType(...)` is renamed to `CDSymbology.getStringFromSymbologyType(...)`.

---

## Version 4.0.0

### New Features
- New Listener Callbacks:
  1. `CDCameraDecodeListener` for camera decodes via `CDCamera.shared.startCamera(...)`.
  2. `CDImageDecodeListener` for image decodes via `CDDecoder.shared.decode(...)`.
  3. `CDLicenseResultListener` for license activation via `CDLicense.shared.activateLicense(...)`.
  4. `CDMultiFrameDecodeCountListener` for multiframe decode counts.
- `CDSymbology.CompositeCode.setRequiredUPCFamily(...)` to enable/disable Composite Code Required UPC/EAN.
- `CDSymbology.CompositeCode.getRequiredUPCFamily()` to check status.
- `CDDevice.shared.setBeep(...)` and `CDDevice.shared.getBeep()`.
- `CDResult` new properties: `frameCoordinates`, `previewCoordinates`, `decodeTime`, and `totalDecodeTime`.

### Improvements & Fixes
- Upgraded minimum support to Android 5 KITKAT (API LEVEL: 21).
- Singleton objects now retrieved via properties (e.g., `CDDecoder.shared`).
- Context is now fetched at runtime; removed passing Context in methods.
- `setSource()` and `CDDecodeMode` removed; components handle scans simultaneously.
- **Bug Fix**: `CDCamera.shared.setCameraAPI(...)` now stops and reinitializes properly.
- **Bug Fix**: `setImageSaving(...)` now uses app sandbox instead of requiring external storage permission.
- Components (`CDDecoder`, `CDCamera`, `CDLicense`) are now decoupled.

### API Changes
- Renamed `GS1Databar14` to `GS1Databar`.
- Updated signature for `CDPerformanceFeatures.shared.setDataFormatting`.
- `CDResolution.res640x480` changed to `CDResolution.res640x360` (16:9 ratio).
- Renamed `stripSystemNumberDigit` to `stripNumberSystemDigit`.
- Renamed `setExtendedRectEnabled` to `setExtendedRectangular`.
- Renamed `setStripStartStopCharacters` to `setSendStartStopCharacters`.
- `CDSymbology` is now a utility class; `shared` is not required.

---

## Version 3.5.2

### Improvements & Fixes
- Camera LifeCycle Management updates: `CDCamera.startCamera()` must be called to start.
- **Bug Fix**: Camera Device now captures exact resolution provided.
- **Bug Fix**: ROI during image scans fixed.
- Note: Resolution and Focus must be defined before starting the camera.

---

## Version 3.5.1

### Improvements & Fixes
- Removed fixed rotation for camera image analysis; orientation always in sync.

---

## Version 3.5.0

### API Changes
- Added `setSendAIMSymbID`, `getSendAIMSymbID`, `setReducedAggressiveness`, and `getReducedAggressiveness` in `CDSymbology`.

---

## Version 3.4.0.noIcon

### Improvements & Fixes
- Removed icons and extra drawables/media.

---

## Version 3.4.0

### New Features
- New Overloaded API: `CDCamera.setHighlightBarcodes(bool enable, int color)`.

### Improvements & Fixes
- `cdResult[n].barcodeCoordinates` now returns preview-based coordinates for Camera1 and Camera2 (unified with CameraX).
- **Bug Fix**: Decoupled `CDLicense` and `CDDecoder` initialization order.

---

## Version 3.3.0

### New Features
- Added support for CameraX and lifecycle handling.
- Added Callback `onMultiFrameDecodeCount`.
- Added `CDCamera.setScaleType(scaleType)` and `CDCamera.getScaleType()`.

### Improvements & Fixes
- Updated decoder library for rigorous error correction.
- **Behavior Change**: Decoder sets decoding to false after successful decode; must call `setDecoding(true)` to resume.
- **Behavior Change**: `getCDResult` returns `noDecode` status for non-decoded frames.
- `CDResult.barcodeCoordinates` are preview-based for CameraX.

---

## Version 3.2.2

### Improvements & Fixes
- Updated decoder library for rigorous error correction.

---

## Version 3.2.0

### New Features
- Added `setAGCDelay()`, `getAGCTargetBrightness()`, and `getAGCMode()`.
- Updated support for QRConfiguration Code with persistence enabled.

### Improvements & Fixes
- Updated `setAGCDelay()` to take one parameter for both ISO and exposure time.
- Upgraded Gradle version and compile SDK.
- **Bug Fix**: ROI and multiframe bugs resolved.

### API Changes
- Renamed `setAGC` to `setAGCMode`.

---

## Version 3.1.1

### New Features
- Added Verification Results and `CDVerifier`.
- Added QR Configuration Code Scanning.

### Improvements & Fixes
- **Bug Fix**: Removed `CDDecoder.setEDKCustomerID`; use `CDLicense.setCustomerID()`.
- **Bug Fix**: `CDCamera.stopPreview()` now destroys preview; use `setVideoCapturing(false)` to pause.
- **Bug Fix**: `CDTorch.ON` in Camera1 now matches Camera2 behavior.

---

## Version 3.1.0

### New Features
- Added AGC control APIs.

### Improvements & Fixes
- **Bug Fix**: Fixed incorrect highlights when using front-facing camera.
- `getSupportedZoom()` now returns `float[2]` for min/max on all APIs.

---

## Version 3.0.0

### New Features
- Modularized classes: `CDCamera`, `CDDecoder`, `CDLicense` are now separate.
- Basic sample app provided for license and decoding.

### API Changes
- Camera API selection moved to `setCameraAPI(CDCameraAPI api)`.
- Mode switching moved to `setSource(CDDecodeMode mode)` in `CDDecoder`.