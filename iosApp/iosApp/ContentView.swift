import SwiftUI
import shared
import MapboxMaps

struct ContentView: View {
	let greet = Greeting().greet()

    var body: some View {
        let center = CLLocationCoordinate2D(latitude: 39.5, longitude: -98.0)
        
        Map(
            initialViewport: .camera(
                center: center,
                zoom: 2,
                bearing: 0,
                pitch: 0
            )
        ).ignoresSafeArea()
        
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
