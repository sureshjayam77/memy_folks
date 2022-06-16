package com.memy.utils;

/*import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.memy.api.UnsafeOkHttpClient;

import java.io.InputStream;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;*/

//@GlideModule
//@Excludes(OkHttpLibraryGlideModule.class)
public class MyAppGlideModule{
      /*  extends AppGlideModule {


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
       // OkHttpClient okHttpClient = new OkHttpClient();
        *//*CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("memyfolks.com", "sha256/a9sboY0AHYP+wb/ZVcHMhcr9oHPlrJZ+QrxzF24WhwU=")
                .build();
        okHttpClient.certificatePinner(certificatePinner);*//*
       // registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));

        OkHttpClient okHttpClient= UnsafeOkHttpClient.getUnsafeOkHttpClient();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));

    }*/
}
