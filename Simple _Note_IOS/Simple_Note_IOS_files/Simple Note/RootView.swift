import SwiftUI

struct RootView: View {
    @State private var path = NavigationPath()
    @State private var showLogoutConfirmation = false
    @State private var showNoteDeleteConfirmation = false

    var body: some View {
        ZStack {
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
                            onTapSettings: { path.append("settings") },
                            onNoteTapped: { note in
                                path.append("noteDetail")
                            }
                        )
                    case "settings":
                        SettingsView(
                            onBack: { path.removeLast() },
                            onChangePassword: { path.append("changePassword") },
                            onLogout: { showLogoutConfirmation = true }
                        )
                    case "addNote":
                        AddNoteView(
                            onBack: { path.removeLast() },
                            onDelete: { path.removeLast() }
                        )
                    case "noteDetail":
                        NoteDetailView(
                            onBack: { path.removeLast() },
                            onDelete: { showNoteDeleteConfirmation = true }
                        )
                    case "changePassword":
                        ChangePasswordView(
                            onBack: { path.removeLast() },
                            onSubmit: { path.removeLast() }
                        )
                    default:
                        Text("Unknown Destination")
                    }
                }
                .toolbar(.hidden, for: .navigationBar)
            }

            // Blurred confirmation overlays
            if showLogoutConfirmation {
                // Blurred background
                Rectangle()
                    .ignoresSafeArea()
                    .background(.ultraThinMaterial)
                    .blur(radius: 10)
                    .opacity(0.7)
                    .transition(.opacity)

                // Logout Confirmation Modal
                LogoutConfirmationView(
                    onCancel: { showLogoutConfirmation = false },
                    onConfirm: {
                        showLogoutConfirmation = false
                        path = NavigationPath()
                    }
                )
                .transition(.scale)
            }

            if showNoteDeleteConfirmation {
                // Blurred background
                Rectangle()
                    .ignoresSafeArea()
                    .background(.ultraThinMaterial)
                    .blur(radius: 10)
                    .opacity(0.7)
                    .transition(.opacity)

                // Note Delete Confirmation Modal
                NoteDeleteConfirmationView(
                    onCancel: { showNoteDeleteConfirmation = false },
                    onDelete: {
                        showNoteDeleteConfirmation = false
                        path.removeLast()  // Perform delete and pop back
                    }
                )
                .transition(.scale)
            }
        }
    }
}

#Preview {
    RootView()
}
