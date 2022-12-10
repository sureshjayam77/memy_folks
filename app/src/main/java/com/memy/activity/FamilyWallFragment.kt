package com.memy.activity

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.memy.R
import com.memy.adapter.MyRecyclerAdapter
import com.memy.databinding.ActivityFamilyWallBinding
import com.memy.databinding.ChoosePhotoDialogBinding
import com.memy.databinding.DialogStorySelectionBinding
import com.memy.fragment.BaseFragment
import com.memy.listener.AdapterListener
import com.memy.pojo.CommonResponse
import com.memy.pojo.WallData
import com.memy.pojo.WallResult
import com.memy.viewModel.AddEventViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*


class FamilyWallFragment : BaseFragment(), AdapterListener, LifecycleObserver {
    lateinit var photoBottomSheet: BottomSheetDialog
    val REQUEST_IMAGE_CAPTURE: Int = 1001
    lateinit var viewModel: AddEventViewModel
    var recylerView: RecyclerView? = null
    private val SELECT_VIDEO = 9002
    var adapter: MyRecyclerAdapter? = null
    var wallList: ArrayList<WallData>? = ArrayList()
    var eventList: ArrayList<WallData>? = ArrayList()
    private lateinit var binding: ActivityFamilyWallBinding
     private var tabPosition=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityFamilyWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddEventViewModel::class.java)

        viewModel.addFamilyRes.observe(requireActivity(), this::validateAddFamilyRes)
        viewModel.deleteWallRes.observe(requireActivity(), this::deleteWallRes)
        viewModel.wallRes.observe(requireActivity(), this::getWallDataResponse)
        recylerView = binding.recFamilyWall
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        //  recylerView?.layoutManager = linearLayoutManager
        var imgUrl = prefhelper.fetchUserData()?.photo
        binding.addFloat.setOnClickListener {
            openAddStoryDialog()
        }
        val layoutManager = GridLayoutManager(activity, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter!!.getItemViewType(position) == adapter!!.TYPE_HEADER) 3 else 1
            }
        }
        recylerView!!.layoutManager = layoutManager
        tabPosition=0
        binding.tab.addTab(binding.tab.newTab().setText("Walls"))
        binding.tab.addTab(binding.tab.newTab().setText("Events"))
        binding.tab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition=tab!!.position
                if(tab!!.position==0){
                    setWallAdapter(true)
                }else{
                    setWallAdapter(false)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tabPosition=tab!!.position
                if(tab!!.position==0){
                    setWallAdapter(true)
                }else{
                    setWallAdapter(false)
                }
            }
        })

        loadProfileImage(imgUrl)
        viewModel.getWallMedia(prefhelper.fetchUserData()?.mid.toString())
        showProgressBar()
    }


    override fun onResume() {
        super.onResume()
    }

    private fun loadProfileImage(url: String?) {
        /* if (!TextUtils.isEmpty(url)) {
             Glide.with(this)
                 .load(url)
                 .centerCrop()
                 .placeholder(R.drawable.ic_profile_male)
                 .error(R.drawable.ic_profile_male)
                 .into(findViewById(R.id.add_story_image))
         }
 */
    }

    private fun deleteWallRes(res: CommonResponse) {
        if (res.statusCode == 200) {
            viewModel.getWallMedia(prefhelper.fetchUserData()?.mid.toString())
            showToast("Deleted successfully")
        } else {
            res.errorDetails?.message?.let { showToast(it) }
        }
    }

    private fun validateAddFamilyRes(res: CommonResponse) {
        hideProgressBar()
        if (res.statusCode == 200) {
            showToast("Post added successfully")
        } else {
            res.errorDetails?.message?.let { showToast(it) }
        }
    }

    private fun getWallDataResponse(res: WallResult) {
        hideProgressBar()
        if (res.statusCode == 200) {
            if (res.data?.walls != null && res.data?.walls?.isNotEmpty()!!) {
                wallList = ArrayList()

                val grouppedByDate =
                    res.data!!.walls.groupBy {
                        if ((it != null) && (it.media != null) && (it.media!!.size > 0)) {
                            it.media!![0].uploaded_at.split("T")[0]
                        } else {
                            ""
                        }
                    }
                var eventsHashMap: HashMap<String, List<WallData>?>? = null
                if (grouppedByDate != null) {
                    eventsHashMap = HashMap(grouppedByDate)
                }
                lateinit var entries: List<String>
                entries = grouppedByDate.keys.toList<String>()
                entries = entries.reversed()
                for (i in entries) {
                    val eventList = ArrayList(grouppedByDate.get(i))
                    if (eventsHashMap != null && eventsHashMap.isNotEmpty()) {
                        val eventData = eventsHashMap.get(i)
                        if (eventData != null) {
                            eventList.addAll(eventData)
                            eventsHashMap.remove(i)
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        eventList.removeIf { wallData -> wallData.media!!.size == 0 }
                    }
                    if (eventList.isNotEmpty()) {
                        var wallDate = WallData("","",
                            "",
                            i,
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,null,
                            "",
                            "",
                            "",
                            "",
                            null,
                            null,
                            ""
                        )
                        wallList?.add(wallDate)
                        wallList?.addAll(eventList)
                    }
                    // wallList?.add(WallGroupData(i.key, eventList))
                }
                var grouppedEventsByDate: Map<String, List<WallData>?>? = null
                if (res.data!!.events != null) {
                    grouppedEventsByDate =
                        res.data!!.events.groupBy {
                            if ((it != null) && (it.media != null) && (it.media!!.size > 0)) {
                                it.media!![0].uploaded_at.split("T")[0]
                            } else {
                                ""
                            }
                        }
                }
                 eventsHashMap= null
                if (grouppedEventsByDate != null) {
                    eventsHashMap = HashMap(grouppedEventsByDate)
                }
                eventList = ArrayList()
                 if (eventsHashMap != null&&eventsHashMap.isNotEmpty()) {
                     var entries: List<String> = grouppedEventsByDate!!.keys.toList()
                     entries = entries.reversed()
                     for (i in entries) {
                         if (grouppedEventsByDate.get(i)!!.isNotEmpty()&&i.isNotEmpty()) {
                             var wallDate = WallData("","",
                                 "",
                                 i,
                                 "",
                                 "",
                                 "",
                                 "",
                                 "",
                                 null,null,
                                 "",
                                 "",
                                 "",
                                 "",
                                 null,
                                 null,
                                 ""
                             )
                             eventList?.add(wallDate)
                             eventList?.addAll(grouppedEventsByDate.get(i)!!)
                         }
                     }
                 }
                /*wallList?.sortBy {
                    it.startedDate
                }
                wallList?.reverse()*/
               /* wallList?.reverse()
                eventList?.reverse()*/
                if(tabPosition==0){
                    setWallAdapter(true)
                }else{
                    setWallAdapter(false)
                }
                Log.d("", "")
            } else {
                eventList = ArrayList()
                  var grouppedEventsByDate: Map<String, List<WallData>?>? = null
                  if (res.data!=null&&res.data!!.events != null&&res.data!!.events.isNotEmpty()) {
                      grouppedEventsByDate =
                          res.data!!.events.groupBy {
                              it.media!![0].uploaded_at.split("T")[0]
                          }
                      var eventsHashMap: HashMap<String, List<WallData>?>? = null
                      if (grouppedEventsByDate != null) {
                          eventsHashMap = HashMap(grouppedEventsByDate)
                      }
                      if (eventsHashMap != null&&eventsHashMap.isNotEmpty()) {
                          var entries: List<String> = grouppedEventsByDate!!.keys.toList()
                          entries = entries.reversed()
                          for (i in entries) {
                              if (grouppedEventsByDate.get(i)!!.isNotEmpty()) {
                                  var wallDate = WallData("","",
                                      "",
                                      i,
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      null,null,
                                      "",
                                      "",
                                      "",
                                      "",
                                      null,
                                      null,
                                      ""
                                  )
                                  eventList?.add(wallDate)
                                  eventList?.addAll(grouppedEventsByDate.get(i)!!)
                              }
                          }
                      }
                    //  eventList?.reverse()
                      if(tabPosition==0){
                          setWallAdapter(true)
                      }else{
                          setWallAdapter(false)
                      }
                  }
            }
        } else {

        }
    }
    fun setWallAdapter(wall:Boolean){
        adapter = MyRecyclerAdapter(
            requireActivity(),
            this,
            if(wall){wallList!!}else{eventList!!},
            prefhelper.fetchUserData()?.mid.toString()
        )
        recylerView?.adapter = adapter
    }

    fun showToast(text: String) {
        Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT).show()
    }


    fun openAddStoryDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        val dialogStorySelectionBinding: DialogStorySelectionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_story_selection, null, false
        )
        dialogStorySelectionBinding.addEventLay.setOnClickListener {
            bottomSheetDialog.dismiss()
            someActivityResultLauncher.launch(Intent(requireActivity(), AddEventActivity::class.java))
        }
        dialogStorySelectionBinding.addImageLay.setOnClickListener {
            bottomSheetDialog.dismiss()
            openChoosePhoto()
        }
        dialogStorySelectionBinding.addVideoLay.setOnClickListener {
            bottomSheetDialog.dismiss()
            videoPicker()
        }
        dialogStorySelectionBinding.addTextLay.setOnClickListener {
            bottomSheetDialog.dismiss()
            someActivityResultLauncher.launch(Intent(activity, AddTextStoryActivity::class.java))
        }
        bottomSheetDialog.setContentView(dialogStorySelectionBinding.root)
        bottomSheetDialog.show()
    }

    private fun openChoosePhoto() {
        photoBottomSheet = BottomSheetDialog(requireActivity(), R.style.bottomSheetDialogTheme)
        val choosePhotoDialogBinding: ChoosePhotoDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.choose_photo_dialog, null, false
        )
        choosePhotoDialogBinding.cameraTextView.setOnClickListener({
            photoBottomSheet.dismiss()
            dispatchTakePictureIntent()
        })
        choosePhotoDialogBinding.galleryTextView.setOnClickListener({
            photoBottomSheet.dismiss()
            dispatchImagePickerIntent()
        })
        choosePhotoDialogBinding.cancelTextView.setOnClickListener({
            photoBottomSheet.dismiss()
        })
        photoBottomSheet.setContentView(choosePhotoDialogBinding.root)
        photoBottomSheet.show()
    }

    override fun updateAction(actionCode: Int, data: Any?) {
        if (actionCode == 1000) {
            val url = data as WallData
            val intent = Intent(requireActivity(), PostViewActivity::class.java)
            intent.putExtra("file", url)
            someActivityResultLauncher.launch(intent)
        } else if (actionCode == 1001) {
            val url = data as WallData
            var isWall = true
            if (!TextUtils.isEmpty(url.location_pin)) {
                isWall = false
            }
            deleteDialog(isWall, url.id)
        }
    }
    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult(),
        { result ->
            if (result.getResultCode() === RESULT_OK) {
                // There are no request codes
                    if(result.getData()!=null){
                        if(!result.data!!.getBooleanExtra("is_back",false)){
                            viewModel.getWallMedia(prefhelper.fetchUserData()?.mid.toString())
                        }
                    }else{
                        viewModel.getWallMedia(prefhelper.fetchUserData()?.mid.toString())
                    }

            }
        })
    fun deleteDialog(isWall: Boolean, id: String) {
        AlertDialog.Builder(requireContext())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(
                "Delete " + if (isWall) {
                    "Wall"
                } else "Event"
            )
            .setMessage("Are you sure you want to delete this wall?")
            .setPositiveButton("Yes",
                { dialog, which ->
                    dialog.dismiss()
                    if (isWall) {
                        viewModel.deleteWall(id, prefhelper.fetchUserData()?.mid.toString())
                    } else {
                        viewModel.deleteEvent(id, prefhelper.fetchUserData()?.mid.toString())
                    }
                })
            .setNegativeButton("No", null)
            .show()
    }

    private fun dispatchImagePickerIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, REQUEST_IMAGE_CAPTURE)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            viewModel.photoFileUri = getFileURL()
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.photoFileUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFileURL(): Uri {
        val applicationContext: Context = requireActivity().applicationContext
        val root: File =
            applicationContext.cacheDir // consider using getExternalFilesDir(Environment.DIRECTORY_PICTURES); you need to check the file_paths.xml
        val timeLng: Long = Date().time
        val capturedPhoto = File(root, "memy_profile_$timeLng.jpeg")
        return FileProvider.getUriForFile(
            applicationContext,
            applicationContext.getPackageName().toString() + ".fileprovider",
            capturedPhoto
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (intent != null) {
                viewModel.photoFileUri = intent.data!!
            }
            val wallIntent = Intent(requireActivity(), WallPostActivity::class.java)
            wallIntent.putExtra("file", getFile(requireActivity(), viewModel.photoFileUri!!))
            someActivityResultLauncher.launch(wallIntent)
        } else if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK) {
            if (intent != null) {
                viewModel.photoFileUri = intent.data!!
                val wallIntent = Intent(requireActivity(), WallPostActivity::class.java)
                wallIntent.putExtra("file", getFile(requireActivity(), viewModel.photoFileUri!!))
                someActivityResultLauncher.launch(wallIntent)
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
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

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    private fun showProgressBar() {
        binding.progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBarLayout.visibility = View.GONE

    }

    private fun videoPicker() {
        val video_picker_intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
//        viewModel.photoFileUri = getFileURL()
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.photoFileUri)
        startActivityForResult(video_picker_intent, SELECT_VIDEO)
    }
}