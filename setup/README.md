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

For compatibility purposes, choose the `version` that better fits your project

| AndroidUiTestingUtils | CompileSDK | Kotlin  | Compose Compiler |
| --------------------- | ---------- | ------- | ---------------- |
| 2.3.0                 | 34         | 1.9.22+ | 1.5.10+          |
| 2.2.x                 | 34         | 1.9.x   | 1.5.x            |
| < 2.2.0               | 33+        | 1.8.x + | 1.4.x +          |

{% hint style="info" %}
The Kotlin and Compose compiler versions need to be compatible. Check the compatibility map [here](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
{% endhint %}
