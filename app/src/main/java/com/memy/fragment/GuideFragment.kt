package com.memy.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.memy.R
import com.memy.utils.Constents

class GuideFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.guide_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(Constents.INTENT_BUNDLE_GUIDE_ARGUMENT_TAG) }?.apply {
            val imageView: AppCompatImageView = view.findViewById(R.id.imageView)
            val imageResId = getInt(Constents.INTENT_BUNDLE_GUIDE_ARGUMENT_TAG)
            imageView.setImageResource(imageResId)
        }
    }
}