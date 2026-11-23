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
           let options = FirebaseOptions(contentsOfFile: path),
           options.bundleID == Bundle.main.bundleIdentifier,
           !options.googleAppID.isEmpty,
           !(options.apiKey ?? "").isEmpty,
           !(options.projectID ?? "").isEmpty {
            FirebaseApp.configure(options: options)
        }

        UNUserNotificationCenter.current().delegate = self
        guard FirebaseApp.app() != nil else { return true }

        Messaging.messaging().isAutoInitEnabled = false
        Messaging.messaging().delegate = self
        BroadcastNotificationManager.shared.initialize()
        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        guard FirebaseApp.app() != nil else { return }
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            guard [.authorized, .provisional, .ephemeral].contains(settings.authorizationStatus) else { return }
            DispatchQueue.main.async {
                guard FirebaseApp.app() != nil else { return }
                Messaging.messaging().isAutoInitEnabled = true
                Messaging.messaging().apnsToken = deviceToken
                self.subscribeToBroadcastTopic()
            }
        }
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        NSLog("APNs registration failed: %@", error.localizedDescription)
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard FirebaseApp.app() != nil, fcmToken != nil, messaging.apnsToken != nil else { return }
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
