import Foundation
import SwiftUI

struct APIConfig {
    static var baseURL: URL {
        // Adjust if running on device/emulator
        return URL(string: "http://localhost:8000/api")!
    }
}

final class AuthStorage {
    static let shared = AuthStorage()

    private let accessTokenKey = "auth_access_token"
    private let refreshTokenKey = "auth_refresh_token"

    var accessToken: String? {
        get { UserDefaults.standard.string(forKey: accessTokenKey) }
        set { UserDefaults.standard.setValue(newValue, forKey: accessTokenKey) }
    }

    var refreshToken: String? {
        get { UserDefaults.standard.string(forKey: refreshTokenKey) }
        set { UserDefaults.standard.setValue(newValue, forKey: refreshTokenKey) }
    }

    func clear() {
        UserDefaults.standard.removeObject(forKey: accessTokenKey)
        UserDefaults.standard.removeObject(forKey: refreshTokenKey)
    }
}

enum APIError: Error {
    case invalidURL
    case decodingFailed
    case serverError(status: Int, message: String?)
    case unauthorized
}

final class APIClient {
    static let shared = APIClient()
    private let session: URLSession

    init(session: URLSession = .shared) {
        self.session = session
    }

    func request<T: Decodable>(_ path: String,
                               method: String = "GET",
                               body: Encodable? = nil,
                               authorized: Bool = false,
                               query: [URLQueryItem] = [],
                               decoder: JSONDecoder = JSONDecoder()) async throws -> T {
        var components = URLComponents(url: APIConfig.baseURL.appendingPathComponent(path), resolvingAgainstBaseURL: false)
        if !query.isEmpty { components?.queryItems = query }
        guard let url = components?.url else { throw APIError.invalidURL }

        var req = URLRequest(url: url)
        req.httpMethod = method
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if authorized, let token = AuthStorage.shared.accessToken {
            req.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        if let body = body {
            let encoder = JSONEncoder()
            req.httpBody = try encoder.encode(AnyEncodable(body))
        }

        let (data, resp) = try await session.data(for: req)
        guard let http = resp as? HTTPURLResponse else { throw APIError.serverError(status: -1, message: nil) }
        if http.statusCode == 401 { throw APIError.unauthorized }
        guard 200..<300 ~= http.statusCode else {
            let message = String(data: data, encoding: .utf8)
            throw APIError.serverError(status: http.statusCode, message: message)
        }
        decoder.dateDecodingStrategy = .iso8601
        return try decoder.decode(T.self, from: data)
    }
}

// Helper to encode unknown Encodable bodies without type erasure pain
struct AnyEncodable: Encodable {
    private let enc: (Encoder) throws -> Void
    init(_ encodable: Encodable) {
        self.enc = encodable.encode
    }
    func encode(to encoder: Encoder) throws { try enc(encoder) }
}