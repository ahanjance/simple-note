import SwiftUI

struct TrashNotesView: View {
    @State private var deletedNotes: [Note] = []
    var onRestore: (Note) -> Void
    var onPermanentDelete: (Note) -> Void
    var onBack: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
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
                Text("Trash")
                    .font(.custom("Inter-Medium", size: 18))
                    .foregroundColor(Color(hex: "#180E25"))
                Spacer()
            }
            .frame(height: 54)
            .padding(.horizontal, 16)
            .background(Color.white)
            .overlay(
                Rectangle()
                    .frame(height: 1)
                    .foregroundColor(Color(hex: "#EFEEF0")),
                alignment: .bottom
            )

            if deletedNotes.isEmpty {
                VStack {
                    Spacer()
                    Text("No deleted notes")
                        .font(.custom("Inter-Regular", size: 16))
                        .foregroundColor(Color(hex: "#827D89"))
                    Spacer()
                }
            } else {
                List {
                    ForEach(deletedNotes) { note in
                        VStack(alignment: .leading, spacing: 4) {
                            Text(note.title)
                                .font(.custom("Inter-Medium", size: 16))
                                .foregroundColor(Color(hex: "#180E25"))
                            Text(note.content)
                                .lineLimit(2)
                                .foregroundColor(Color(hex: "#827D89"))
                        }
                        .contextMenu {
                            Button(action: { onRestore(note) }) {
                                Label("Restore", systemImage: "arrow.uturn.left")
                            }
                            Button(role: .destructive, action: { onPermanentDelete(note) }) {
                                Label("Delete Permanently", systemImage: "trash")
                            }
                        }
                    }
                    .onDelete { indices in
                        indices.forEach { idx in
                            onPermanentDelete(deletedNotes[idx])
                        }
                        deletedNotes.remove(atOffsets: indices)
                    }
                }
                .listStyle(PlainListStyle())
            }
        }
        .background(Color.white.ignoresSafeArea())
    }
}

#Preview {
    TrashNotesView(onRestore: { _ in }, onPermanentDelete: { _ in })
}
