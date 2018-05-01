package com.tubbert.powdroid.android.context

import android.content.Context
import android.content.res.AssetManager
import android.os.Environment

/**
 * Wrapper for using the native [AssetManager] as [AssetProvider].
 */
class ContextAssetProvider(private val context: Context) : AssetProvider {

    override fun openAsset(fileName: String) = context.resources.assets.open(fileName)

    override fun getFilesDir() = context.filesDir

    override fun getExternalStorageDir()= Environment.getExternalStorageDirectory()
}