package com.memy.activity


import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.memy.R
import com.memy.databinding.OtpActivityBinding
import com.memy.pojo.MobileNumberVerifyResObj
import com.memy.pojo.ProfileVerificationResObj
import com.memy.receiver.MySMSBroadcastReceiver
import com.memy.utils.AppSignatureHelper
import com.memy.utils.Constents
import com.memy.utils.Utils
import com.memy.viewModel.OTPViewModel


class OTPActivity : AppBaseActivity() {

    lateinit var binding: OtpActivityBinding
    var viewModel: OTPViewModel? = null
    var mySMSBroadcastReceiver: MySMSBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
        setResendSpannable()
        setTextChangeListener()
        initObservers()
        getIntentData()
        viewModel?.otpSMSKey = generateOTPKey()
        prefhelper.saveVeryFirstGuideShow(true)
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mySMSBroadcastReceiver != null)
                unregisterReceiver(mySMSBroadcastReceiver);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.otp_activity)
        binding.lifecycleOwner = this
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(OTPViewModel::class.java)
        binding.viewModel = viewModel
        initSMSReceiver()
    }

    private fun setTextChangeListener() {
        binding.otpOneEditText.addTextChangedListener(GenericTextWatcher(binding.otpOneEditText))
        binding.otpTwoEditText.addTextChangedListener(GenericTextWatcher(binding.otpTwoEditText))
        binding.otpThreeEditText.addTextChangedListener(GenericTextWatcher(binding.otpThreeEditText))
        binding.otpFourEditText.addTextChangedListener(GenericTextWatcher(binding.otpFourEditText))
    }

    private fun setResendSpannable() {
        val contentString1 = getString(R.string.label_otp_not_receive_1)
        val contentString2 = getString(R.string.label_otp_not_receive_2)

        val contentText = "$contentString1 $contentString2"

        val tStart = contentText.indexOf(contentString2)
        val tEnd = tStart + contentString2.length
        val spannableString = SpannableString(contentText)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                forgotOTP()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, tStart, tEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#0C8AB7")),
            tStart,
            tEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.otpNotReceiveTextView.setText(spannableString)
        binding.otpNotReceiveTextView.setMovementMethod(LinkMovementMethod.getInstance())
    }

    private fun initObservers() {
        viewModel?.otpCharOne?.observe(this, { s ->
            viewModel?.validateVerifyBtnEnable()
        })
        viewModel?.otpCharTwo?.observe(this, { s ->
            viewModel?.validateVerifyBtnEnable()
        })
        viewModel?.otpCharThree?.observe(this, { s ->
            viewModel?.validateVerifyBtnEnable()
        })
        viewModel?.otpCharFour?.observe(this, { s ->
            viewModel?.validateVerifyBtnEnable()
        })
        viewModel?.otpVerifyResObj?.observe(this, this::validateOtpResponse)
        viewModel?.loginResponse?.observe(this, this::validateMobileNumberRes)
    }

    fun verifyOTP(v: View?) {
        requestMakeOTPVerifyCall()
    }

    fun requestMakeOTPVerifyCall(){
        if (Utils.isNetworkConnected(this)) {
            binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
            viewModel?.verifyOtp()
        } else {
            showAlertDialog(
                R.id.do_nothing,
                getString(R.string.no_internet),
                getString(R.string.close_label),
                ""
            )
        }
    }

    inner class GenericTextWatcher internal constructor(private val view: View) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            // TODO Auto-generated method stub
            val text = editable.toString()
            when (view.id) {
                binding.otpOneEditText.id -> if (text.length == 1) binding.otpTwoEditText.requestFocus()
                binding.otpTwoEditText.id -> if (text.length == 1) binding.otpThreeEditText.requestFocus() else if (text.length == 0) binding.otpOneEditText.requestFocus()
                binding.otpThreeEditText.id -> if (text.length == 1) binding.otpFourEditText.requestFocus() else if (text.length == 0) binding.otpTwoEditText.requestFocus()
                binding.otpFourEditText.id -> if (text.length == 0) binding.otpThreeEditText.requestFocus()
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

        }
    }

    private fun validateOtpResponse(res: ProfileVerificationResObj) {
        binding.progressInclude.progressBarLayout.visibility = View.GONE
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    prefhelper.saveUserData(res?.data)
                    var intent: Intent?
                    if ((res?.data?.firstname == null) || (TextUtils.isEmpty(res?.data?.firstname.trim()))) {
                        intent = Intent(this, AddFamilyActivity::class.java)
                        intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
                        intent.putExtra(Constents.OWN_NEW_PROFILE_INTENT_TAG, true)
                    } else {
                        intent = Intent(this, DashboardActivity::class.java)
                    }
                    startActivityIntent(intent, false)
                } else {
                    errorHandler(res)
                }
            } else {
                errorHandler(res)
            }
        }
    }

    private fun getIntentData() {
        viewModel?.phoneNumber?.value = intent?.getStringExtra(Constents.MOBILE_NUMBER_INTENT_TAG)
        viewModel?.countryCode?.value = intent?.getStringExtra(Constents.COUNTRY_CODE_INTENT_TAG)
    }

    private fun errorHandler(res: ProfileVerificationResObj) {
        var message = ""
        if (res.errorDetails != null) {
            message = res.errorDetails.message ?: ""
        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.something_went_wrong)
        }
        showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
    }

    private fun forgotOTP() {
        if (Utils.isNetworkConnected(this)) {
            binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
            viewModel?.verifyMobileNumber()
        } else {
            showAlertDialog(
                R.id.do_nothing,
                getString(R.string.no_internet),
                getString(R.string.close_label),
                ""
            )
        }
    }

    fun validateMobileNumberRes(res: MobileNumberVerifyResObj) {
        binding.progressInclude.progressBarLayout.visibility = View.GONE
        if (res != null) {
            if (res.statusCode == 200) {
                showAlertDialog(
                    R.id.do_nothing,
                    getString(R.string.success_otp_sent),
                    getString(R.string.close_label),
                    ""
                )
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

    private fun startSMSRetrieverClient() {
        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid ->
            Log.d("LogOTP", "-Success")
        }
        task.addOnFailureListener { e ->
            Log.d("LogOTP", "-Success")
        }
    }

    private fun initSMSReceiver() {
        startSMSRetrieverClient()
        mySMSBroadcastReceiver = MySMSBroadcastReceiver()
        registerReceiver(mySMSBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        mySMSBroadcastReceiver?.init(object : MySMSBroadcastReceiver.OTPReceiveListener {

            override fun onOTPReceived(otp: String?) {
                if((!TextUtils.isEmpty(otp)) && (!TextUtils.isEmpty(otp?.trim())) && (otp?.trim()?.length == 4)){
                    viewModel?.otpCharOne?.value = (otp.get(0)).toString()
                    viewModel?.otpCharTwo?.value = (otp.get(1)).toString()
                    viewModel?.otpCharThree?.value = (otp.get(2)).toString()
                    viewModel?.otpCharFour?.value = (otp.get(3)).toString()

                    binding.otpOneEditText.setText((otp.get(0)).toString())
                    binding.otpTwoEditText.setText((otp.get(1)).toString())
                    binding.otpThreeEditText.setText((otp.get(2)).toString())
                    binding.otpFourEditText.setText((otp.get(3)).toString())
                    Log.d("LogOTP", "-"+otp)
                    requestMakeOTPVerifyCall()
                }
            }

            override fun onOTPTimeOut() {
                Log.d("LogOTP", "-");
            }

        })

        var appSignatureHelper = AppSignatureHelper(this)
        var key = appSignatureHelper.appSignatures
      //  binding.keyValue.setText(""+key+"")
        Log.d("LogOTP-key",""+key+"")
    }

    override fun onBackPressed() {
        if(binding.progressInclude.progressBarLayout.visibility == View.GONE) {
            super.onBackPressed()
        }
    }


}