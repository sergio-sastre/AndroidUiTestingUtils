# Setup

Add jitpack to your root `build.gradle` file:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    // instrumentation tests
    androidTestImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>')
    // jvm tests (e.g. robolectric)
    testImplementation('com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>')
}
```

