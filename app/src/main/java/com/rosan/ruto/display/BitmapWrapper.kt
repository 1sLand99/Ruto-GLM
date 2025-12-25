package com.rosan.ruto.display

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.os.SharedMemory
import com.rosan.installer.ext.util.closeQuietly

class BitmapWrapper(val bitmap: Bitmap) : Parcelable {
    override fun describeContents(): Int =
        Parcelable.CONTENTS_FILE_DESCRIPTOR

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(bitmap.width)
        dest.writeInt(bitmap.height)
        dest.writeInt(bitmap.config!!.ordinal)

        val sharedMemory = SharedMemory.create("bitmap:${bitmap.hashCode()}", bitmap.byteCount)
        val buffer = sharedMemory.mapReadWrite()
        bitmap.copyPixelsToBuffer(buffer)

        dest.writeParcelable(sharedMemory, flags)
        SharedMemory.unmap(buffer)

        sharedMemory.closeQuietly()
    }

    companion object CREATOR : Parcelable.Creator<BitmapWrapper> {
        override fun createFromParcel(parcel: Parcel): BitmapWrapper? {
            val width = parcel.readInt()
            val height = parcel.readInt()
            val configId = parcel.readInt()
            val config = Bitmap.Config.entries.first { it.ordinal == configId }

            val sharedMemory = parcel.readParcelable(
                SharedMemory::class.java.classLoader,
                SharedMemory::class.java
            ) ?: return null

            try {
                val buffer = sharedMemory.mapReadWrite()
                val bitmap = Bitmap.createBitmap(width, height, config)
                bitmap.copyPixelsFromBuffer(buffer)
                SharedMemory.unmap(buffer)
                return BitmapWrapper(bitmap)
            } finally {
                sharedMemory.closeQuietly()
            }
        }

        override fun newArray(size: Int): Array<BitmapWrapper?> = arrayOfNulls(size)
    }
}