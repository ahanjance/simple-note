import SwiftUI

struct SettingsView: View {
    var onBack: () -> Void = {}
    var onChangePassword: () -> Void = {}
    var onLogout: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
            // Nav Bar
            ZStack {
                Color.white
                    .frame(height: 54)
                    .shadow(color: Color(hex: "#EFEEF0"), radius: 0, x: 0, y: 1)

                HStack {
                    Button(action: onBack) {
                        HStack(spacing: 8) {
                            Image(systemName: "chevron.left")
                                .resizable()
                                .frame(width: 6, height: 10)
                                .foregroundColor(Color(hex: "#504EC3"))
                                .padding(.leading, 7)
                                .padding(.top, 5)
                            Text("Back")
                                .font(.custom("Inter-Medium", size: 16))
                                .foregroundColor(Color(hex: "#504EC3"))
                                .frame(height: 22)
                        }
                    }
                    Spacer()
                    Text("Settings")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color.black)
                        .frame(width: 64, height: 22)
                        .padding(.trailing, 155)
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)
            }

            // Profile Section
            HStack(spacing: 16) {
                Image("profile_image")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 64, height: 64)
                    .clipShape(Circle())
                VStack(alignment: .leading, spacing: 8) {
                    Text("Qazal Ahanjan")
                        .font(.custom("Inter-Bold", size: 20))
                        .foregroundColor(Color(hex: "#180E25"))
                        .frame(height: 28)
                    HStack(spacing: 4) {
                        Image(systemName: "envelope")
                            .resizable()
                            .frame(width: 11.25, height: 8.75)
                            .foregroundColor(Color(hex: "#827D89"))
                        Text("qazalahanjan@gmail.com")
                            .font(.custom("Inter-Regular", size: 12))
                            .foregroundColor(Color(hex: "#827D89"))
                            .frame(height: 15)
                    }
                }
                Spacer()
            }
            .frame(width: 328, height: 64)
            .padding(.top, 24)
            .padding(.leading, 16)
            .padding(.trailing, 16)

            Divider()
                .frame(width: 328)
                .padding(.vertical, 16)

            // App Settings Label
            Text("APP SETTINGS")
                .font(.custom("Inter-Regular", size: 10))
                .foregroundColor(Color(hex: "#827D89"))
                .frame(width: 100, height: 12)
                .padding(.leading, 16)
                .padding(.bottom, 8)
                .padding(.top, 8)
                .frame(maxWidth: .infinity, alignment: .leading)

            // Settings Options (Change Password)
            Button(action: onChangePassword) {
                HStack {
                    Image(systemName: "lock.fill")
                        .resizable()
                        .frame(width: 16, height: 18)
                        .foregroundColor(Color(hex: "#180E25"))
                    Text("Change Password")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#180E25"))
                    Spacer()
                    Image(systemName: "chevron.right")
                        .resizable()
                        .frame(width: 9.33, height: 4.66)
                        .rotationEffect(.degrees(0))
                        .foregroundColor(Color(hex: "#827D89"))
                }
                .padding()
                .frame(width: 328, height: 56)
                .background(Color.white)
                .cornerRadius(8)
            }
            .padding(.bottom, 8)
            .padding(.horizontal, 16)

            Divider()
                .frame(width: 328)

            // Logout Option
            Button(action: onLogout) {
                HStack {
                    Image("locklogo")
                        .resizable()
                        .frame(width: 18, height: 16)
                        .foregroundColor(Color(hex: "#CE3A54"))
                    Text("Log Out")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#CE3A54"))
                    Spacer()
                }
                .padding()
                .frame(width: 328, height: 56)
                .background(Color.white)
                .cornerRadius(8)
            }
            .padding(.top, 8)
            .padding(.horizontal, 16)

            Spacer()

            // App version
            Text("Qazal Notes v1")
                .font(.custom("Inter-Regular", size: 12))
                .foregroundColor(Color(hex: "#C8C5CB"))
                .frame(width: 88, height: 15)
                .padding(.bottom, 24)
                .frame(maxWidth: .infinity)
                .multilineTextAlignment(.center)
        }
        .background(Color.white)
        .navigationBarHidden(true)
    }
}

#Preview {
    SettingsView()
}
