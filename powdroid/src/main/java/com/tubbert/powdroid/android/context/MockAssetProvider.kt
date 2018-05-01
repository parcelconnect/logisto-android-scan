package com.tubbert.powdroid.android.context

import java.io.File
import java.io.InputStream

/**
 *
 */
class MockAssetProvider: AssetProvider {

    override fun openAsset(fileName: String): InputStream {
        throw NotImplementedError("MockAssetProvider.open is not implemented.")
    }

    override fun getFilesDir(): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getExternalStorageDir(): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}