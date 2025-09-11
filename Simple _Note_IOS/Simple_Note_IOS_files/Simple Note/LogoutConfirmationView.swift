import SwiftUI

struct LogoutConfirmationView: View {
    var onCancel: () -> Void = {}
    var onConfirm: () -> Void = {}

    var body: some View {
        VStack(spacing: 48) {
            VStack(spacing: 8) {
                Text("Log Out")
                    .font(.custom("Inter-Bold", size: 20))
                    .foregroundColor(Color(hex: "#180E25"))
                    .multilineTextAlignment(.center)
                    .frame(width: 232, height: 28)
                Text("Are you sure you want to log out from the application?")
                    .font(.custom("Inter-Regular", size: 16))
                    .foregroundColor(Color(hex: "#827D89"))
                    .multilineTextAlignment(.center)
                    .frame(width: 232, height: 44)
            }
            HStack(spacing: 16) {
                Button(action: onCancel) {
                    Text("Cancel")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(Color(hex: "#504EC3"))
                        .frame(width: 53, height: 22)
                        .padding(.vertical, 8)
                        .padding(.horizontal, 16)
                        .background(Color.white)
                        .cornerRadius(100)
                        .overlay(
                            RoundedRectangle(cornerRadius: 100)
                                .stroke(Color(hex: "#504EC3"), lineWidth: 1)
                        )
                }
                .frame(width: 108, height: 38)

                Button(action: onConfirm) {
                    Text("Yes")
                        .font(.custom("Inter-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(width: 53, height: 22)
                        .padding(.vertical, 8)
                        .padding(.horizontal, 16)
                        .background(Color(hex: "#504EC3"))
                        .cornerRadius(100)
                }
                .frame(width: 108, height: 38)
            }
        }
        .padding(EdgeInsets(top: 32, leading: 24, bottom: 32, trailing: 24))
        .frame(width: 280, height: 230)
        .background(Color.white)
        .cornerRadius(16)
        .shadow(radius: 16)
    }
}

#Preview {
    LogoutConfirmationView()
}
