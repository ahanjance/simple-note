import SwiftUI

struct HomeView: View {
    var onTapAdd: () -> Void = {}
    var onTapSettings: () -> Void = {}
    var onNoteTapped: (Note) -> Void = { _ in }

    @State private var searchText: String = ""
    @State private var notes: [Note] = []
    @State private var isLoading = false

    var body: some View {
        ZStack(alignment: .bottom) {
            Color(hex: "#FAF8FC").ignoresSafeArea()

            VStack(spacing: 0) {
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(Color(hex: "#C8C5CB"))
                        .padding(.leading, 12)
                    TextField("Search...", text: $searchText)
                        .font(.custom("Inter-Regular", size: 16))
                        .foregroundColor(Color(hex: "#180E25"))
                        .padding(.vertical, 12)
                        .padding(.trailing, 12)
                        .onSubmit {
                            handleSearch()
                        }
                }
                .frame(height: 36)
                .background(Color.white)
                .cornerRadius(12)
                .padding(.horizontal, 16)
                .padding(.top, 18)
                .shadow(color: Color(hex: "#EFEEF0"), radius: 0, x: 0, y: 1)

                HStack {
                    Text("Notes")
                        .font(.custom("Inter-Bold", size: 14))
                        .foregroundColor(Color(hex: "#180E25"))
                        .frame(height: 20)
                        .padding(.leading, 16)
                        .padding(.top, 16)
                    Spacer()
                }

                ScrollView {
                    LazyVGrid(columns: [GridItem(.flexible(), spacing: 16), GridItem(.flexible(), spacing: 16)], spacing: 16) {
                        ForEach(notes) { note in
                            VStack(alignment: .leading, spacing: 12) {
                                Text(note.title)
                                    .font(.custom("Inter-Medium", size: 16))
                                    .foregroundColor(Color(hex: "#180E25"))
                                    .frame(height: 44, alignment: .topLeading)
                                Text(note.content)
                                    .font(.custom("Inter-Regular", size: 10))
                                    .foregroundColor(Color(hex: "#180E25").opacity(0.6))
                                    .frame(height: 140, alignment: .topLeading)
                            }
                            .padding(12)
                            .background(note.backgroundColor)
                            .cornerRadius(8)
                            .frame(width: 155, height: 224, alignment: .topLeading)
                            .onTapGesture {
                                onNoteTapped(note)
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 16)
                }
                .frame(maxWidth: .infinity)
            }

            VStack {
                Spacer()
                ZStack {
                    HStack {
                        VStack {
                            Image(systemName: "house.fill")
                                .resizable()
                                .frame(width: 24, height: 24)
                                .foregroundColor(Color(hex: "#504EC3"))
                            Text("Home")
                                .font(.custom("Inter-Regular", size: 10))
                                .foregroundColor(Color(hex: "#504EC3"))
                        }
                        .frame(width: 52, height: 52)
                        Spacer()

                        VStack {
                            Button(action: onTapSettings) {
                                VStack(spacing: 4) {
                                    Image(systemName: "gearshape")
                                        .resizable()
                                        .frame(width: 24, height: 24)
                                        .foregroundColor(Color(hex: "#827D89"))
                                    Text("Settings")
                                        .font(.custom("Inter-Regular", size: 10))
                                        .foregroundColor(Color(hex: "#827D89"))
                                }
                            }
                        }
                        .frame(width: 52, height: 52)
                        .padding(.trailing, 24)
                    }
                    .padding(.horizontal, 36)
                    .padding(.bottom, 8)
                    .frame(height: 70)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))

                    Button(action: onTapAdd) {
                        ZStack {
                            Circle()
                                .fill(Color(hex: "#FAF8FC"))
                                .frame(width: 80, height: 80)
                            Circle()
                                .fill(Color(hex: "#504EC3"))
                                .frame(width: 64, height: 64)
                                .shadow(color: Color.black.opacity(0.2), radius: 4, x: 0, y: 3)
                            Image(systemName: "plus")
                                .resizable()
                                .scaledToFit()
                                .foregroundColor(.white)
                                .frame(width: 24, height: 24)
                        }
                    }
                    .offset(y: -20)
                }
            }
            .ignoresSafeArea(edges: .bottom)
        }
        .navigationBarHidden(true)
        .onAppear(perform: loadNotes)
    }

    private func loadNotes() {
        guard !isLoading else { return }
        Task { @MainActor in
            isLoading = true
            do {
                let page = try await NotesService.shared.list(pageSize: 50)
                notes = page.results.map(Note.init(from:))
            } catch {
                // keep empty list on error
            }
            isLoading = false
        }
    }

    private func handleSearch() {
        Task { @MainActor in
            do {
                let page = try await NotesService.shared.filter(title: searchText)
                notes = page.results.map(Note.init(from:))
            } catch {
                // ignore
            }
        }
    }
}

#Preview {
    HomeView()
}
