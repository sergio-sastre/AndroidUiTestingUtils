# Robolectric setup

Robolectric supports screenshot testing via [Robolectric Native graphics (RNG)](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.10) since 4.10, and libraries like [Roborazzi,](https://github.com/takahirom/roborazzi) rely on it.

With _AndroidUiTestingUtils_, you can configure your Robolectric screenshot tests similar to how you'd do it with on-device tests!&#x20;

Moreover, it offers some utility methods to generate Robolectric screenshot tests for different screen sizes and configurations.

For that, add the following dependencies in your `build.gradle`:

```groovy
// Available from version 2.0.0+
testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>'
testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:robolectric:<version>'
```

## Application Modules

If you get any error due to "Activity not found" in your application module, add the following to your `debug/manifest`

```xml
<activity android:name="sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioConfigurator$SnapshotConfiguredActivity"/>
```
