package com.wlsh.shortcut.badge;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author wlsh
 * @date 2018/7/11 16:09
 * @description 角标设置工具
 */
public final class BadgeUtil {

    //zuk 定义ContentProvider的uri
    private static final Uri CONTENT_URI = Uri.parse("content://" + "com.android.badge" + "/" + "badge");

    private BadgeUtil() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /**
     * 设置Badge
     *
     * @param context context
     * @param count   count
     */
    public static void setBadgeCount(Context context, int count) {
        if (count <= 0) {
            count = 0;
        } else {
            count = Math.max(0, Math.min(count, 99));
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            setBadgeOfMiui(context, count);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            setBadgeOfSony(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
            setBadgeOfSamsung(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("lg")) {
            setBadgeOfSamsung(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("htc")) {
            setBadgeOfHtc(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("nova")) {
            setBadgeOfNova(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("huawei")) {
            setBadgeOfHuaWei(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("oppo")) {
            setBadgeOfOppo(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            setBadgeOfVivo(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("zuk")) {
            setBadgeOfZuk(context, count);
        } else {
            setBadgeOfDefault(context, count);
        }
    }

    /**
     * 设置MIUI的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfMiui(Context context, int count, int iconResId) {
        Log.d("xys", "Launcher : MIUI");
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("title").setContentText("text").setSmallIcon(iconResId);
        Notification notification = builder.build();
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mNotificationManager.notify(0, notification);
    }


    /**
     * 设置MIUI的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfMiui(Context context, int count) {
        //do nothing 小米角标是绑定通知的
    }

    /**
     * 设置索尼的Badge
     * <p/>
     * 需添加权限：<uses-permission android:name="com.sonyericsson.home.permission.BROADCAST_BADGE" />
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfSony(Context context, int count) {
        String launcherClassName = AppInfoUtil.getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        boolean isShow = true;
        if (count == 0) {
            isShow = false;
        }
        try {
            Intent localIntent = new Intent();
            localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);//启动页
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count));//数字
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
            context.sendBroadcast(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SONY" + " Badge error", "set Badge failed");
        }
    }

    /**
     * 设置三星的Badge\设置LG的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfSamsung(Context context, int count) {
        // 获取你当前的应用
        String launcherClassName = AppInfoUtil.getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    /**
     * 设置HTC的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfHtc(Context context, int count) {
        Intent intentNotification = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
        ComponentName localComponentName = new ComponentName(context.getPackageName(),
                AppInfoUtil.getLauncherClassName(context));
        intentNotification.putExtra("com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());
        intentNotification.putExtra("com.htc.launcher.extra.COUNT", count);
        context.sendBroadcast(intentNotification);

        Intent intentShortcut = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
        intentShortcut.putExtra("packagename", context.getPackageName());
        intentShortcut.putExtra("count", count);
        context.sendBroadcast(intentShortcut);
    }

    /**
     * 设置Nova的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfNova(Context context, int count) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("tag", context.getPackageName() + "/" +
                AppInfoUtil.getLauncherClassName(context));
        contentValues.put("count", count);
        context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"),
                contentValues);
    }

    /**
     * 设置华为的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfHuaWei(Context context, int count) {

        try {
            if (count < 0) count = 0;
            Bundle bundle = new Bundle();
            bundle.putString("package", context.getPackageName());
            String launchClassName = AppInfoUtil.getLauncherClassName(context);
            bundle.putString("class", launchClassName);
            bundle.putInt("badgenumber", count);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置OPPO的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfOppo(Context context, int count) {

        try {
            if (count == 0) {
                count = -1;
            }
            Intent intent = new Intent("com.oppo.unsettledevent");
            intent.putExtra("pakeageName", context.getPackageName());
            intent.putExtra("number", count);
            intent.putExtra("upgradeNumber", count);
            if (canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent);
            } else {
                try {
                    Bundle extras = new Bundle();
                    extras.putInt("app_badge_count", count);
                    context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extras);
                } catch (Throwable th) {
                    Log.e("OPPO" + " Badge error", "unable to resolve intent: " + intent.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OPPO" + " Badge error", "set Badge failed");
        }
    }

    /**
     * 设置VIVO的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfVivo(Context context, int count) {

        try {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            String launchClassName = AppInfoUtil.getLauncherClassName(context);
            intent.putExtra("className", launchClassName);
            intent.putExtra("notificationNum", count);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Zuk的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfZuk(Context context, int count) {

        Bundle extra = new Bundle();
        ArrayList<String> ids = new ArrayList<String>();

        // 以列表形式传递快捷方式id，可以添加多个快捷方式id
        //ids.add("custom_id_1");

        //示例中ids这个参数可以为空或者“null”表示对主图标进行角标标记；custom_id_1等id值，是应用自定义的对应快捷方式的id，如果应用有快捷方式在桌面创建需要将此id传给桌面在后面将有说明。
        //这个例子中如果有多个id，那么表示多个id的角标值是一样的为counts。如果每个id有不同的值，需要分别循环调用并设置值，也就是一个id和对应counts值调用一次此接口。如果ids为null将更新主图标。
        extra.putStringArrayList("app_shortcut_custom_id", ids);
        extra.putInt("app_badge_count", count);

        Bundle b = null;
        b = context.getContentResolver().call(CONTENT_URI, "setAppBadgeCount", null, extra);
        boolean result = false;
        if (b != null) {
            result = true;
        } else {
            result = false;
        }
    }

    /**
     * 设置其他手机的Badge
     *
     * @param context
     * @param count
     */
    private static void setBadgeOfDefault(Context context, int count) {
        //do nothing
    }


    private static void setBadgeOfMadMode(Context context, int count, String packageName, String className) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", packageName);
        intent.putExtra("badge_count_class_name", className);
        context.sendBroadcast(intent);
    }

    private static boolean canResolveBroadcast(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }

    /**
     * 重置Badge
     *
     * @param context context
     */
    public static void cleanBadgeCount(Context context) {
        setBadgeCount(context, 0);
    }
}
