package com.myshopping.utils

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

object UtilsFunctions {

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity.startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

}