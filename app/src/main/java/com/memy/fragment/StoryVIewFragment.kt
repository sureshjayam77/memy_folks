package com.memy.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.lifecycle.ViewModelProvider
import com.memy.activity.AddFamilyActivity
import com.memy.activity.AppBaseActivity
import com.memy.activity.ViewProfileActivity
import com.memy.api.BaseRepository
import com.memy.databinding.StoryViewFragmentBinding
import com.memy.databinding.TreeViewFragmentBinding
import com.memy.utils.Constents
import com.memy.viewModel.DashboardViewModel
import com.squareup.moshi.Moshi
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResult
import com.memy.activity.AddStoryActivity
import androidx.activity.result.ActivityResultCallback

import androidx.activity.result.contract.ActivityResultContracts.GetContent

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class StoryVIewFragment : BaseFragment(){


    private lateinit var binding : StoryViewFragmentBinding
    private lateinit var viewModel : DashboardViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StoryViewFragmentBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)

        initializeView();
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initializeView(){
        WebView.setWebContentsDebuggingEnabled(true);
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.setWebViewClient(WebViewClient())
        binding.webview.addJavascriptInterface(
            WebAppInterface(
                activity,
                binding.webview,
                        startForResult), "Android")
        loadStoryView()
    }

    private fun loadStoryView() {
        val storyURL = BaseRepository.BASE_URL + "api/story_mobile_view/?userid=${viewModel.userData.value?.mid}&owner_id=${prefhelper.fetchUserData()?.mid}&apikey=${BaseRepository.APP_KEY_VALUE}&page=1"
        binding.webview.loadUrl(storyURL)
    }

    public val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
           // val intent = result.data
            // Handle the Intent
            val storyURL = BaseRepository.BASE_URL + "api/story_mobile_view/?userid=${viewModel.userData.value?.mid}&owner_id=${prefhelper.fetchUserData()?.mid}&apikey=${BaseRepository.APP_KEY_VALUE}&page=1"
            binding.webview.loadUrl(storyURL)
        }
    }

    public class WebAppInterface {
        var mContext: Context? = null
        var mView: WebView? = null
        lateinit var moshi: Moshi
        var startForResult :ActivityResultLauncher<Intent>? = null

        /** Instantiate the interface and set the context  */
        constructor(c: Context?, w: WebView?,sResult :ActivityResultLauncher<Intent>?) {
            mContext = c
            mView = w
            moshi = Moshi.Builder().build()
            startForResult = sResult
        }

        @JavascriptInterface
        fun postShareCallBack(message: String?) {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = message
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Share"
            )

            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            (mContext as AppBaseActivity).startActivityIntent(
                Intent.createChooser(intent, "share"),
                false
            )
        }

        @JavascriptInterface
        fun addStoryCallBack(userId: String?) {
            val intent = Intent(mContext, AddStoryActivity::class.java)
            intent.putExtra(Constents.ADD_STORY_USER_ID_INTENT_TAG, userId)
            startForResult?.launch(intent)
        }
    }
}