### Hilt
I always worked with Dagger, but I choose Hilt for dependency injection. It is simpler
to implement, it avoids all the Android plumbing to do by hand, and it is a good opportunity
to discover Hilt for me.

### :core:network module
I decided to create this module in order to be able to initialize and use Retrofit with OkHttp
in several modules

### Dependencies, versions, libraries and plugins sort
I like to sort by type and alphabetically in order to find things quickly

### build-logic
I didn't take the time to add this module, but it will be good to remove all redundant setup
in several build.gradle files.

### Clean architecture
I migrate the architecture to modular clean architecture.
It addresses that every app runs into as it grows.

### Kotlin Result of Michael Bull
I choose to use Michael Bull Kotlin Result, because it handles result with value and error.

### Analytics
I decided to move AnalyticsHelper from :app to :core:analytics in order to be able to track events
or actions in view model. I make it Singleton, because it is initialized in activity (:app)
and use in view model (:feature:albums:iu).

### Compose navigation
I choose to use compose navigation for navigation of the feature Alums. This feature
contains albums list and album details. With compose navigation, each view model is stored in its
destination back stack entry, which is kept across activity recreation.

### Unit tests
For unit tests, I used Mockito with JUnit4 for the simplicity. Usually, I work with
Mockito with JUnit5.

### Configuration changes
Compose navigation keep the view model across activity recreation.
Load albums on init instead of LaunchedEffect(Unit) avoid to load new albums on each
activity recreation.
The scroll position is keep.

