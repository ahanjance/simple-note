import Foundation
import SwiftUI

struct CreateNoteBody: Encodable {
    let title: String
    let description: String
}

final class NotesService {
    static let shared = NotesService()

    func list(page: Int = 1, pageSize: Int = 50, query: String? = nil) async throws -> Paginated<APINote> {
        var items: [URLQueryItem] = [
            URLQueryItem(name: "page", value: String(page)),
            URLQueryItem(name: "page_size", value: String(pageSize))
        ]
        if let q = query, !q.isEmpty {
            items.append(URLQueryItem(name: "search", value: q))
        }
        return try await APIClient.shared.request("/notes/", authorized: true, query: items)
    }

    func filter(title: String? = nil) async throws -> Paginated<APINote> {
        var items: [URLQueryItem] = []
        if let t = title, !t.isEmpty { items.append(URLQueryItem(name: "title", value: t)) }
        return try await APIClient.shared.request("/notes/filter", authorized: true, query: items)
    }

    func create(title: String, description: String) async throws -> APINote {
        let body = CreateNoteBody(title: title, description: description)
        return try await APIClient.shared.request("/notes/", method: "POST", body: body, authorized: true)
    }

    func update(id: Int, title: String, description: String) async throws -> APINote {
        let body = CreateNoteBody(title: title, description: description)
        return try await APIClient.shared.request("/notes/\(id)/", method: "PUT", body: body, authorized: true)
    }

    func delete(id: Int) async throws {
        struct Empty: Decodable {}
        let _: Empty = try await APIClient.shared.request("/notes/\(id)/", method: "DELETE", authorized: true)
    }
}