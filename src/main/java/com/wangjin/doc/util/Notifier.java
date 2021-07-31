package com.wangjin.doc.util;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.wangjin.doc.base.Application;

public class Notifier {

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Custom Notification Group", NotificationDisplayType.BALLOON, true);


    public static void notifyError(String content) {
        try {
            NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR)
                    .notify(Application.project);
        } catch (Exception ignored) {
        }
    }


    public static void notifyInfo(String content) {
        try {
            NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION)
                    .notify(Application.project);
        } catch (Exception ignored) {
        }
    }

    public static void notifyWarn(String content) {
        try {
            NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING)
                    .notify(Application.project);
        } catch (Exception ignored) {

        }
    }

}