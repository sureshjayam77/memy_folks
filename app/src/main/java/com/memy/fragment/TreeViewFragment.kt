package com.memy.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.squareup.moshi.Moshi


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
                binding.webview
            ), "Android")
        loadProfileTree()
    }

    private fun loadProfileTree() {
        binding.webview.loadUrl(BaseRepository.BASE_URL + "api/familytreeForApp/?userid=${viewModel.userData.value?.mid}&owner_id=${prefhelper.fetchUserData()?.mid}&apikey=${BaseRepository.APP_KEY_VALUE}")
    }

    class WebAppInterface {
        var mContext: Context? = null
        var mView: WebView? = null
        lateinit var moshi: Moshi

        /** Instantiate the interface and set the context  */
        constructor(c: Context?, w: WebView?) {
            mContext = c
            mView = w
            moshi = Moshi.Builder().build()
        }

        /** Show a toast from the web page  */
        @JavascriptInterface
        fun addNewMember(userData: String?) {
            /*var userDataObj = moshi.adapter(ProfileData::class.java).fromJson(userData)
            if(userDataObj != null){

            }*/

            val intent = Intent(mContext, AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, userData?.toInt())
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
            val intent = Intent(mContext, FamilyMemberProfileActivity::class.java)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, user?.toInt())
            intent.putExtra(Constents.SHOW_PROFILE_INTENT_TAG, true)
            (mContext as AppBaseActivity).startActivityIntent(intent, false)
        }

        @JavascriptInterface
        fun viewFamilyTree(user: String?) {
            val intent = Intent(mContext, FamilyMemberProfileActivity::class.java)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, user?.toInt())
            (mContext as AppBaseActivity).startActivityIntent(intent, false)
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