import SwiftUI

struct ProfileEditView: View {
    @State private var name: String = ""
    @State private var email: String = ""
    @State private var showImagePicker = false
    @State private var profileImage: Image? = nil

    var onBack: () -> Void = {}

    var body: some View {
        VStack(spacing: 16) {
            // Custom navigation bar with centered title
            HStack {
                Button(action: onBack) {
                    HStack(spacing: 8) {
                        Image(systemName: "chevron.left")
                            .resizable()
                            .frame(width: 12, height: 20)
                            .foregroundColor(Color(hex: "#504EC3"))
                        Text("Back")
                            .font(.custom("Inter-Medium", size: 16))
                            .foregroundColor(Color(hex: "#504EC3"))
                    }
                }
                Spacer()
                Text("Edit Profile")
                    .font(.custom("Inter-Medium", size: 18))
                    .foregroundColor(Color(hex: "#180E25"))
                Spacer()
                Color.clear.frame(width: 60, height: 44)  // Invisible spacer for centering
            }
            .frame(height: 54)
            .padding(.horizontal, 16)
            .background(Color.white)
            .overlay(Rectangle().frame(height: 1).foregroundColor(Color(hex: "#EFEEF0")), alignment: .bottom)

            // Profile image and form (unchanged)
            VStack {
                if let img = profileImage {
                    img
                        .resizable()
                        .scaledToFill()
                        .frame(width: 120, height: 120)
                        .clipShape(Circle())
                        .shadow(radius: 4)
                } else {
                    Circle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 120, height: 120)
                        .overlay(Text("Tap to add photo").font(.caption))
                }
            }
            .onTapGesture {
                showImagePicker = true
            }

            VStack(alignment: .leading, spacing: 16) {
                Text("Name")
                    .font(.custom("Inter-Medium", size: 16))
                    .foregroundColor(Color(hex: "#180E25"))
                TextField("Your name", text: $name)
                    .textFieldStyle(RoundedBorderTextFieldStyle())

                Text("Email")
                    .font(.custom("Inter-Medium", size: 16))
                    .foregroundColor(Color(hex: "#180E25"))
                TextField("Your email", text: $email)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .keyboardType(.emailAddress)
            }
            .padding(.horizontal, 24)

            Spacer()

            Button(action: { onBack() }) {
                Text("Save")
                    .font(.custom("Inter-Medium", size: 16))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(hex: "#504EC3"))
                    .cornerRadius(12)
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .background(Color.white.ignoresSafeArea())
        .sheet(isPresented: $showImagePicker) {
            Text("Image Picker Placeholder")
        }
    }
}


#Preview {
    ProfileEditView()
}
