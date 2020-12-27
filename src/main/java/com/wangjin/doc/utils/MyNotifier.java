package com.wangjin.doc.utils;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.wangjin.doc.base.Main;

public class MyNotifier {

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Custom Notification Group", NotificationDisplayType.BALLOON, true);


    public static void notifyError(String content) {
        NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR)
                .notify(Main.PROJECT);
    }


    public static void notifyInfo(String content) {
        NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION)
                .notify(Main.PROJECT);
    }

    public static void notifyWarn(String content) {
        NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING)
                .notify(Main.PROJECT);
    }

}