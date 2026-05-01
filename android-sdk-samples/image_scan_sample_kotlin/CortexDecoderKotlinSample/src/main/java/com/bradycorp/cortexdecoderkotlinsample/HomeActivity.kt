package com.bradycorp.cortexdecoderkotlinsample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codecorp.CDDecoder
import com.codecorp.CDLicense
import com.codecorp.CDLicenseResult
import com.codecorp.cortexdecoderlibrary.BuildConfig
import com.bradycorp.cortexdecoderkotlinsample.LicenseInfo.CUSTOMER_ID
import com.bradycorp.cortexdecoderkotlinsample.LicenseInfo.LICENSE_KEY
import com.bradycorp.cortexdecoderkotlinsample.ui.theme.NextGenAndroidSamplesTheme

class HomeActivity : ComponentActivity() {

    // Look at constant [CUSTOMER_ID] in object [LicenseInfo] for updating customer id
    // Look at constant [LICENSE_KEY] in object [LicenseInfo] for updating key

    /**
     * Method used to set customer id, license key and listen to callback after activating license
     * [CDLicense.setCustomerID]
     * [CDLicense.activateLicense]
     */
    private fun activateLicense(customerId:String, licenseKey:String){
        // Skip this part for 'G'(Gemalto) releases, no customer id is required for 'G'(Gemalto) releases. EDK must set customer id.
        if(BuildConfig.FLAVOR == "edk"){
            // set customer id
            CDLicense.shared.setCustomerID(customerId)
        }
        // set license key
        CDLicense.shared.activateLicense(licenseKey){
            // callback listener for activating license
            onLicenseActivation(it)
        }
    }
    /**
     * Callback listener for activating license
     */
    private fun onLicenseActivation(cdLicenseResult: CDLicenseResult){
        // show license result message in toast
        Toast.makeText(this, cdLicenseResult.message , Toast.LENGTH_SHORT).show()
        licenseResult.value = cdLicenseResult
    }

    private val licenseResult = mutableStateOf<CDLicenseResult?>(null)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("licenseResult", licenseResult.value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            licenseResult.value = savedInstanceState.getSerializable("licenseResult") as? CDLicenseResult
        }
        setContent {
            NextGenAndroidSamplesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
    @Composable
    fun HomeScreen() {
        val context = LocalContext.current

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), modifier = Modifier.padding(12.dp), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.Center // Space between rows
        ) {
            // Retrive SDK VERSION
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(10.dp),) {
                    Text(
                        text = "SDK Version: " + CDDecoder.shared.sdkVersion() + when(BuildConfig.FLAVOR) {
                            "gemalto" -> " G"
                            "edk" -> " E"
                            else -> " noFlavor"
                        },
                        style = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    )
                }
            }

            val buttons = listOf("ACTIVATE LICENSE", "IMAGE SCAN")
            items(buttons) { item ->
                Button(
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        when (item) {
                            // Activate License
                            "ACTIVATE LICENSE" -> activateLicense(CUSTOMER_ID, LICENSE_KEY)
                            "IMAGE SCAN"->{
                                if(licenseResult.value?.status != CDLicenseResult.CDLicenseStatus.activated){
                                    Toast.makeText(context, "No active license found. Please activate license again", Toast.LENGTH_SHORT).show()
                                }else{
                                    val imageScanActivity = Intent(context, ImageScanActivity::class.java)
                                    startActivity(imageScanActivity)
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = item,
                        style = TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    )
                }
            }
        }

    }
}



