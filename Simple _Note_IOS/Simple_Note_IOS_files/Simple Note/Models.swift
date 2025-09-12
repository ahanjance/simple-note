import Foundation
import SwiftUI

struct AuthTokenPair: Decodable {
    let access: String
    let refresh: String
}

struct APIUser: Decodable {
    let id: Int
    let username: String
    let email: String
    let first_name: String
    let last_name: String
}

struct APINote: Decodable, Identifiable {
    let id: Int
    let title: String
    let description: String
    let created_at: Date
    let updated_at: Date
    let creator_name: String?
    let creator_username: String
}

struct Paginated<T: Decodable>: Decodable {
    let count: Int
    let next: String?
    let previous: String?
    let results: [T]
}

extension Color {
    static func randomSoft() -> Color {
        let palette = [
            Color(hex: "#F5FCD0"),
            Color(hex: "#FDEBAB"),
            Color(hex: "#D0F0FD"),
            Color(hex: "#FAD0FC"),
            Color(hex: "#D0FDE2")
        ]
        return palette.randomElement() ?? .white
    }
}

extension Note {
    init(from apiNote: APINote) {
        self.init(title: apiNote.title, content: apiNote.description, backgroundColor: Color.randomSoft(), serverId: apiNote.id)
    }
}