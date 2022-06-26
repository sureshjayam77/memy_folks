package com.memy.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.memy.R
import com.memy.adapter.CountryCodeAdapter
import com.memy.adapter.CountryCodeAdapter.SEARCH_DATA_VALUES
import com.memy.adapter.CountryCodeAdapter.SELECTED_COUNTRY_VALUE
import com.memy.databinding.SigninActivityBinding
import com.memy.listener.AdapterListener
import com.memy.pojo.CountryListObj
import com.memy.pojo.MobileNumberVerifyResObj
import com.memy.utils.Constents
import com.memy.utils.Utils
import com.memy.viewModel.SignInViewModel
import java.lang.String
import java.util.*
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.widget.TextView.BufferType

import android.text.method.LinkMovementMethod

import android.text.style.UnderlineSpan

import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat


class SignInActivity : AppBaseActivity(), AdapterListener {

    lateinit var binding: SigninActivityBinding
    private var countryAdapter: CountryCodeAdapter? = null
    private var countryListDialog: Dialog? = null
    private var countryCodeRecyclerView: RecyclerView? = null
    private var emptyTextView: AppCompatTextView? = null
    private var removeIconImageView: AppCompatImageView? = null
    private var viewModel : SignInViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initViewModel()
        initObservers()
        initTermsSpannable()
        viewModel?.otpSMSKey = generateOTPKey()
    }

    override fun dialogPositiveCallBack(id : Int?) {
        dismissAlertDialog()
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    override fun updateAction(actionCode: Int, data: Any?) {
        if (actionCode == SEARCH_DATA_VALUES && data != null) {
            val key = data as kotlin.String
            searchViewChange(key)
        }else if((actionCode == SELECTED_COUNTRY_VALUE) && (data != null)){
            val code = "+" + String.valueOf(data)
            viewModel?.countryCode?.value = code
            binding.countryCodeTextView.setText(code)
            binding.mobileNumberEditText.setText("")
            if (countryListDialog != null) {
                countryListDialog?.getWindow()
                    ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                try {
                    val input = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    input.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.countryCodeTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT)
                binding.mobileNumberEditText.setFocusable(true)
                binding.mobileNumberEditText.setEnabled(true)
                binding.mobileNumberEditText.setCursorVisible(true)
                countryListDialog?.dismiss()
            }
        }
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.signin_activity)
        binding.lifecycleOwner = this
    }
    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        binding.viewModel = viewModel
        viewModel?.countryCode?.value = (getString(R.string.static_country_code))
    }

    private fun initObservers(){
        viewModel?.phoneNumber?.observe(this, { s  ->
            viewModel?.validateGenerateBtnEnable()
        })
        viewModel?.countryCode?.observe(this, { s  ->
            viewModel?.validateGenerateBtnEnable()
        })
        viewModel?.isTermsChecked?.observe(this, { s  ->
            viewModel?.validateGenerateBtnEnable()
        })
        viewModel?.loginResponse?.observe(this, this::validateMobileNumberRes)
    }

    /**
     * method used to change the view when click the search button
     * @param searchKey given search key
     */
    private fun searchViewChange(searchKey: kotlin.String) {
        val isEmpty = countryAdapter != null && countryAdapter!!.itemCount === 0
        if (countryCodeRecyclerView != null) {
            countryCodeRecyclerView!!.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
        if (emptyTextView != null) {
            emptyTextView!!.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
        removeIconImageView?.setVisibility(if (TextUtils.isEmpty(searchKey)) View.GONE else View.VISIBLE)
    }

    fun countryListDropDown(v: View) {
        countryListDialog = Dialog(this)
        val dialogWinHeight = (height * 0.7432) as Double
        val dialogWinWidth = (width * 0.8) as Double
        val viewHeight = (height * 0.8) as Double
        val viewWidth = (width * 0.9) as Double
        val leftSpace = (width * 0.0416) as Double
        val topSpace = (height * 0.0161) as Double
        val searchLeftSpace = (width * 0.0277) as Double
        if (countryListDialog?.getWindow() != null) {
            countryListDialog?.getWindow()
                ?.setLayout(dialogWinWidth.toInt(), dialogWinHeight.toInt() / 2)
            countryListDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
            countryListDialog?.getWindow()
                ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }
        countryListDialog?.setContentView(R.layout.country_code_drop_down)
        val parentDialogLayout =
            countryListDialog?.findViewById<View>(R.id.parentDialogLayout) as LinearLayout
        val searchViewLayout =
            countryListDialog?.findViewById<View>(R.id.searchViewLayout) as LinearLayout
        val searchViewEditText: AppCompatEditText =
            countryListDialog?.findViewById<View>(R.id.searchViewEditText) as AppCompatEditText
        removeIconImageView =
            countryListDialog?.findViewById<View>(R.id.removeIconImageView) as AppCompatImageView
        emptyTextView =
            countryListDialog?.findViewById<View>(R.id.emptyTextView) as AppCompatTextView
        countryCodeRecyclerView =
            countryListDialog?.findViewById<View>(R.id.countryCodeRecyclerView) as RecyclerView
        emptyTextView?.setVisibility(View.GONE)
        searchViewLayout.setPadding(
            leftSpace.toInt(),
            topSpace.toInt(),
            leftSpace.toInt(),
            topSpace.toInt()
        )
        searchViewEditText.setPadding(searchLeftSpace.toInt(), 0, 0, 0)
        searchViewEditText.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus -> })
        searchViewEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (countryAdapter != null) {
                    countryAdapter?.getFilter()?.filter(cs)
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })
        val country_code = resources.getStringArray(R.array.countryCode)
        val countryList: ArrayList<CountryListObj> = ArrayList<CountryListObj>()
        for (aCountry_code in country_code) {
            val obj = CountryListObj()
            val countryArray = aCountry_code.split(",").toTypedArray()
            obj.setCountryName(Utils.getCountryName(countryArray[0].trim { it <= ' ' }))
            obj.setCountryNameHint(countryArray[0].trim { it <= ' ' })
            obj.setCountryCode(Integer.valueOf(countryArray[1].trim { it <= ' ' }))
            countryList.add(obj)
        }
        val parentDialogLay = parentDialogLayout.layoutParams as LinearLayout.LayoutParams
        parentDialogLay.height = viewHeight.toInt()
        parentDialogLay.width = viewWidth.toInt()
        parentDialogLayout.layoutParams = parentDialogLay
        countryAdapter = CountryCodeAdapter(this, countryList, this, height, width)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        countryCodeRecyclerView?.setLayoutManager(layoutManager)
        countryCodeRecyclerView?.setAdapter(countryAdapter)
        //countryCodeRecyclerView?.addOnItemTouchListener(RecyclerItemClickListener(this, this::OnItemClickListener))
        countryListDialog?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        removeIconImageView?.setOnClickListener(View.OnClickListener {
            searchViewEditText.setText("")
            countryCodeRecyclerView?.setAdapter(countryAdapter)
            emptyTextView?.setVisibility(View.GONE)
            countryCodeRecyclerView?.setVisibility(View.VISIBLE)
            removeIconImageView?.setVisibility(View.GONE)
        })
        countryListDialog?.show()
    }

    fun navigateOTPScreen(v : View){
        if(Utils.isNetworkConnected(this)){
            binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
            viewModel?.verifyMobileNumber()
        }else{
            showAlertDialog(R.id.do_nothing,getString(R.string.no_internet),getString(R.string.close_label),"")
        }
    }

    fun validateMobileNumberRes(res : MobileNumberVerifyResObj){
        binding.progressInclude.progressBarLayout.visibility = View.GONE
        if(res != null){
            if(res.statusCode == 200){
                val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra(Constents.MOBILE_NUMBER_INTENT_TAG,viewModel?.phoneNumber?.value!!)
                intent.putExtra(Constents.COUNTRY_CODE_INTENT_TAG,viewModel?.countryCode?.value!!)
                startActivityIntent(intent,false)
            }else{
                var message = ""
                if(res.errorDetails != null){
                    message = res.errorDetails.message!!
                }
                if(TextUtils.isEmpty(message)){
                    message = getString(R.string.something_went_wrong)
                }
                showAlertDialog(R.id.do_nothing,message,getString(R.string.close_label),"")
            }
        }
    }

    override fun onBackPressed() {
        if(binding.progressInclude.progressBarLayout.visibility == View.GONE) {
            super.onBackPressed()
        }
    }

    private fun initTermsSpannable(){
        var str = getString(R.string.i_agreed_terms_condition)
        val SpanString = SpannableString(str)
        val teremsAndCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(this@SignInActivity,TermsAndConditionActivity::class.java))
            }
        }

        SpanString.setSpan(teremsAndCondition, 9, str.length, 0)
        SpanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.label_phone_number_color)), 9, str.length, 0)
        SpanString.setSpan(UnderlineSpan(), 9, str.length, 0)

        binding.agreeTermsTextView.setMovementMethod(LinkMovementMethod.getInstance())
        binding.agreeTermsTextView.setText(SpanString, BufferType.SPANNABLE)
        binding.agreeTermsTextView.setSelected(true)
    }

}