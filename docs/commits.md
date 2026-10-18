### Commit 1
- Initialize project and make it run

### Commit 2
- Add Hilt dependency injection
- Update analytics helper initialization in order to remove AppDependencies class and provider
- Update view model initialization on composable
- Create :core:network module in order to be able to initialize and use Retrofit/OkHttp on several modules
- Sort alphabetically versions, libraries and plugins
- Force jvm target to version 17

### Commit 3
- Start apply clean architecture
- Create :feature:albums:data (android) and :feature:albums:domain (jvm) modules
- Add coroutines and kotlin result (of Michael Bull) dependencies
- Add service, remote data source, dto, mapper, data repository in :feature:albums:data module
- Add model and repository in :feature:albums:domain

### Commit 4
- Add use cases in :feature:albums:domain
- Replace :data module by :feature:albums:data and update AlbumsViewModel to work with the new module.

### Commit 5
- Move Analytics helper from :app to :core:analytics module

### Commit 6
- Move ui part from :app to :feature:albums:ui and migrate to clean architecture for album screen
- Add compose navigation

### Commit 7
- Add album details ui part (view model, ui state and composable)

### Commit 8
- Add unit tests on :feature:albums modules (data, domain and ui)
Create :core:unittest module in order to work with dispatcher for unit tests

### Commit 9
- Add docs folder with documentation

### Commit 10
- Update Analytics helper to be Singleton

### Commit 11
- Fix all modules packages

### Commit 12
- Add architecture documentation

### Commit 13
- Fix Analytics memory leak and update its initialization

### Commit 14
- Add offline mode with Room

### Commit 15
- Add favorite feature in album details screen.