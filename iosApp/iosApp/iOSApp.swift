import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        if FirebaseApp.app() == nil,
           let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: path) {
            FirebaseApp.configure(options: options)
        }
    }
    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
