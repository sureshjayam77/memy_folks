package com.memy.pojo

import android.net.Uri
import com.memy.utils.StoryMediaType

data class AddStoryMediaObj(val filePath : String, val fileURI : Uri,val mediaType : StoryMediaType) {
}