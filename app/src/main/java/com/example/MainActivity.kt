package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import coil.Coil
import coil.ImageLoader
import com.example.data.viewmodel.CoinCollectionViewModel
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MisEurosTheme
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {

    private val viewModel: CoinCollectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup custom Coil ImageLoader with User-Agent header to avoid 403 Forbidden errors from Wikimedia/ECB image servers
        val imageLoader = ImageLoader.Builder(applicationContext)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val requestWithUserAgent = original.newBuilder()
                            .header(
                                "User-Agent",
                                "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36 EuroCoinCatalog/1.0 (Contact: collector@miseuros.app)"
                            )
                            .build()
                        chain.proceed(requestWithUserAgent)
                    }
                    .build()
            }
            .crossfade(true)
            .build()
        Coil.setImageLoader(imageLoader)

        enableEdgeToEdge()
        setContent {
            MisEurosTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

