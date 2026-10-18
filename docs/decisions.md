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
or actions in view model. I make it Singleton in order to have only one instance. Hilt will handle
the initialization and give the application context. Like that, we can remove the strong reference
to the MainActivity.

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

### Offline mode
Offline mode with Room. Room is the single source of truth.
The albums list always tries the API first, replaces the stored albums in a transaction,
and falls back on the database if the call fails, so the app keeps working offline
once albums have been loaded at least once. Album details are read from the database,
which also survives process death. The in-memory cache was removed since Room replaces it.

### Favorite feature
Favorites are stored in their own Room table (`favorites`), not as a column of the albums table.
Refreshing albums replaces all album rows (`replaceAll`), which would erase a column. There is no
foreign key either, because `ON DELETE CASCADE` would delete favorites with the albums.
`FavoriteDao.toggle` runs in a transaction, so a fast double tap cannot leave an inconsistent state.

Both screens must show the same favorite state. With Compose navigation, the albums list
ViewModel stays alive in the back stack while the details screen is open, so a one-shot read
would show an outdated heart when going back.

To solve it, favorite ids are observed as a `Flow` from Room, which emits a new set each time
the table changes. Each ViewModel keeps its loaded albums in a private `loadState` and merges it
with favorite ids using `combine`, then exposes the result with
`stateIn(WhileSubscribed(5_000))`:
- a toggle only writes to the database; Room emits, `combine` recomputes the ui state,
  and every screen observing favorites is updated, without any link between screens,
- albums are not reloaded when a favorite changes, only the `isFavorite` flags are recomputed,
  and the `LazyColumn` keys limit recomposition to the changed row,
- the 5 seconds timeout keeps the flow active during a configuration change.

Albums themselves are still loaded once (`suspend`), because favorites are the only data that
changes while a screen is displayed. Observing albums from Room too would be the next step
toward a fully reactive offline-first architecture.