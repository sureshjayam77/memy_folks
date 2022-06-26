package com.memy.activity

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.memy.R
import com.memy.databinding.TermsConditionActivityBinding

class TermsAndConditionActivity : AppBaseActivity() {
    lateinit var binding : TermsConditionActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.terms_condition_activity)

        WebView.setWebContentsDebuggingEnabled(true);
        binding.termsWebView.settings.javaScriptEnabled = true
        binding.termsWebView.setWebViewClient(WebViewClient())
        binding.termsWebView.loadUrl("https://memyfolks.com/terms-conditions/")

        binding.backIconImageView.setOnClickListener {
            onBackPressed()
        }
    }

    override fun dialogPositiveCallBack(id: Int?) {

    }

    override fun dialogNegativeCallBack() {

    }
}