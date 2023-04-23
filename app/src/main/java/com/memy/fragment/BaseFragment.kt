package com.memy.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.memy.dialog.CommunicationDialog
import com.memy.listener.DialogClickCallBack
import com.memy.pojo.CommunicationDialogData
import com.memy.utils.PreferenceHelper

abstract class BaseFragment : Fragment() {
    protected lateinit var prefhelper : PreferenceHelper
    private var alertDialog : CommunicationDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefhelper = PreferenceHelper().getInstance(requireActivity())
    }

    fun showAlertDialog(activity : AppCompatActivity,dialogId : Int,msg  : String?,positiveBtn : String?,negativeBtn : String?,callback : DialogClickCallBack?){
        dismissAlertDialog()
        alertDialog = CommunicationDialog(activity, CommunicationDialogData(dialogId,msg,positiveBtn,negativeBtn),callback)
        alertDialog?.show()
    }

    fun dismissAlertDialog(){
        if(alertDialog != null){
            alertDialog?.dismiss()
        }
    }
}