# Fix Dependency Resolution Error

The build is failing because `mavenCentral()` is missing from the project's repository configurations. Modern Android libraries, including the Material components and Kotlin libraries, rely on dependencies hosted on Maven Central. The current configuration only includes `google()` and `jitpack.io`, causing the resolution to fail when looking for `kotlin-bom` and other transitive dependencies.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle](file:///D:/@ANDROID/BlackMove/build.gradle)
Add `mavenCentral()` to the `allprojects` repositories block to ensure that transitive dependencies like `kotlin-bom` can be resolved.

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug` (or `gradlew :app:assembleDebug` on Windows) to verify that dependencies are resolved correctly and the project builds successfully.

### Manual Verification
- Trigger a Gradle Sync in Android Studio to confirm that the "Could Not Resolve" errors disappear from the Build output.
