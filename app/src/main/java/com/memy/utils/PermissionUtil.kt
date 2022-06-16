package com.memy.utils

import android.Manifest
import android.app.Activity
import android.content.Context

import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager


open class PermissionUtil {

    val MANDATORY_FOR_CAMERA_CODE = 1643
    val MANDATORY_FOR_STORAGE_CODE = 1644

    /**
     * Method helps to add a permission into permission list
     */
    private fun addPermission(
        permissionList: MutableList<String>,
        permission: String,
        context: Context
    ): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(permission)
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as Activity),
                    permission
                )
            ) return false
        }
        return true
    }

    /**
     * Method handling the permissions for camera
     *
     * @param context    activity context
     * @return boolean variable
     */
    fun initRequestPermissionForCamera(context: Context?,showRationalDialog: Boolean?): Boolean {
        var status = true
        val permissionList: MutableList<String> = ArrayList()
        addPermission(permissionList, Manifest.permission.CAMERA, context!!)
        addPermission(permissionList, Manifest.permission.READ_EXTERNAL_STORAGE, context)
        addPermission(permissionList, Manifest.permission.WRITE_EXTERNAL_STORAGE, context)
        if (permissionList.size > 0) {
            if(showRationalDialog == true) {
                ActivityCompat.requestPermissions(
                    (context as Activity?)!!,
                    permissionList.toTypedArray(),
                    0
                )
            }
            status = false
        }
        return status
    }

    /**
     * Method handling the permissions for camera
     *
     * @param context    activity context
     * @return boolean variable
     */
    fun requestPermissionForCamera(context: Context?,showRationalDialog: Boolean?): Boolean {
        var status = true
        val permissionList: MutableList<String> = ArrayList()
        addPermission(permissionList, Manifest.permission.CAMERA, context!!)
        addPermission(permissionList, Manifest.permission.READ_EXTERNAL_STORAGE, context)
        addPermission(permissionList, Manifest.permission.WRITE_EXTERNAL_STORAGE, context)
        if (permissionList.size > 0) {
            if(showRationalDialog == true) {
                ActivityCompat.requestPermissions(
                    (context as Activity?)!!,
                    permissionList.toTypedArray(),
                    MANDATORY_FOR_CAMERA_CODE
                )
            }
            status = false
        }
        return status
    }

    fun isCameraStoragePermissionUnderDontAsk(context : Context) : Boolean{
        return ((!ActivityCompat.shouldShowRequestPermissionRationale((context as Activity),Manifest.permission.CAMERA)) || (!ActivityCompat.shouldShowRequestPermissionRationale((context as Activity),Manifest.permission.READ_EXTERNAL_STORAGE)) || (!ActivityCompat.shouldShowRequestPermissionRationale((context as Activity),Manifest.permission.WRITE_EXTERNAL_STORAGE)))
    }
}