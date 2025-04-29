//
//  AppDelegate+Push.swift
//  AppcuesReactNativeExample
//
//  Created by Matt on 2025-04-28.
//

// Disabled in favor of automatic configuration

/*
import Foundation
import UserNotifications
import appcues_react_native

extension AppDelegate: UNUserNotificationCenterDelegate {
    /// Call from `UIApplicationDelegate.application(_:didFinishLaunchingWithOptions:)`
    func setupPush(application: UIApplication) {
        // 1: Register to get a device token
        application.registerForRemoteNotifications()

        UNUserNotificationCenter.current().delegate = self
    }

    // 2: Pass device token to Appcues
    override func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
      appcues_react_native.Implementation.setPushToken(deviceToken)
    }

    // 3: Pass the user's response to a delivered notification to Appcues
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        if appcues_react_native.Implementation.didReceiveNotification(response: response, completionHandler: completionHandler) {
            return
        }

        completionHandler()
    }

    // 4: Configure handling for notifications that arrive while the app is in the foreground
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        if #available(iOS 14.0, *) {
            completionHandler([.banner, .list])
        } else {
            completionHandler(.alert)
        }
    }
}
*/
