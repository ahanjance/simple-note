import SwiftUI

struct NoteDeleteConfirmationView: View {
    var onCancel: () -> Void = {}
    var onDelete: () -> Void = {}
    @State private var isVisible = false

    var body: some View {
        ZStack {
            // Dimmed background
            Color.black.opacity(0.4).ignoresSafeArea()

            // Bottom pop-up with animation
            VStack(spacing: 0) {
                Spacer()
                VStack(spacing: 0) {
                    HStack {
                        Text("Want to Delete this Note?")
                            .font(.custom("Inter-Medium", size: 16))
                            .foregroundColor(Color(hex: "#180E25"))
                        Spacer()
                        Button(action: {
                            withAnimation(.spring(response: 0.4, dampingFraction: 0.7)) {
                                isVisible = false
                            } completion: {
                                onCancel()
                            }
                        }) {
                            Image(systemName: "xmark")
                                .resizable()
                                .frame(width: 16, height: 16)
                                .foregroundColor(Color(hex: "#827D89"))
                                .padding(4)
                                .background(Color(hex: "#EFEEF0"))
                                .clipShape(Circle())
                        }
                    }
                    .padding(.horizontal, 18)
                    .padding(.top, 16)
                    .padding(.bottom, 10)

                    Divider()
                        .frame(height: 1)
                        .background(Color(hex: "#EFEEF0"))
                        .padding(.horizontal, 18)

                    Button(action: {
                        withAnimation(.spring(response: 0.4, dampingFraction: 0.7)) {
                            isVisible = false
                        } completion: {
                            onDelete()
                        }
                    }) {
                        HStack(spacing: 12) {
                            Image(systemName: "trash")
                                .resizable()
                                .frame(width: 16, height: 18)
                                .foregroundColor(Color(hex: "#CE3A54"))
                            Text("Delete Note")
                                .font(.custom("Inter-Medium", size: 16))
                                .foregroundColor(Color(hex: "#CE3A54"))
                        }
                        .padding(.vertical, 11)
                        .padding(.leading, 32)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .background(Color.white)
                    .padding(.top, 6)
                    .padding(.bottom, 40)
                }
                .background(Color.white)
                .cornerRadius(24, corners: [.topLeft, .topRight])
                .frame(maxWidth: .infinity)
                .offset(y: isVisible ? 0 : 200)  // Slide from bottom
                .opacity(isVisible ? 1 : 0)      // Fade in
                .animation(.spring(response: 0.4, dampingFraction: 0.7), value: isVisible)
            }
            .ignoresSafeArea(edges: .bottom)
        }
        .onAppear {
            withAnimation(.spring(response: 0.4, dampingFraction: 0.7)) {
                isVisible = true
            }
        }
    }
}

// Extension for rounding specific corners
struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}
extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

#Preview {
    NoteDeleteConfirmationView()
}
