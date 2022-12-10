package com.memy.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.memy.R
import com.memy.adapter.ItemClickListener
import com.memy.databinding.ActivityAddStoryEventBinding
import com.memy.pojo.CommonResponse
import com.memy.viewModel.AddEventViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class AddTextStoryActivity : AppBaseActivity(), ItemClickListener {
    lateinit var binding: ActivityAddStoryEventBinding
    lateinit var viewModel: AddEventViewModel

    var clrPos=0
    var fontPos=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_story_event)
        viewModel = ViewModelProvider(this).get(AddEventViewModel::class.java)
        viewModel.addFamilyRes.observe(this, this::validateAddFamilyRes)
        binding.addEventBtn.setOnClickListener {
            binding.clrLay.visibility=View.GONE
            showProgressBar()
            var destinationFilename =
                File(filesDir.path + File.separatorChar + "story_text.png")
            val bm =getBitmapFromView(binding.bgLay)
            saveBitmapToFile(destinationFilename,bm, Bitmap.CompressFormat.PNG,100);
            viewModel.addStatusEvent(prefhelper.fetchUserData()?.mid.toString(), destinationFilename,"")
        }
        binding.imgColor.setOnClickListener {
            clrPos++
            if(clrPos==1){
                binding.storyEditText.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))
            }else if(clrPos==2){
                binding.storyEditText.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
            }else if(clrPos==3){
                binding.storyEditText.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_500))
            }else if(clrPos==4){
                binding.storyEditText.setBackgroundColor(ContextCompat.getColor(this,R.color.teal_200))
            }else if(clrPos==5){
                binding.storyEditText.setBackgroundColor(ContextCompat.getColor(this,R.color.teal_700))
            }else{
                clrPos=0
                binding.storyEditText.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
            }
        }
        binding.imgStyle.setOnClickListener {
            fontPos++
            if(fontPos==1){
                binding.storyEditText.setTypeface(null,Typeface.BOLD)
            }else if(fontPos==2){
                binding.storyEditText.setTypeface(null,Typeface.BOLD_ITALIC)
            }else if(fontPos==3){
                binding.storyEditText.setTypeface(null,Typeface.ITALIC)
            }else{
                fontPos=0
                binding.storyEditText.setTypeface(null,Typeface.NORMAL)
            }

        }


    }
    private fun validateAddFamilyRes(res: CommonResponse) {
        hideProgressBar()
        if (res.statusCode == 200) {
            showToast("Post added successfully")
            val intent= Intent()
            intent.putExtra("is_done",true)
            setResult(Activity.RESULT_OK,intent)
            finish()
        } else {
            binding.clrLay.visibility=View.VISIBLE
            res.errorDetails?.message?.let { showToast(it) }
        }
    }
    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    private fun autosizeText(size: Float): Float {
        return size / (resources.displayMetrics.density +0.2f)
    }
    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun dialogPositiveCallBack(id: Int?) {
        TODO("Not yet implemented")
    }

    override fun dialogNegativeCallBack() {
        TODO("Not yet implemented")
    }



    override fun onItemClicked(tag: String?, pos: String?) {


    }



    fun saveBitmapToFile(
        dir: File?, bm: Bitmap,
        format: Bitmap.CompressFormat?, quality: Int
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