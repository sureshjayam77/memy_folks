package com.memy.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import android.widget.Toast
import androidx.databinding.adapters.SeekBarBindingAdapter
import com.memy.MemyApplication
import com.memy.utils.PermissionUtil
import java.io.File
import android.webkit.MimeTypeMap
import com.memy.dialog.CommunicationDialog
import com.memy.listener.DialogClickCallBack
import com.memy.pojo.CommunicationDialogData


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
        var moshi: Moshi
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
        fun downloadFile(url: String?) {
            Log.d("downloadFile",""+url)
            if (!TextUtils.isEmpty(url)){
                viewModel?.downloadURL = url ?: ""
                viewModel?.isDownloadFileCick = true
               // if(PermissionUtil().requestPermissionForStorage(mContext,true)) {
                    downloadTreeSS()
               // }
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

        fun downloadTreeSS(){
            if(MemyApplication.instance?.manager == null) {
                MemyApplication.instance?.manager =
                    mContext?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            }
            /*val uri =
                Uri.parse(viewModel?.downloadURL ?: "")
            val request: DownloadManager.Request = DownloadManager.Request(uri)*/


            val url = viewModel?.downloadURL ?: ""
            var fileName = url.substring(url.lastIndexOf('/') + 1)
            fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1)
            val extension: String =
                url.substring(url.lastIndexOf("."))

            val request = DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Visibility of the download Notification
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"MeMyFolks_Family_Tree$extension") // Uri of the destination file
                .setTitle("MeMyFolks_Family_Tree$extension") // Title of the Download Notification
                .setDescription("Downloading") // Description of the Download Notification
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true) // Set if download is allowed on roaming network


            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            MemyApplication.downloadFileUniqueId = MemyApplication.instance?.manager?.enqueue(request) ?: 0
        }


    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(onDownloadComplete)
    }

    protected val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                //Fetching the download id received with the broadcast
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (MemyApplication.downloadFileUniqueId == id) {
                    // Toast.makeText(requireActivity().applicationContext, "Download Completed", Toast.LENGTH_SHORT).show()
                    loadProfileTree()
                    showDownloadSuccessPopup()
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }


    private fun showDownloadSuccessPopup(){
        if(isAdded) {
            showAlertDialog(
                (requireActivity() as AppBaseActivity),
                R.id.do_nothing,
                "MeMyFolks Family Tree downloaded successfully",
                getString(R.string.label_ok),
                "",
                object : DialogClickCallBack{
                    override fun dialogPositiveCallBack(id: Int?) {
                        dismissAlertDialog()
                        openDownloadFile()
                    }

                    override fun dialogNegativeCallBack() {
                        dismissAlertDialog()
                    }

                }
            )
        }
    }

    fun openDownloadFile(){
        try{
            if(isAdded) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(
                    MemyApplication.instance?.manager?.getUriForDownloadedFile(
                        MemyApplication.downloadFileUniqueId
                    ), "*/*"
                )
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                (requireActivity() as AppBaseActivity).startActivity(intent)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }



}