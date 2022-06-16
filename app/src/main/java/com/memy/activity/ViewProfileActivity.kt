package com.memy.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.memy.R
import com.memy.api.BaseRepository
import com.memy.databinding.DashboardActivityBackupBinding
import com.memy.databinding.DashboardActivityBinding
import com.memy.pojo.ProfileVerificationResObj
import com.memy.utils.Constents
import com.memy.viewModel.DashboardViewModel


class ViewProfileActivity : AppBaseActivity() {
    lateinit var binding: DashboardActivityBackupBinding
    lateinit var viewModel: DashboardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIBinding()
        setupViewModel()
        setupObservers()
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    override fun onStart() {
        super.onStart()
        fetchProfileData()
    }

    private fun setupUIBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.dashboard_activity_backup)
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.addJavascriptInterface(WebAppInterface(this, binding.webview), "Android")
        binding.bottomBarLayout.visibility = View.GONE
        binding.titleTextView.text = getString(R.string.label_profile)
        binding.menuIconImageView.visibility = View.GONE
        binding.bottomTempView.visibility = View.GONE
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.viewModel = viewModel
    }

    private fun fetchProfileData() {
        var userId = intent?.getIntExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, -1)
        viewModel.showProfile = intent?.getBooleanExtra(Constents.SHOW_PROFILE_INTENT_TAG,false);
        if (userId!! > -1) {
            binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
            viewModel.fetchProfile(userId)
            if(viewModel.showProfile == true){
                viewModel.isTreeView.value = false
            }
        }
    }

    private fun loadProfileTree() {
        binding.webview.loadUrl(BaseRepository.BASE_URL + "api/familytreeForApp/?userid=${viewModel.userData.value?.mid}&apikey=${BaseRepository.APP_KEY_VALUE}")
    }

    override fun onBackPressed() {
        if(binding.progressInclude.progressBarLayout.visibility == View.GONE) {
            super.onBackPressed()
        }
    }

    private fun setupObservers() {
        viewModel.profileVerificationResObj.observe(this, this::validateProfileRes)
    }

    private fun validateProfileRes(res: ProfileVerificationResObj) {
        binding.progressInclude.progressBarLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                viewModel.userData.value = res.data
                if ((viewModel.userData.value != null) && (viewModel.userData.value?.mid != null)) {
                    loadProfileTree()
                    loadProfileImage(viewModel.userData.value?.photo)
                }
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
            }
        }
    }

    private fun loadProfileImage(url: String?) {
        if (!TextUtils.isEmpty(url)) {
            Glide
                .with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_male)
                .error(R.drawable.ic_profile_male)
                .into(binding.profileImageView)
        }
    }

    fun navigateAddFamily(v: View) {
        try {
            val intent = Intent(this, AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
            intent.putExtra(
                Constents.FAMILY_MEMBER_ID_INTENT_TAG,
                viewModel?.userData?.value?.mid
            )
            intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG, true)
            startActivityIntent(intent, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun navigateProfileScreen(v: View) {
        if (viewModel?.userData?.value?.mid == prefhelper.fetchUserData()?.mid) {
            val intent = Intent(this, AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
            startActivityIntent(intent, false);
        }
    }

    open class WebAppInterface {
        var mContext: Context? = null
        var mView: WebView? = null

        /** Instantiate the interface and set the context  */
        constructor(c: Context?, w: WebView?) {
            mContext = c
            mView = w
        }

        @JavascriptInterface
        fun addNewMember(user: String?) {
            val intent = Intent(mContext,AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG,false)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG,user?.toInt())
            intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG,true)
            (mContext as AppBaseActivity).startActivityIntent(intent,false)
        }

        @JavascriptInterface
        fun editFamilyMember(user: String?) {
            val intent = Intent(mContext,AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG,false)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG,user?.toInt())
            intent.putExtra(Constents.FAMILY_MEMBER_EDIT_INTENT_TAG,true)
            (mContext as AppBaseActivity).startActivityIntent(intent,false)
        }
        @JavascriptInterface
        fun viewFamilyMemberProfile(user: String?) {
            val intent = Intent(mContext, ViewProfileActivity::class.java)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, user?.toInt())
            intent.putExtra(Constents.SHOW_PROFILE_INTENT_TAG,true)
            (mContext as AppBaseActivity).startActivityIntent(intent, false)
        }

        @JavascriptInterface
        fun viewFamilyTree(user: String?) {
            val intent = Intent(mContext,ViewProfileActivity::class.java)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG,user?.toInt())
            (mContext as AppBaseActivity).startActivityIntent(intent,false)
        }

        @JavascriptInterface
        fun inviteFamilyMember(user: String?,message : String?) {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = message
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Share")

            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            (mContext as AppBaseActivity).startActivityIntent(Intent.createChooser(intent, "share"),false)
        }
    }


    fun callUser(v : View){
        var number = ""
        if((!TextUtils.isEmpty(viewModel.userData.value?.country_code)) && (!TextUtils.isEmpty(viewModel.userData.value?.mobile))) {
            number =
                viewModel.userData.value?.country_code + "" + viewModel.userData.value?.mobile
        }
        startDial(number)
    }
}