import SwiftUI

struct NoteDetailView: View {
    @State var title: String = "💡 New Product Ideas"
    @State var content: String = """
Create a mobile app UI Kit that provide a basic notes functionality but with some improvement.

There will be a choice to select what kind of notes that user needed, so the experience while taking notes can be unique based on the needs.
"""
    @State var lastEdited: Date = Date()

    var onBack: () -> Void = {}
    var onDelete: () -> Void = {}

    private var lastEditedString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH.mm"
        return "Last edited on \(formatter.string(from: lastEdited))"
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            Color.white.ignoresSafeArea()

            VStack(alignment: .leading, spacing: 24) {
                // Navigation Bar (smaller arrow)
                HStack(spacing: 0) {
                    Button(action: onBack) {
                        HStack(spacing: 4) {
                            Image(systemName: "chevron.left")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: 12, height: 18)
                                .foregroundColor(Color(hex: "#504EC3"))
                                .padding(.leading, 4)
                            Text("Back")
                                .font(.custom("Inter-Medium", size: 16))
                                .foregroundColor(Color(hex: "#504EC3"))
                        }
                    }
                    Spacer()
                }
                .frame(height: 54)
                .padding(.top, 12)
                .padding(.horizontal, 8)
                .background(Color.white)
                .overlay(
                    Rectangle()
                        .frame(height: 1)
                        .foregroundColor(Color(hex: "#EFEEF0")),
                    alignment: .bottom
                )

                // Title and content as one block, NO divider
                VStack(alignment: .leading, spacing: 14) {
                    Text(title)
                        .font(.custom("Inter-Bold", size: 24))
                        .foregroundColor(Color(hex: "#180E25"))
                        .fixedSize(horizontal: false, vertical: true)
                        .multilineTextAlignment(.leading)

                    Text(content)
                        .font(.custom("Inter-Regular", size: 16))
                        .foregroundColor(Color(hex: "#827D89"))
                        .lineSpacing(2)
                        .fixedSize(horizontal: false, vertical: true)
                        .multilineTextAlignment(.leading)

                    // Subtle divider only at the bottom of context
                    Rectangle()
                        .frame(height: 1)
                        .foregroundColor(Color(hex: "#EFEEF0"))
                        .padding(.top, 10)
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)

                Spacer()
            }

            // Task Bar with delete button and last edited timestamp
            HStack {
                Text(lastEditedString)
                    .font(.custom("Inter-Regular", size: 12))
                    .foregroundColor(.black)
                    .frame(width: 140, alignment: .leading)
                    .padding(.leading, 16)

                Spacer()

                Button(action: onDelete) {
                    Image(systemName: "trash")
                        .resizable()
                        .frame(width: 16, height: 18)
                        .foregroundColor(.white)
                        .padding(12)
                        .background(Color(hex: "#504EC3"))
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.white, lineWidth: 2)
                        )
                }
                .frame(width: 48, height: 48)
            }
            .frame(height: 48)
            .background(Color.white)
            .overlay(
                Rectangle()
                    .frame(height: 1)
                    .foregroundColor(Color(hex: "#EFEEF0")),
                alignment: .top
            )
            .shadow(color: Color(hex: "#EFEEF0"), radius: 0, x: 0, y: -1)
        }
        .navigationBarHidden(true)
    }
}


#Preview {
    NoteDetailView()
}
