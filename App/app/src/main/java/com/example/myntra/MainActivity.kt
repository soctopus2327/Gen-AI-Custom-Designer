package com.example.clothcustomizer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myntra.R
import com.example.myntra.R.*
import com.example.myntra.R.layout.*
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var uploadButton: Button

    private var imageFile1: File? = null
    private var imageFile2: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val box = this.androidx.compose.foundation.layout.Box {
            setContentView(
                // layoutResID =
                /* layoutResID = */ activity_main,
            )
        }

        imageView1 = findViewById(id.imageView1)
        imageView2 = findViewById(id.imageView2)
        uploadButton = findViewById(id.uploadButton)

        imageView1.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .start(101)
        }

        imageView2.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .start(102)
        }

        uploadButton.setOnClickListener {
            if (imageFile1 != null && imageFile2 != null) {
                uploadImages(imageFile1!!, imageFile2!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val fileUri = data.data
            val file = ImagePicker.getFile(data)!!

            if (requestCode == 101) {
                imageFile1 = file
                Glide.with(this).load(fileUri).into(imageView1)
            } else if (requestCode == 102) {
                imageFile2 = file
                Glide.with(this).load(fileUri).into(imageView2)
            }
        }
    }

    private fun uploadImages(file1: File, file2: File) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-server.com/") // Replace with your server URL
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UploadService::class.java)

        val requestBody1 = file1.asRequestBody("image/*".toMediaTypeOrNull())
        val requestBody2 = file2.asRequestBody("image/*".toMediaTypeOrNull())

        val body1 = MultipartBody.Part.createFormData("file1", file1.name, requestBody1)
        val body2 = MultipartBody.Part.createFormData("file2", file2.name, requestBody2)

        val call = service.uploadImages(body1, body2)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle success
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
            }
        })
    }

    interface UploadService {
        @Multipart
        @POST("upload")
        fun uploadImages(
            @Part file1: MultipartBody.Part,
            @Part file2: MultipartBody.Part
        ): Call<ResponseBody>
    }
}

class ImagePicker {

}
