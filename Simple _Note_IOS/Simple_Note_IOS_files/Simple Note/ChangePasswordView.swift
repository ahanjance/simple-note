import SwiftUI

struct ChangePasswordView: View {
    @State private var currentPassword: String = ""
    @State private var newPassword: String = ""
    @State private var retypeNewPassword: String = ""
    var onBack: () -> Void = {}
    var onSubmit: () -> Void = {}  // Callback to navigate back to Settings after submit
    @State private var isLoading = false
    @State private var errorMessage: String? = nil

    var body: some View {
        VStack(spacing: 0) {
            // Navigation Bar
            HStack(spacing: 0) {
                Button(action: onBack) {
                    HStack(spacing: 4) {
                        Image(systemName: "chevron.left")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 12, height: 18)
                            .foregroundColor(Color(hex: "#504EC3"))
                        Text("Back")
                            .font(.custom("Inter-Medium", size: 16))
                            .foregroundColor(Color(hex: "#504EC3"))
                    }
                }
                Spacer()
                Text("Change Password")
                    .font(.custom("Inter-Medium", size: 16))
                    .foregroundColor(.black)
                    .frame(height: 22)
                Spacer()
            }
            .frame(height: 54)
            .padding(.horizontal, 8)
            .padding(.top, 12)
            .background(Color.white)
            .overlay(
                Rectangle()
                    .frame(height: 1)
                    .foregroundColor(Color(hex: "#EFEEF0")),
                alignment: .bottom
            )

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    // Current password section
                    Text("Please input your current password first")
                        .font(.custom("Inter-Medium", size: 12))
                        .foregroundColor(Color(hex: "#504EC3"))
                        .padding(.top, 24)
                        .padding(.leading, 16)

                    Text("Current Password")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#180E25"))
                        .padding(.top, 12)
                        .padding(.leading, 16)

                    SecureField("********", text: $currentPassword)
                        .font(.custom("Inter-Medium", size: 16))
                        .padding(16)
                        .background(Color.white)
                        .cornerRadius(8)
                        .overlay(RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(hex: "#C8C5CB"), lineWidth: 1))
                        .padding(.horizontal, 16)
                        .padding(.top, 8)

                    // Divider
                    Rectangle()
                        .frame(height: 1)
                        .foregroundColor(Color(hex: "#EFEEF0"))
                        .padding(.top, 24)
                        .padding(.horizontal, 16)

                    // New password section
                    Text("Now, create your new password")
                        .font(.custom("Inter-Medium", size: 12))
                        .foregroundColor(Color(hex: "#504EC3"))
                        .padding(.top, 24)
                        .padding(.leading, 16)

                    Text("New Password")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#180E25"))
                        .padding(.top, 12)
                        .padding(.leading, 16)

                    SecureField("********", text: $newPassword)
                        .font(.custom("Inter-Medium", size: 16))
                        .padding(16)
                        .background(Color.white)
                        .cornerRadius(8)
                        .overlay(RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(hex: "#C8C5CB"), lineWidth: 1))
                        .padding(.horizontal, 16)
                        .padding(.top, 8)

                    Text("Password should contain a-z, A-Z, 0-9")
                        .font(.custom("Inter-Regular", size: 12))
                        .foregroundColor(Color(hex: "#C8C5CB"))
                        .padding(.leading, 16)
                        .padding(.top, 4)

                    Text("Retype New Password")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#180E25"))
                        .padding(.top, 28)
                        .padding(.leading, 16)

                    SecureField("********", text: $retypeNewPassword)
                        .font(.custom("Inter-Medium", size: 16))
                        .padding(16)
                        .background(Color.white)
                        .cornerRadius(8)
                        .overlay(RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(hex: "#C8C5CB"), lineWidth: 1))
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                }
            }

            Spacer()

            // Submit Button with onSubmit callback
            Button(action: handleSubmit) {
                HStack {
                    Spacer()
                    Text("Submit New Password")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(.white)
                    Image(systemName: isLoading ? "hourglass" : "arrow.right")
                        .resizable()
                        .frame(width: 15, height: 12)
                        .foregroundColor(.white)
                        .padding(.leading, 8)
                    Spacer()
                }
            }
            .frame(height: 54)
            .background(Color(hex: "#504EC3"))
            .cornerRadius(100)
            .padding(.horizontal, 16)
            .padding(.vertical, 18)
        }
        .background(Color.white.ignoresSafeArea())
        .navigationBarBackButtonHidden(true)  // Hide default navigation back button
        .overlay(
            Group {
                if let errorMessage = errorMessage {
                    Text(errorMessage)
                        .font(.custom("Inter-Regular", size: 12))
                        .foregroundColor(.red)
                        .padding(.bottom, 90)
                }
            }, alignment: .bottom
        )
    }

    private func handleSubmit() {
        guard newPassword == retypeNewPassword else {
            errorMessage = "Passwords do not match"
            return
        }
        Task { @MainActor in
            isLoading = true
            errorMessage = nil
            do {
                try await AuthService.shared.changePassword(old: currentPassword, new: newPassword)
                onSubmit()
            } catch {
                errorMessage = "Failed to change password"
            }
            isLoading = false
        }
    }
}

#Preview {
    ChangePasswordView()
}
