import SwiftUI  // For Color

struct Note: Identifiable {
    let id = UUID()
    let title: String
    let content: String
    let backgroundColor: Color
}
