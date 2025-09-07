import SwiftUI

struct HomeEmptyView: View {
    var onTapAdd: () -> Void = {}
    var onTapSettings: () -> Void = {}

    var body: some View {
        ZStack(alignment: .bottom) {
            Color.white.ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()
                Image("home_illustration")  // Place your illustration PNG in Assets
                    .resizable()
                    .scaledToFit()
                    .frame(width: 240, height: 240)
                    .offset(y: 153)
                    
                Spacer().frame(height: 16)

                VStack(spacing: 16) {
                    Text("Start Your Journey")
                        .font(.custom("Inter-Bold", size: 24))
                        .foregroundColor(Color(hex: "#180E25"))
                        .frame(width: 221, height: 29)
                        .multilineTextAlignment(.center)

                    Text("Every big step start with small step.\nNotes your first idea and start\nyour journey!")
                        .font(.custom("Inter-Regular", size: 14))
                        .foregroundColor(Color(hex: "#827D89"))
                        .frame(width: 237, height: 60)
                        .multilineTextAlignment(.center)
                }
                .frame(width: 237, height: 105)
                .padding(.top, 200)

                Spacer()

                // --- Arrow Illustration ---
                Image("arrow_icon")  // Place your arrow PNG in Assets (named "arrow_icon")
                    .resizable()
                    .frame(width: 150, height: 100)   // or use actual size of your asset
                    .opacity(1)
                    .padding(.bottom, 40)

                // --- Tab Bar and Floating Button ---
                ZStack(alignment: .bottom) {
                    Rectangle()
                        .fill(Color.white)
                        .frame(height: 84)
                        .shadow(color: Color.black.opacity(0.06), radius: 1, y: -1)

                    // Floating + Button (Bordered)
                    ZStack {
                        Circle()
                            .strokeBorder(Color(hex: "#FAF8FC"), lineWidth: 3)
                            .frame(width: 80, height: 80)
                            .background(Circle().fill(Color.clear))
                        Button(action: onTapAdd) {
                            Circle()
                                .fill(Color(hex: "#504EC3"))
                                .frame(width: 64, height: 64)
                                .shadow(color: Color.black.opacity(0.2), radius: 4, x: 0, y: 3)
                                .overlay(
                                    Image(systemName: "plus")
                                        .resizable()
                                        .frame(width: 21.3, height: 21.3)
                                        .foregroundColor(.white)
                                )
                        }
                    }
                    .offset(y: -35)

                    // Tab items
                    HStack {
                        VStack(spacing: 8) {
                            Image(systemName: "house.fill")
                                .resizable()
                                .frame(width: 32, height: 32)
                                .foregroundColor(Color(hex: "#504EC3"))
                            Text("Home")
                                .font(.custom("Inter-Regular", size: 10))
                                .foregroundColor(Color(hex: "#504EC3"))
                        }
                        .frame(width: 52, height: 52)
                        .padding(.leading, 24)
                        Spacer()
                        VStack(spacing: 8) {
                            Image("gear_icon") // Your gear PNG ("gear_icon") or use systemName
                                .resizable()
                                .frame(width: 24, height: 24)
                                .foregroundColor(Color(hex: "#827D89"))
                            Text("Settings")
                                .font(.custom("Inter-Regular", size: 10))
                                .foregroundColor(Color(hex: "#827D89"))
                        }
                        .frame(width: 52, height: 52)
                        .padding(.trailing, 24)
                    }
                    .padding(.bottom, 12)
                    .frame(height: 84)
                }
            }
            .padding(.bottom, 0)
        }
        .ignoresSafeArea(edges: .bottom)
        .navigationBarHidden(true)
    }
}

#Preview {
    HomeEmptyView()
}
