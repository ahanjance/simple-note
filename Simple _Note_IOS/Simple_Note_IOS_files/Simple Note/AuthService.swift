import Foundation

struct LoginRequestBody: Encodable {
    let username: String
    let password: String
}

struct RegisterRequestBody: Encodable {
    let username: String
    let password: String
    let email: String
    let first_name: String
    let last_name: String
}

struct ChangePasswordBody: Encodable {
    let old_password: String
    let new_password: String
}

final class AuthService {
    static let shared = AuthService()

    func login(usernameOrEmail: String, password: String) async throws {
        // Backend expects username for token endpoint; if email provided, assume username==email for simplicity
        let body = LoginRequestBody(username: usernameOrEmail, password: password)
        let tokens: AuthTokenPair = try await APIClient.shared.request("/auth/token/", method: "POST", body: body, authorized: false)
        AuthStorage.shared.accessToken = tokens.access
        AuthStorage.shared.refreshToken = tokens.refresh
    }

    func refresh() async throws {
        struct RefreshBody: Encodable { let refresh: String }
        guard let refresh = AuthStorage.shared.refreshToken else { return }
        let tokens: AuthTokenPair = try await APIClient.shared.request("/auth/token/refresh/", method: "POST", body: RefreshBody(refresh: refresh), authorized: false)
        AuthStorage.shared.accessToken = tokens.access
    }

    func register(username: String, email: String, password: String, first: String, last: String) async throws {
        let body = RegisterRequestBody(username: username, password: password, email: email, first_name: first, last_name: last)
        let _: APIUser = try await APIClient.shared.request("/auth/register/", method: "POST", body: body, authorized: false)
    }

    func userInfo() async throws -> APIUser {
        try await APIClient.shared.request("/auth/userinfo/", method: "GET", authorized: true)
    }

    func changePassword(old: String, new: String) async throws {
        let body = ChangePasswordBody(old_password: old, new_password: new)
        struct Message: Decodable { let detail: String }
        let _: Message = try await APIClient.shared.request("/auth/change-password/", method: "POST", body: body, authorized: true)
    }
}