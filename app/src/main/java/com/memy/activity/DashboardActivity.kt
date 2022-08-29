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
import com.google.firebase.messaging.FirebaseMessaging
import com.memy.R
import com.memy.databinding.DashboardActivityBinding
import com.memy.fragment.StoryVIewFragment
import com.memy.fragment.TreeViewFragment
import com.memy.pojo.*
import com.memy.utils.Constents
import com.memy.utils.PermissionUtil
import com.memy.viewModel.DashboardViewModel


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
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
        if (id == R.id.storage_camera_permission) {
            navigatePermissionSettingsPage()
        }
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onPause() {
        super.onPause()
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

    private fun fetchProfileData(refreshStroy : Boolean) {
        if ((viewModel.userData.value != null) && (viewModel.userData.value?.mid != null)) {
          //  binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
            viewModel.fetchProfile(viewModel.userData.value?.mid)
            if (viewModel.isTreeView.value == true) {
                val manager: FragmentManager = supportFragmentManager
                val transaction: FragmentTransaction = manager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, TreeViewFragment(), TreeViewFragment::javaClass.name)
                transaction.addToBackStack(null)
                transaction.commit()
                binding.familyImageView.setImageResource(R.drawable.ic_mmf_select)
                binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
                binding.storyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
                binding.familyTextView.setTextColor(ContextCompat.getColor(this,R.color.app_color))
            }else if(refreshStroy){
                val manager: FragmentManager = supportFragmentManager
                val transaction: FragmentTransaction = manager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, StoryVIewFragment(), StoryVIewFragment::javaClass.name)
                transaction.addToBackStack(null)
                transaction.commit()
                binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
                binding.storyImageView.setImageResource(R.drawable.ic_story_select)
                binding.storyTextView.setTextColor(ContextCompat.getColor(this,R.color.app_color))
                binding.familyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
            }
            loadProfileImage(viewModel.userData.value?.photo)
        }
    }

    override fun onBackPressed() {
        if(viewModel.showSocialLinkAddView.value == true){
            viewModel.showSocialLinkAddView.value = false
        }else if (binding.progressInclude.progressBarLayout.visibility == View.GONE) {
            super.onBackPressed()
            finishAffinity()
            System.exit(0)
        }
    }

    private fun setupObservers() {
        viewModel.profileVerificationResObj.observe(this, this::validateProfileRes)
        viewModel.profileSocialLinkUpdateRes.observe(this,this::validateSocialLinkRes)
        viewModel.isTreeView.observe(this, { v ->
            fetchProfileData(true)
        })
        viewModel.updateFcmRes.observe(this,this::validateFCMRes)
        viewModel.instagramLink.observe(this,{
            binding.instagramLayout.isErrorEnabled = false
        })
        viewModel.facebookLink.observe(this,{
            binding.facebookLayout.isErrorEnabled = false
        })
        viewModel.twitterLink.observe(this,{
            binding.twitterLayout.isErrorEnabled = false
        })
        viewModel.linkedInLink.observe(this,{
            binding.linkedInLayout.isErrorEnabled = false
        })
        viewModel.aboutContent.observe(this,{
            binding.aboutLayout.isErrorEnabled = false
        })
    }

    private fun validateProfileRes(res: ProfileVerificationResObj) {
        binding.progressInclude.progressBarLayout.visibility = View.GONE
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
        binding.progressInclude.progressBarLayout.visibility = View.GONE
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
        binding.txtFirstname.text=viewModel.userData.value!!.firstname
        binding.txtMobile.text=viewModel.userData.value!!.mobile

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
        intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
        startActivityIntent(intent, false);
    }
    fun navigateNotificationScreen(v: View) {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        val notificationActivity=NotificationFragment()
        val bundle=Bundle()
        bundle.putBoolean(Constents.OWN_PROFILE_INTENT_TAG, true)
        bundle.putInt(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid!!)
        notificationActivity.arguments=bundle
        transaction.replace(R.id.fragmentContainer,notificationActivity, NotificationFragment::javaClass.name)
        transaction.addToBackStack(null)
        transaction.commit()
        binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_unselect)
        binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
        binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
        binding.notificationImageView.setImageResource(R.drawable.ic_notification_select__1_)
        binding.storyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.familyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.notificationTextView.setTextColor(ContextCompat.getColor(this,R.color.app_color))

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

    fun validateOpenStoryView(){
        if(PermissionUtil().requestPermissionForCamera(this, false)){
            viewModel.isTreeView.value = false
        }else if (PermissionUtil().isCameraStoragePermissionUnderDontAsk(this)) {
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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (PermissionUtil().requestPermissionForCamera(this, false)) {
                if(viewModel.isTreeSwitched == true){
                    viewModel.isTreeSwitched = false
                    validateOpenStoryView()
                }
            }
        }
    }

    fun switchTree(v:View){
        viewModel.isTreeSwitched = true
        val isTreeView = viewModel.isTreeView.value
        if((isTreeView == true) && (!(PermissionUtil().requestPermissionForCamera(this, false)))){
            validateOpenStoryView()
            return
        }
        viewModel.isTreeView.value = true
    }

    fun switchProfile(v:View){
        viewModel.isTreeSwitched = false
        val isTreeView = viewModel.isTreeView.value
        if((isTreeView == true) && (!(PermissionUtil().requestPermissionForCamera(this, false)))){
            validateOpenStoryView()
            return
        }
        viewModel.isTreeView.value = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (PermissionUtil().requestPermissionForCamera(this, false)) {
            if(viewModel.isTreeSwitched == true){
                viewModel.isTreeSwitched = false
                validateOpenStoryView()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun openAboutIntent(v:View){
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        /*val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://memyfolks.com/")
        startActivity(defaultBrowser)*/
        val intent = Intent(this,TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG,Constents.WEB_VIEW_ABOUT)
        startActivity(intent)
    }

    fun openBlogIntent(v:View){
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        /*val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://blog.memyfolks.com/")
        startActivity(defaultBrowser)*/

        val intent = Intent(this,TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG,Constents.WEB_VIEW_BLOG)
        startActivity(intent)
    }
    fun openFamilyWall(v:View){
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, FamilyWallFragment(), FamilyWallFragment::javaClass.name)
        transaction.addToBackStack(null)
        transaction.commit()
        binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_select)
        binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
        binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
        binding.notificationImageView.setImageResource(R.drawable.ic_notification_unselect)
        binding.storyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.familyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.notificationTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.app_color))

    }
    fun openTermsIntent(v:View){
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        val intent = Intent(this,TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG,Constents.WEB_VIEW_TERMS_AND_CONDITION)
        startActivity(intent)
    }

    fun openHelpIntent(v:View){
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        val intent = Intent(this,TermsAndConditionActivity::class.java)
        intent.putExtra(Constents.WEB_VIEW_INTENT_TAG,Constents.WEB_VIEW_HELP)
        startActivity(intent)
        /*val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://memyfolks.com/help/")
        startActivity(defaultBrowser)*/
    }

    fun openFeedbackIntent(v:View){
        binding.drwayerLay.closeDrawer(Gravity.LEFT)
        startActivity(Intent(this,FeedbackActivity::class.java))
    }

    fun updateFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            viewModel.fcmStrKey = task.result
            val prefToken = prefhelper.fetchFCMTokenData()
           // if((!TextUtils.isEmpty(viewModel.fcmStrKey)) && ((TextUtils.isEmpty(prefToken)) || (!prefToken.equals(viewModel.fcmStrKey)))){
                val req = FCMTokenUpdateReq((prefhelper.fetchUserData()?.mid).toString(),viewModel.fcmStrKey)
                viewModel.updateFCMToken(req);
          //  }
        })
    }

    fun validateFCMRes(res : CommonResponse?){
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                prefhelper.saveFCMTokenData(viewModel.fcmStrKey)
            }
        }
    }

    fun checkDeepLink(){
        if(getIntent() != null) {
            val deeplink = getIntent().getStringExtra(Constents.NOTIFICATION_INTENT_EXTRA_DEEPLINK)
            intent.putExtra(Constents.NOTIFICATION_INTENT_EXTRA_DEEPLINK, deeplink)
            if((!TextUtils.isEmpty(deeplink)) && (deeplink.equals(Constents.DEEPLINK_NOTIFICATION,true))){
                navigateNotificationScreen(binding.menuIconImageView)
            }
        }
    }

    fun updateSocialMediaIcons(){
        if((!TextUtils.isEmpty(viewModel.instagramLink.value)) && (URLUtil.isValidUrl(viewModel.instagramLink.value?.trim()))){
            binding.instagramIcon.setImageResource(R.drawable.ic_instagram)
        }else{
            binding.instagramIcon.setImageResource(R.drawable.ic_instagram_unselect)
        }
        if((!TextUtils.isEmpty(viewModel.facebookLink.value)) && (URLUtil.isValidUrl(viewModel.facebookLink.value?.trim()))){
            binding.facebookIcon.setImageResource(R.drawable.ic_facebook)
        }else{
            binding.facebookIcon.setImageResource(R.drawable.ic_facebook_unselect)
        }
        if((!TextUtils.isEmpty(viewModel.twitterLink.value)) && (URLUtil.isValidUrl(viewModel.twitterLink.value?.trim()))){
            binding.twitterIcon.setImageResource(R.drawable.ic_twitter)
        }else{
            binding.twitterIcon.setImageResource(R.drawable.ic_twitter_unselect)
        }
        if((!TextUtils.isEmpty(viewModel.linkedInLink.value)) && (URLUtil.isValidUrl(viewModel.linkedInLink.value?.trim()))){
            binding.linkedInIcon.setImageResource(R.drawable.ic_linkedin)
        }else{
            binding.linkedInIcon.setImageResource(R.drawable.ic_linkedin_unselect)
        }
    }

    fun validateSocialLinks(v:View){
        if((!TextUtils.isEmpty(viewModel.instagramLink.value)) && (!URLUtil.isValidUrl(viewModel.instagramLink.value?.trim()))){
            binding.instagramLayout.isErrorEnabled = true
            binding.instagramLayout.error = getString(R.string.error_invalid_url)
        }else if((!TextUtils.isEmpty(viewModel.facebookLink.value)) && (!URLUtil.isValidUrl(viewModel.facebookLink.value?.trim()))){
            binding.facebookLayout.isErrorEnabled = true
            binding.facebookLayout.error = getString(R.string.error_invalid_url)
        }else if((!TextUtils.isEmpty(viewModel.twitterLink.value)) && (!URLUtil.isValidUrl(viewModel.twitterLink.value?.trim()))){
            binding.twitterLayout.isErrorEnabled = true
            binding.twitterLayout.error = getString(R.string.error_invalid_url)
        }else if((!TextUtils.isEmpty(viewModel.linkedInLink.value)) && (!URLUtil.isValidUrl(viewModel.linkedInLink.value?.trim()))){
            binding.linkedInLayout.isErrorEnabled = true
            binding.linkedInLayout.error = getString(R.string.error_invalid_url)
        }else if((TextUtils.isEmpty(viewModel.aboutContent.value))){
            binding.aboutLayout.isErrorEnabled = true
            binding.aboutLayout.error = getString(R.string.error_invalid_about)
        }else{
            binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
            viewModel.saveSocialMediaLink(prefhelper.fetchUserData()?.mid)
        }
    }

    fun onFbClick(v:View){
        if((!TextUtils.isEmpty(viewModel.facebookLink.value)) && (URLUtil.isValidUrl(viewModel.facebookLink.value?.trim()))){
            openSocialMediaIntent(viewModel.facebookLink.value)
        }else{
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun onInstaClick(v:View){
        if((!TextUtils.isEmpty(viewModel.instagramLink.value)) && (URLUtil.isValidUrl(viewModel.instagramLink.value?.trim()))){
            openSocialMediaIntent(viewModel.instagramLink.value)
        }else{
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun onTwitterClick(v:View){
        if((!TextUtils.isEmpty(viewModel.twitterLink.value)) && (URLUtil.isValidUrl(viewModel.twitterLink.value?.trim()))){
            openSocialMediaIntent(viewModel.twitterLink.value)
        }else{
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun onLinkedInClick(v:View){
        if((!TextUtils.isEmpty(viewModel.linkedInLink.value)) && (URLUtil.isValidUrl(viewModel.linkedInLink.value?.trim()))){
            openSocialMediaIntent(viewModel.linkedInLink.value)
        }else{
            viewModel.showSocialLinkAddView.value = true
        }
    }

    fun openSocialMediaIntent(strUrl : String?){
        try{
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.setData(Uri.parse(strUrl))
            startActivity(browserIntent)
        }catch(e:Exception){
            e.printStackTrace()
        }
    }

}