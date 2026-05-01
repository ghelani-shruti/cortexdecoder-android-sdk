package com.bradycorp.cortexdecoderkotlinsample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.codecorp.CDDecoder
import com.codecorp.CDResult
import com.bradycorp.cortexdecoderkotlinsample.ui.theme.NextGenAndroidSamplesTheme
import java.nio.ByteBuffer


/**
 * ImageScanActivity demonstrates image-based barcode scanning functionality.
 * Users can select an image from their device gallery, and scan for barcodes.
 * The activity handles storage permissions appropriately
 * for different Android API levels and displays the decoded results.
 */
class ImageScanActivity : ComponentActivity() {

    // Activity result launcher for getting content (image picker)
    private val getContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    // Activity result launcher for requesting storage permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, launch the image picker
            getContentLauncher.launch("image/*")
        } else {
            // Permission denied, show toast message
            Toast.makeText(
                this,
                "Please allow photo access from app settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Member variables to track image URI and scan results
    private var imageUri by mutableStateOf<Uri?>(null)
    private lateinit var showResult: MutableState<Boolean>
    private var barcodeResults: Array<CDResult>? = null

    /**
     * Method decodes barcodes from a bitmap image
     */
    private fun decodeImage(imageBitmap:Bitmap){

        // declare width, height val
        val width: Int = imageBitmap.width
        val height: Int = imageBitmap.height

        // allocate buffer to store bitmap RGB pixel data
        val rgb = IntArray(width * height)
        val gray = ByteArray(width * height)

        // copy color pixels into rgb buffer
        imageBitmap.getPixels(rgb, 0, width, 0, 0, width, height)

        // convert RGB pixels to grayscale pixels and place them in gray buffer
        for (i in 0 until width * height - 1) {
            gray[i] =
                ((306 * (rgb[i] shr 16 and 0xFF) + 601 * (rgb[i] shr 8 and 0xFF) + 117 * (rgb[i] and 0xFF)) / 1024).toByte()
        }

        // Getting result with a callback method
        val pixBuf = ByteBuffer.allocateDirect(width * height)
        pixBuf.put(gray)

        // Pass the buffer, width, height and stride
        CDDecoder.shared.decode(pixBuf, width, height, width){
            // callback listener to show dialog of result
            barcodeResults = it
            showResult.value = true
        }
    }

    /**
     * Check if storage permission is granted based on Android API level
     * For Android 13+: checks READ_MEDIA_IMAGES permission
     * For Android 12 and below: checks READ_EXTERNAL_STORAGE permission
     */
    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires READ_MEDIA_IMAGES
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 and below requires READ_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Request appropriate storage permission based on Android API level
     * For Android 13+: requests READ_MEDIA_IMAGES
     * For Android 12 and below: requests READ_EXTERNAL_STORAGE
     */
    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestPermissionLauncher.launch(permission)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NextGenAndroidSamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }

    private fun resizeBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val maxWidth = 3840 // Set the maximum width you desire
        val maxHeight = 2160 // Set the maximum height you desire

        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        // Check if resizing is necessary
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            // No resizing needed
            return bitmap
        }

        // Calculate the desired width and height while maintaining the aspect ratio
        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
        val desiredWidth = if (aspectRatio > 1) maxWidth else (maxHeight * aspectRatio).toInt()
        val desiredHeight = if (aspectRatio > 1) (maxWidth / aspectRatio).toInt() else maxHeight

        // Create a matrix for the resizing operation
        val matrix = Matrix()
        matrix.postScale(
            desiredWidth.toFloat() / originalWidth,
            desiredHeight.toFloat() / originalHeight
        )

        // Create a new bitmap with the desired dimensions
        val resizedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, originalWidth, originalHeight, matrix, false
        )

        // Recycle the original bitmap to free up memory
        bitmap.recycle()

        // Return the resized bitmap
        return resizedBitmap
    }


    /**
     * Launch image picker with proper permission handling
     * Checks if storage permission is granted before opening the image picker
     * If permission is not granted, requests it first
     */
    private fun selectImage() {
        if (hasStoragePermission()) {
            // Permission is granted, launch the image picker
            getContentLauncher.launch("image/*")
        } else {
            // Permission is not granted, request it
            requestStoragePermission()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppContent() {
        val context = LocalContext.current

        // MutableState to control the visibility of the dialog
        showResult = remember { mutableStateOf(false) }

        // Single ScrollState for the whole page
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            TopAppBar(
                title = {
                    Text(
                        text = "Image Scan", fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis,
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Image View
            val imageBitmap: Bitmap? = imageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempImg = BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                if (tempImg != null) {
                    resizeBitmapIfNeeded(tempImg.asAndroidBitmap())
                } else {
                    null
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 400.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap.asImageBitmap(), contentDescription = "QR Code Image", modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(text = "No Image Selected")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BigIconButton(
                        onClick = { selectImage() }, drawableResId = R.drawable.ic_select_image, text = "SELECT IMAGE"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    BigIconButton(
                        onClick = {
                            if (imageBitmap != null) {
                                decodeImage(imageBitmap)
                            }
                        },
                        drawableResId = R.drawable.ic_scan, text = "SCAN"
                    )
                }
            }
            // RESULTS SECTION
            if (showResult.value) {
                if (barcodeResults != null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    ResultCard(results = barcodeResults!!)
                }
            }
        }
    }

    @Composable
    fun ResultCard(results: Array<CDResult>) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SCANNED RESULTS",
                modifier = Modifier.padding(bottom = 12.dp),
                style = TextStyle(fontSize = 16.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
                )
            )
            results.forEachIndexed { counter, item ->
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "[$counter] Status: ${item.status.name}",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Barcode Data: ${item.barcodeData}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                    )
                    Text(
                        text = "Symbology: ${item.symbology}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                    )
                    Text(
                        text = "Decode Time: ${item.decodeTime}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                    )
                }
            }
        }
    }

    @Composable
    fun BigIconButton(
        modifier: Modifier = Modifier,
        drawableResId: Int,
        text: String,
        onClick: () -> Unit
    ) {
        Column(
            modifier = modifier.clickable(onClick = onClick).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(drawableResId), contentDescription = null
                )
            }
            Text(
                text = text, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
