import SwiftUI  // For Color

struct Note: Identifiable {
    let id: UUID
    let serverId: Int?
    let title: String
    let content: String
    let backgroundColor: Color

    init(title: String, content: String, backgroundColor: Color, serverId: Int? = nil) {
        self.id = UUID()
        self.serverId = serverId
        self.title = title
        self.content = content
        self.backgroundColor = backgroundColor
    }
}
