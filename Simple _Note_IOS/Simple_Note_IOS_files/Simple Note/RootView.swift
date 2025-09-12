import SwiftUI

struct RootView: View {
    @State private var path = NavigationPath()
    @State private var showLogoutConfirmation = false
    @State private var showNoteDeleteConfirmation = false
    @State private var searchText: String = ""

    // Notes data (shared state for the app)
    @State private var notes: [Note] = []

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
                            onDelete: { showNoteDeleteConfirmation = true },
                            onSave: { title, content, color in
                                Task { @MainActor in
                                    do {
                                        let created = try await NotesService.shared.create(title: title, description: content)
                                        notes.insert(Note(from: created), at: 0)
                                        path.removeLast()
                                    } catch {
                                        // If API fails, keep UX: still pop back
                                        path.removeLast()
                                    }
                                }
                            }
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
                    case "profileEdit":
                        ProfileEditView(onBack: { path.removeLast() })
                    case "searchResults":
                        SearchResultsView(
                            searchText: $searchText,
                            notes: notes,
                            onNoteSelected: { note in
                                path.append("noteDetail")
                            }
                        )
                    case "folders":
                        FoldersView(onBack: { path.removeLast() })
                    case "trash":
                        TrashNotesView(
                            onRestore: { note in
                                notes.append(note)
                            },
                            onPermanentDelete: { note in
                                // Permanent delete logic (remove from deleted list)
                            },
                            onBack: { path.removeLast() }
                        )
                    default:
                        Text("Unknown Destination")
                    }
                }
                .toolbar(.hidden, for: .navigationBar)
            }

            // Modals with blurry backgrounds
            if showLogoutConfirmation {
                Rectangle()
                    .ignoresSafeArea()
                    .background(.ultraThinMaterial)
                    .blur(radius: 10)
                    .opacity(0.7)
                    .transition(.opacity)

                LogoutConfirmationView(
                    onCancel: { showLogoutConfirmation = false },
                    onConfirm: {
                        showLogoutConfirmation = false
                        path = NavigationPath()  // Log out and reset to onboarding/login
                        AuthStorage.shared.clear()
                        notes.removeAll()
                    }
                )
                .transition(.scale)
            }

            if showNoteDeleteConfirmation {
                Rectangle()
                    .ignoresSafeArea()
                    .background(.ultraThinMaterial)
                    .blur(radius: 10)
                    .opacity(0.7)
                    .transition(.opacity)

                NoteDeleteConfirmationView(
                    onCancel: { showNoteDeleteConfirmation = false },
                    onDelete: {
                        showNoteDeleteConfirmation = false
                        path.removeLast()  // Delete and pop view
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
