# Password Generator

A lightweight Android application for generating secure, customizable passwords with real-time strength evaluation and file export support.

## Features

- **Customizable character sets** — choose any combination of:
  - Uppercase letters (A–Z)
  - Lowercase letters (a–z)
  - Digits (0–9)
  - Symbols (`!@#$%^&*()-_=+[]{}|;:,.<>?`)
- **Adjustable length** — slider from 1 to 128 characters (default: 12)
- **Password strength indicator** — color-coded label (Weak / Medium / Strong / Very Strong) updated in real time
- **Copy to clipboard** — one tap to copy the generated password
- **Regenerate** — instant new password with current settings
- **Save to file** — exports the password as a `.txt` file to the Downloads folder via MediaStore (no storage permissions required)
- **Dark mode** support (follows system theme)
- **Polish language** localization

## Screenshots

> _Add screenshots here_

## Requirements

| Requirement | Value |
|---|---|
| Minimum Android version | Android 10 (API 29) |
| Target Android version | Android 16 (API 36) |

## Tech Stack

- **Language**: Kotlin
- **UI**: Material Design 3 (Material3 components, NestedScrollView, SeekBar)
- **Randomness**: `java.security.SecureRandom` (cryptographically secure)
- **File storage**: MediaStore API
- **Build system**: Gradle (Kotlin DSL)

## Getting Started

### Clone the repository

```bash
git clone https://github.com/Matiks112/Password-Generator.git
cd Password-Generator
```

### Build and run

1. Open the project in **Android Studio** (Arctic Fox or later recommended).
2. Let Gradle sync finish automatically.
3. Select an emulator or connect a physical device running Android 10+.
4. Click **Run ▶** or run from the terminal:

```bash
./gradlew installDebug
```

### Build a release APK

```bash
./gradlew assembleRelease
```

The APK will be output to `app/build/outputs/apk/release/`.

## Usage

1. Launch the app.
2. A 12-character password is generated automatically using all character types.
3. Adjust the **length slider** or toggle character type **checkboxes** to regenerate with new settings.
4. Tap the **copy icon** to copy the password to the clipboard.
5. Tap the **refresh icon** to generate a new password with the current settings.
6. Tap **Save to File**, enter a filename, and the password is saved as `<filename>.txt` in your Downloads folder.

## Password Strength Calculation

Strength is scored based on length and character variety:

| Criterion | Points |
|---|---|
| Length ≥ 8 | +1 |
| Length ≥ 12 | +1 |
| Length ≥ 20 | +1 |
| Each character type selected | +1 (max +4) |

| Score | Label | Color |
|---|---|---|
| ≤ 3 or length < 6 | Weak | Red |
| 4–5 | Medium | Orange |
| 6 | Strong | Green |
| 7+ | Very Strong | Dark Green |

## License

This project is open source. See the repository for details.
