package com.memy.api

import android.text.TextUtils
import com.memy.BuildConfig
import com.memy.MemyApplication
import com.memy.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import okhttp3.CertificatePinner
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import okhttp3.MultipartBody

import okhttp3.RequestBody

open class BaseRepository {
    var retrofit: Retrofit
    lateinit var LEVEL: HttpLoggingInterceptor.Level
    companion object{
       // val BASE_URL = "https://stage.memyfolks.com/"
        val BASE_URL = "https://memyfolks.com/"
       // val BASE_URL = "http://3.6.102.87/"
        val APP_KEY_VALUE = "xyz"
    }

    constructor() {
        logInterceptor()
        retrofit = createRetrofitClient()
    }

    private fun logInterceptor(){
        LEVEL = if(BuildConfig.DEBUG) (HttpLoggingInterceptor.Level.BODY) else HttpLoggingInterceptor.Level.NONE
    }

    private fun createRetrofitClient() : Retrofit{
        var client = OkHttpClient.Builder().hostnameVerifier ( hostnameVerifier = HostnameVerifier{ _, _ -> true })
        var logging = HttpLoggingInterceptor().setLevel(LEVEL)
        client.addInterceptor(logging)
        client.connectTimeout(60,TimeUnit.SECONDS)
        client.readTimeout(60,TimeUnit.SECONDS)
        client.writeTimeout(60,TimeUnit.SECONDS)

        try{
           var domainBaseURL : URL =  URL(BASE_URL)
            var domain = domainBaseURL.host
           if(!TextUtils.isEmpty(domain)){
               var publicKey = getSSLPubickKey()
               val certificatePinner: CertificatePinner = CertificatePinner.Builder()
                   .add(domain, publicKey)
                   .build()
               client.certificatePinner(certificatePinner)
           }
        }catch (e:Exception){
            e.printStackTrace()
        }

        var retrofitBuilder = Retrofit.Builder().addConverterFactory(ToStringConverterFactory())
        retrofitBuilder.baseUrl(BASE_URL)
        retrofitBuilder.addConverterFactory(MoshiConverterFactory.create())
        return retrofitBuilder.client(client.build()).build()
    }

    private fun getSSLPubickKey():String{
        var inputStream: InputStream? = null
        try {
            /*inputStream =
                MemyApplication.instance?.resources?.openRawResource(
                    R.raw.stage_memyfolks
                )*/
            inputStream = MemyApplication.instance?.resources?.openRawResource(
                    R.raw.memyfolks
                )
            if (inputStream != null) {
                var x509Certificate: X509Certificate = CertificateFactory.getInstance("X509")
                    .generateCertificate(inputStream) as X509Certificate

                var publicKeyEncoded = x509Certificate.publicKey.encoded
                var messageDigest = MessageDigest.getInstance("SHA-256")
                var publicKeySha256 = messageDigest.digest(publicKeyEncoded)

                var publicKeyBase64 = ""
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    publicKeyBase64 = java.util.Base64.getEncoder().encodeToString(publicKeySha256)
                } else {
                    publicKeyBase64 = android.util.Base64.encodeToString(
                        publicKeySha256,
                        android.util.Base64.DEFAULT
                    )
                }
                return "sha256/" + publicKeyBase64;
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            try{
                if(inputStream != null){
                    inputStream.close()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return ""
    }

    fun createPartFromString(partString: String?): RequestBody? {
        if(!TextUtils.isEmpty(partString)) {
            return RequestBody.create(MultipartBody.FORM, partString ?: "")
        }
        return null
    }
}