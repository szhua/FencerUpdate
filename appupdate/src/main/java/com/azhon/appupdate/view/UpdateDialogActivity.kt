package com.azhon.appupdate.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import com.azhon.appupdate.R
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.service.DownloadService
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.DensityUtil
import java.io.File


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.view
 * FileName:    UpdateDialogActivity
 * CreateDate:  2022/4/7 on 17:40
 * Desc:
 * @author   azhon
 */

class UpdateDialogActivity : AppCompatActivity(), View.OnClickListener {

    private val install = 0x45
    private lateinit var manager: DownloadManager
    private lateinit var apk: File
    private lateinit var progressBar: NumberProgressBar
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        title = ""
        setContentView(R.layout.dialog_update)
        init()
    }

    private fun init() {
        manager = DownloadManager.getInstance()
        manager.onDownloadListeners.add(listenerAdapter)
        setWindowSize()
        initView()
    }

    private fun initView() {
        val ibClose = findViewById<View>(R.id.ib_close)
        val lineHor = findViewById<View>(R.id.line_hor)
        val lineVer = findViewById<View>(R.id.line_vertical)
        val ivBg = findViewById<ImageView>(R.id.iv_bg)
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvSize = findViewById<TextView>(R.id.tv_size)
        val tvDescription = findViewById<TextView>(R.id.tv_description)
        val dialogContainer =findViewById<LinearLayout>(R.id.dialog_container)
        val btnContainer = findViewById<LinearLayout>(R.id.close_btn_container)
        progressBar = findViewById(R.id.np_bar)
        btnUpdate = findViewById(R.id.btn_update)
       // progressBar.visibility = if (manager.forcedUpgrade) View.VISIBLE else View.GONE
        btnUpdate.tag = 0
        btnUpdate.setOnClickListener(this)
        ibClose.setOnClickListener(this)



        if (manager.dialogImage != -1) {
            ivBg.setBackgroundResource(manager.dialogImage)
        }
        if (manager.dialogButtonTextColor != -1) {
            btnUpdate.setTextColor(manager.dialogButtonTextColor)
        }
        if (manager.dialogProgressBarColor != -1) {
            progressBar.reachedBarColor = manager.dialogProgressBarColor
            progressBar.setProgressTextColor(manager.dialogProgressBarColor)
        }
        if (manager.dialogButtonColor != -1) {
            val colorDrawable = GradientDrawable().apply {
                setColor(manager.dialogButtonColor)
                cornerRadius = DensityUtil.dip2px(this@UpdateDialogActivity, 3f)
            }
            val drawable = StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), colorDrawable)
                addState(IntArray(0), colorDrawable)
            }
            btnUpdate.background = drawable
        }

        if (manager.isPad){
            val dialogInner  = findViewById<LinearLayout>(R.id.dialog_inner)
            val layoutParams = dialogInner.layoutParams
            layoutParams.width = DensityUtil.dip2px(this,320.toFloat()).toInt()
            //layoutParams.setMargins(0,DensityUtil.dip2px(this, -20f).toInt(),0,0)
            dialogInner.layoutParams =layoutParams
        }

        if (manager.showOrientationVertical){
            lineVer.visibility =View.VISIBLE
            lineHor.visibility=View.GONE
            dialogContainer.orientation = LinearLayout.VERTICAL
            btnContainer.orientation  = LinearLayout.VERTICAL
        }else{
            lineVer.visibility =View.GONE
            lineHor.visibility=View.VISIBLE
            dialogContainer.orientation = LinearLayout.HORIZONTAL
            btnContainer.orientation  = LinearLayout.HORIZONTAL
        }

        if (manager.forcedUpgrade) {
            lineHor.visibility = View.GONE
            lineVer.visibility =View.GONE
            ibClose.visibility = View.GONE
        }
        if (manager.apkVersionName.isNotEmpty()) {
            tvTitle.text =
                String.format(resources.getString(R.string.dialog_new), manager.apkVersionName)
        }
        if (manager.apkSize.isNotEmpty()) {
            tvSize.text =
                String.format(resources.getString(R.string.dialog_new_size), manager.apkSize)
            tvSize.visibility = View.VISIBLE
        }




        tvDescription.text = manager.apkDescription
    }

    private fun setWindowSize() {
        val attributes = window.attributes
        attributes.width = (resources.displayMetrics.widthPixels * 0.75f).toInt()
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT
        attributes.gravity = Gravity.CENTER
        window.attributes = attributes
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_close -> {
                manager.onButtonClickListener?.onButtonClick(OnButtonClickListener.CANCEL)
                finish()
            }
            R.id.btn_update -> {
                if (btnUpdate.tag == install) {
                    ApkUtil.installApk(this, Constant.AUTHORITIES!!, apk)
                    return
                }
//                if (manager.forcedUpgrade) {
//                    btnUpdate.isEnabled = false
//                    btnUpdate.text = resources.getString(R.string.background_downloading)
//                } else {
//                    finish()
//                }
                btnUpdate.isEnabled = false
                btnUpdate.text = resources.getString(R.string.background_downloading)
                manager.onButtonClickListener?.onButtonClick(OnButtonClickListener.UPDATE)
                startService(Intent(this, DownloadService::class.java))
            }
        }
    }

    override fun onBackPressed() {
        if (manager.forcedUpgrade) return
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {

        override fun downloading(max: Int, progress: Int) {
            if (max != -1 && progressBar.visibility == View.VISIBLE) {
                val curr = (progress / max.toDouble() * 100.0).toInt()
                progressBar.progress = curr
            } else {
                progressBar.visibility = View.GONE
            }
        }

        override fun done(apk: File) {
            this@UpdateDialogActivity.apk = apk
            if (manager.forcedUpgrade) {
                btnUpdate.tag = install
                btnUpdate.isEnabled = true
                btnUpdate.text = resources.getString(R.string.click_hint)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        overrideFontScale(newBase)
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun overrideFontScale(context: Context?) {
        if (context == null) return
        context.resources.configuration.let {
            it.fontScale = 1f // 保持字体比例不变，始终为 1.
            applyOverrideConfiguration(it) // 应用新的配置
        }
    }

}