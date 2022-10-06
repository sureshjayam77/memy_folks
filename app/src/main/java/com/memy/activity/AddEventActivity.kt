package com.memy.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.memy.R
import com.memy.adapter.ItemClickListener
import com.memy.databinding.ActivityAddEventBinding
import com.memy.databinding.ChoosePhotoDialogBinding
import com.memy.pojo.AddEvent
import com.memy.pojo.CommonResponse
import com.memy.pojo.FamilyMembersObject
import com.memy.pojo.FamilyMembersResult
import com.memy.viewModel.AddEventViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class AddEventActivity : AppBaseActivity(), ItemClickListener {
    lateinit var photoBottomSheet: BottomSheetDialog
    lateinit var binding: ActivityAddEventBinding
    val REQUEST_IMAGE_CAPTURE: Int = 1001
    lateinit var viewModel: AddEventViewModel
    var familyMembersList:List<FamilyMembersObject>? =ArrayList()
    var familyMembersStringList:ArrayList<String>? =ArrayList()
    var mHour = 0
    var mMinute = 0
    var eventImageType=0
    var mFile:File?=null
    var hostId=""
    var hostId2=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_event)
        viewModel = ViewModelProvider(this).get(AddEventViewModel::class.java)
        viewModel.addFamilyRes.observe(this, this::validateAddFamilyRes)
        viewModel.familyMemRes.observe(this, this::getFamilyResponse)

        binding.endDateEditText.setOnClickListener {
            openDatePicker(it)
        }
        binding.addCustomImage.setOnClickListener {
            dispatchImagePickerIntent()
        }
        binding.startDateEditText.setOnClickListener {
            openDatePicker(it)
        }
        binding.backIconImageView.setOnClickListener {
            onBackPressed()
        }
        binding.popupCancelBtn.setOnClickListener {
            if (TextUtils.isEmpty(binding.startDateEditText.text.toString())) {
                showToast("Please select start date")
            } else if (TextUtils.isEmpty(binding.endDateEditText.text.toString())) {
                showToast("Please select end date")
            } else if (TextUtils.isEmpty(binding.descEditText.text.toString())) {
                showToast("Please enter user content")
            } else if (TextUtils.isEmpty(binding.locationEditText.text.toString())) {
                showToast("Please enter the location")
            } else {
                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
                val simpleDateFormat1 =
                    SimpleDateFormat("MMM dd,yyyy\n hh:mm a", Locale.ENGLISH)
                val startDate = simpleDateFormat.parse(binding.startDateEditText.text.toString())
                val formattedDate = simpleDateFormat1.format(startDate)
                binding.displayLay.visibility = View.VISIBLE
                binding.eventLay.visibility = View.GONE
                binding.dateTextView.text = formattedDate
                binding.contentTextView.text = binding.descEditText.text.toString()
                binding.addressTextView.text = binding.locationEditText.text.toString()
                binding.host1TextView.text = "Host: "+binding.hostEditText.text.toString()
                if(!TextUtils.isEmpty(binding.host2EditText.text.toString())){
                    binding.host1TextView.text =  binding.host1TextView.text.toString()+" & "+binding.host2EditText.text.toString()
                }
                if(!TextUtils.isEmpty(binding.host3EditText.text.toString())){
                    binding.host3TextView.text ="Click here to view link"
                    binding.host3TextView.setOnClickListener {
                       try{
                           startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(binding.host3EditText.text.toString())))
                       }catch (ex:Exception){
                           ex.message
                       }
                    }
                }
            }
        }

        binding.addEventBtn.setOnClickListener {
            showProgressBar()
            var destinationFilename =
                File(filesDir.path + File.separatorChar + "event_template.png")
            if(eventImageType==0){
                val bm = BitmapFactory.decodeResource(getResources(), R.drawable.event_template_1)
                saveBitmapToFile(destinationFilename,bm, CompressFormat.PNG,100);
            }else{
                destinationFilename= mFile!!
            }
            val addEvent=AddEvent(prefhelper.fetchUserData()?.mid.toString(),"HBD163","2",binding.startDateEditText.text.toString(),binding.endDateEditText.text.toString(),binding.descEditText.text.toString(), binding.locationEditText.text.toString(),eventImageType.toString(),"center",destinationFilename,hostId,hostId2,binding.host3EditText.text.toString());
            viewModel.addEvent(addEvent)
        }
        binding.imgTemplate.setOnClickListener {
            binding.contentTextView.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.txtInfo.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.dateTextView.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.addressTextView.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.host1TextView.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.host2TextView.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.host3TextView.setTextColor(ContextCompat.getColor(this,R.color.black))
            eventImageType=0
            binding.statusImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.event_template_1))
        }
        binding.hostEditText.setOnClickListener {
            showUserDialog(0)
        }
        binding.host2EditText.setOnClickListener {
            showUserDialog(1)
        }
        viewModel.getFamilyMembersList(prefhelper.fetchUserData()?.mid.toString())
    }
    fun showUserDialog(pos:Int){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select")
        builder.setItems(
            familyMembersStringList?.toArray(arrayOfNulls<String>(0)),
            { dialog, item -> // Do something with the selection
                dialog.dismiss()
                if(pos==1){
                    hostId2=familyMembersList!![item].id
                    binding.host2EditText.text=familyMembersList!![item].firstname
                }else{
                    hostId=familyMembersList!![item].id
                    binding.hostEditText.text=familyMembersList!![item].firstname
                }
            })
        builder.show()
    }
    fun saveBitmapToFile(
        dir: File?, bm: Bitmap,
        format: CompressFormat?, quality: Int
    ): Boolean {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(dir)
            bm.compress(format, quality, fos)
            fos.close()
            return true
        } catch (e: IOException) {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
        return false
    }
    private fun getFamilyResponse(res: FamilyMembersResult) {
        hideProgressBar()
        if (res.statusCode == 200) {
            familyMembersList=res.data?.Members
            familyMembersList!!.forEach { family ->
                familyMembersStringList!!.add(family.firstname)
            }
            hostId=familyMembersList!![0].id
            binding.hostEditText.text=familyMembersList!![0].firstname
        }
    }
    private fun validateAddFamilyRes(res: CommonResponse) {
        hideProgressBar()
        if (res.statusCode == 200) {
            showToast("Event added successfully")
            finish()
        } else {
            res.errorDetails?.message?.let { showToast(it) }
        }
    }
    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (binding.displayLay.visibility == View.GONE) {
            super.onBackPressed()
        } else {
            binding.displayLay.visibility = View.GONE
            binding.eventLay.visibility = View.VISIBLE
        }

    }

    override fun dialogPositiveCallBack(id: Int?) {
        TODO("Not yet implemented")
    }

    override fun dialogNegativeCallBack() {
        TODO("Not yet implemented")
    }


    private fun openChoosePhoto() {
        photoBottomSheet = BottomSheetDialog(this, R.style.bottomSheetDialogTheme)
        val choosePhotoDialogBinding: ChoosePhotoDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.choose_photo_dialog, null, false
        )
        choosePhotoDialogBinding.cameraTextView.setOnClickListener({
            photoBottomSheet.dismiss()
        })
        choosePhotoDialogBinding.galleryTextView.setOnClickListener({
            dispatchImagePickerIntent()
            photoBottomSheet.dismiss()
        })
        choosePhotoDialogBinding.cancelTextView.setOnClickListener({
            photoBottomSheet.dismiss()
        })
        photoBottomSheet.setContentView(choosePhotoDialogBinding.root)
        photoBottomSheet.show()
    }

    override fun onItemClicked(tag: String?, pos: String?) {


    }

    fun openDatePicker(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            when (v.id) {
                R.id.startDateEditText -> {
                    binding.startDateEditText.text =
                        "" + (if (dayOfMonth > 9) (dayOfMonth) else ("0" + dayOfMonth)) + "-" + (if ((monthOfYear + 1) > 9) (monthOfYear + 1) else ("0" + (monthOfYear + 1))) + "-" + year
                }
                R.id.endDateEditText -> {
                    binding.endDateEditText.text =
                        "" + (if (dayOfMonth > 9) (dayOfMonth) else ("0" + dayOfMonth)) + "-" + (if ((monthOfYear + 1) > 9) (monthOfYear + 1) else ("0" + (monthOfYear + 1))) + "-" + year
                }
            }
            timePicker(v)
        }, year, month, day)
        dpd.datePicker.minDate = Date().time
        dpd.show()
        dpd.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.app_color));
        dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.app_color));
    }

    private fun timePicker(v: View) {
        // Get Current Time
        val c = Calendar.getInstance()
        mHour = c[Calendar.HOUR_OF_DAY]
        mMinute = c[Calendar.MINUTE]

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(
            this,
            { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                val hrs =
                    if (hourOfDay.toString().length == 1) "0" + hourOfDay.toString() else hourOfDay.toString()
                val mins =
                    if (minute.toString().length == 1) "0" + minute.toString() else minute.toString()
                when (v.id) {
                    R.id.startDateEditText -> {
                        binding.startDateEditText.text =
                            binding.startDateEditText.text.toString() + " " + hrs + ":" + mins
                        binding.endDateEditText.text=
                            binding.startDateEditText.text.toString()
                    }
                }
            }, mHour, mMinute, true
        )
        timePickerDialog.show()
        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.app_color));
        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.app_color));

    }

    private fun dispatchImagePickerIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, REQUEST_IMAGE_CAPTURE)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            //viewModel.photoFileUri = getFileURL()
            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.photoFileUri)
            // startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            binding.contentTextView.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.txtInfo.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.dateTextView.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.addressTextView.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.host2TextView.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.host1TextView.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.host3TextView.setTextColor(ContextCompat.getColor(this,R.color.white))
            viewModel.photoFileUri = intent?.data
            eventImageType=1
            mFile= getFile(this, viewModel.photoFileUri!!)
            loadProfileImageFromURI(intent?.data!!)
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    private fun loadProfileImageFromURI(url: Uri?) {
        if (url != null) {
            Glide
                .with(this)
                .load(url)
                .centerCrop()
                .into(binding.statusImage)
        }
    }
    private fun showProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.GONE

    }
    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            ex.message?.let { Log.e("Save File", it) }
            ex.printStackTrace()
        }
    }
    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                ins?.let {
                    createFileFromStream(
                        it,
                        destinationFilename
                    )
                }
            }
        } catch (ex: java.lang.Exception) {
            ex.message?.let { Log.e("Save File", it) }
            ex.printStackTrace()
        }
        return destinationFilename
    }
}