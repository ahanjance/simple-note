import SwiftUI

struct RegisterView: View {
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var username = ""
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""

    var onGoLogin: () -> Void = {}
    var onRegister: () -> Void = {}

    var body: some View {
        ZStack {
            Color.white.ignoresSafeArea()

            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 0) {
                    // --- Nav Bar with Back to Login ---
                    HStack(spacing: 8) {
                        Button(action: { onGoLogin() }) {
                            HStack(spacing: 8) {
                                Image(systemName: "chevron.left")
                                    .resizable()
                                    .frame(width: 6, height: 10)
                                    .foregroundColor(Color(hex: "#504EC3"))
                                    .padding(.leading, 7)
                                    .padding(.top, 5)
                                Text("Back to Login")
                                    .font(.custom("Inter-Medium", size: 16))
                                    .foregroundColor(Color(hex: "#504EC3"))
                            }
                        }
                        .buttonStyle(.plain)
                        .frame(height: 22)
                        Spacer()
                    }
                    .padding(.top, 16)
                    .padding(.leading, 16)
                    .padding(.bottom, 8)

                    // --- Title and Subtitle (Left-Aligned) ---
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Register")
                            .font(.custom("Inter-Bold", size: 32))
                            .foregroundColor(Color(hex: "#180E25"))
                            .frame(height: 38, alignment: .leading)
                        Text("And start taking notes")
                            .font(.custom("Inter-Regular", size: 16))
                            .foregroundColor(Color(hex: "#827D89"))
                            .frame(height: 22, alignment: .leading)
                    }
                    .frame(width: 328, height: 76, alignment: .leading)
                    .padding(.top, 8)
                    .padding(.bottom, 32)
                    .padding(.leading, 16)

                    // --- Input Fields ---
                    VStack(alignment: .leading, spacing: 32) {
                        RegisterFormField(label: "First Name", placeholder: "Example: Taha", text: $firstName)
                        RegisterFormField(label: "Last Name", placeholder: "Example: Hamifar", text: $lastName)
                        RegisterFormField(label: "Username", placeholder: "Example: @HamifarTaha", text: $username)
                        RegisterFormField(label: "Email Address", placeholder: "Example: hamifar.taha@gmail.com", text: $email, keyboard: .emailAddress)
                        RegisterFormField(label: "Password", placeholder: "********", text: $password, isSecure: true)
                        RegisterFormField(label: "Retype Password", placeholder: "********", text: $confirmPassword, isSecure: true)
                    }
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.bottom, 40)

                    // --- Register Button ---
                    Button(action: onRegister) {
                        HStack {
                            Spacer()
                            Text("Register")
                                .font(.custom("Inter-Medium", size: 17))
                                .frame(height: 22)
                            Spacer()
                            Image(systemName: "arrow.right")
                                .font(.system(size: 20, weight: .bold))
                                .padding(.trailing, 12)
                        }
                        .frame(width: 328, height: 54)
                    }
                    .foregroundColor(.white)
                    .background(Color(hex: "#504EC3"))
                    .cornerRadius(100)
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.bottom, 16)

                    // --- Bottom Link ---
                    Button(action: onGoLogin) {
                        Text("Already have an account? Login here")
                            .font(.custom("Inter-Medium", size: 16))
                            .foregroundColor(Color(hex: "#504EC3"))
                            .frame(width: 282, height: 22)
                    }
                    .background(Color.clear)
                    .cornerRadius(100)
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.bottom, 32)
                }
                .padding(.top, 10)
            }
        }
    }
}

// --- Reusable Register Form Field ---
struct RegisterFormField: View {
    var label: String
    var placeholder: String
    @Binding var text: String
    var isSecure: Bool = false
    var keyboard: UIKeyboardType = .default

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(label)
                .font(.custom("Inter-Medium", size: 16))
                .foregroundColor(Color(hex: "#180E25"))
                .frame(height: 22, alignment: .leading)
            Group {
                if isSecure {
                    SecureField(placeholder, text: $text)
                        .font(.custom("Inter-Regular", size: 16))
                } else {
                    TextField(placeholder, text: $text)
                        .font(.custom("Inter-Regular", size: 16))
                        .keyboardType(keyboard)
                        .autocapitalization(.none)
                }
            }
            .padding(16)
            .frame(height: 54)
            .background(Color.white)
            .cornerRadius(8)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color(hex: "#C8C5CB"), lineWidth: 1)
            )
        }
        .frame(width: 328)
    }
}


#Preview {
    RegisterView()
}
