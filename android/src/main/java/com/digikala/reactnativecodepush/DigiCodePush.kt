package com.digikala.reactnativecodepush

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import java.io.*
import java.lang.Exception
import java.net.*
import java.util.zip.ZipException
import java.util.zip.ZipFile

object DigiCodePush {

    /**
     * Get last bundle file path or default bundle path
     *
     * @param context
     * @param defaultFilePath
     *
     * @return
     */
    fun getJSBundleFilePath(context: Context, defaultFilePath: String?): String? {
        val sharedPreferences =
            context.getSharedPreferences(PREF_SCOPE, Context.MODE_PRIVATE)
        val address = sharedPreferences.getString(PREF_BUNDLE_ADDRESS_KEY, "")
        val appVersion = sharedPreferences.getInt(PREF_APP_VERSION_KEY, -1)
        return if (!address.isNullOrEmpty() && appVersion == getAppVersionCode(context)) {
            address
        } else defaultFilePath
    }

    internal fun getDocumentDirectoryPath(context: Context): String {
        return context.filesDir.absolutePath
    }


    internal fun storeBundlePath(context: Context, bundleFileAddress: String) {
        val sharedPreferences =
            context.getSharedPreferences(PREF_SCOPE, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString(PREF_BUNDLE_ADDRESS_KEY, bundleFileAddress)
        editor.putInt(PREF_APP_VERSION_KEY, getAppVersionCode(context))
        editor.apply()
    }

    internal fun downloadFile(
        fileUrl: URL,
        destinationPath: String,
        onComplete: (downloadedFile: File?, error: Throwable?) -> Unit
    ) {
        Thread {
            try {
                val `is`: InputStream = fileUrl.openStream()

                val dis = DataInputStream(`is`)

                val buffer = ByteArray(1024)
                var length: Int

                val file = File(
                    destinationPath
                )

                val fos = FileOutputStream(
                    file
                )
                while (dis.read(buffer).also { length = it } > 0) {
                    fos.write(buffer, 0, length)
                }

                Handler(Looper.getMainLooper()).post {
                    onComplete(file, null)
                }

            } catch (e: ProtocolException) {
                onComplete(null, e)
                Log.e("DOWNLOAD FILE", e.message, e)
            } catch (e: SocketException) {
                onComplete(null, e)
                Log.e("DOWNLOAD FILE", e.message, e)
            } catch (e: IOException) {
                onComplete(null, e)
                Log.e("DOWNLOAD FILE", e.message, e)
            } catch (e: ZipException) {
                onComplete(null, e)
                Log.e("DOWNLOAD FILE", e.message, e)
            } catch (e: Exception) {
                onComplete(null, e)
                Log.e("DOWNLOAD FILE", e.message, e)
            }


        }.start()
    }


    internal fun findFileWithExtensionInDirectory(dir: File, ext: String): File? {
        val files = dir.listFiles()
        return files?.first { it.isFile && it.path.endsWith(ext) }
    }

    internal fun getBundleFileName(context: Context): String {
        return "${context.packageName.replace(".", "-")}-bundle"
    }


    /**
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    @Throws(IOException::class)
    internal fun unzip(zipFilePath: File, destDirectory: String) {

        File(destDirectory).run {
            if (!exists()) {
                mkdirs()
            }
        }

        ZipFile(zipFilePath).use { zip ->

            zip.entries().asSequence().forEach { entry ->

                zip.getInputStream(entry).use { input ->


                    val filePath = destDirectory + File.separator + entry.name

                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else {
                        // if the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdir()
                    }

                }

            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param inputStream
     * @param destFilePath
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }


    private fun getAppVersionCode(context: Context): Int {
        val packageInfo: PackageInfo =
            context.packageManager.getPackageInfo(context.packageName, 0)
        return PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
    }

    private const val BUFFER_SIZE = 4096
    private const val PREF_SCOPE = "DigiPush"
    private const val PREF_BUNDLE_ADDRESS_KEY = "lastAddress"
    private const val PREF_APP_VERSION_KEY = "DigiPush"
    internal const val MODULE_NAME = "RNDigiCodePushModule"

}
