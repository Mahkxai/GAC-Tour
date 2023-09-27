# GAC Tour App Documentation

## Overview
The GAC Tour App allows users to experience an interactive 3D image of the campus map. With geolocation tracking, users can navigate through the campus and access media streams within a 50m radius of their current location. Users can also contribute by dropping pins on the map to upload media, providing a rich, immersive, and constantly evolving tour experience.

## Main Features

- **3D Image Campus Map**: Navigate through a detailed and scaled 3D image representation of the campus.
- **Location Tracking**: Tracks and displays the user's current position on the map.
- **Media Streaming**: Discover media content such as photos, videos, or audios within a 50m radius of the user's current location. A banner on the current location indicator displays the number of media items available nearby.
- **Media Pinning**: Users can drop pins on any location within the map to upload and share media for that specific location.

## Component Details

### MapFragment
This is the main component where the core functionalities of the app reside.
For details on how the interactive map functionality is implemented, see the [MapPhotoView Documentation](#mapphotoview-documentation).

#### Variables:
- `fusedLocationClient`: Responsible for retrieving the user's current location.
- `locationRequest`: Configurations and settings for requesting location updates.
- `locationCallback`: Callback to handle location updates.
- Firebase references: `storageRef` and `dbRef` for accessing Firebase storage and database respectively.
- UI components: `currentLoc`, `dropPin`, `promptView`, and `uploadProgressView`.
- `mapPhotoView`: The main view representing the 3D image map.
- `mapFrameLayout`: The layout container for the map view.
- Constants: `animationDuration`, `picCount`, `promptX`, and `promptY`.

#### Key Methods:

- **`onCreateView`**: Initializes the view, sets up the 3D map, applies zoom settings, and adds a tap listener for user interactions.
  
- **`onViewCreated`**: After the main view is created, this method initializes the location services and sets up Firebase configurations.

- **`onPhotoTapListener`**: Handles user taps on the 3D map. It provides functionality for dropping pins and prompts users for media uploads.

- **`filePickerActivityResult`**: Uses the Android activity result API to retrieve selected media from the user's device.

## Usage

### Accessing Media Near You:
1. Navigate the 3D map to your current location.
2. If there's any media within a 50m radius, a banner will appear on the current location indicator displaying the number of media items available.
3. Interact with the banner to stream and view the media.

### Uploading Media:
1. Navigate to a location on the 3D map where you wish to share media.
2. Tap on the desired location. A pin will drop.
3. A prompt will appear, asking if you'd like to upload media for that location.
4. Choose the media from your device to upload.
5. Once the upload is successful, the media will be accessible to all users near that location.

## Contributing
For developers looking to contribute or extend the functionalities of the app, the source code is structured to ensure easy integration of new features. Kindly follow the established patterns and make sure to test your code thoroughly before merging.

## License

### No Reuse License (NRL)

**Copyright © [2023], [Hardik Shrestha]**

All rights reserved.

#### Terms and Conditions:

1. **Grant of License**: This license permits temporary use of the software, GAC Tour App, contained herein. It does not constitute any form of transfer of rights, ownership, or claim on the software.
2. **Restrictions**: You may not:
   - Reproduce, distribute, or transmit the software or portions thereof in any form or by any means, electronic or mechanical, for any purpose.
   - Modify, adapt, translate, reverse engineer, decompile, or disassemble the software.
   - Remove any proprietary notices, labels, or marks from the software.
   - Use the software for commercial purposes.

---

[Back to Top](#gac-tour-app-documentation)

# MapPhotoView Documentation

The `MapPhotoView` class enables you to utilize an image of a map as an actual, interactive map by correlating real-world coordinates with image coordinates. With this, you can pin any real-world location within the area of the map image.

## Class Initialization

The `MapPhotoView` class extends `PhotoView` to provide zoom and pan capabilities.

## Real and Image Coordinates

Real-world and corresponding pixel coordinates are set for 4 reference points:

- Tennis Court (`realTennis`, `pixelTennis`)
- Plex (`realPlex`, `pixelPlex`)
- Pittman (`realPitt`, `pixelPitt`)
- Roundabout near the High School (`realRound`, `pixelRound`)

## Key Methods

### Initialization (`init` Block)

- Initializes OpenCV for matrix transformations.
- Sets a matrix change listener that updates the positions of views as the image (map) is zoomed or panned.

### `addView(view: View, x: Double, y: Double, isPixel: Boolean = false)`

- Allows the addition of views (e.g., pins) to the map.
- Parameters:
  - `view`: The view to be added.
  - `x`: x-coordinate. This can be either a pixel or a real-world coordinate, depending on `isPixel`.
  - `y`: y-coordinate.
  - `isPixel`: A boolean that denotes whether the given coordinates are in pixels (true) or real-world (false). Default is `false`.

### `getPinPosition(x: Double, y: Double, isPixel: Boolean = false): Pair<Double, Double>`

- Transforms the provided real-world or pixel coordinates to the corresponding pixel or real-world coordinates on the image map.
- Uses the OpenCV `findHomography` method to determine the transformation matrix and `perspectiveTransform` to transform the points.

### `updateViewPosition(view: View, x: Double, y: Double)`

- Maps and positions the transformed coordinates on the `PhotoView`.

## Additional Notes

- **Scaling**: The class computes scales based on image dimensions and reference dimensions to ensure pins are placed accurately regardless of image size.
- **View Tags**: Views added to the map will have their `tag` property set to a float array containing their x and y coordinates. This is used to track the view's original position on the map and to ensure its correct placement during zooms or pans.
- **OpenCV Dependency**: This class relies on the OpenCV library for matrix operations and transformations. Ensure OpenCV is integrated and properly initialized in your project.

## Usage Example

To use the `MapPhotoView`, first integrate it into your layout:

```xml
<com.bignerdranch.android.gactour.MapPhotoView
    android:id="@+id/mapPhotoView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

In your Activity or Fragment (kotlin):
```kotlin
val mapPhotoView = findViewById<MapPhotoView>(R.id.mapPhotoView)
val pinView = Button(this)
pinView.text = "My Location"
mapPhotoView.addView(pinView, -93.9700, 44.3275)  // Example real-world coordinates
```

