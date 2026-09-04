# BodhiScan Android TV App

A 10-foot Android TV application for **BodhiScan Digitization Services**, ported 1:1 from the original Roku SceneGraph application into Kotlin and Jetpack Compose.

## Features

- **Leanback & 10-Foot TV Experience**: Built specifically for Android TV remote control navigation with seamless D-Pad focus handling, smooth focus animations, and hardware remote numeric key input.
- **Dynamic Server Announcements**: Fetches live server announcements (`https://streaming.bodhiscan.com/api/config.php`) with offline fallback defaults.
- **Secure 6-Digit PIN Authentication**: Direct keypad or remote-driven 6-digit access code validation with BodhiScan servers (`https://streaming.bodhiscan.com/api/auth.php`).
- **Cassette Tape Collection View**:
  - Replicates the custom cassette tape card design (`TapeGridItem`), featuring decorative cassette reels, top cyan accent line, "TAPE" label, giant tape number extracted from title, and subtitle.
  - Focused detail banner at the top displaying title and description of currently highlighted tape.
- **High-Definition Video Playback**: Fullscreen video player with D-Pad controls (Play/Pause, Rewind 10s, Forward 10s, scrub/progress bar, timestamps), error recovery, and automatic return to the tape grid.
- **Sentry Error Logging**: Equivalent Sentry telemetry reporting ported directly to capture API/parser issues.
- **Bodhi Industries Branding**: Preserves original branding footer logos and signature cyan (`#00A8FF`) on dark obsidian (`#0F1115`) visual identity.
