package com.memy.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.URLUtil
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.memy.MemyApplication
import com.memy.R
import com.memy.adapter.GuideFragmentAdapter
import com.memy.adapter.RelationSelectionAdapter
import com.memy.databinding.DashboardActivityBinding
import com.memy.fragment.StoryVIewFragment
import com.memy.fragment.TreeViewFragment
import com.memy.listener.AdapterListener
import com.memy.pojo.*
import com.memy.utils.Constents
import com.memy.utils.PermissionUtil
import com.memy.viewModel.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DashboardActivity : AppBaseActivity() {
    lateinit var binding: DashboardActivityBinding
    lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIBinding()
        setupViewModel()
        setupObservers()
        PermissionUtil().initRequestPermissionForCamera(this, true)
        updateFCMToken()
        checkDeepLink()
        fetchProfileData(false)
        if((!prefhelper.getGuideSkipClick())) {
            fetchMemberRelationShipData(viewModel.userData.value?.mid?.toString())
        }
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
        if (id == R.id.storage_camera_permission) {
            navigatePermissionSettingsPage()
        }else if(id == R.id.delete_account_res_id){
            if(viewModel.selectedMemberId?.toInt() == prefhelper.fetchUserData()?.mid){
                prefhelper.clearPref()
                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                startActivityIntent(intent, true)
            }else{
                val intent = Intent(this, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                startActivityIntent(intent, true)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.userData.value = prefhelper.fetchUserData()
        loadProfileImage(viewModel.userData.value?.photo)
        if(viewModel.tabPos == 0){
            viewModel.isTreeView.value = true
        }
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    private fun setupUIBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.dashboard_activity)
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener(View.OnClickListener {
            binding.drwayerLay.openDrawer(Gravity.LEFT)
        })
        binding.bottomTempView.visibility = View.VISIBLE
        binding.bottomBarLayout.visibility = View.VISIBLE
        // binding.switchTreeFamily.visibility = View.GONE
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.userData.value = prefhelper.fetchUserData()
        viewModel.pageNumber = 1
        viewModel.totalItemCount = 0
        viewModel.lastVisibleItem = 0
    }

    private fun fetchProfileData(refreshStroy: Boolean) {
        if ((viewModel.userData.value != null) && (viewModel.userData.value?.mid != null)) {
            viewModel.fetchProfile(viewModel.userData.value?.mid)
            if (viewModel.isTreeView.value == true) {
                val manager: FragmentManager = supportFragmentManager
                val transaction: FragmentTransaction = manager.beginTransaction()
                transaction.replace(
                    R.id.fragmentContainer,
                    TreeViewFragment(),
                    TreeViewFragment::javaClass.name
                )
                transaction.addToBackStack(null)
                transaction.commit()
                viewModel.tabPos = 0
                binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_unselect)
                binding.notificationImageView.setImageResource(R.drawable.ic_notification_unselect)

                binding.familyImageView.setImageResource(R.drawable.ic_mmf_select)
                binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
                binding.storyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
                binding.familyTextView.setTextColor(ContextCompat.getColor(this, R.color.app_color))
                binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
                binding.notificationTextView.setTextColor(ContextCompat.getColor(this, R.color.footer_bar_txt_color))
            } else if (refreshStroy) {
                val manager: FragmentManager = supportFragmentManager
                val transaction: FragmentTransaction = manager.beginTransaction()
                transaction.replace(
                    R.id.fragmentContainer,
                    StoryVIewFragment(),
                    StoryVIewFragment::javaClass.name
                )
                transaction.addToBackStack(null)
                transaction.commit()
                viewModel.tabPos = 1

                binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_unselect)
                binding.notificationImageView.setImageResource(R.drawable.ic_notification_unselect)
                binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
                binding.storyImageView.setImageResource(R.drawable.ic_story_select)
                binding.storyTextView.setTextColor(ContextCompat.getColor(this, R.color.app_color))
                binding.familyTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.footer_bar_txt_color
                    )
                )
                binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
                binding.notificationTextView.setTextColor(ContextCompat.getColor(this, R.color.footer_bar_txt_color))
            }
            loadProfileImage(viewModel.userData.value?.photo)
        }
    }

    override fun onBackPressed() {
        if (viewModel.showAddRelationView.value == true) {
            viewModel.showAddRelationView.value = false
        } else if (viewModel.showSocialLinkAddView.value == true) {
            viewModel.showSocialLinkAddView.value = false
        } else if (binding.progressFrameLayout.visibility == View.GONE) {
            super.onBackPressed()
            finishAffinity()
            System.exit(0)
        }
    }

    private fun setupObservers() {
        viewModel.profileVerificationResObj.observe(this, this::validateProfileRes)
        viewModel.profileSocialLinkUpdateRes.observe(this, this::validateSocialLinkRes)
        viewModel.memberRelationData.observe(this, this::validateMemberRelationRes)
        viewModel.profileResForEdit.observe(this, this::validateProfileForEditRes)
        viewModel.deleteAccountRes.observe(this,this::validateDeleteAccountRes)
        viewModel.inviteCommonResData.observe(this,this::validateInviteApiRes)

        viewModel.isTreeView.observe(this, { v ->
            fetchProfileData(true)
        })
        viewModel.updateFcmRes.observe(this, this::validateFCMRes)
        viewModel.instagramLink.observe(this, {
            binding.instagramLayout.isErrorEnabled = false
        })
        viewModel.facebookLink.observe(this, {
            binding.facebookLayout.isErrorEnabled = false
        })
        viewModel.twitterLink.observe(this, {
            binding.twitterLayout.isErrorEnabled = false
        })
        viewModel.linkedInLink.observe(this, {
            binding.linkedInLayout.isErrorEnabled = false
        })
        viewModel.aboutContent.observe(this, {
            binding.aboutLayout.isErrorEnabled = false
        })
    }

    private fun validateProfileRes(res: ProfileVerificationResObj) {
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                viewModel.userData.value = res.data
                prefhelper.saveUserData(res.data)
                loadProfileImage(viewModel.userData.value?.photo)
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                // showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
            }
        }
    }

    private fun validateSocialLinkRes(res: CommonResponse) {
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                viewModel.showSocialLinkAddView.value = false
                var userData = viewModel.userData.value

                userData?.instagram_link = viewModel.instagramLink.value
                userData?.facebook_link = viewModel.facebookLink.value
                userData?.twitter_link = viewModel.twitterLink.value
                userData?.linkedin_link = viewModel.linkedInLink.value
                userData?.about_me = viewModel.aboutContent.value
                viewModel.userData.value = userData
                updateSocialMediaIcons()

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
            Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_male)
                .error(R.drawable.ic_profile_male)
                .into(binding.profileImageView)
        }
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_male)
                .error(R.drawable.ic_profile_male)
                .into(binding.imgProfile)
        }
        binding.txtFirstname.text = viewModel.userData.value!!.firstname
        binding.txtMobile.text = viewModel.userData.value!!.mobile

        viewModel.instagramLink.value = viewModel.userData.value?.instagram_link
        viewModel.facebookLink.value = viewModel.userData.value?.facebook_link
        viewModel.twitterLink.value = viewModel.userData.value?.twitter_link
        viewModel.linkedInLink.value = viewModel.userData.value?.linkedin_link
        viewModel.aboutContent.value = viewModel.userData.value?.about_me
        updateSocialMediaIcons()
    }

    fun navigateAddFamily(v: View) {
        val intent = Intent(this, AddFamilyActivity::class.java)
        intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
        intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
        intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG, true)
        startActivityIntent(intent, false)
    }

    fun navigateProfileScreen(v: View) {
        val intent = Intent(this, AddFamilyActivity::class.java)
        intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
        intent.putExtra(Constents.FAMILY_MEMBER_FNAME_INTENT_TAG, viewModel?.userData?.value?.firstname)
        intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
        startActivityIntent(intent, false)
    }

    fun navigateNotificationScreen(v: View) {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        val notificationActivity = NotificationFragment()
        val bundle = Bundle()
        bundle.putBoolean(Constents.OWN_PROFILE_INTENT_TAG, true)
        bundle.putInt(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid!!)
        notificationActivity.arguments = bundle
        transaction.replace(
            R.id.fragmentContainer,
            notificationActivity,
            NotificationFragment::javaClass.name
        )
        transaction.addToBackStack(null)
        transaction.commit()
        binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_unselect)
        binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
        binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
        binding.notificationImageView.setImageResource(R.drawable.ic_notification_select__1_)
        binding.storyTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.footer_bar_txt_color
            )
        )
        binding.familyTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.footer_bar_txt_color
            )
        )
        binding.bubblesTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.footer_bar_txt_color
            )
        )
        binding.notificationTextView.setTextColor(ContextCompat.getColor(this, R.color.app_color))

    }

    /* fun navigateBottomProfileScreen(v: View) {
         val intent = Intent(this, AddFamilyActivity::class.java)
         intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
         intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
         startActivityIntent(intent, false);
     }*/

    fun callUser(v: View) {
        var number = ""
        if ((!TextUtils.isEmpty(viewModel.userData.value?.country_code)) && (!TextUtils.isEmpty(
                viewModel.userData.value?.mobile
            ))
        ) {
            number =
                viewModel.userData.value?.country_code + "" + viewModel.userData.value?.mobile
        }
        startDial(number)
    }

    fun showMenuPopup(v: View?) {
        prefhelper.clearPref()
        val intent = Intent(this@DashboardActivity, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivityIntent(intent, true)
        /* val popup = PopupMenu(this, v)
         popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu())
         popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
             override fun onMenuItemClick(item: MenuItem): Boolean {
                 if (item != null) {
                     when (item.itemId) {
                         R.id.logout -> {

                         }
                     }
                 }
                 return true
             }
         })
         popup.show()*/
    }

    fun navigateAddStoryScreen(v: View) {
        /*  if (viewModel?.userData?.value?.mid == prefhelper.fetchUserData()?.mid) {
              val intent = Intent(this, AddStoryActivity::class.java)
              resultLauncher.launch(intent)
          }*/
    }

    fun validateOpenStoryView() {
        if (PermissionUtil().requestPermissionForCamera(this, false)) {
            viewModel.isTreeView.value = false
        } else if (PermissionUtil().isCameraStoragePermissionUnderDontAsk(this)) {
            showPermissionDialog()
        } else {
            PermissionUtil().requestPermissionForCamera(this, true)
        }
    }

    private fun showPermissionDialog() {
        showAlertDialog(
            R.id.storage_camera_permission,
            getString(R.string.label_camera_storage_permission_req),
            getString(R.string.label_settings),
            getString(R.string.label_cancel)
        )
    }

    private fun navigatePermissionSettingsPage() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //startActivityForResult(intent, PERMISSION_SETTINGS_NAVIGATION)
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (PermissionUtil().requestPermissionForCamera(this, false)) {
                    if (viewModel.isTreeSwitched == true) {
                        viewModel.isTreeSwitched = false
                        validateOpenStoryView()
                    }
                }
            }
        }

    fun switchTree(v: View) {
        viewModel.isTreeSwitched = true
        val isTreeView = viewModel.isTreeView.value
       /* if ((isTreeView == true)*//* && (!(PermissionUtil().requestPermissionForCamera(this, false)))*//*) {
            validateOpenStoryView()
            return
        }*/
        viewModel.tabPos = 0
        viewModel.isTreeView.value = true
    }

    fun switchProfile(v: View) {
        viewModel.isTreeSwitched = false
        val isTreeView = viewModel.isTreeView.value
        /*if ((isTreeView == true)*//* && (!(PermissionUtil().requestPermissionForCamera(this, false)))*//*) {
            validateOpenStoryView()
            return
        }*/
        viewModel.tabPos = 1
        viewModel.isTreeView.value = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (PermissionUtil().requestPermissionForCamera(this, false)) {
            if (viewModel.isTreeSwitched == true) {
                viewModel.isTreeSwitched = false
                validateOpenStoryView()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun openAboutIntent(v: View) {
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        /*val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://memyfolks.com/")
        startActivity(defaultBrowser)*/
        val intent = Intent(this, TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG, Constents.WEB_VIEW_ABOUT)
        startActivity(intent)
    }

    fun openBlogIntent(v: View) {
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        /*val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://blog.memyfolks.com/")
        startActivity(defaultBrowser)*/

        val intent = Intent(this, TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG, Constents.WEB_VIEW_BLOG)
        startActivity(intent)
    }

    fun openFamilyWall(v: View) {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(
            R.id.fragmentContainer,
            FamilyWallFragment(),
            FamilyWallFragment::javaClass.name
        )
        transaction.addToBackStack(null)
        transaction.commit()

        viewModel.tabPos = 2
        binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_select)
        binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
        binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
        binding.notificationImageView.setImageResource(R.drawable.ic_notification_unselect)
        binding.storyTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.footer_bar_txt_color
            )
        )
        binding.familyTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.footer_bar_txt_color
            )
        )
        binding.notificationTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.footer_bar_txt_color
            )
        )
        binding.bubblesTextView.setTextColor(ContextCompat.getColor(this, R.color.app_color))

    }

    fun openTermsIntent(v: View) {
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        val intent = Intent(this, TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG, Constents.WEB_VIEW_TERMS_AND_CONDITION)
        startActivity(intent)
    }

    fun openHelpIntent(v: View) {
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        val intent = Intent(this, TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG, Constents.WEB_VIEW_HELP)
        startActivity(intent)
        /*val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://memyfolks.com/help/")
        startActivity(defaultBrowser)*/
    }

    fun openFeedbackIntent(v: View) {
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        startActivity(Intent(this, FeedbackActivity::class.java))
    }

    fun openGuideIntent(v: View) {
        startActivity(Intent(this, HelpActivity::class.java))
    }

    fun updateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            viewModel.fcmStrKey = task.result
            val prefToken = prefhelper.fetchFCMTokenData()
            // if((!TextUtils.isEmpty(viewModel.fcmStrKey)) && ((TextUtils.isEmpty(prefToken)) || (!prefToken.equals(viewModel.fcmStrKey)))){
            val req =
                FCMTokenUpdateReq((prefhelper.fetchUserData()?.mid).toString(), viewModel.fcmStrKey)
            viewModel.updateFCMToken(req);
            //  }
        })
    }

    fun validateFCMRes(res: CommonResponse?) {
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                prefhelper.saveFCMTokenData(viewModel.fcmStrKey)
            }
        }
    }

    fun checkDeepLink() {
        if (getIntent() != null) {
            val deeplink = getIntent().getStringExtra(Constents.NOTIFICATION_INTENT_EXTRA_DEEPLINK)
            intent.putExtra(Constents.NOTIFICATION_INTENT_EXTRA_DEEPLINK, deeplink)
            if ((!TextUtils.isEmpty(deeplink)) && (deeplink.equals(
                    Constents.DEEPLINK_NOTIFICATION,
                    true
                ))
            ) {
                navigateNotificationScreen(binding.menuIconImageView)
            }
        }
    }

    fun updateSocialMediaIcons() {
        if ((!TextUtils.isEmpty(viewModel.instagramLink.value)) && (URLUtil.isValidUrl(viewModel.instagramLink.value?.trim()))) {
            binding.instagramIcon.setImageResource(R.drawable.ic_instagram)
        } else {
            binding.instagramIcon.setImageResource(R.drawable.ic_instagram_unselect)
        }
        if ((!TextUtils.isEmpty(viewModel.facebookLink.value)) && (URLUtil.isValidUrl(viewModel.facebookLink.value?.trim()))) {
            binding.facebookIcon.setImageResource(R.drawable.ic_facebook)
        } else {
            binding.facebookIcon.setImageResource(R.drawable.ic_facebook_unselect)
        }
        if ((!TextUtils.isEmpty(viewModel.twitterLink.value)) && (URLUtil.isValidUrl(viewModel.twitterLink.value?.trim()))) {
            binding.twitterIcon.setImageResource(R.drawable.ic_twitter)
        } else {
            binding.twitterIcon.setImageResource(R.drawable.ic_twitter_unselect)
        }
        if ((!TextUtils.isEmpty(viewModel.linkedInLink.value)) && (URLUtil.isValidUrl(viewModel.linkedInLink.value?.trim()))) {
            binding.linkedInIcon.setImageResource(R.drawable.ic_linkedin)
        } else {
            binding.linkedInIcon.setImageResource(R.drawable.ic_linkedin_unselect)
        }
    }

    fun validateSocialLinks(v: View) {
        if ((!TextUtils.isEmpty(viewModel.instagramLink.value)) && (!URLUtil.isValidUrl(viewModel.instagramLink.value?.trim()))) {
//            binding.instagramLayout.isErrorEnabled = true
//            binding.instagramLayout.error = getString(R.string.error_invalid_url)
            viewModel.instagramLink.value =
                "https://www.instagram.com/" + (viewModel.instagramLink.value)
        }
        if ((!TextUtils.isEmpty(viewModel.facebookLink.value)) && (!URLUtil.isValidUrl(viewModel.facebookLink.value?.trim()))) {
//            binding.facebookLayout.isErrorEnabled = true
//            binding.facebookLayout.error = getString(R.string.error_invalid_url)
            viewModel.facebookLink.value =
                "https://www.facebook.com/" + viewModel.facebookLink.value
        }
        if ((!TextUtils.isEmpty(viewModel.twitterLink.value)) && (!URLUtil.isValidUrl(viewModel.twitterLink.value?.trim()))) {
//            binding.twitterLayout.isErrorEnabled = true
//            binding.twitterLayout.error = getString(R.string.error_invalid_url)
            viewModel.twitterLink.value = "https://twitter.com/" + viewModel.twitterLink.value
        }
        if ((!TextUtils.isEmpty(viewModel.linkedInLink.value)) && (!URLUtil.isValidUrl(viewModel.linkedInLink.value?.trim()))) {
//            binding.linkedInLayout.isErrorEnabled = true
//            binding.linkedInLayout.error = getString(R.string.error_invalid_url)
            viewModel.linkedInLink.value =
                "https://www.linkedin.com/in/" + viewModel.linkedInLink.value
        }
        if ((TextUtils.isEmpty(viewModel.aboutContent.value))) {
//            binding.aboutLayout.isErrorEnabled = true
//            binding.aboutLayout.error = getString(R.string.error_invalid_about)
        }

        binding.progressFrameLayout.visibility = View.VISIBLE
        viewModel.saveSocialMediaLink(prefhelper.fetchUserData()?.mid)

    }

    fun onFbClick(v: View) {
        if ((!TextUtils.isEmpty(viewModel.facebookLink.value)) && (URLUtil.isValidUrl(viewModel.facebookLink.value?.trim()))) {
            openSocialMediaIntent(viewModel.facebookLink.value)
        } else {
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun onInstaClick(v: View) {
        if ((!TextUtils.isEmpty(viewModel.instagramLink.value)) && (URLUtil.isValidUrl(viewModel.instagramLink.value?.trim()))) {
            openSocialMediaIntent(viewModel.instagramLink.value)
        } else {
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun onTwitterClick(v: View) {
        if ((!TextUtils.isEmpty(viewModel.twitterLink.value)) && (URLUtil.isValidUrl(viewModel.twitterLink.value?.trim()))) {
            openSocialMediaIntent(viewModel.twitterLink.value)
        } else {
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun onLinkedInClick(v: View) {
        if ((!TextUtils.isEmpty(viewModel.linkedInLink.value)) && (URLUtil.isValidUrl(viewModel.linkedInLink.value?.trim()))) {
            openSocialMediaIntent(viewModel.linkedInLink.value)
        } else {
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun openSocialMediaIntent(strUrl: String?) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.setData(Uri.parse(strUrl))
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchMemberRelationShipData(userId: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressFrameLayout.visibility = View.VISIBLE
        }
        viewModel.fetchMemberRelationShip(userId)
    }

    fun fetchTempProfileData(userId: String?){
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressFrameLayout.visibility = View.VISIBLE
        }
        viewModel.fetchProfileForEdit(viewModel.selectedMemberId?.toInt())
    }

    fun closeMemberRelationPopup(v: View) {
        viewModel.showAddRelationView.value = false
    }



    private fun validateMemberRelationRes(res: MemberRelationShipResData) {
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                    if ((TextUtils.isEmpty(viewModel.selectedMemberId)) && (!prefhelper.getGuideSkipClick())) {
                        var canShowGuideView = true
                        val list = res.data as List<RelationSelectionObj>?
                        if ((list != null) && (list.size > 0)) {
                            for (item in list) {
                                if ((item?.is_applicable == false) && (item?.id!! <= 3)) {
                                    canShowGuideView = false
                                }
                            }
                            if (canShowGuideView) {
                                startActivity(
                                    Intent(
                                        this@DashboardActivity,
                                        GuideActivity::class.java
                                    )
                                )
                            }
                        }
                    }else{
                    openAddRelationPopup(res.data as MutableList)
                }
            }
            prefhelper.saveGuideSkipClick(false)
        }
    }

    private fun openAddRelationPopup(data: MutableList<RelationSelectionObj>?) {
        data?.add(RelationSelectionObj("M", "Edit Profile", 0, 1001, true))
        data?.add(RelationSelectionObj("M", "Remove Profile", 0, 1002, true))
        val adapter =
            RelationSelectionAdapter(this@DashboardActivity, data!!, object : AdapterListener {
                override fun updateAction(actionCode: Int, data: Any?) {
                    val item = data as RelationSelectionObj
                    viewModel.selectedMemberAction = actionCode
                    val editProfileData = if(viewModel.profileResForEdit.value != null) (viewModel.profileResForEdit.value?.data) else (null)
                    if(editProfileData != null) {
                        if ((viewModel.selectedMemberAction == 1001) || (viewModel.selectedMemberAction == 1002)) {
                            if ((editProfileData.mid == prefhelper.fetchUserData()?.mid) || (editProfileData.owner_id == prefhelper.fetchUserData()?.mid)) {
                                if (actionCode == 1001) {
                                    val intent = Intent(this@DashboardActivity, AddFamilyActivity::class.java)
                                    intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
                                    intent.putExtra(Constents.FAMILY_MEMBER_EDIT_INTENT_TAG, true)
                                    intent.putExtra(
                                        Constents.FAMILY_MEMBER_ID_INTENT_TAG,
                                        editProfileData.mid
                                    )
                                    intent.putExtra(
                                        Constents.FAMILY_MEMBER_FNAME_INTENT_TAG,
                                        editProfileData.firstname
                                    )
                                    startActivityIntent(intent, false)
                                } else if (actionCode == 1002) {
                                    binding.progressFrameLayout.visibility = View.VISIBLE
                                    viewModel.deleteAccount(editProfileData.mid)
                                }
                            } else {
                                showAlertDialog(
                                    R.id.do_nothing,
                                    getString(R.string.modify_profile_error),
                                    getString(R.string.close_label),
                                    ""
                                )
                            }
                        } else {
                            val intent =
                                Intent(this@DashboardActivity, AddFamilyActivity::class.java)
                            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
                            intent.putExtra(
                                Constents.FAMILY_MEMBER_RELATIONSHIP_ID_INTENT_TAG,
                                actionCode
                            )
                            intent.putExtra(
                                Constents.FAMILY_MEMBER_FNAME_INTENT_TAG,
                                editProfileData.firstname
                            )
                            intent.putExtra(
                                Constents.FAMILY_MEMBER_LNAME_INTENT_TAG,
                                editProfileData.lastname
                            )
                            intent.putExtra(
                                Constents.FAMILY_MEMBER_ID_INTENT_TAG,
                                viewModel.selectedMemberId?.toInt()
                            )
                            intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG, true)
                            intent.putExtra(Constents.FAMILY_MEMBER_GENDER_INTENT_TAG, item.gender)
                            intent.putExtra(Constents.FAMILY_MEMBER_GENDER_NAME_INTENT_TAG, item.name)
                            startActivityIntent(intent, false)
                        }
                    }
                    viewModel.showAddRelationView.value = false
                }

            })
        binding.addMemberPopupRecyclerview.adapter = adapter

        binding.inviteBtn.setOnClickListener {
            if(!TextUtils.isEmpty(viewModel.selectedMemberId)) {
                binding.progressFrameLayout.visibility = View.VISIBLE
                viewModel.inviteFamilyMember(viewModel.selectedMemberId)
            }
            viewModel.showAddRelationView.value = false
        }
        binding.addMemberPopupRecyclerview.postDelayed(Runnable {
            if((prefhelper.fetchUserData()?.mid != viewModel.selectedMemberId?.toInt()) && (viewModel.profileResForEdit.value != null) && (viewModel.profileResForEdit.value?.data != null) && (!TextUtils.isEmpty(viewModel.profileResForEdit.value?.data?.mobile))){
                binding.inviteBtn.visibility = View.VISIBLE
            }else{
                binding.inviteBtn.visibility = View.GONE
            }
            viewModel.showAddRelationView.value = true
            binding.chooseActionTextView.text = viewModel.profileResForEdit.value?.data?.firstname+" - "+getString(R.string.label_choose_action)
            binding.relationPopupLayout.visibility = View.VISIBLE
        }, 1000)
    }

    private fun validateProfileForEditRes(res: ProfileVerificationResObj) {
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                fetchMemberRelationShipData(viewModel.selectedMemberId)
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

    private fun validateDeleteAccountRes(res: CommonResponse){
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    showAlertDialog(
                        R.id.delete_account_res_id,
                        getString(R.string.account_delete_success),
                        getString(R.string.label_ok),
                        ""
                    )
                } else {
                    errorHandler(res)
                }
            } else {
                errorHandler(res)
            }
        }
    }

    private fun errorHandler(res: CommonResponse) {
        var message = ""
        if ((res != null) && (res.errorDetails != null)) {
            message = res.errorDetails.message!!
        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.something_went_wrong)
        }
        showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
    }

    private fun validateInviteApiRes(res : CommonResponse){
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    showAlertDialog(R.id.do_nothing, "Invitation sent successfully", getString(R.string.close_label), "")
                }
            }
        }
    }

}