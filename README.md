# Démo
![Démo Gif, wait for it !](https://github.com/NinoDLC/Android_P4_Ma_reu/blob/master/example.gif)

![Démo Gif for detail screen, wait for it !](https://github.com/NinoDLC/Android_P4_Ma_reu/blob/master/example_detail.gif)

# Sujets abordés / démontrés
 * Architecture MVVM (Model View ViewModel)
 * `LiveData` (en particulier `MediatorLiveData`)
 * Utilisation d'un `Fragment` comme vue (`CreateMeetingFragment`)
 * Utilisation d'une `Activity` comme vue (`MeetingActivity`)
 * Utilisation d'une `DialogFragment` customisée (`SortDialogFragment`)
 * `RecyclerView` (et son `ListAdapter` / `DiffItemCallback`)
 * Dialogue entre un `Adapter` et son `Activity` (via l'interface `OnMeetingClickedListener`)
 * Utilisation d'un Repository pour persister les différents Meetings pendant la vie de l'Application (`MeetingRepository`)
 * Utilisation d'un Repository pour faire communiquer 2 ViewModels : `MeetingActivity` et `SortDialogFragment` (`SortingParametersRepository`)
 * `Spinner` de MaterialDesign avec des vues complexes : TextInputLayout & AutoCompleteTextView (`CreateMeetingSpinnerAdapter`)
 * `AnimatedVectorDrawable` avec multiple état et animations (`asd_sort.xml`)
 * Enums (`Room`)
 * Singleton (`ViewModelFactory`)
 * Tests unitaires (TU) avec des `LiveData` et `ViewModels` (grâce à `Mockito`)
 * Code Coverage à 97% (`JaCoCo`)
 * Tests d'intégration poussés avec `Espresso`

# Commandes utiles :
`./gradlew jacocoDebugReport` pour générer le rapport de tests unitaires
`./gradlew connectedDebugAndroidTest` pour lancer les tests d'intégration (avec un émulateur lancé ou device branché)