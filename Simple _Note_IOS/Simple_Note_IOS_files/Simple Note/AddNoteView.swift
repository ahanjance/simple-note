import SwiftUI

struct AddNoteView: View {
    var initialTitle: String = ""
    var initialContent: String = ""
    var initialColor: Color = .white

    var onBack: () -> Void = {}
    var onDelete: () -> Void = {}
    var onSave: (String, String, Color) -> Void = { _, _, _ in }

    @State private var title: String
    @State private var content: String
    @State private var backgroundColor: Color

    init(initialTitle: String = "", initialContent: String = "", initialColor: Color = .white, onBack: @escaping () -> Void = {}, onDelete: @escaping () -> Void = {}, onSave: @escaping (String, String, Color) -> Void = { _, _, _ in }) {
        self.initialTitle = initialTitle
        self.initialContent = initialContent
        self.initialColor = initialColor
        self.onBack = onBack
        self.onDelete = onDelete
        self.onSave = onSave

        _title = State(initialValue: initialTitle)
        _content = State(initialValue: initialContent)
        _backgroundColor = State(initialValue: initialColor)
    }

    private var lastEditedString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return "Last edited on \(formatter.string(from: Date()))"
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            backgroundColor.ignoresSafeArea()

            VStack(alignment: .leading, spacing: 24) {
                HStack(spacing: 8) {
                    Button(action: onBack) {
                        HStack(spacing: 8) {
                            Image(systemName: "chevron.left")
                                .resizable()
                                .frame(width: 10, height: 10)
                                .foregroundColor(Color(hex: "#504EC3"))
                            Text("Back")
                                .font(.custom("Inter-Medium", size: 16))
                                .foregroundColor(Color(hex: "#504EC3"))
                                .frame(height: 22)
                        }
                    }
                    Spacer()
                    Button("Save") {
                        onSave(title, content, backgroundColor)
                    }
                    .font(.custom("Inter-Medium", size: 16))
                    .foregroundColor(Color(hex: "#504EC3"))
                }
                .frame(height: 54)
                .padding(.horizontal, 16)
                .padding(.top, 12)
                .background(Color.white)
                .overlay(
                    Rectangle()
                        .frame(height: 1)
                        .foregroundColor(Color(hex: "#EFEEF0")),
                    alignment: .bottom
                )

                TextField("Title", text: $title)
                    .font(.custom("Inter-Bold", size: 32))
                    .foregroundColor(Color(hex: "#180E25"))
                    .frame(width: 328, height: 38)
                    .padding(.leading, 16)

                ZStack(alignment: .topLeading) {
                    if content.isEmpty {
                        Text("Feel Free to Write Here...")
                            .font(.custom("Inter-Regular", size: 16))
                            .foregroundColor(Color(hex: "#827D89"))
                            .padding(.horizontal, 16)
                            .padding(.top, 8)
                    }
                    TextEditor(text: $content)
                        .font(.custom("Inter-Regular", size: 16))
                        .frame(width: 328, height: 120)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.white)
                }

                Spacer()
            }

            HStack(spacing: 0) {
                Text(lastEditedString)
                    .font(.custom("Inter-Regular", size: 12))
                    .foregroundColor(.black)
                    .frame(width: 116, height: 15)
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
                .padding(.trailing, 0)
            }
            .frame(width: 360, height: 48)
            .background(Color.white)
            .overlay(
                Rectangle()
                    .frame(height: 1)
                    .foregroundColor(Color(hex: "#EFEEF0")),
                alignment: .top
            )
            .shadow(color: Color(hex: "#EFEEF0"), radius: 0, x: 0, y: -1)
        }
        .ignoresSafeArea(.keyboard, edges: .bottom)
    }
}


#Preview {
    AddNoteView()
}
