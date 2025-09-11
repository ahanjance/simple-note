import SwiftUI

struct SearchResultsView: View {
    @Binding var searchText: String
    var notes: [Note]
    var onNoteSelected: (Note) -> Void

    var filteredNotes: [Note] {
        if searchText.isEmpty {
            return notes
        }
        return notes.filter { note in
            note.title.localizedCaseInsensitiveContains(searchText) || note.content.localizedCaseInsensitiveContains(searchText)
        }
    }

    var body: some View {
        VStack {
            List(filteredNotes) { note in
                Text(note.title)
                    .onTapGesture {
                        onNoteSelected(note)
                    }
            }
        }
        .navigationTitle("Search Results")
    }
}

#Preview {
    SearchResultsView(searchText: Binding.constant(""), notes: [], onNoteSelected: { _ in })
}
