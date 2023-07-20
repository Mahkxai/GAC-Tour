# GAC Tour
GAC Tour is a crowd-sourced app that streams media based on the user's location. The app serves as a social media platform where current students can share their stories from particular campus buildings onto the app whereby the visitors can view these stories as they pass by various locations during their tour.

![login_page](https://github.com/hardikshr/GAC-Tour/assets/110008888/54b4b36e-dbee-4ba0-8bac-f23a68770512)

## How It's Made:

**Tech used:** Android Studio, Kotlin, Firebase Database, Firebase Storage, GeoLocationApi

The starting screen of the app prompts the user to login
as a student or a guest. Both the parties will be streamed
the same media but the students will have an additional permission to
upload media of their liking as well. 

For the prototype, the students can select from a list of
buildings where they want to upload the media. The media
is stored in a firebase storage containing media folders for
every building on the list. The metadata about the media is
stored separately on a firebase database in order to facilitate
scheduling algorithms for streaming the appropriate media.

### Student's Screen
![student](https://github.com/hardikshr/GAC-Tour/assets/110008888/259c86d1-04ff-4d40-b8a0-61e34c646b4c)

### Guest's Screen
![guest](https://github.com/hardikshr/GAC-Tour/assets/110008888/25ecc31a-541e-4144-ac76-e2bcca383826)

### Firebase Storage Snapshot
![fb_storage](https://github.com/hardikshr/GAC-Tour/assets/110008888/78052890-21a2-45a1-912d-de529bd49ce3)

### Firebase Database Snapshot
![fb_database](https://github.com/hardikshr/GAC-Tour/assets/110008888/56d0e4fb-61ca-4e11-8527-a308b0129291)

For the final version, the students will have access to a
map where they can drop a pin to a desired location and
upload a media for the region enclosing the pin.
Currently, the media for a particular building is streamed
onto the app once the user is within 50 meters of the building.
This is achieved by having a reference latitude and longitude
for a building and specifying a 50 meter radius around those
coordinates indicating the area covered by the building. The
reference coordinates are chosen in a way that no two areas
overlap. The user’s current location can then be gathered
using the GeoLocationApi and the app detects if the current
location is within the specified area of a building and streams
media accordingly.

## Optimizations
*(optional)*

You don't have to include this section but interviewers *love* that you can not only deliver a final product that looks great but also functions efficiently. Did you write something then refactor it later and the result was 5x faster than the original implementation? Did you cache your assets? Things that you write in this section are **GREAT** to bring up in interviews and you can use this section as reference when studying for technical interviews!

## Lessons Learned:

No matter what your experience level, being an engineer means continuously learning. Every time you build something you always have those *whoa this is awesome* or *fuck yeah I did it!* moments. This is where you should share those moments! Recruiters and interviewers love to see that you're self-aware and passionate about growing.




