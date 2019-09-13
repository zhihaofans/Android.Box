package com.zhihaofans.androidbox.util


import android.annotation.SuppressLint
import androidx.exifinterface.media.ExifInterface


/**
 * 在此写用途

 * @author: zhihaofans

 * @date: 2019-09-13 20:12

 */
@SuppressLint("RestrictedApi")
class ImageExifUtil(fileName: String) {
    private val exif = ExifInterface(fileName)

    private fun getExifStr(tagName: String) = exif.getAttribute(tagName)
    private fun getExifInt(tagName: String) = exif.getAttributeInt(tagName, -1)
    private fun setExif(tagName: String, value: String?) = exif.setAttribute(tagName, value)
    var deviceBrand
        get() = getExifStr(ExifInterface.TAG_MAKE)
        set(value) = setExif(ExifInterface.TAG_MAKE, value)
    var deviceModel
        get() = getExifStr(ExifInterface.TAG_MODEL)
        set(value) = setExif(ExifInterface.TAG_MODEL, value)
    var dateTime
        get() = exif.dateTime
        set(value) {
            exif.dateTime = value
        }
    var imageLength
        get() = getExifStr(ExifInterface.TAG_IMAGE_LENGTH)
        set(value) {
            setExif(ExifInterface.TAG_IMAGE_LENGTH, value)
        }

    fun getWidth() = getExifInt(ExifInterface.TAG_IMAGE_WIDTH)
    fun removeImageWidth(value: String?) = setExif(ExifInterface.TAG_IMAGE_WIDTH, value)

    fun getLocationLatitude() = getExifStr(ExifInterface.TAG_GPS_LATITUDE)
    fun removeLocationLatitude(value: String?) = setExif(ExifInterface.TAG_GPS_LATITUDE, value)

    fun getLocationLongitude() = getExifStr(ExifInterface.TAG_GPS_LONGITUDE)
    fun removeLocationLongitude(value: String?) = setExif(ExifInterface.TAG_GPS_LONGITUDE, value)

    fun getLocationLatitudeRef() = getExifStr(ExifInterface.TAG_GPS_LATITUDE_REF)
    fun removeLocationLatitudeRef(value: String?) = setExif(ExifInterface.TAG_GPS_LATITUDE_REF, value)

    fun getLocationLongitudeRef() = getExifStr(ExifInterface.TAG_GPS_LONGITUDE_REF)
    fun removeLocationLongitudeRef(value: String?) = setExif(ExifInterface.TAG_GPS_LONGITUDE_REF, value)


}