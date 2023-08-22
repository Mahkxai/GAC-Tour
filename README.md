# GAC Tour - Discover Campus Stories in Real Time
Welcome to GAC Tour, an innovative crowd-sourced app that brings campus stories to life through dynamic media streams based on users' locations. Designed as a social media platform, this app empowers current students to share their unique stories from specific campus buildings. Visitors, in turn, can access and enjoy these stories as they explore different buildings during their tour.

![login_page](https://github.com/hardikshr/GAC-Tour/assets/110008888/54b4b36e-dbee-4ba0-8bac-f23a68770512)

## Tech Stack
Built with: Android Studio (Kotlin), Firebase Database, Firebase Storage, GeoLocationApi

## How It Works
**1. User Authentication:** The app greets users with a login screen where they can identify as students or guests. Both groups can enjoy the same media streams, but students enjoy the added privilege of contributing their own media.

**1. Media Sharing:** For the prototype, students can choose from a curated list of buildings where they can upload their media. The media content is stored in Firebase storage, organized neatly into folders that correspond to each building on the list.

**1. Smart Metadata Handling:** To enhance the media streaming experience, metadata about the media is stored separately in a Firebase database. This separation allows for the implementation of intelligent scheduling algorithms, ensuring that the right media is streamed to the right location at the right time.

**1. Location-Based Streaming:** Currently, media associated with a particular building is streamed to the app when the user is within a 50-meter radius of the building. By leveraging reference latitude and longitude coordinates, the app identifies designated areas around each building. These coordinates are meticulously chosen to avoid overlapping areas. Using the GeoLocationApi, the app determines the user's location and triggers media streaming when the user enters a designated area.

### Student's Screen
![student](https://github.com/hardikshr/GAC-Tour/assets/110008888/259c86d1-04ff-4d40-b8a0-61e34c646b4c)

### Guest's Screen
![guest](https://github.com/hardikshr/GAC-Tour/assets/110008888/25ecc31a-541e-4144-ac76-e2bcca383826)

### Firebase Storage Snapshot
![fb_storage](https://github.com/hardikshr/GAC-Tour/assets/110008888/78052890-21a2-45a1-912d-de529bd49ce3)

### Firebase Database Snapshot
![fb_database](https://github.com/hardikshr/GAC-Tour/assets/110008888/56d0e4fb-61ca-4e11-8527-a308b0129291)

## Future Enhancements
**Interactive Mapping:** Soon, students will have access to an interactive map feature. This will allow them to drop a pin on any desired location and upload media for that specific geographical area.

## Optimization Ideas
**2. Real-Time Notifications:** Implement real-time notifications to inform users when they enter an area with available media, enhancing their experience and engagement.

**2. Personalized Recommendations:** Use machine learning algorithms to provide personalized media recommendations based on user preferences and historical interactions.

## Lessons Learned

Through the development of GAC Tour, the team gained insights into effective integration of Firebase services, handling location-based triggers, and designing user-centric features. The project also highlighted the potential for future optimizations to create an even more seamless and engaging user experience.

## Project Disclaimer

This project and its contents are intended to showcase a unique concept and idea. The primary focus is on the innovative concept, and while code and implementation details are provided, they are not released under an open-source license. The project owner retains all rights to the idea and its implementation.

Any use, modification, or distribution of the code or content provided in this repository without explicit permission from the project owner is strictly prohibited. This includes, but is not limited to, copying the concept, replicating functionality, or using any design elements.

## License

This project is **All Rights Reserved**, meaning that all rights for the code and content in this repository are reserved by the owner (you). No one is allowed to use, modify, or distribute the code without explicit permission from the owner. For inquiries or requests related to the concept, please contact shr.hardik@gmail.com.

(C) 2023 Hardik Shrestha




