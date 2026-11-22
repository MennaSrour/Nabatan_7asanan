import SwiftUI
import UIKit
import UserNotifications
import ComposeApp
import FirebaseCore
import FirebaseMessaging

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}

final class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        if FirebaseApp.app() == nil,
           let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: path) {
            FirebaseApp.configure(options: options)
        }

        guard FirebaseApp.app() != nil else { return true }

        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self
        BroadcastNotificationManager.shared.initialize()
        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
        subscribeToBroadcastTopic()
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        NSLog("APNs registration failed: %@", error.localizedDescription)
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard fcmToken != nil, messaging.apnsToken != nil else { return }
        subscribeToBroadcastTopic()
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .list, .sound, .badge])
    }

    private func subscribeToBroadcastTopic() {
        let broadcastTopic = BroadcastNotificationManager.shared.topic
        Messaging.messaging().subscribe(toTopic: broadcastTopic) { error in
            if let error {
                NSLog("FCM topic subscription failed: %@", error.localizedDescription)
            }
        }
    }
}
