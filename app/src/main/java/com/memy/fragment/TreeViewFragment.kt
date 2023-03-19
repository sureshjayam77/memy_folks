package com.memy.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.memy.R
import com.memy.api.BaseRepository
import com.memy.databinding.TreeViewFragmentBinding
import com.memy.viewModel.DashboardViewModel
import androidx.lifecycle.ViewModelProvider
import com.memy.activity.*
import com.memy.utils.Constents
import com.memy.utils.PreferenceHelper
import com.squareup.moshi.Moshi

import androidx.databinding.adapters.SeekBarBindingAdapter.setProgress

import android.webkit.WebChromeClient
import androidx.databinding.adapters.SeekBarBindingAdapter
import com.memy.utils.PermissionUtil


class TreeViewFragment : BaseFragment() {

    private lateinit var binding : TreeViewFragmentBinding
    private lateinit var viewModel : DashboardViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TreeViewFragmentBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)

        initializeView();
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeView(){
        WebView.setWebContentsDebuggingEnabled(true);
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.addJavascriptInterface(
            WebAppInterface(
                activity,
                viewModel,
                binding.webview
            ), "Android")

        binding.webview.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...

                // Return the app name after finish loading
                binding.webViewProgress.setProgress(progress)
                binding.webViewProgress.visibility = if (progress == 100) (View.GONE) else(View.VISIBLE)
            }
        })
        loadProfileTree()
    }

    private fun loadProfileTree() {
        binding.webview.loadUrl(BaseRepository.BASE_URL + "api/familytreeForApp/?userid=${viewModel.userData.value?.mid}&owner_id=${prefhelper.fetchUserData()?.mid}&apikey=${BaseRepository.APP_KEY_VALUE}")
    }

    class WebAppInterface {
        var mContext: Activity? = null
        var mView: WebView? = null
        lateinit var moshi: Moshi
        var viewModel : DashboardViewModel? = null
        var prefHelper: PreferenceHelper? = null

        /** Instantiate the interface and set the context  */
        constructor(c: Activity?,vm : DashboardViewModel, w: WebView?) {
            mContext = c
            prefHelper = c?.let { PreferenceHelper().getInstance(it!! as Context) }
            mView = w
            moshi = Moshi.Builder().build()
            viewModel = vm
        }

        /** Show a toast from the web page  */
        @JavascriptInterface
        fun addNewMember(userId: String?) {
            /*var userDataObj = moshi.adapter(ProfileData::class.java).fromJson(userData)
            if(userDataObj != null){

            }*/
            viewModel?.selectedMemberId = userId
            if(mContext is DashboardActivity) {
                (mContext as DashboardActivity).fetchTempProfileData(userId)
            }else if(mContext is FamilyMemberProfileActivity) {
                (mContext as FamilyMemberProfileActivity).fetchTempProfileData(userId)
            }

            /*val intent = Intent(mContext, AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, userId?.toInt())
            intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG, true)
            (mContext as AppBaseActivity).startActivityIntent(intent, false)*/
        }

        @JavascriptInterface
        fun addNewMember(userId: String?,userName: String?) {
            /*var userDataObj = moshi.adapter(ProfileData::class.java).fromJson(userData)
            if(userDataObj != null){

            }*/
            val intent = Intent(mContext, AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, userId?.toInt())
            intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG, true)
            (mContext as AppBaseActivity).startActivityIntent(intent, false)
        }

        @JavascriptInterface
        fun editFamilyMember(user: String?) {
            val intent = Intent(mContext, AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, user?.toInt())
            intent.putExtra(Constents.FAMILY_MEMBER_EDIT_INTENT_TAG, true)
            (mContext as AppBaseActivity).startActivityIntent(intent, false)
        }

        @JavascriptInterface
        fun viewFamilyMemberProfile(user: String?) {
            if (user?.toInt() != viewModel?.userData?.value?.mid) {
                val intent = Intent(mContext, FamilyMemberProfileActivity::class.java)
                intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, user?.toInt())
                intent.putExtra(Constents.SHOW_PROFILE_INTENT_TAG, true)
                (mContext as AppBaseActivity).startActivityIntent(intent, false)
            }
        }

        @JavascriptInterface
        fun downloadFile(url: String?,data : String?) {
            Log.d("downloadFile",""+url)
            if (!TextUtils.isEmpty(url)){
                viewModel?.downloadURL = url ?: ""
                viewModel?.isDownloadFileCick = true
                PermissionUtil().requestPermissionForStorage(mContext,true)
            }
        }

        @JavascriptInterface
        fun downloadFile(url: String?) {
            Log.d("downloadFile",""+url)
            if (!TextUtils.isEmpty(url)){
                viewModel?.downloadURL = url ?: ""
                viewModel?.isDownloadFileCick = true
                PermissionUtil().requestPermissionForStorage(mContext,true)
            }
        }

        @JavascriptInterface
        fun viewFamilyTree(user: String?) {
            if (user?.toInt() != viewModel?.userData?.value?.mid) {
                val intent = Intent(mContext, FamilyMemberProfileActivity::class.java)
                intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, user?.toInt())
                (mContext as AppBaseActivity).startActivityIntent(intent, false)
            }
        }


        @JavascriptInterface
        fun inviteFamilyMember(user: String?, message: String?) {
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
    }


}