package com.digikala.reactnativecodepush

import android.os.Looper

import com.facebook.react.ReactInstanceManager

import com.facebook.react.ReactActivity

import com.facebook.react.ReactApplication

import java.lang.Exception

import android.content.Context;
import android.content.pm.PackageInfo
import android.util.Log
import java.lang.reflect.Field;
import android.os.Handler;
import androidx.core.content.pm.PackageInfoCompat
import com.facebook.react.bridge.*
import java.io.*
import java.util.*

import java.net.ProtocolException
import java.net.SocketException
import java.net.URL
import java.util.zip.ZipException


class RNDigiCodePushModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName() = DigiCodePush.MODULE_NAME

    private val appContext: Context? by lazy {
        currentActivity?.applicationContext
    }


    private val instanceManager: ReactInstanceManager? by lazy {
        val application = (currentActivity as? ReactActivity?)?.application as? ReactApplication
        application?.reactNativeHost?.reactInstanceManager
    }


    /**
     * Use reflection to find and set the appropriate fields on ReactInstanceManager
     *
     * @param latestJSBundleFile
     */
    @Throws(IllegalAccessException::class)
    private fun setJSBundle(latestJSBundleFile: String) {
        try {
            val latestJSBundleLoader: JSBundleLoader =
                if (latestJSBundleFile.lowercase(Locale.getDefault()).startsWith("assets://")) {
                    JSBundleLoader.createAssetLoader(
                        reactApplicationContext,
                        latestJSBundleFile,
                        false
                    )
                } else {
                    JSBundleLoader.createFileLoader(latestJSBundleFile)
                }
            val bundleLoaderField: Field =
                instanceManager!!.javaClass.getDeclaredField("mBundleLoader")
            bundleLoaderField.isAccessible = true
            bundleLoaderField.set(instanceManager, latestJSBundleLoader)
        } catch (e: Exception) {
            throw IllegalAccessException("Could not setJSBundle")
        }
    }


    /**
     * set new bundle
     *
     * @param bundleFileAddress address of new downloaded bundle file
     * @param promise
     *
     */
    @ReactMethod
    fun setBundle(bundleFileAddress: String, promise: Promise) {
        val file = File(bundleFileAddress)
        if (!file.exists()) {
            promise.reject(Throwable("Bundle file not exist"))
            return
        }

        // check if context is not null
        if (appContext == null) {
            Log.e("TAG", "loadBundle: Context Null")
            promise.reject(Throwable("Context is null"))
            return
        }

        if (instanceManager == null) {
            Log.e("TAG", "loadBundle: instanceManager Null")
            promise.reject(Throwable("InstanceManager is null"))
            return
        }

        // store bundle path in shared preferences
        DigiCodePush.storeBundlePath(appContext!!, bundleFileAddress)

        try {
            // set new bundle in bridge
            setJSBundle(bundleFileAddress)
            promise.resolve(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            promise.reject(e)
        }
    }

    /**
     * Reload Application
     */
    @ReactMethod
    fun reloadBundle() {
        Handler(Looper.getMainLooper()).post {
            try {
                // Recreate the react application and context
                instanceManager?.recreateReactContextInBackground()
            } catch (e: Exception) {
                // restart activity in old school way
                currentActivity?.let { currentActivity ->
                    currentActivity.runOnUiThread { currentActivity.recreate() }
                }
            }
        }
    }


    /**
     * download bundle file
     *
     * @param bundleUrl url of file to download
     * @param promise
     *
     */
    @ReactMethod
    fun downloadBundle(bundleUrl: String, promise: Promise) {
        // check if context is not null
        if (appContext == null) {
            Log.e("TAG", "loadBundle: Context Null")
            promise.reject(Throwable("Context is null"))
            return
        }

        val fileName = DigiCodePush.getBundleFileName(appContext!!)

        // get zip path and destination path
        val zipFilePath = DigiCodePush.getDocumentDirectoryPath(appContext!!) + "/" + fileName + ".zip"

        val url = URL(bundleUrl)

        // download bundle file
        DigiCodePush.downloadFile(url, zipFilePath) { file, error ->
            if (file != null) {
                Thread {
                    // unzip downloaded file
                    unzipBundleFile(file)?.let { bundlePath ->
                        Handler(Looper.getMainLooper()).post {
                            // send bundle file path to js side
                            promise.resolve(bundlePath)
                        }
                    }
                }.start()
            } else {
                promise.reject(error)
            }
        }

    }


    /**
     * Unzip downloaded file
     * @param zipFile
     * @return
     */
    private fun unzipBundleFile(zipFile: File): String? {
        val fileName = DigiCodePush.getBundleFileName(appContext!!)
        val destDirPath =  DigiCodePush.getDocumentDirectoryPath(appContext!!) + "/" + fileName

        try {
            DigiCodePush.unzip(zipFile, destDirPath)
            val bundle =
                DigiCodePush.findFileWithExtensionInDirectory(File(destDirPath), ".bundle")
            bundle?.name?.let { name ->
                return "$destDirPath/$name"
            }

        } catch (e: ProtocolException) {
            Log.e("UNZIP BUNDLE", e.message, e)
        } catch (e: SocketException) {
            Log.e("UNZIP BUNDLE", e.message, e)
        } catch (e: IOException) {
            Log.e("UNZIP BUNDLE", e.message, e)
        } catch (e: ZipException) {
            Log.e("UNZIP BUNDLE", e.message, e)
        } catch (e: Exception) {
            Log.e("UNZIP BUNDLE", e.message, e)
        }

        return null

    }
}
