package com.tubbert.powdroid.android.context

import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Interface that can be used to mock [android.content.res.AssetManager].
 *
 * Should be used in place of [android.content.Context] where only the parts of Context
 * which deal with the assets and file system are needed.
 *
 */
interface AssetProvider {

    @Throws(IOException::class)
    fun openAsset(fileName: String): InputStream;

    /**
     * Returns the absolute path to the directory on the filesystem where
     * files created with {@link #openFileOutput} are stored.
     */
    fun getFilesDir() : File;

    fun getExternalStorageDir(): File;

}