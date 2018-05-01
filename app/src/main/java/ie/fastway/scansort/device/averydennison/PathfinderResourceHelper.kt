package ie.fastway.scansort.device.averydennison

import avd.api.core.exceptions.ApiException
import avd.api.core.imports.ResourceMediaType
import avd.api.resources.ResourceManager
import com.tubbert.powdroid.android.context.AssetProvider
import com.tubbert.powdroid.util.Syncer
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber
import java.io.*


internal class PathfinderResourceHelper(private val assetProvider: AssetProvider) {

    companion object {
        const val RESOURCE_DIRECTORY_NAME = "fastway_scansort/avd_resources"

        const val FONT_ARIAL_ALIAS = "Arial"
        const val FONT_ARIAL_ASSET = "Arial.ttf"

        const val LNT_SHIPMENT_LABEL_ALIAS = "ShipmentLabel"
        const val LNT_SHIPMENT_LABEL_ASSET = "LabelShipment.lnt"

        /*
        const val LNT_SAMPLE_ALIAS = "SamplePrint"
        const val LNT_SAMPLE_ASSET = "SamplePrint.LNT"
        */

    }

    private lateinit var resourceStorageDir: File

    private lateinit var resourcePath: String

    @Synchronized
    fun initialiseResources() {

        if (LogConfig.AVD_PRINTER) {
            Timber.d("Initialising resources for Pathfinder printer.")
        }

        setupStorageDirectory()


        printResourceContentsToLogs("Before clearAllResources")

        try {
            clearAllResources()
        }
        catch (e: ApiException) {
            if (LogConfig.AVD_PRINTER) {
                Timber.e(e, "Could not execute clearAllResources on Pathfinder.")
            }
        }

        printResourceContentsToLogs("Before Setup")

        try {
            // Register the Arial font:
            val previouslyRegisteredFonts = ResourceManager
                    .getResourceList(ResourceMediaType.Font)
                    .asList()

            if (!previouslyRegisteredFonts.contains(FONT_ARIAL_ALIAS)) {
                registerResource(ResourceMediaType.Font, FONT_ARIAL_ALIAS, FONT_ARIAL_ASSET)
            }

            // Find the LNTs that are already registered.
            val previouslyRegisteredLnts = ResourceManager
                    .getResourceList(ResourceMediaType.Lnt)
                    .asList()

            /*
            // FIXME: Don't add the sample LNT
            // Register the Sample label LNT file:
            if (!previouslyRegisteredLnts.contains(LNT_SAMPLE_ALIAS)) {
                registerResource(ResourceMediaType.Lnt, LNT_SAMPLE_ALIAS, LNT_SAMPLE_ASSET)
            }
            */

            // Register the ShipmentLabel label LNT file:
            if (!previouslyRegisteredLnts.contains(LNT_SHIPMENT_LABEL_ALIAS)) {
                registerResource(ResourceMediaType.Lnt, LNT_SHIPMENT_LABEL_ALIAS, LNT_SHIPMENT_LABEL_ASSET)
            }

        }
        catch (e: ApiException) {
            if (LogConfig.AVD_PRINTER) {
                Timber.e(e, "Could not initialiseResources.")
            }
        }

        printResourceContentsToLogs("After Setup")
    }

    private fun setupStorageDirectory() {

        resourceStorageDir = File(assetProvider.getExternalStorageDir().toString(), RESOURCE_DIRECTORY_NAME)
        if (!resourceStorageDir.exists()) {
            resourceStorageDir.mkdirs()
        }
        if (!resourceStorageDir.canWrite()) {

            // The external storage directory is not writeable, so we write
            // to the "files" directory instead.

            if (LogConfig.AVD_PRINTER) {
                Timber.e("Cannot write to the data directory. Will attempt to write to filesDir instead. resourceStorageDir=$resourceStorageDir;")
            }

            resourceStorageDir = File(assetProvider.getFilesDir().absolutePath + "/" + RESOURCE_DIRECTORY_NAME)

            Syncer.doubleCheckedLock(
                    PathfinderResourceHelper::class,
                    { !resourceStorageDir.exists() }) {

                if (LogConfig.AVD_PRINTER) {
                    Timber.d("Creating resource directory in filesDir. resourceStorageDir=$resourceStorageDir")
                }

                val createdOk = resourceStorageDir.mkdirs()

                if (LogConfig.AVD_PRINTER) {
                    Timber.d("Resource directory createdOk=$createdOk;")
                }
            }

            if (!resourceStorageDir.canWrite()) {
                throw IllegalStateException("The app's resource storage directory is not writeable.")
            }

        }
        resourcePath = resourceStorageDir.absolutePath + "/"
        ResourceManager.initializeResourcePath(resourcePath)
    }

    private fun clearAllResources() {
        for (file in resourceStorageDir.listFiles()) {
            if (!file.isDirectory) {
                if (LogConfig.AVD_PRINTER) {
                    Timber.d("Deleting resource file. fileName=${file.name}")
                }

                file.delete()
            }
        }
    }

    private inline fun printResourceContentsToLogs(startMessage: String? = null) {
        if (LogConfig.AVD_PRINTER) {
            Timber.d("printResourceContentsToLogs; ---- $startMessage ----")

            for (file in resourceStorageDir.listFiles()) {
                Timber.d("Resource file; absolutePath=${file.absolutePath}; name=${file.name}")
            }
        }
    }


    private fun registerResource(
            mediaType: ResourceMediaType, alias: String, fileName: String) {

        if (LogConfig.AVD_PRINTER) {
            Timber.v("Registering resource file. mediaType=$mediaType; alias=$alias; fileName=$fileName;")
        }

        val file = File(resourcePath, fileName)

        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = assetProvider.openAsset(fileName)
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var readSize = 0
            do {
                readSize = inputStream.read(buffer, 0, buffer.size)
                if (readSize == -1) break

                outputStream.write(buffer, 0, readSize)
            } while (true)

            outputStream.flush()
            outputStream.close()

        }
        catch (e: FileNotFoundException) {
            if (LogConfig.AVD_PRINTER) {
                Timber.e(e, "Resource asset file not found; fileName=$fileName; alias=$alias;")
            }
            throw e
        }
        catch (e: IOException) {
            if (LogConfig.AVD_PRINTER) {
                Timber.e(e, "Could not write to asset file. fileName=$fileName; alias=$alias;")
            }
            throw e
        }
        finally {
            outputStream?.flush()
            outputStream?.close()
            inputStream?.close()
        }

        ResourceManager.registerResource(mediaType, alias, file.absolutePath);

        // We do a final check to be sure the resource is registered:
        if (!ResourceManager.checkResource(mediaType, alias)) {
            if (LogConfig.AVD_PRINTER) {
                Timber.e("Resource registered, but ResourceManager checkResource check failed. fileName=$fileName; alias=$alias;")
            }
        }
    }

}