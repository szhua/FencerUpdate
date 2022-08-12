package com.fencer.updatesdk

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareUpdate(VersionBean.Version().apply {
            fileUrl ="http://120.224.28.51:8014/share/yzt_file/20220719/apk/19ad4a9e-d0de-4e8c-b72d-80ac35498634.apk"
            updateDescription="www"
            versionName="wwwww"
            versionCode = 100
        })


    }


    /**
     * 进行版信息的更新
     */
    private fun prepareUpdate(version: VersionBean.Version) {
        val url =  version.fileUrl
        val manager = DownloadManager.Builder(this).run {
            apkUrl(url)
            showOrientationVertical(true)
            apkName("聊城水利一张图.apk")
            smallIcon(R.mipmap.ic_launcher)
            apkVersionCode(version.versionCode)
            apkVersionName(version.versionName)
            apkSize("62.4")
            forcedUpgrade(false)
            apkDescription(version.updateDescription?:"---")
            enableLog(false)
            jumpInstallPage(true)
            dialogButtonTextColor(Color.WHITE)
            showNotification(true)
            showBgdToast(false)
            isPad(false)
            dialogButtonColor(Color.parseColor("#3885d0"))
            forcedUpgrade(false)
            onDownloadListener(listenerAdapter)
            build()
        }
        manager.download()
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {
        override fun downloading(max: Int, progress: Int) {

        }
    }


}