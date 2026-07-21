# Family Safety Platform — Complete Setup Guide

## Project Overview
**Name:** Family Safety & Device Management Platform
**Platform:** Android (Kotlin)
**Backend:** Firebase (Auth + Realtime Database + FCM + Crashlytics + Analytics)
**Storage:** Cloudinary (images, files, backups)
**Architecture:** MVVM + Clean Architecture

---

## FREE Services Used
| Service | Provider | Purpose |
|---------|----------|---------|
| Email/Password Auth | Firebase Auth | Login/Register |
| Google Sign-In | Firebase Auth | Social Login |
| Database | Firebase Realtime DB | All data storage |
| Push Notifications | Firebase FCM | Alerts & commands |
| Image Upload | Cloudinary | Profile photos, screenshots |
| File Upload | Cloudinary | Backups, recordings |
| Crash Reports | Firebase Crashlytics | Error tracking |
| Analytics | Firebase Analytics | Usage analytics |

## Services NOT Used (Paid)
- ~~Cloud Firestore~~ — Using Realtime DB instead
- ~~Cloud Functions~~ — Client-side logic only
- ~~Firebase Storage~~ — Using Cloudinary instead

---

## STEP 1: Firebase Project Setup

### 1.1 Create Firebase Project
1. Go to https://console.firebase.google.com
2. Click "Add Project"
3. Project name: `family-safety-platform`
4. Disable Google Analytics (add later if needed)
5. Click "Create Project"

### 1.2 Register Android App
1. In Firebase Console, click Android icon
2. Package name: `com.family.safety.platform`
3. App nickname: `Family Safety`
4. SHA-1 key (for Google Sign-In):
   ```
   keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android
   ```
5. Download `google-services.json`
6. Place it in `app/google-services.json`

### 1.3 Enable Firebase Services
Go to Firebase Console and enable:

**Authentication:**
- Email/Password ✅
- Google Sign-In ✅
- Anonymous Auth ✅ (for child devices)

**Realtime Database:**
- Create database
- Location: Choose nearest region
- Start in test mode, then apply rules

**Cloud Messaging:**
- Enabled by default ✅

**Crashlytics:**
- Enable from Crashlytics tab ✅

**Analytics:**
- Enable from Analytics tab ✅

---

## STEP 2: Cloudinary Setup

### 2.1 Create Cloudinary Account
1. Go to https://cloudinary.com
2. Sign up (free tier: 25GB storage, 25GB bandwidth/month)
3. Note your credentials from Dashboard:
   - Cloud Name
   - API Key
   - API Secret

### 2.2 Create Upload Folders
In Cloudinary Media Library, create these folders:
```
family-safety/
├── profiles/          (profile photos)
├── families/          (family photos)
├── screenshots/       (device screenshots)
├── recordings/        (voice recordings)
├── backups/           (device backups)
└── misc/              (other files)
```

### 2.3 Cloudinary Upload Presets
Go to Settings → Upload → Upload presets → Add new:
```
Preset Name: family_safety_upload
Folder: family-safety/misc
Unsigned: Yes (for mobile app)
```

### 2.4 Add to Android
```gradle
// build.gradle (app)
dependencies {
    implementation 'com.cloudinary:cloudinary-android:2.4.0'
}
```

### 2.5 Cloudinary Config (in app)
```kotlin
// CloudinaryConfig.kt
object CloudinaryConfig {
    const val CLOUD_NAME = "YOUR_CLOUD_NAME"
    const val API_KEY = "YOUR_API_KEY"
    // Don't store API Secret in app — use unsigned uploads only

    val instance = Cloudinary(mapOf(
        "cloud_name" to CLOUD_NAME,
        "api_key" to API_KEY
    ))
}
```

---

## STEP 3: Firebase Security Rules (Realtime Database)

Copy rules from `firebase_security_rules.txt` file in this project.

Go to Realtime Database → Rules → Paste the rules.

---

## STEP 4: Database Structure (Realtime DB)

The app auto-creates all nodes on first run. Here's the complete structure:

```
rtdb/
├── users/{userId}
│   ├── name: string
│   ├── email: string
│   ├── phone: string
│   ├── photoUrl: string (Cloudinary URL)
│   ├── role: "parent" | "child" | "admin"
│   ├── pin: string (hashed)
│   ├── isActive: boolean
│   ├── joinedFamilies: { familyId: true }
│   ├── lastLogin: timestamp
│   └── createdAt: timestamp
│
├── families/{familyId}
│   ├── name: string
│   ├── createdBy: userId
│   ├── members: { userId: true }
│   ├── inviteCode: string
│   ├── photoUrl: string (Cloudinary URL)
│   ├── createdAt: timestamp
│   └── settings: { ... }
│
├── devices/{deviceId}
│   ├── userId: string (owner)
│   ├── familyId: string
│   ├── deviceName: string
│   ├── deviceModel: string
│   ├── androidVersion: string
│   ├── fcmToken: string
│   ├── isOnline: boolean
│   ├── lastSeen: timestamp
│   ├── batteryLevel: number
│   ├── isCharging: boolean
│   ├── photoUrl: string (Cloudinary URL)
│   ├── registeredAt: timestamp
│   └── settings: { ... }
│
├── commands/{deviceId}/{commandId}
│   ├── fromUserId: string
│   ├── type: string
│   ├── payload: { ... }
│   ├── status: "pending" | "executing" | "completed" | "failed"
│   ├── createdAt: timestamp
│   └── executedAt: timestamp
│
├── locations/{deviceId}/{locationId}
│   ├── latitude: number
│   ├── longitude: number
│   ├── accuracy: number
│   ├── battery: number
│   └── timestamp: timestamp
│
├── geofences/{geofenceId}
│   ├── familyId: string
│   ├── name: string
│   ├── latitude: number
│   ├── longitude: number
│   ├── radius: number
│   ├── type: "home" | "school" | "custom"
│   ├── isActive: boolean
│   └── createdBy: userId
│
├── app_usage/{deviceId}/{date}/{packageName}
│   ├── appName: string
│   ├── usageTime: number (ms)
│   ├── lastUsed: timestamp
│   └── date: string (YYYY-MM-DD)
│
├── notifications/{userId}/{notifId}
│   ├── title: string
│   ├── message: string
│   ├── type: string
│   ├── data: { ... }
│   ├── isRead: boolean
│   └── createdAt: timestamp
│
├── automation_rules/{ruleId}
│   ├── familyId: string
│   ├── name: string
│   ├── trigger: { type, conditions }
│   ├── actions: [ { type, params } ]
│   ├── isActive: boolean
│   ├── createdBy: userId
│   └── createdAt: timestamp
│
├── activity_logs/{logId}
│   ├── userId: string
│   ├── deviceId: string
│   ├── action: string
│   ├── details: string
│   └── timestamp: timestamp
│
├── settings/{settingKey}
│   ├── key: string
│   ├── value: any
│   └── updatedAt: timestamp
│
├── admin/{userId}
│   ├── role: "super_admin" | "admin" | "staff"
│   ├── permissions: { permKey: true }
│   ├── isActive: boolean
│   └── createdAt: timestamp
│
├── system_config/{configKey}
│   ├── key: string
│   ├── value: any
│   └── updatedAt: timestamp
│
├── online_status/{deviceId}
│   ├── online: boolean
│   └── lastSeen: timestamp
│
├── live_location/{deviceId}
│   ├── lat: number
│   ├── lng: number
│   ├── accuracy: number
│   └── timestamp: timestamp
│
├── device_status/{deviceId}
│   ├── battery: number
│   ├── isCharging: boolean
│   ├── storage: { total, used, free }
│   ├── ram: { total, used, free }
│   ├── network: { type, connected }
│   └── updatedAt: timestamp
│
├── screen_state/{deviceId}
│   ├── isScreenOn: boolean
│   ├── screenTime: number
│   └── updatedAt: timestamp
│
├── active_sessions/{userId}
│   ├── deviceId: string
│   ├── loginTime: timestamp
│   └── ipAddress: string
│
├── presence/{userId}
│   ├── online: boolean
│   ├── lastSeen: timestamp
│   └── deviceIds: { deviceId: true }
│
├── media/{mediaId}
│   ├── userId: string
│   ├── type: "profile" | "family" | "screenshot" | "recording" | "backup"
│   ├── cloudinaryUrl: string
│   ├── cloudinaryPublicId: string
│   ├── fileName: string
│   ├── fileSize: number
│   ├── mimeType: string
│   ├── createdAt: timestamp
│   └── metadata: { ... }
│
├── families/{familyId}/children/{userId}
│   ├── name: string
│   ├── age: number
│   ├── deviceId: string
│   ├── restrictions: { ... }
│   ├── screenTimeLimit: number (minutes)
│   ├── bedtimeStart: string (HH:MM)
│   ├── bedtimeEnd: string (HH:MM)
│   └── studyModeEnabled: boolean
│
└── reports/{reportId}
    ├── userId: string
    ├── familyId: string
    ├── type: "daily" | "weekly" | "monthly"
    ├── data: { ... }
    ├── generatedAt: timestamp
    └── fileUrl: string (Cloudinary URL)
```

---

## STEP 5: Auto-Init on App Launch

The app auto-creates everything on first run. See `AutoInitializer.kt`.

### What gets auto-created:
1. **Default Admin Account**
   - Email: `rajahaider@gmail.com`
   - Password: `Admin@123`
   - Role: `super_admin`

2. **Default Settings** (22 settings)
   - Max devices, screen time limits, bedtime, etc.

3. **Admin Permissions**
   - Role-based permission structure

4. **Database Structure**
   - Root nodes initialized

### How to use:
```kotlin
// In your Application class or SplashActivity
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            AutoInitializer.initialize()
            // Continue to main activity
        }
    }
}
```

---

## STEP 6: Android Project Setup

### 6.1 build.gradle (Project - root)
```gradle
buildscript {
    ext.kotlin_version = '1.9.0'
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.1.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath 'com.google.gms:google-services:4.4.0'
        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.9.9'
    }
}
```

### 6.2 build.gradle (app)
```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.gms.google-services'
    id 'com.google.firebase.crashlytics'
}

dependencies {
    // Firebase (FREE services only)
    implementation platform('com.google.firebase:firebase-bom:32.3.1')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-database-ktx'
    implementation 'com.google.firebase:firebase-messaging-ktx'
    implementation 'com.google.firebase:firebase-crashlytics-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'

    // Cloudinary (for file storage)
    implementation 'com.cloudinary:cloudinary-android:2.4.0'

    // AndroidX
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.viewpager2:viewpager2:1.0.0'

    // Navigation
    implementation "androidx.navigation:navigation-fragment-ktx:2.7.5"
    implementation "androidx.navigation:navigation-ui-ktx:2.7.5"

    // ViewModel & LiveData
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.7.0"

    // Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3"

    // WorkManager
    implementation "androidx.work:work-runtime-ktx:2.9.0"

    // CameraX
    implementation "androidx.camera:camera-camera2:1.3.0"
    implementation "androidx.camera:camera-lifecycle:1.3.0"
    implementation "androidx.camera:camera-view:1.3.0"

    // Location
    implementation 'com.google.android.gms:play-services-location:21.0.1'
    implementation 'com.google.android.gms:play-services-maps:18.2.0'

    // Lottie
    implementation 'com.airbnb.android:lottie:6.1.0'

    // DataStore
    implementation "androidx.datastore:datastore-preferences:1.0.0"

    // Security
    implementation "androidx.security:security-crypto:1.1.0-alpha06"

    // Biometric
    implementation "androidx.biometric:biometric:1.1.0"

    // QR Code
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'

    // SwipeRefreshLayout
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
}
```

### 6.3 Minimum SDK
```
minSdk = 24 (Android 7.0)
targetSdk = 34
compileSdk = 34
```

---

## STEP 7: App Permissions (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FLASHLIGHT" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
<uses-permission android:name="android.permission.DEVICE_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />
```

---

## STEP 8: Folder Structure

```
app/
├── src/main/
│   ├── java/com/family/safety/platform/
│   │   ├── FamilySafetyApp.kt
│   │   ├── MainActivity.kt
│   │   │
│   │   ├── auth/
│   │   │   ├── LoginActivity.kt
│   │   │   ├── RegisterActivity.kt
│   │   │   ├── ForgotPasswordActivity.kt
│   │   │   ├── PinSetupActivity.kt
│   │   │   └── BiometricHelper.kt
│   │   │
│   │   ├── model/
│   │   │   ├── User.kt
│   │   │   ├── Family.kt
│   │   │   ├── Device.kt
│   │   │   ├── Command.kt
│   │   │   ├── LocationData.kt
│   │   │   ├── Geofence.kt
│   │   │   ├── AppUsage.kt
│   │   │   ├── Notification.kt
│   │   │   ├── AutomationRule.kt
│   │   │   ├── Media.kt
│   │   │   └── ActivityLog.kt
│   │   │
│   │   ├── viewmodel/
│   │   │   ├── AuthViewModel.kt
│   │   │   ├── DashboardViewModel.kt
│   │   │   ├── DeviceViewModel.kt
│   │   │   ├── LocationViewModel.kt
│   │   │   ├── AppManagementViewModel.kt
│   │   │   ├── AutomationViewModel.kt
│   │   │   ├── ReportsViewModel.kt
│   │   │   └── SettingsViewModel.kt
│   │   │
│   │   ├── repository/
│   │   │   ├── AuthRepository.kt
│   │   │   ├── UserRepository.kt
│   │   │   ├── FamilyRepository.kt
│   │   │   ├── DeviceRepository.kt
│   │   │   ├── LocationRepository.kt
│   │   │   ├── CommandRepository.kt
│   │   │   ├── MediaRepository.kt (Cloudinary)
│   │   │   └── SettingsRepository.kt
│   │   │
│   │   ├── service/
│   │   │   ├── LocationService.kt
│   │   │   ├── DeviceStatusService.kt
│   │   │   ├── CommandService.kt
│   │   │   ├── NotificationService.kt
│   │   │   ├── SyncService.kt
│   │   │   ├── ScreenMonitorService.kt
│   │   │   └── AppMonitorService.kt
│   │   │
│   │   ├── receiver/
│   │   │   ├── BootReceiver.kt
│   │   │   ├── ScreenReceiver.kt
│   │   │   ├── PackageReceiver.kt
│   │   │   └── NotificationReceiver.kt
│   │   │
│   │   ├── storage/
│   │   │   ├── CloudinaryHelper.kt
│   │   │   └── MediaUploader.kt
│   │   │
│   │   ├── ui/
│   │   │   ├── splash/
│   │   │   ├── onboarding/
│   │   │   ├── home/
│   │   │   ├── devices/
│   │   │   ├── location/
│   │   │   ├── apps/
│   │   │   ├── screen/
│   │   │   ├── reports/
│   │   │   ├── automation/
│   │   │   ├── settings/
│   │   │   ├── admin/
│   │   │   └── common/
│   │   │
│   │   ├── util/
│   │   │   ├── Constants.kt
│   │   │   ├── Extensions.kt
│   │   │   ├── Helpers.kt
│   │   │   ├── CryptoHelper.kt
│   │   │   ├── DeviceHelper.kt
│   │   │   ├── LocationHelper.kt
│   │   │   ├── NotificationHelper.kt
│   │   │   └── PreferenceManager.kt
│   │   │
│   │   └── db/
│   │       └── AutoInitializer.kt
│   │
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   ├── values/
│   │   ├── navigation/
│   │   ├── menu/
│   │   ├── raw/
│   │   └── xml/
│   │
│   └── AndroidManifest.xml
```

---

## STEP 9: Quick Start Commands

```bash
# Enter project
cd Projects/HAIDER\ FX\ BOT/

# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase (select RTDB only, no Firestore)
firebase init

# Deploy RTDB rules only
firebase deploy --only database:rules
```

---

## STEP 10: Default Credentials

| Account | Email | Password | Role |
|---------|-------|----------|------|
| Super Admin | rajahaider@gmail.com | Admin@123 | super_admin |
| Test Parent | parent@test.com | Test@123 | parent |
| Test Child | child@test.com | Test@123 | child |

---

## Notes
- All data stored in Realtime Database (FREE)
- Files/images stored in Cloudinary (FREE: 25GB storage)
- No paid Firebase services used
- Admin account auto-created on first launch
- Default settings auto-inserted if empty
- Security rules use role-based access control
- Location data cleaned up after 30 days
- Device offline triggers notification to parent