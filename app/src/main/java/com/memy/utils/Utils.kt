package com.memy.utils

import android.app.Activity
import android.net.ConnectivityManager
import android.graphics.Bitmap
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Point
import android.net.Uri
import android.webkit.MimeTypeMap
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Base64
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

/**
 * Common util class
 */
object Utils {
    /**
     * Method Handling to get current country code
     *
     * @param cID country code id
     * @return country code
     */
    fun getCountryName(cID: String?): String {
        val loc = Locale("en", cID)
        return loc.displayCountry.trim { it <= ' ' }
    }

    /**
     * Method used to get the device width and height
     *
     * @param context activity context
     * @return point value
     */
    fun getMeasurementDetail(context: Context): Point {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    fun isNetworkConnected(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri
                    .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase()
            )
        }
        return mimeType
    }

    fun getRealPath(context: Context, uri: Uri?): String? {
        var docId = ""
        try {
            docId = DocumentsContract.getDocumentId(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                docId = DocumentsContract.getTreeDocumentId(uri)
            } catch (e1: Exception) {
                e.printStackTrace()
            }
        }
        val split = docId.split(":").toTypedArray()
        val type = split[0]
        val contentUri: Uri
        contentUri = when (type) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
        val selection = "_id=?"
        val selectionArgs = arrayOf(
            split[1]
        )
        return getDataColumn(context, contentUri, selection, selectionArgs)
    }

    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                val value = cursor.getString(column_index)
                return if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith(
                        "file://"
                    )
                ) {
                    null
                } else value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getFilePathFromContentUri(
        selectedVideoUri: Uri?,
        contentResolver: ContentResolver
    ): String {
        val filePath: String
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(selectedVideoUri!!, filePathColumn, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }

    fun getImageRealPath(contentResolver: ContentResolver, uri: Uri, whereClause: String?): String {
        var ret = ""
        // Query the URI with the condition.
        val cursor = contentResolver.query(uri, null, whereClause, null, null)
        if (cursor != null) {
            val moveToFirst = cursor.moveToFirst()
            if (moveToFirst) {
                // Get columns name by URI type.
                var columnName = MediaStore.Images.Media.DATA
                if (uri === MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA
                } else if (uri === MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA
                } else if (uri === MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA
                }
                // Get column index.
                val imageColumnIndex = cursor.getColumnIndex(columnName)
                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex)
            }
        }
        return ret
    }

    @JvmStatic
    fun isImageFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.toLowerCase().startsWith("image")
    }

    @JvmStatic
    fun isVideoFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.toLowerCase().startsWith("video")
    }

    @JvmStatic
    fun isAudioFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.toLowerCase().startsWith("audio")
    }

    @JvmStatic
    fun getFormattedDate(dateStr: String?): String {
        try {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = simpleDateFormat.parse(dateStr)
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = date.time
            val now = Calendar.getInstance()
            val timeFormatString = "hh:mm aa"
            val dateTimeFormatString = "MMMM d, hh:mm aa"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
                "Today " + DateFormat.format(timeFormatString, smsTime)
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                "Yesterday " + DateFormat.format(timeFormatString, smsTime)
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format("MMMM dd yyyy, hh:mm aa", smsTime).toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun isValidMobileNumber(countryCode: String?, mobileNumber: String?): Boolean {
        var countryCode = countryCode
        var mobileNumber = mobileNumber
        if (TextUtils.isEmpty(countryCode)) {
            countryCode = ""
        }
        if (TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(mobileNumber?.trim { it <= ' ' })) {
            mobileNumber = ""
        }
        return if (countryCode.equals("+91", ignoreCase = true) || countryCode.equals(
                "91",
                ignoreCase = true
            )
        ) {
            mobileNumber?.length == 10
        } else {
            mobileNumber?.length ?: 0 >= 8
        }
    }

    fun splitMobileNumber(mobileNumber: String) : Pair<String,String> {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()

        try {
            // Parse the input mobile number
            val phoneNumber = phoneNumberUtil.parse(mobileNumber, null)

            // Get the country code and phone number
            val countryCode = "+" + phoneNumber.countryCode
            val localPhoneNumber = phoneNumber.nationalNumber.toString()

            // Print or use the components as needed
            println("Country Code: $countryCode")
            println("Phone Number: $localPhoneNumber")
            return Pair(countryCode,localPhoneNumber)
        } catch (e: Exception) {
            // Handle parsing errors here
            println("Error parsing mobile number: ${e.message}")
        }
        return Pair("+91","")
    }
}