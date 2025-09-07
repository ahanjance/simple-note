import SwiftUI

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""

    var onLogin: () -> Void = {}
    var onRegisterTap: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
            // ---- Screen Title ----
            VStack(alignment: .leading, spacing: 16) {
                Text("Let’s Login")
                    .font(.custom("Inter-Bold", size: 32))
                    .foregroundColor(Color(hex: "#180E25"))
                    .frame(height: 38, alignment: .leading)
                Text("And notes your idea")
                    .font(.custom("Inter-Regular", size: 16))
                    .foregroundColor(Color(hex: "#827D89"))
                    .frame(height: 22, alignment: .leading)
            }
            .frame(width: 328, height: 76, alignment: .leading)
            .padding(.top, 24)
            .padding(.bottom, 32)

            // ---- Input Fields ----
            VStack(spacing: 32) {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Email Address")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#180E25"))
                        .frame(height: 22, alignment: .leading)

                    TextField("", text: $email)
                        .padding(16)
                        .frame(width: 328, height: 54)
                        .background(Color.white)
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color(hex: "#C8C5CB"), lineWidth: 1)
                        )
                }
                VStack(alignment: .leading, spacing: 12) {
                    Text("Password")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#180E25"))
                        .frame(height: 22, alignment: .leading)
                    SecureField("", text: $password)
                        .padding(16)
                        .frame(width: 328, height: 54)
                        .background(Color.white)
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color(hex: "#C8C5CB"), lineWidth: 1)
                        )
                }
            }
            .frame(width: 328, height: 208)
            .padding(.bottom, 40)

            // ---- Actions (Login, Or, Register) ----
            VStack(spacing: 0) {
                Button(action: onLogin) {
                    HStack {
                        Spacer()
                        Text("Login")
                            .font(.custom("Inter-Medium", size: 17))
                        Spacer()
                        Image(systemName: "arrow.right")
                            .font(.system(size: 17, weight: .semibold))
                    }
                    .frame(width: 328, height: 54)
                }
                .foregroundColor(.white)
                .background(Color(hex: "#504EC3"))
                .cornerRadius(100)

                // "Or" text small and centered
                Text("Or")
                    .font(.custom("Inter-Regular", size: 14))
                    .foregroundColor(Color(hex: "#8E8E93"))
                    .frame(maxWidth: .infinity)
                    .multilineTextAlignment(.center)
                    .padding(.vertical, 12)

                // Register link
                Button(action: onRegisterTap) {
                    Text("Don’t have any account? Register here")
                        .font(.custom("Inter-SemiBold", size: 16))
                        .foregroundColor(Color(hex: "#504EC3"))
                        .frame(width: 328, height: 54)
                }
                .background(Color.clear)
                .cornerRadius(100)
            }
            .frame(width: 328, height: 120)

            Spacer()
        }
        .frame(width: 360, height: 543)
        .padding(.top, 101)
        .padding(.horizontal, 16)
        .background(Color.white)
    }
}
#Preview {
    LoginView()
}
