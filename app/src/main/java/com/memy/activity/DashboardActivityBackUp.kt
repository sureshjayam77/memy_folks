package com.memy.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.memy.R
import com.memy.adapter.StoryMediaFeedListAdapter
import com.memy.api.BaseRepository
import com.memy.databinding.DashboardActivityBinding
import com.memy.utils.Constents
import com.memy.viewModel.DashboardViewModel
import androidx.recyclerview.widget.PagerSnapHelper

import androidx.recyclerview.widget.SnapHelper
import com.memy.databinding.DashboardActivityBackupBinding
import com.memy.pojo.*
import com.squareup.moshi.Moshi


class DashboardActivityBackUp : AppBaseActivity() {
    lateinit var binding: DashboardActivityBackupBinding
    lateinit var viewModel: DashboardViewModel
    var feedListAdapter: StoryMediaFeedListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIBinding()
        setupViewModel()
        setupObservers()
        setupFeedListAdapter()
        initPagination()
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
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
        pauseMediaPlayer();
    }

    private fun setupUIBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.dashboard_activity_backup)
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        WebView.setWebContentsDebuggingEnabled(true);
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.addJavascriptInterface(WebAppInterface(this, binding.webview), "Android")

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
        pauseMediaPlayer()
        if ((viewModel.userData.value != null) && (viewModel.userData.value?.mid != null)) {
            binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
            viewModel.fetchProfile(viewModel.userData.value?.mid)
            if (viewModel.isTreeView.value == true) {
                loadProfileTree()
            }else if(refreshStroy){
                loadStoryTree()
              //  fetchStory(true,viewModel.pageNumber,viewModel.feedLimit)
            }
            loadProfileImage(viewModel.userData.value?.photo)
        }
    }

    private fun fetchStory(isReset: Boolean,pageNumber:Int,itemLimit : Int) {
        if (isReset) {
            resetFeedRecyclerView()
            binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
        }
        viewModel.isLoadingProfilePage = true
        viewModel.fetchAllStory(viewModel.userData.value?.mid,0,pageNumber,itemLimit)
    }

    private fun loadProfileTree() {
        binding.webview.loadUrl(BaseRepository.BASE_URL + "api/familytreeForApp/?userid=${viewModel.userData.value?.mid}&owner_id=${prefhelper.fetchUserData()?.mid}&apikey=${BaseRepository.APP_KEY_VALUE}")
    }
    private fun loadStoryTree() {
        val storyURL = BaseRepository.BASE_URL + "api/story_mobile_view/?userid=${viewModel.userData.value?.mid}&owner_id=${prefhelper.fetchUserData()?.mid}&apikey=${BaseRepository.APP_KEY_VALUE}"
        binding.storyWebView.loadUrl(storyURL)
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
           // binding.addStory.visibility = if (v == true) (View.GONE) else (View.VISIBLE)
            fetchProfileData(true)
        })
        viewModel.storyListRes.observe(this, this::validateStoryListRes)
    }

    private fun validateProfileRes(res: ProfileVerificationResObj) {
        binding.progressInclude.progressBarLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                viewModel.userData.value = res.data
                prefhelper.saveUserData(res.data)
                var url = binding.webview.url

                // if(TextUtils.isEmpty(url)){
                loadProfileTree()
                //  }
                loadProfileImage(viewModel.userData.value?.photo)
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message ?: ""
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
            val intent = Intent(mContext, ViewProfileActivity::class.java)
            intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, user?.toInt())
            intent.putExtra(Constents.SHOW_PROFILE_INTENT_TAG, true)
            (mContext as AppBaseActivity).startActivityIntent(intent, false)
        }

        @JavascriptInterface
        fun viewFamilyTree(user: String?) {
            val intent = Intent(mContext, ViewProfileActivity::class.java)
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
        val popup = PopupMenu(this, v)
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu())
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                if (item != null) {
                    when (item.itemId) {
                        R.id.logout -> {
                            prefhelper.clearPref()
                            val intent = Intent(this@DashboardActivityBackUp, SignInActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivityIntent(intent, true)
                        }
                    }
                }
                return true
            }
        })
        popup.show()
    }

    fun navigateAddStoryScreen(v: View) {
        if (viewModel?.userData?.value?.mid == prefhelper.fetchUserData()?.mid) {
            val intent = Intent(this, AddStoryActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                // val data: Intent? = result.data
                //doSomeOperations()
                viewModel.isTreeView.value == false
                fetchStory(true,viewModel.pageNumber,viewModel.feedLimit)
            }
        }

    fun validateStoryListRes(res: StoryListRes) {
        binding.progressInclude.progressBarLayout.visibility = View.GONE
        viewModel.isLoadingProfilePage = false
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                /*if ((viewModel.storyListData != null) && (viewModel.storyListData.size > 0) && (viewModel.storyListData.size > )) {
                    for ((pos, obj) in (viewModel.storyListData).withIndex()) {
                        for ((pos1, obj1) in res.data.withIndex()) {
                            if (obj.id == obj1.id) {
                                obj1.also { viewModel.storyListData[pos] = it }
                            }
                        }
                    }
                }*/
                viewModel.storyListData.addAll(res.data)
                setupFeedListAdapter()
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message ?: ""
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
            }
        }
    }

    fun setupFeedListAdapter() {
        if (feedListAdapter == null) {
            feedListAdapter = StoryMediaFeedListAdapter(this, viewModel.storyListData)
            val layoutManager = LinearLayoutManager(this)
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
            binding.feedRecyclerView.layoutManager = layoutManager
            binding.feedRecyclerView.adapter = feedListAdapter
            /*val mSnapHelper: SnapHelper = PagerSnapHelper()
            mSnapHelper.attachToRecyclerView(binding.feedRecyclerView)*/
        } else {
            feedListAdapter?.updateFeeds(viewModel.storyListData)
        }
        feedListAdapter?.notifyDataSetChanged()
    }

    private fun resetFeedRecyclerView() {
        viewModel.pageNumber = 1
        viewModel.totalItemCount = 0
        viewModel.lastVisibleItem = 0
        if (feedListAdapter != null) {
            viewModel.storyListData.clear()
            feedListAdapter?.notifyDataSetChanged()
        }
    }

    private fun initPagination(){
        binding.feedRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int, dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.totalItemCount = binding.feedRecyclerView.layoutManager?.getItemCount()!!
                viewModel.lastVisibleItem = ((binding.feedRecyclerView.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if (!viewModel.isLoadingProfilePage
                    && viewModel.totalItemCount <= viewModel.lastVisibleItem + viewModel.visibleThreshold
                ) {
                    fetchStory(false,viewModel.pageNumber,viewModel.feedLimit)
                }
            }
        })
    }

    private fun pauseMediaPlayer(){
        if(feedListAdapter != null) {
            val layoutManager = binding.feedRecyclerView.getLayoutManager() as LinearLayoutManager
            if (layoutManager != null) {
                for (pos in 0 until layoutManager.childCount) {
                    val viewHolder = binding.feedRecyclerView.findViewHolderForAdapterPosition(pos)
                    feedListAdapter?.pauseMedia(viewHolder)
                }
            }
        }
    }
}