# FencerUpdate

###### 使用示例

```kotlin
/**
 * 版本更新管理工具；
 * 
 * 
 * @param setting 是否是设置界面-》有新版本就弹出弹窗
 *  1。 val updateManager = UpdateManager(context,setting=false)
 *  2。 updateManager.init()
 *  3. 主动请求 requestVersion ，页面的话 在 onResume（） 会触发版本的更新网络请求
 * 
 */
class UpdateManager(private val context:AppCompatActivity ,private val setting:Boolean =false) {

    /**
     * 这里的viewModel 代码请求的逻辑处理 封装
     */
    private val viewModel by context.viewModels<VersionViewModel>()

  /**
  需要主动调用此方法-》
  */
    fun init(){
        //
        viewModel.currentNetVersion.observe(context){
            it?.let {
                judgeVersion(it)
            }
        }
        context.lifecycle.addObserver(object :DefaultLifecycleObserver{
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                if (!downloding&&!setting){
                    requestVersion()
                }
            }
        })
    }

  /**
  主动请求
  *
  /
    fun requestVersion(){
        viewModel.loadData()
    }


    /***
     * 判断版本号
     */
    private  fun judgeVersion(version:AppVersionBean){
        val appVersionCode = AppUtils.getAppVersionCode()
        val netVersionCode =version.version.toInt()
        val skipCode = SPUtils.getInstance().getInt("versionSkipCode",0)
        val forceUpdate =version.isupdate == "1"
        //最新版本的情况下；
        if (appVersionCode>=netVersionCode){
            com.orhanobut.logger.Logger.d("当前已经是最新版本")
            if (setting){
                ToastUtils.showShort("当前已经是最新版本")
            }
            return
        }
        //强制更新或者设置中的更新
        if (forceUpdate||setting){
             prepareUpdate(version)
        }else{
            //跳过了更新，且不是强制更新
            if (skipCode>=netVersionCode){
                com.orhanobut.logger.Logger.d("用户原来跳过了更新")
                return
            }
            prepareUpdate(version)
        }


    }



    /**
     * 进行版信息的更新
     */
    private fun prepareUpdate(version: AppVersionBean) {
        val url =  version.versionurl
        val  manager = DownloadManager.Builder(context).run {
           apkUrl(url)
          //横向
           showOrientationVertical(true)
           apkName("碧水积分APP.apk")
           smallIcon(R.mipmap.ic_launcher)
          //网络版本
           apkVersionCode(version.version.toInt())
          //版本名称
           apkVersionName(version.versionName)
          //size
           apkSize("42.4")
          //是否是pad
           isPad(false)
          //是否强制更新
           forcedUpgrade(version.isupdate == "1")
          //更新内容描述
           apkDescription(version.remark?:"---")
           enableLog(false)
           jumpInstallPage(true)
           dialogButtonTextColor(Color.WHITE)
           showNotification(true)
           showBgdToast(true)
            dialogProgressBarColor(Color.parseColor("#3592c4"))
           dialogButtonColor(Color.parseColor("#3885d0"))
           onDownloadListener(listenerAdapter)
          
           onButtonClickListener(object :OnButtonClickListener{
               override fun onButtonClick(id: Int) {
                   //保存跳过（跳过的情况下次不再进行请求）
                   if (id==CANCEL){
                       SPUtils.getInstance().put("versionSkipCode",version.version.toInt())
                   }
               }

           })
           build()
       }
        manager.download()
    }

    private var downloding =false
    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {
        override fun downloading(max: Int, progress: Int) {
            downloding = max>progress
        }

        override fun done(apk: File) {
            super.done(apk)
            downloding=true
        }

        override fun cancel() {
            super.cancel()
            downloding=false
        }

        override fun error(e: Throwable) {
            super.error(e)
            downloding=false
        }
    }

}
```

