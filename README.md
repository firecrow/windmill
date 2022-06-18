*Windmill - Application Manager*
android app by Firecrow Silvernight - https://windmill.firecrow.com - windmill@firecrow.com

Windmill is a simple homescreen app that shows icons in a grid with a minimal edge to edge modern look where icons are no longer floating in space but fill entire grid cells with their respective background colors.

The app is broken up into several parts, WMActivity has the main behavior and anything essentially shared, the rest of the app splits into pieces based on the behavior domain they own.

AppsFragment.kt - this is the generic fragment class for both the list and grid views
AppsObservables.kt - this has the observable management classes to communication between the activity and fragments
Fetcher.kt - manages querying the application manager of the sysetm for the list and logos of the apps
GridFragment.kt - inherits from AppsFragment to supply a grid specific adapter
ListFragment.kt - inherits from AppsFragment to supply a list specific adapter
Model.kt - definition of the main data object which holds the apps
RowBuilder.kt - build rows or cells for the list/grid views
ScreenToken.kt - constant used to determine which framgnet to show
SearchObj.kt - behavior to manage the search bar interactivity including navigation icons
WMActivity.kt - main activity with everything that needs to be shared across the system
WMAdapter.kt - contains adapters for the list and grid view


I"ve decided to factor our ui components into view components, it feels really good to have thier logic encapsulated in a seperate place and have them accessible via xml for quick configuration of the container and attributes, though a lot of code happens in code now instead of xml I think the components themsleves are a less frequencly changing part than the asesembly of those components

got the appiconview unit test to work, created a getter for backdropColor, the implementation test is much nicer with a smaller component