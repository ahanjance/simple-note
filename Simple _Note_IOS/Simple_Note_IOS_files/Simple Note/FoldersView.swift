import SwiftUI

struct Folder: Identifiable {
    let id = UUID()
    var name: String
}

struct FoldersView: View {
    @State private var folders: [Folder] = [Folder(name: "Work"), Folder(name: "Personal"), Folder(name: "Ideas")]
    @State private var newFolderName: String = ""
    var onBack: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
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
                Text("Folders")
                    .font(.custom("Inter-Medium", size: 18))
                    .foregroundColor(Color(hex: "#180E25"))
                Spacer()
                Color.clear.frame(width: 60, height: 44)  // Invisible spacer for centering
            }
            .frame(height: 54)
            .padding(.horizontal, 16)
            .background(Color.white)
            .overlay(Rectangle().frame(height: 1).foregroundColor(Color(hex: "#EFEEF0")), alignment: .bottom)

            HStack {
                TextField("New folder name", text: $newFolderName)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                Button("Add") {
                    guard !newFolderName.isEmpty else { return }
                    folders.append(Folder(name: newFolderName))
                    newFolderName = ""
                }
            }
            .padding(16)

            List {
                ForEach(folders) { folder in
                    Text(folder.name)
                }
                .onDelete { i in
                    folders.remove(atOffsets: i)
                }
            }
            .listStyle(PlainListStyle())
        }
        .background(Color.white.ignoresSafeArea())
    }
}


#Preview {
    FoldersView()
}
