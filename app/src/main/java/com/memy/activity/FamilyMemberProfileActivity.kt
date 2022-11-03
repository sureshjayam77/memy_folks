package com.memy.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.webkit.URLUtil
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.memy.R
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
import androidx.fragment.app.Fragment
import com.memy.fragment.ShareFragment


class FamilyMemberProfileActivity : AppBaseActivity() {
    lateinit var binding: DashboardActivityBinding
    lateinit var viewModel: DashboardViewModel
    private var isInitialCall = true
    var accessList = emptyList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIBinding()
        setupViewModel()
        setupObservers()
        PermissionUtil().initRequestPermissionForCamera(this, true)
        fetchProfileData(false)
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
        }else if (id == R.id.member_admin_access_id) {
            binding.progressFrameLayout.visibility = View.VISIBLE
            viewModel.updateAdminAccess()
        }
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    override fun onStart() {
        super.onStart()
        if(viewModel.tabPos == 0){
            isInitialCall = true
        }

        fetchProfileData(false)
    }

    override fun onPause() {
        super.onPause()
    }

    private fun setupUIBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.dashboard_activity)
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        binding.backIconImageView.setImageResource(R.drawable.ic_back_arrow)
        binding.bottomBarLayout.visibility = View.VISIBLE
        binding.titleTextView.text = getString(R.string.label_profile)
        binding.menuIconImageView.visibility = View.GONE
        binding.bottomTempView.visibility = View.VISIBLE
        binding.editTextView.visibility = View.GONE
        binding.guideImageView.visibility = View.GONE
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.showProfile = intent?.getBooleanExtra(Constents.SHOW_PROFILE_INTENT_TAG, false);
        if (viewModel.showProfile == true) {
            switchProfile(null)
        }else{
            switchTree(null)
        }
    }

    private fun fetchProfileData(refreshStroy: Boolean) {
        var userId = intent?.getIntExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, -1)

        if (userId!! > -1) {
            binding.progressFrameLayout.visibility = View.VISIBLE
            viewModel.fetchProfile(userId)
        }
    }

    override fun onBackPressed() {
        /*if (binding.progressInclude.progressBarLayout.visibility == View.GONE) {
            super.onBackPressed()
            finishAffinity()
            System.exit(0)
        }*/
        finish()
    }

    private fun setupObservers() {
        viewModel.profileVerificationResObj.observe(this, this::validateProfileRes)
        viewModel.memberRelationData.observe(this,this::validateMemberRelationRes)
        viewModel.profileResForEdit.observe(this, this::validateProfileForEditRes)
        viewModel.deleteAccountRes.observe(this,this::validateDeleteAccountRes)
        viewModel.inviteCommonResData.observe(this,this::validateInviteApiRes)
        viewModel.updateAdminRes.observe(this, this::validateAdminAccessRes)
        viewModel.isTreeView.observe(this, { v ->
            fetchProfileData(true)
        })
    }

    private fun validateProfileRes(res: ProfileVerificationResObj) {
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                viewModel.userData.value = res.data
                binding.callTextView.visibility = if ((viewModel.userData.value?.mid != prefhelper.fetchUserData()?.mid) && (!TextUtils.isEmpty(
                        viewModel.userData.value?.mobile
                    ))
                ) (View.VISIBLE) else (View.GONE)
//                val f: Fragment? = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//                if((f== null) || ((!( f is FamilyWallFragment)) && (!( f is NotificationFragment)))){
                       if(isInitialCall) {
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
                               viewModel.actionTitle.value = getString(R.string.label_family_tree)
                               binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_unselect)
                               binding.shareTabImageView.setImageResource(R.drawable.ic_notification_unselect)
                               binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
                               binding.shareTabTextView.setTextColor(ContextCompat.getColor(this, R.color.footer_bar_txt_color))

                               binding.familyImageView.setImageResource(R.drawable.ic_mmf_select)
                               binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
                               binding.storyTextView.setTextColor(
                                   ContextCompat.getColor(
                                       this,
                                       R.color.footer_bar_txt_color
                                   )
                               )
                               binding.familyTextView.setTextColor(
                                   ContextCompat.getColor(
                                       this,
                                       R.color.app_color
                                   )
                               )
                           } else {
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
                               viewModel.actionTitle.value = getString(R.string.label_story)
                               binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_unselect)
                               binding.shareTabImageView.setImageResource(R.drawable.ic_share_unselect)
                               binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
                               binding.shareTabTextView.setTextColor(ContextCompat.getColor(this, R.color.footer_bar_txt_color))


                               binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
                               binding.storyImageView.setImageResource(R.drawable.ic_story_select)
                               binding.storyTextView.setTextColor(
                                   ContextCompat.getColor(
                                       this,
                                       R.color.app_color
                                   )
                               )
                               binding.familyTextView.setTextColor(
                                   ContextCompat.getColor(
                                       this,
                                       R.color.footer_bar_txt_color
                                   )
                               )
                           }
                       }
                isInitialCall = false
                   // }
                loadProfileImage(viewModel.userData.value?.photo)
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
              //  showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
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

        viewModel.instagramLink.value = viewModel.userData.value?.instagram_link
        viewModel.facebookLink.value = viewModel.userData.value?.facebook_link
        viewModel.twitterLink.value = viewModel.userData.value?.twitter_link
        viewModel.linkedInLink.value = viewModel.userData.value?.linkedin_link
        viewModel.aboutContent.value = viewModel.userData.value?.about_me
        updateSocialMediaIcons()
    }
    fun openFamilyWall(v:View){
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, FamilyWallFragment(), FamilyWallFragment::javaClass.name)
        transaction.addToBackStack(null)
        transaction.commit()
        viewModel.tabPos = 2
        viewModel.actionTitle.value = getString(R.string.label_bubbles)
        binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_select)
        binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
        binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
        binding.shareTabImageView.setImageResource(R.drawable.ic_share_unselect)
        binding.storyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.familyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.shareTabTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.app_color))

    }
    fun navigateAddFamily(v: View) {
        val intent = Intent(this, AddFamilyActivity::class.java)
        intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
        intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
        intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG, true)
        startActivityIntent(intent, false)
    }

    fun navigateProfileScreen(v: View) {
        if ((viewModel?.userData?.value?.mid == prefhelper.fetchUserData()?.mid) || (viewModel?.userData?.value?.owner_id == prefhelper.fetchUserData()?.mid)) {
            val intent = Intent(this, AddFamilyActivity::class.java)
            intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
            intent.putExtra(Constents.FAMILY_MEMBER_EDIT_INTENT_TAG,true)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
            intent.putExtra(Constents.FAMILY_MEMBER_FNAME_INTENT_TAG, viewModel?.userData?.value?.firstname)
            startActivityIntent(intent, false)
        }else{
            navigateBottomProfileScreen()
        }
    }

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

    fun switchTree(v:View?){
        isInitialCall = true
        viewModel.isTreeSwitched = true
        val isTreeView = viewModel.isTreeView.value
        /*if((isTreeView == true)*//* && (!(PermissionUtil().requestPermissionForCamera(this, false)))*//*){
            validateOpenStoryView()
            return
        }*/
        viewModel.tabPos = 0
        viewModel.isTreeView.value = true
    }

    fun switchProfile(v:View?){
        isInitialCall = true
        viewModel.isTreeSwitched = false
        /*val isTreeView = viewModel.isTreeView.value
        if((isTreeView == true) *//*&& (!(PermissionUtil().requestPermissionForCamera(this, false)))*//*){
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


    fun onFbClick(v:View){
        if((!TextUtils.isEmpty(viewModel.facebookLink.value)) && (URLUtil.isValidUrl(viewModel.facebookLink.value?.trim()))){
            openSocialMediaIntent(viewModel.facebookLink.value)
        }
    }

    fun onInstaClick(v:View){
        if((!TextUtils.isEmpty(viewModel.instagramLink.value)) && (URLUtil.isValidUrl(viewModel.instagramLink.value?.trim()))){
            openSocialMediaIntent(viewModel.instagramLink.value)
        }
    }

    fun onTwitterClick(v:View){
        if((!TextUtils.isEmpty(viewModel.twitterLink.value)) && (URLUtil.isValidUrl(viewModel.twitterLink.value?.trim()))){
            openSocialMediaIntent(viewModel.twitterLink.value)
        }
    }

    fun onLinkedInClick(v:View){
        if((!TextUtils.isEmpty(viewModel.linkedInLink.value)) && (URLUtil.isValidUrl(viewModel.linkedInLink.value?.trim()))){
            openSocialMediaIntent(viewModel.linkedInLink.value)
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





    fun navigateShareScreen(v: View) {

        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        val notificationActivity=ShareFragment()
        val bundle=Bundle()
        bundle.putBoolean(Constents.OWN_PROFILE_INTENT_TAG, true)
        bundle.putInt(Constents.FAMILY_MEMBER_ID_INTENT_TAG, prefhelper.fetchUserData()?.mid!!)
        notificationActivity.arguments=bundle
        transaction.replace(R.id.fragmentContainer, notificationActivity, NotificationFragment::javaClass.name)
        transaction.addToBackStack(null)
        transaction.commit()
        viewModel.actionTitle.value = getString(R.string.label_share)
        binding.bubblesImageView.setImageResource(R.drawable.ic_bubbles_unselect)
        binding.familyImageView.setImageResource(R.drawable.ic_mmf_unselect)
        binding.storyImageView.setImageResource(R.drawable.ic_story_unselect)
        binding.shareTabImageView.setImageResource(R.drawable.ic_share_select)
        binding.storyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.familyTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.bubblesTextView.setTextColor(ContextCompat.getColor(this,R.color.footer_bar_txt_color))
        binding.shareTabTextView.setTextColor(ContextCompat.getColor(this,R.color.app_color))
    }
    fun navigateBottomProfileScreen() {
        val intent = Intent(this, AddFamilyActivity::class.java)
        intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
        intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, prefhelper.fetchUserData()?.mid)
        startActivityIntent(intent, false);
    }


    fun fetchMemberRelationShipData(userId : String?){
        viewModel.fetchMemberRelationShip(userId)
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.showProgressBar.value = true
            binding.progressFrameLayout.visibility = View.VISIBLE
        }
    }

    fun closeMemberRelationPopup(v : View){
        viewModel.showAddRelationView.value = false
    }


    private fun validateMemberRelationRes(res : MemberRelationShipResData){
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                openAddRelationPopup(res.data as MutableList)
            }
        }
    }

    private fun openAddRelationPopup(data : MutableList<RelationSelectionObj>?){
        data?.add(RelationSelectionObj("M","Edit Profile",0,1001,true))
        data?.add(RelationSelectionObj("M","Remove Profile",0,1002,true))
        if(prefhelper?.fetchUserData()?.mid == viewModel.profileResForEdit.value?.data?.owner_id) {
            data?.add(RelationSelectionObj("M", "Admin Access", 0, 1003, true))
        }
        val adapter = RelationSelectionAdapter(this@FamilyMemberProfileActivity,data!!,object :
            AdapterListener {
            override fun updateAction(actionCode: Int, data: Any?) {
                val item = data as RelationSelectionObj
                viewModel.selectedMemberAction = actionCode
                /*if (actionCode == 1001) {
                    viewModel.fetchProfileForEdit(viewModel.selectedMemberId?.toInt())
                } else if (actionCode == 1002) {
                    viewModel.fetchProfileForEdit(viewModel.selectedMemberId?.toInt())
                }else {
                    val intent = Intent(this@FamilyMemberProfileActivity, AddFamilyActivity::class.java)
                    intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
                    intent.putExtra(Constents.FAMILY_MEMBER_RELATIONSHIP_ID_INTENT_TAG, actionCode)
                    intent.putExtra(
                        Constents.FAMILY_MEMBER_ID_INTENT_TAG,
                        viewModel.selectedMemberId?.toInt()
                    )
                    intent.putExtra(Constents.FAMILY_MEMBER_INTENT_TAG, true)
                    intent.putExtra(Constents.FAMILY_MEMBER_GENDER_INTENT_TAG,item.gender )
                    startActivityIntent(intent, false)
                }*/

                val editProfileData = if(viewModel.profileResForEdit.value != null) (viewModel.profileResForEdit.value?.data) else (null)
                if(editProfileData != null) {
                    if ((viewModel.selectedMemberAction == 1001) || (viewModel.selectedMemberAction == 1002) || (viewModel.selectedMemberAction == 1003)) {
                        if ((editProfileData.mid == prefhelper.fetchUserData()?.mid) || (editProfileData.owner_id == prefhelper.fetchUserData()?.mid) || ((prefhelper.fetchUserData()?.can_access?.contains(editProfileData.mid.toString()))?: false)) {
                            if (actionCode == 1001) {
                                val intent = Intent(this@FamilyMemberProfileActivity, AddFamilyActivity::class.java)
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
                            } else if (actionCode == 1003) {
                                accessList = editProfileData.can_access ?: emptyList<String>()
                                showAlertDialog(
                                    R.id.member_admin_access_id,
                                    if ((accessList == null) || (accessList.size == 0)) (String.format(getString(R.string.give_admin_access_msg),editProfileData?.firstname)) else (String.format(getString(R.string.remove_admin_access_msg),editProfileData?.firstname)),
                                    getString(R.string.label_ok),
                                    getString(R.string.label_cancel)
                                )
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
                            Intent(this@FamilyMemberProfileActivity, AddFamilyActivity::class.java)
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
            val editProfileData =
                if (viewModel.profileResForEdit.value != null) (viewModel.profileResForEdit.value?.data) else (null)

            if (editProfileData != null) {
                if ((!TextUtils.isEmpty(viewModel.selectedMemberId)) && (!TextUtils.isEmpty(
                        editProfileData?.mobile
                    ))
                ) {
                    binding.progressFrameLayout.visibility = View.VISIBLE
                    viewModel.inviteFamilyMember(viewModel.selectedMemberId)
                } else {
                    val intent =
                        Intent(this@FamilyMemberProfileActivity, AddFamilyActivity::class.java)
                    intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
                    intent.putExtra(Constents.FAMILY_MEMBER_EDIT_INTENT_TAG, true)
                    intent.putExtra(
                        Constents.FAMILY_MEMBER_ID_INTENT_TAG,
                        editProfileData?.mid
                    )
                    intent.putExtra(
                        Constents.FAMILY_MEMBER_FNAME_INTENT_TAG,
                        editProfileData?.firstname
                    )
                    intent.putExtra(
                        Constents.FAMILY_MEMBER_INVITE_INTENT_TAG,
                        true
                    )

                    startActivityIntent(intent, false)
                }
            }
            viewModel.showAddRelationView.value = false
        }


        binding.addMemberPopupRecyclerview.postDelayed(Runnable {
        val editProfileData =
            if (viewModel.profileResForEdit.value != null) (viewModel.profileResForEdit.value?.data) else (null)
        if ((editProfileData != null) && ((prefhelper.fetchUserData()?.mid != viewModel.selectedMemberId?.toInt())) && ((editProfileData.owner_id == prefhelper.fetchUserData()?.mid) || ((prefhelper.fetchUserData()?.can_access?.contains(editProfileData.mid.toString()))?: false))) {
            binding.inviteBtn.visibility = View.VISIBLE
                binding.inviteBtn.text = String.format(getString(R.string.label_invite_with_fname),viewModel.profileResForEdit.value?.data?.firstname)
            }else{
                binding.inviteBtn.visibility = View.GONE
            }

            viewModel.showAddRelationView.value = true
            binding.chooseActionTextView.text = viewModel.profileResForEdit.value?.data?.firstname+" - "+getString(R.string.label_choose_action)
            binding.relationPopupLayout.visibility = View.VISIBLE
        },1000)
    }

    private fun validateProfileForEditRes(res: ProfileVerificationResObj) {
        binding.progressFrameLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                fetchMemberRelationShipData(viewModel.selectedMemberId)
                /*if ((res.data.mid == prefhelper.fetchUserData()?.mid) || (res.data.owner_id == prefhelper.fetchUserData()?.mid)) {
                    if(viewModel.selectedMemberAction == 1001){
                        val intent = Intent(this, AddFamilyActivity::class.java)
                        intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
                        intent.putExtra(Constents.FAMILY_MEMBER_EDIT_INTENT_TAG,true)
                        intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, res.data.mid)
                        intent.putExtra(Constents.FAMILY_MEMBER_NAME_INTENT_TAG, res.data.firstname)
                        startActivityIntent(intent, false)
                    }else if(viewModel.selectedMemberAction == 1002){
                        binding.progressFrameLayout.visibility = View.VISIBLE
                        viewModel.deleteAccount(res.data.mid)
                    }
                }else{
                    showAlertDialog(R.id.do_nothing, getString(R.string.modify_profile_error), getString(R.string.close_label), "")
                }*/
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

    fun fetchTempProfileData(userId: String?){
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressFrameLayout.visibility = View.VISIBLE
        }
        viewModel.fetchProfileForEdit(viewModel.selectedMemberId?.toInt())
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
                    showAlertDialog(
                        R.id.do_nothing,
                        getString(R.string.invitation_sent_success_fully),
                        getString(R.string.label_ok),
                        ""
                    )
                }
            }
        }
    }
    private fun validateAdminAccessRes(res: CommonResponse) {
        binding.progressFrameLayout.visibility = View.GONE
        if ((res != null) && (res.statusCode == 200) && (res?.data != null)) {
            showAlertDialog(
                R.id.do_nothing,
                getString(R.string.success_admin_access_msg),
                getString(R.string.label_ok),
                ""
            )
        }
    }

}