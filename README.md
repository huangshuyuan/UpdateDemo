# Android应用更新-自动检测版本及自动升级

## 步骤：
* 1.检测当前版本的信息AndroidManifest.xml-->manifest-->[Android]

* 2.从服务器获取版本号（版本号存在于xml文件中）并与当前检测到的版本进行匹配，如果不匹配，提示用户进行升级，如果匹配则进入程序主界面。（demo中假设需要更新）

* 3.当提示用户进行版本升级时，如果用户点击了“更新”，系统将自动从服务器上下载安装包并进行自动升级，如果点击取消将进入程序主界面。

## 效果图如下：

![更新](http://upload-images.jianshu.io/upload_images/3805053-cbbe809e3cbf8c96.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![下载1](http://upload-images.jianshu.io/upload_images/3805053-cf963c6429bd3147.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![下载2](http://upload-images.jianshu.io/upload_images/3805053-03ce7e0933e1f8ea.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![安装](http://upload-images.jianshu.io/upload_images/3805053-4ebbc800d33af51e.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
****



## 下面介绍一下代码的实现：

* 1.获取应用的当前版本号，我是封装了一个工具类来获取

```
 // 获取本版本号，是否更新
        int vision = Tools.getVersion(this);

```
获取当前版本号工具类：
```

public class Tools {
    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
     */
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            int versioncode = info.versionCode;
            return versioncode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
   
}
```

* 2.获取服务器版本号，是否要更新（此处就是简单的网络请求拿到需要的数据即可，我是写了固定值）
```
 // 获取更新版本号
    private void getVersion(final int vision) {
//         {"data":{"content":"其他bug修复。","id":"2","api_key":"android",
//         // "version":"2.1"},"msg":"获取成功","status":1}
        String data = "";
        //网络请求获取当前版本号和下载链接
        //实际操作是从服务器获取
        //demo写死了

        String newversion = "2.1";//更新新的版本号
        String content = "\n" +
                "就不告诉你我们更新了什么-。-\n" +
                "\n" +
                "----------万能的分割线-----------\n" +
                "\n" +
                "(ㄒoㄒ) 被老板打了一顿，还是来告诉你吧：\n" +

                "1.下架商品误买了？恩。。。我搞了点小动作就不会出现了\n" +
                "2.侧边栏、弹框优化 —— 这个你自己去探索吧，总得留点悬念嘛-。-\n";//更新内容
        String url = "http://openbox.mobilem.360.cn/index/d/sid/3429345";//安装包下载地址

        double newversioncode = Double
                .parseDouble(newversion);
        int cc = (int) (newversioncode);

        System.out.println(newversion + "v" + vision + ",,"
                + cc);
        if (cc != vision) {
            if (vision < cc) {
                System.out.println(newversion + "v"
                        + vision);
                // 版本号不同
                ShowDialog(vision, newversion, content, url);
            }
        }
    }
```
* 3.接下来就是下载文件了
（1） 显示下载
此处用的是自定义按钮：

```
 /**
     * 升级系统
     *
     * @param content
     * @param url
     */
    private void ShowDialog(int vision, String newversion, String content,
                            final String url) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.content(content).btnText("取消", "更新").title("版本更新 ")
                .titleTextSize(15f).show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {// left btn click listener
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {// right btn click listener

            @Override
            public void onBtnClick() {
                dialog.dismiss();
                // pBar = new ProgressDialog(MainActivity.this,
                // R.style.dialog);
                pBar = new CommonProgressDialog(MainActivity.this);
                pBar.setCanceledOnTouchOutside(false);
                pBar.setTitle("正在下载");
                pBar.setCustomTitle(LayoutInflater.from(
                        MainActivity.this).inflate(
                        R.layout.title_dialog, null));
                pBar.setMessage("正在下载");
                pBar.setIndeterminate(true);
                pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pBar.setCancelable(true);
                // downFile(URLData.DOWNLOAD_URL);
                final DownloadTask downloadTask = new DownloadTask(
                        MainActivity.this);
                downloadTask.execute(url);
                pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            }
        });
    }

```

原生的按钮：

```  
 new android.app.AlertDialog.Builder(this)
                .setTitle("版本更新")
                .setMessage(content)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        pBar = new CommonProgressDialog(MainActivity.this);
                        pBar.setCanceledOnTouchOutside(false);
                        pBar.setTitle("正在下载");
                        pBar.setCustomTitle(LayoutInflater.from(
                                MainActivity.this).inflate(
                                R.layout.title_dialog, null));
                        pBar.setMessage("正在下载");
                        pBar.setIndeterminate(true);
                        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pBar.setCancelable(true);
                        // downFile(URLData.DOWNLOAD_URL);
                        final DownloadTask downloadTask = new DownloadTask(
                                MainActivity.this);
                        downloadTask.execute(url);
                        pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                downloadTask.cancel(true);
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
```
（2）通过异步任务实现进度++

```

    /**
     * 下载应用
     *
     * @author Administrator
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    file = new File(Environment.getExternalStorageDirectory(),
                            DOWNLOAD_NAME);

                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pBar.setIndeterminate(false);
            pBar.setMax(100);
            pBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pBar.dismiss();
            if (result != null) {

//                // 申请多个权限。大神的界面
//                AndPermission.with(MainActivity.this)
//                        .requestCode(REQUEST_CODE_PERMISSION_OTHER)
//                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
//                        .rationale(new RationaleListener() {
//                                       @Override
//                                       public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                                           // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
//                                           AndPermission.rationaleDialog(MainActivity.this, rationale).show();
//                                       }
//                                   }
//                        )
//                        .send();
                // 申请多个权限。
                AndPermission.with(MainActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION_SD)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(rationaleListener
                        )
                        .send();


                Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(context, "File downloaded",
                // Toast.LENGTH_SHORT)
                // .show();
                update();
            }

        }
    }
```
此处下载apk文件，需要获取SD的读写权限(用的是严大的权限库)
权限库GitHub：https://github.com/yanzhenjie/AndPermission

```
  private static final int REQUEST_CODE_PERMISSION_SD = 101;

    private static final int REQUEST_CODE_SETTING = 300;
    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            // 这里使用自定义对话框，如果不想自定义，用AndPermission默认对话框：
            // AndPermission.rationaleDialog(Context, Rationale).show();

            // 自定义对话框。
            AlertDialog.build(MainActivity.this)
                    .setTitle(R.string.title_dialog)
                    .setMessage(R.string.message_permission_rationale)
                    .setPositiveButton(R.string.btn_dialog_yes_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })

                    .setNegativeButton(R.string.btn_dialog_no_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    })
                    .show();
        }
    };
    //----------------------------------SD权限----------------------------------//


    @PermissionYes(REQUEST_CODE_PERMISSION_SD)
    private void getMultiYes(List<String> grantedPermissions) {
        Toast.makeText(this, R.string.message_post_succeed, Toast.LENGTH_SHORT).show();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_SD)
    private void getMultiNo(List<String> deniedPermissions) {
        Toast.makeText(this, R.string.message_post_failed, Toast.LENGTH_SHORT).show();

        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                    .setTitle(R.string.title_dialog)
                    .setMessage(R.string.message_permission_failed)
                    .setPositiveButton(R.string.btn_dialog_yes_permission)
                    .setNegativeButton(R.string.btn_dialog_no_permission, null)
                    .show();

            // 更多自定dialog，请看上面。
        }
    }

    //----------------------------------权限回调处理----------------------------------//

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /**
         * 转给AndPermission分析结果。
         *
         * @param object     要接受结果的Activity、Fragment。
         * @param requestCode  请求码。
         * @param permissions  权限数组，一个或者多个。
         * @param grantResults 请求结果。
         */
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                Toast.makeText(this, R.string.message_setting_back, Toast.LENGTH_LONG).show();
                //设置成功，再次请求更新
                getVersion(Tools.getVersion(MainActivity.this));
                break;
            }
        }
    }
```

(3) 当apk文件下载完毕时，打开安装

```
   private void update() {
        //安装应用
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), DOWNLOAD_NAME)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }
```

##Android 7.0 FileUriExposedException 的处理

###发现问题

前几天把手机系统升级到基于 Android 7.0，后来在升级调试一个应用时抛出如下异常信息：
```
android.os.FileUriExposedException: file:///storage/emulated/0/Android/data/com.skyrin.bingo/cache/app/app.apk exposed beyond app through Intent.getData()
at android.os.StrictMode.onFileUriExposed(StrictMode.java:1799)

at com.skyrin.bingo.update.AppUpdate.installApk(AppUpdate.java:295)
```
根据如上日志找到 AppUpdate 类下的 installApk 方法：
```
/**
 * 安装apk
 */
public static void installApk(Context context,String apkPath) {
    if (TextUtils.isEmpty(apkPath)){
        Toast.makeText(context,"更新失败！未找到安装包", Toast.LENGTH_SHORT).show();
        return;
    }

    File apkFile = new File(apkPath
            + apkCacheName);

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setDataAndType(
            Uri.fromFile(apkFile),
            "application/vnd.android.package-archive");
    context.startActivity(intent); 
}
```
问题出在启动安装程序阶段
由于没升级 7.0 系统之前都没有问题，于是就在 Android 官网查看了一下 [Android 7.0 新特性](https://link.jianshu.com/?t=https%3A%2F%2Fdeveloper.android.google.cn%2Fabout%2Fversions%2Fnougat%2Fandroid-7.0-changes.html)，终于发现其中 “在应用间共享文件” 一栏明确指出了这个问题
![](http://upload-images.jianshu.io/upload_images/3805053-236823b510646117.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



## 解决问题

官方给出的解决方式是通过 FileProvider 来为所共享的文件 Uri 添加临时权限，详细[请看这里](https://link.jianshu.com?t=https%3A%2F%2Fdeveloper.android.google.cn%2Ftraining%2Fsecure-file-sharing%2Fsetup-sharing.html%23DefineMetaData)

*   在 <application> 标签下添加 FileProvider 节点

```
<application
   ...>
   ...
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.skyrin.bingo.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
   ...
</application>

```

`android：authority` 属性指定要用于 FileProvider 生成的 content URI 的 URI 权限，这里推荐使用 `包名.fileprovider` 以确保其唯一性。

`<provider>` 的 `<meta-data>` 子元素指向一个 XML 文件，用于指定要共享的目录。

*   在 `res/xml` 目录下创建文件 file_paths.xml 内容如下：

```
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-cache-path path="app/" name="apk"/>
</paths>

```

`<external-cache-path>` 表示应用程序内部存储目录下的 `cache/` 目录，完整路径为 `Android/data/com.xxx.xxx/cache/`。

`path` 属性用于指定子目录。

`name` 属性告诉 FileProvider 为 `Android/data/com.xxx.xxx/cache/app/` 创建一个名为 `apk` 的路径字段。

想要通过 FileProvider 为文件生成 content URI 只能在此处指定目录，以上示例就表示我将要共享 `Android/data/com.xxx.xxx/cache/app/` 这个目录，除此之外还可以共享其它目录，对应的路径如下：


|标签|路径|
|---|---|
|<files-path name="name" path="path" />|[Context.getFilesDir()](https://link.jianshu.com?t=https%3A%2F%2Fdeveloper.android.google.cn%2Freference%2Fandroid%2Fcontent%2FContext.html%23getFilesDir%28%29)|
|<cache-path name="name" path="path" />|[getCacheDir()](https://link.jianshu.com?t=https%3A%2F%2Fdeveloper.android.google.cn%2Freference%2Fandroid%2Fcontent%2FContext.html%23getCacheDir%28%29)
|<external-path name="name" path="path" />|[Environment.getExternalStorageDirectory()](https://link.jianshu.com?t=https%3A%2F%2Fdeveloper.android.google.cn%2Freference%2Fandroid%2Fos%2FEnvironment.html%23getExternalStorageDirectory%28%29)
|<external-files-path name="name" path="path" />|[Context.getExternalFilesDir()](https://link.jianshu.com?t=https%3A%2F%2Fdeveloper.android.google.cn%2Freference%2Fandroid%2Fcontent%2FContext.html%23getExternalFilesDir%28java.lang.String%29)
|<external-cache-path name="name" path="path" />|[Context.getExternalCacheDir()](https://link.jianshu.comt=https%3A%2F%2Fdeveloper.android.google.cn%2Freference%2Fandroid%2Fcontent%2FContext.html%23getExternalCacheDir%28%29)



*   完成以步骤后，我们修改出问题的代码如下：


```
/**
 * 安装apk
 */
public static void installApk(Context context,String apkPath) {
    if (TextUtils.isEmpty(apkPath)){
        Toast.makeText(context,"更新失败！未找到安装包", Toast.LENGTH_SHORT).show();
        return;
    }

    File apkFile = new File(apkPath
            + apkCacheName);

    Intent intent = new Intent(Intent.ACTION_VIEW);
    //Android 7.0 系统共享文件需要通过 FileProvider 添加临时权限，否则系统会抛出 FileUriExposedException .
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(context,"com.skyrin.bingo.fileprovider",apkFile);
        intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
    }else {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(
                Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");
    }
    context.startActivity(intent);
}
...
//调用，apkPath 入参就是 xml 中共享的路径
String apkPath = context.getExternalCacheDir().getPath()+ File.separator+"app"+File.separator;
AppUpdate.installApk(context,apkPath );

```

## 结语

除了上面这个问题，在 Android 7.0 之前开发的分享图文、浏览编辑本地图片、共享互传文件等功能如果没有使用 FileProvider 来生成 URI 的话，在 Android 7.0 上就必须做这种适配了，所以平时建议大家多关注 Android 新的 API ，尽早替换已被官方废弃的 API ，实际上 [FileProvider](https://link.jianshu.com?t=https%3A%2F%2Fdeveloper.android.google.cn%2Freference%2Fandroid%2Fsupport%2Fv4%2Fcontent%2FFileProvider.html)  在 API Level 22 已经添加了。




