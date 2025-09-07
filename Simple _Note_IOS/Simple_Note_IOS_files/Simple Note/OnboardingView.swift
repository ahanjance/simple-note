import SwiftUI

struct OnboardingView: View {
    var onGetStarted: () -> Void = {}

    var body: some View {
        ZStack {
            Color(hex: "#504EC3")  // بنفش دقیق پس‌زمینه
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()
                    .frame(height: 56) // فاصله تا بالای تصویر تقریباً 128-280/2-28 (جهت هماهنگی با استاتوس بار)

                // Illustration
                Image("onboarding_1_illustration") // تصویر را با همین نام در Assets قرار بدهید
                    .resizable()
                    .renderingMode(.original)
                    .frame(width: 280, height: 280)
                    .padding(.bottom, 32)

                // Title
                Text("Jot Down anything you want to achieve, today or in the future")
                    .font(.custom("Inter-Bold", size: 20)) // فونت Inter بولد
                    .foregroundColor(.white)
                    .multilineTextAlignment(.leading)
                    .frame(width: 328, height: 56, alignment: .leading)
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.bottom, 40)

                Spacer()

                // Button
                Button(action: onGetStarted) {
                    HStack {
                        Spacer()
                        Text("Let’s Get Started")
                            .font(.custom("Inter-Medium", size: 17))
                        Spacer()
                        Image(systemName: "arrow.right")
                            .font(.system(size: 20, weight: .bold))
                    }
                    .frame(width: 328, height: 54)
                }
                .foregroundColor(Color(hex: "#504EC3"))
                .background(Color.white)
                .cornerRadius(100)
                .overlay(
                    RoundedRectangle(cornerRadius: 100)
                        .stroke(Color(hex: "#504EC3"), lineWidth: 1)
                )
                .padding(.bottom, 32)
                .padding(.top, 8)
                .padding(.leading, 16)
                .padding(.trailing, 16)
            }
        }
    }
}


#Preview {
    OnboardingView()
}
