import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init() {
        AppModuleHelper.shared.setupAppModule()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
