import SwiftUI

struct RootView: View {
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            OnboardingView {
                path.append("login")
            }
            .navigationDestination(for: String.self) { destination in
                switch destination {
                case "login":
                    LoginView(
                        onLogin: { path.append("home") },
                        onRegisterTap: { path.append("register") }
                    )
                case "register":
                    RegisterView(
                        onGoLogin: { path.removeLast() },
                        onRegister: { path.append("home") }
                    )
                case "home":
                    HomeView(
                        onTapAdd: { path.append("addNote") },
                        onTapSettings: { path.append("settings") }
                    )
                case "settings":
                    SettingsView(
                        onBack: { path.removeLast() },
                        onLogout: { path = NavigationPath() }
                    )
                case "addNote":
                    AddNoteView(
                        onBack: { path.removeLast() },
                        onDelete: { path.removeLast() }
                    )
                default:
                    Text("Unknown Destination")
                }
            }
            .toolbar(.hidden, for: .navigationBar)
        }
    }
}

#Preview {
    RootView()
}
