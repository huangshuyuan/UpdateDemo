package com.hsy.update.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
/**
 * Created by huagnshuyuan on 2017/3/16.
 */
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
    // if (VERSION.SDK_INT > 16) {
    // Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
    // final RenderScript rs = RenderScript.create(context);
    // final Allocation input = Allocation.createFromBitmap(rs, sentBitmap,
    // Allocation.MipmapControl.MIPMAP_NONE,
    // Allocation.USAGE_SCRIPT);
    // final Allocation output = Allocation.createTyped(rs, input.getType());
    // final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
    // Element.U8_4(rs));
    // script.setRadius(radius /* e.g. 3.f */);
    // script.setInput(input);
    // script.forEach(output);
    // output.copyTo(bitmap);
    // return bitmap;
    // }
}
