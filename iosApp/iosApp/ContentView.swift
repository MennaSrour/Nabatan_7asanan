import SwiftUI
import UIKit
import ComposeApp
import FirebaseCore

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(firebaseConfigured: FirebaseApp.app() != nil)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
