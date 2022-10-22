package com.memy.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.memy.BuildConfig
import com.memy.R
import com.memy.activity.DashboardActivity
import com.memy.databinding.ShareFragmentBinding
import com.memy.pojo.ShareResponse
import com.memy.viewModel.DashboardViewModel
import java.io.File
import java.io.FileOutputStream


class ShareFragment : BaseFragment(), View.OnClickListener {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var binding: ShareFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShareFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.whatsappIcon.setOnClickListener(this)
        binding.instagramIcon.setOnClickListener(this)
        binding.facebookIcon.setOnClickListener(this)
        binding.twitterIcon.setOnClickListener(this)
        binding.linkedInIcon.setOnClickListener(this)
        viewModel.let {
            it.shareResData.observe(requireActivity(), this::validateShareRes)
        }
        viewModel.showProgressBar.value = true
        viewModel.shareFamilyMember(prefhelper?.fetchUserData()?.mid?.toString())
        return binding.root
    }

    private fun validateShareRes(res: ShareResponse) {
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                val imageUrl = res?.data?.share_template
                if (!TextUtils.isEmpty(imageUrl)) {
                    Glide.with(requireActivity())
                        .load(imageUrl)
                        .placeholder(R.mipmap.app_icon)
                        .error(R.mipmap.app_icon)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                viewModel.showProgressBar.value = false
                                return true
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                viewModel.showProgressBar.value = false
                                binding.imageView.setImageDrawable(resource)
                                return true
                            }

                        })
                        .into(binding.imageView)
                    binding.socialIconLayout.visibility = View.VISIBLE
                }
            } else {
                viewModel.showProgressBar.value = false
                var message = ""
                if ((res != null) && (res.errorDetails != null)) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                (activity as DashboardActivity).showAlertDialog(
                    R.id.do_nothing,
                    message,
                    getString(R.string.close_label),
                    ""
                )
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.facebookIcon -> {
                share("com.facebook.katana")
            }
            R.id.instagramIcon -> {
                share("com.instagram.android")
            }
            R.id.twitterIcon -> {
                share("com.twitter.android")
            }
            R.id.linkedInIcon -> {
                share("com.linkedin.android")
            }
            R.id.whatsappIcon -> {
                share("com.whatsapp")
            }
        }
    }

    private fun share(packageId: String?) {
        try {
            val bitmap = getmageToShare(binding.imageView.drawable?.toBitmap())
            val intent = Intent(Intent.ACTION_SEND)
            intent.setPackage(packageId)
            //intent.type = "image/jpeg"
            intent.type = "*/*"
            intent.putExtra(
                Intent.EXTRA_STREAM,
                bitmap)
            intent.putExtra(Intent.EXTRA_TEXT, "Free & Easy Family Tree Maker"+" \n https://play.google.com/store/apps/details?id=com.memyfolks")
            requireActivity().startActivity(Intent.createChooser(intent, "Share Image"))
        } catch (ex: Exception) {
            //  requireActivity().customToast("Please first install the app")
            requireActivity().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageId")
                )
            )
        }
    }


    // Retrieving the url to share
    private fun getmageToShare(bitmap: Bitmap?): Uri? {
        var uri: Uri? = null
        if (bitmap != null) {
            val imagefolder = File(requireActivity().getCacheDir(), "images")
            try {
                imagefolder.mkdirs()
                val file = File(imagefolder, "shared_image.png")
                val outputStream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                uri = FileProvider.getUriForFile(
                    requireActivity(),
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )
            } catch (e: java.lang.Exception) {
                Toast.makeText(
                    requireActivity(),
                    "Image not loading" + e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return uri
    }
}