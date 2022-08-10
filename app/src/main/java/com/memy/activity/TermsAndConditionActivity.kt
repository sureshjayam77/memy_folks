package com.memy.activity

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.memy.R
import com.memy.databinding.TermsConditionActivityBinding
import com.memy.utils.Constents

class TermsAndConditionActivity : AppBaseActivity() {
    lateinit var binding : TermsConditionActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.terms_condition_activity)

        WebView.setWebContentsDebuggingEnabled(true);
        binding.termsWebView.settings.javaScriptEnabled = true
        binding.termsWebView.setWebViewClient(WebViewClient())
        if(intent != null){
            val intentTagValue = intent.getStringExtra(Constents.WEB_VIEW_INTENT_TAG)
            when(intentTagValue){
                Constents.WEB_VIEW_TERMS_AND_CONDITION -> {
                    binding.titleTextView.setText(getString(R.string.terms_condition))
                    binding.termsWebView.loadUrl("https://memyfolks.com/terms-conditions/")
                }
                Constents.WEB_VIEW_ABOUT -> {
                    binding.termsWebView.loadUrl("https://memyfolks.com/")
                    binding.titleTextView.setText(getString(R.string.label_about))
                }
                Constents.WEB_VIEW_BLOG -> {
                    binding.termsWebView.loadUrl("https://blog.memyfolks.com/")
                    binding.titleTextView.setText(getString(R.string.label_blog))
                }
                Constents.WEB_VIEW_HELP -> {
                    binding.termsWebView.loadUrl("https://memyfolks.com/help/")
                    binding.titleTextView.setText(getString(R.string.label_help))
                }
            }
        }

        binding.backIconImageView.setOnClickListener {
            onBackPressed()
        }
    }

    override fun dialogPositiveCallBack(id: Int?) {

    }

    override fun dialogNegativeCallBack() {

    }
}