package com.memy.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
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
        fetchProfileData(false)
    }

    override fun onPause() {
        super.onPause()
    }

    private fun setupUIBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.dashboard_activity)
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener(View.OnClickListener {
            binding.drwayerLay.openDrawer(Gravity.START)
        })
        binding.bottomTempView.visibility = View.GONE
        binding.bottomBarLayout.visibility = View.GONE
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
            }else if(refreshStroy){
                val manager: FragmentManager = supportFragmentManager
                val transaction: FragmentTransaction = manager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, StoryVIewFragment(), StoryVIewFragment::javaClass.name)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            loadProfileImage(viewModel.userData.value?.photo)
        }
    }

    override fun onBackPressed() {
        if (binding.progressInclude.progressBarLayout.visibility == View.GONE) {
            super.onBackPressed()
            finishAffinity()
            System.exit(0)
        }
    }

    private fun setupObservers() {
        viewModel.profileVerificationResObj.observe(this, this::validateProfileRes)
        viewModel.isTreeView.observe(this, { v ->
            fetchProfileData(true)
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
        val intent = Intent(this, NotificationActivity::class.java)
        intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, true)
        intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel?.userData?.value?.mid)
        startActivityIntent(intent, false);
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

    fun switchTreeAndProfile(v:View){
        viewModel.isTreeSwitched = true
        val isTreeView = viewModel.isTreeView.value
        if((isTreeView == true) && (!(PermissionUtil().requestPermissionForCamera(this, false)))){
            validateOpenStoryView()
            return
        }
        viewModel.isTreeView.value = if(viewModel.isTreeView.value == true) (false) else(true)
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
        val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("http://memyfolks.com/")
        startActivity(defaultBrowser)
    }

    fun openBlogIntent(v:View){
        val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://blog.memyfolks.com/")
        startActivity(defaultBrowser)
    }

    fun openTermsIntent(v:View){
        startActivity(Intent(this,TermsAndConditionActivity::class.java))
    }

    fun openHelpIntent(v:View){
        val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse("https://memyfolks.com/help/")
        startActivity(defaultBrowser)
    }
}