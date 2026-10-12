<!-- GoreeCloud Gallery: proposal-only feature scope. Implementation status must be verified against authoritative source and release evidence. -->

> **Status: Proposed capability catalog, not an implementation or release claim.** This document preserves the owner-submitted 18-section feature scope. Individual capabilities require design, implementation, testing and verification before they can be reported as implemented. The editable Word catalog is stored in [GoreeCloud Feature Roadmap](https://docs.google.com/document/d/1wXQzRwkEj4T_16sa-jlNXGVAfo5ww1C7/edit). This Markdown file is the repository-native reference, not the authority for live application status.

# GoreeCloud Gallery
## Features and Capabilities
**Product Type:** Photo and Video Gallery, Media Manager, Editor, and Secure Vault  
**Platform:** Android  
**Architecture:** Local-First and Offline-First  
**Feature Scope:** Fully Offline Capabilities  
**Specification Status:** Proposed

---

## 1. Photo and Video Library Management

GoreeCloud Gallery provides a unified local media library for browsing, viewing, managing, and organizing photos and videos stored on an Android device.

### Media Discovery and Management
- Automatically discover locally stored photographs and videos.
- Scan internal device storage for supported media files.
- Detect media stored on removable SD cards.
- Access supported external USB storage devices.
- Scan user-selected directories.
- Automatically detect newly created, downloaded, or modified media.
- Refresh and rescan media directories.
- Maintain a local media catalog.
- Generate and cache photo thumbnails.
- Generate and cache video thumbnails.
- Browse large photo and video collections efficiently.
- Display photos and videos together or separately.
- Browse media by its original filesystem location.
- Browse all media chronologically.
- Group media by day, month, and year.
- Navigate media collections using calendar-based views.
- Display full-resolution images.
- View high-resolution and extremely large photographs.
- Open media directly from other installed applications.
- Operate without requiring remote storage or an internet connection.

### Library Views
- All Photos.
- All Videos.
- Recent Media.
- Albums.
- Folders.
- Favorites.
- Stories.
- Tags.
- Hidden Media.
- Private Vault.
- Recycle Bin.

---

## 2. Albums, Collections, and Media Organization

GoreeCloud Gallery provides comprehensive organization tools for photos, videos, albums, folders, and collections.

### Album Management
- Create photo albums.
- Create video albums.
- Create mixed-media albums.
- Rename albums.
- Delete albums.
- Rearrange albums.
- Sort albums alphabetically.
- Sort albums by date.
- Sort albums by item count.
- Set custom album cover images.
- Move photos and videos between albums.
- Copy photos and videos into other albums.
- Merge supported albums.
- Group albums into categories.
- Create nested album structures.
- Navigate hierarchical album groups.
- Hide selected albums.
- Show previously hidden albums.
- Exclude selected folders from ordinary gallery browsing.

### Collections and Grouping
- Create collections of related media.
- Organize photos using custom tags.
- Organize tags into tag groups.
- Rename and manage tags.
- View all items associated with a tag.
- Automatically group supported burst photography sequences.
- Group similar-looking images using local processing.
- Stack related photographs.
- Expand grouped images.
- Ungroup photographs.
- Organize supported people-based collections using on-device processing.
- Create collections using local metadata filters.
- Maintain collections without physically moving the original media files.

### Favorites
- Mark individual photos as favorites.
- Mark videos as favorites.
- Remove favorite status.
- Add or remove favorites in bulk.
- Browse all favorites in a dedicated collection.
- Sort and filter favorite items.

---

## 3. Gallery Layouts, Navigation, and Viewing

GoreeCloud Gallery offers multiple layouts and navigation methods for browsing media collections.

### Gallery Layouts
- Standard thumbnail grid.
- Adaptive thumbnail grid.
- Mosaic layout.
- List layout.
- Calendar layout.
- Timeline layout.
- Album grid.
- Album list.
- Full-screen image viewer.
- Full-screen video viewer.
- Immersive edge-to-edge media viewing.

### Navigation and Gestures
- Swipe between photos.
- Swipe between videos.
- Swipe between albums.
- Pinch to zoom photographs.
- Double-tap to zoom.
- Pan and navigate zoomed images.
- Pinch to adjust gallery thumbnail sizes.
- Change gallery column counts.
- Long-press to select media.
- Drag across multiple items for selection where supported.
- Quickly scroll through large collections.
- Jump between chronological sections.
- Navigate through breadcrumbs and collection paths.
- Navigate between related albums and tags.
- Show or hide viewing controls.
- Support portrait and landscape orientation.
- Automatically adapt media orientation.
- Provide customizable navigation tabs.
- Provide customizable bottom navigation.
- Offer side navigation menus.
- Support Android back-navigation gestures.

### Slideshows
- Start slideshows from albums.
- Start slideshows from selected media.
- Display slideshows in full-screen mode.
- Automatically advance photographs.
- Configure slideshow intervals.
- Use supported slideshow transitions.
- Manually navigate forward or backward.
- Pause and resume slideshows.
- Play supported animated media automatically.

---

## 4. Image Format Support

GoreeCloud Gallery is designed to support common, modern, professional, animated, and specialized image formats.

### Standard Image Formats
- JPEG / JPG.
- PNG.
- BMP.
- WebP.
- GIF.

### Modern Image Formats
- AVIF.
- HEIF.
- HEIC.
- JPEG XL.
- Animated WebP.

### Professional and Specialized Formats
- DNG.
- Supported camera RAW formats.
- TIFF.
- Multi-page TIFF.
- SVG.
- GeoTIFF.
- Panoramic images.
- 360-degree photographs.
- Spherical panoramas.
- Supported motion photos.
- High-resolution photographs.

### Image Viewing Capabilities
- Decode supported formats locally.
- Preserve original aspect ratios.
- Display high-resolution images.
- Zoom into large photographs.
- Navigate supported multi-page images.
- Render supported animated images.
- Display panoramic photographs.
- Interactively navigate supported 360-degree images.
- Display available embedded previews.
- Read supported embedded metadata.
- Preserve original image data when viewing.
- Export images using available local encoders.

Format support depends on installed decoders, the Android version, and device capabilities.

---

## 5. Video Playback and Viewing

GoreeCloud Gallery includes an integrated video player for viewing locally stored videos without requiring an external application.

### Video Format Support
- MP4.
- MKV.
- AVI.
- MOV.
- WebM.
- 3GP.
- Other supported video formats and containers.

### Video Playback Features
- Full-screen video playback.
- High-definition video playback.
- Hardware-accelerated decoding where supported.
- Software-decoding fallback for compatible formats.
- Play and pause.
- Seek forward and backward.
- Timeline scrubbing.
- Playback progress indicators.
- Volume controls.
- Mute and unmute.
- Playback speed controls.
- Replay videos.
- Display supported subtitles.
- Recognize supported slow-motion videos.
- Adjust supported slow-motion playback ranges.
- Play supported 360-degree videos.
- View video thumbnails.
- Browse videos by album.
- Browse videos in a dedicated video library.
- Display video duration and resolution.
- Inspect available technical metadata.
- Open supported videos from other installed applications.

---

## 6. Search, Sorting, and Advanced Filtering

GoreeCloud Gallery provides advanced local search and filtering for finding specific media without online indexing.

### Search
- Search by filename.
- Search by album name.
- Search by folder name.
- Search by custom tag.
- Search by description or caption.
- Search by capture date.
- Search by creation date.
- Search by modification date.
- Search by year, month, or day.
- Search recently added media.
- Search by file type.
- Search by file extension.
- Search by image or video category.
- Search using supported metadata fields.
- Search using existing GPS coordinates.
- Search by rating.
- Search within individual albums.
- Search within collections.
- Search using supported regular expressions.
- Integrate with Android device-level search where available.

### Sorting
- Filename.
- Date taken.
- Creation date.
- Modification date.
- File size.
- Media duration.
- Image dimensions.
- Alphabetical order.
- Natural filename order.
- Ascending order.
- Descending order.
- Album-specific sorting.

### Advanced Filtering
- Photos only.
- Videos only.
- Specific albums.
- Specific folders.
- Favorites.
- Tags and tag groups.
- File formats.
- Aspect ratio.
- Image orientation.
- Date ranges.
- Metadata availability.
- Existing GPS coordinates.
- Image ratings.
- Recently added media.
- Burst photographs.
- Motion photographs.
- Panoramic images.
- Slow-motion videos.
- Multiple combined filtering conditions.
- Saved and persistent local filters where supported.

---

## 7. File and Folder Management

GoreeCloud Gallery includes integrated file-management capabilities for performing local media operations.

### File Operations
- Create folders.
- Rename folders.
- Rename photos.
- Rename videos.
- Move individual files.
- Move multiple files.
- Copy individual files.
- Copy multiple files.
- Duplicate files.
- Delete files.
- Delete folders where permitted.
- Select multiple files.
- Select all files.
- Perform bulk move operations.
- Perform bulk copy operations.
- Perform bulk deletion operations.
- Organize files across internal storage and SD cards.
- Copy files to supported attached USB storage devices.
- Preserve filenames when copying or moving files.
- View the original filesystem path.
- Open the containing folder.
- Exclude directories from media scanning.
- Display supported hidden folders.
- Respect Android filesystem and media permissions.
- Open files in other installed local applications.
- Save modified files as new copies.
- Replace original files when explicitly selected.
- Restore supported non-destructive edits.

### Recycle Bin
- Built-in recycle bin.
- Move deleted photos into the recycle bin.
- Move deleted videos into the recycle bin.
- Browse deleted media.
- Restore individual deleted files.
- Restore multiple deleted files.
- Restore media to its original directory where possible.
- Permanently delete selected files.
- Empty the recycle bin.
- Manage deleted media separately from the main library.

---

## 8. EXIF, IPTC, XMP, and Metadata Management

GoreeCloud Gallery provides detailed media metadata inspection and editing tools.

### Metadata Viewing
- Filename and extension.
- File size.
- File path.
- Creation date.
- Modification date.
- Capture date and time.
- Image resolution.
- Width and height.
- Aspect ratio.
- Image orientation.
- Color and encoding information.
- Camera manufacturer.
- Camera model.
- Lens model.
- Focal length.
- Exposure time.
- Aperture.
- ISO sensitivity.
- Flash information.
- Exposure settings.
- GPS latitude and longitude.
- Altitude where recorded.
- Image descriptions.
- Copyright information.
- Attribution information.
- Image ratings.
- Keywords and tags.
- EXIF metadata.
- IPTC metadata.
- XMP metadata.
- Video metadata.
- Supported RAW metadata.
- Supported TIFF and GeoTIFF metadata.

### Metadata Editing
- Edit capture dates.
- Edit capture times.
- Correct incorrect timestamps.
- Update supported timestamps in bulk.
- Edit GPS coordinates.
- Remove GPS coordinates.
- Add and edit keywords.
- Add and edit tags.
- Edit image descriptions.
- Modify image ratings.
- Edit supported orientation metadata.
- Edit supported EXIF fields.
- Edit supported IPTC fields.
- Edit supported XMP fields.
- Derive dates from existing metadata fields.
- Remove selected sensitive metadata.
- Strip EXIF information from exported copies.
- Preserve selected metadata during export.
- Refresh the media library after metadata changes.

### Offline Location Metadata
- View embedded geographical coordinates.
- Identify media containing GPS information.
- Filter media by coordinates.
- Group media using existing geographical metadata.
- Inspect supported GeoTIFF geographical information.
- Remove geographical metadata for privacy.
- Preserve existing coordinates when requested.

All geographical operations remain offline and do not require online map services.

---

## 9. Photo Editing and Image Enhancement

GoreeCloud Gallery includes an integrated editor for basic and advanced local image modifications.

### Basic Photo Editing
- Crop.
- Freeform crop.
- Preset aspect-ratio crop.
- Rotate left.
- Rotate right.
- Custom rotation.
- Horizontal flip.
- Vertical flip.
- Mirror.
- Straighten.
- Perspective correction.
- Resize.
- Change image dimensions.
- Apply filters.
- Adjust filter intensity.
- Create supported custom filters.
- Undo.
- Redo.
- Save edited copies.
- Revert supported edits.

### Lighting and Color
- Brightness.
- Contrast.
- Exposure.
- Saturation.
- Highlights.
- Shadows.
- Color temperature.
- Warmth.
- Color tone.
- Selective color.
- Spot color.
- Black-and-white conversion.
- Supported color grading.
- Automatic locally processed adjustments.

### Drawing and Decorations
- Freehand drawing.
- Multiple drawing colors.
- Adjustable drawing tools.
- Straight-line drawing.
- Geometric shapes.
- Supported automatic shape correction.
- Add text.
- Adjust text styles.
- Add text backgrounds.
- Add stickers.
- Create custom stickers from photographs.
- Move and resize stickers.
- Rotate stickers.
- Freeform selection.
- Lasso selection.
- Shape-based selection.
- Copy selected image regions.
- Extract selected image regions.
- Save supported subject cutouts.
- Add decorative photo elements.
- Re-edit supported decorations.

### Advanced Local Enhancements
- Automatic photo remastering.
- Automatic brightness and detail enhancement.
- Local object removal.
- Shadow removal.
- Reflection removal.
- Subject extraction.
- Background removal.
- Portrait background-blur adjustments.
- Before-and-after comparisons.
- High-dynamic-range display on supported devices.

Advanced processing must operate entirely on-device.

---

## 10. Video Editing and Audio Enhancement

GoreeCloud Gallery includes local video-editing tools for modifying video clips.

### Video Editing
- Trim video beginnings.
- Trim video endings.
- Select video segments.
- Crop supported videos.
- Rotate videos.
- Correct video orientation.
- Apply video filters.
- Adjust supported colors and tones.
- Adjust brightness and contrast.
- Change playback speed.
- Create slow-motion sections.
- Edit supported slow-motion ranges.
- Combine multiple clips.
- Rearrange clips.
- Add supported transitions.
- Add text overlays.
- Add stickers.
- Add decorative elements.
- Draw on supported video content.
- Animate supported decorations.
- Undo and redo operations.
- Preview modifications.
- Export edited videos.
- Save edited copies.

### Audio Controls
- Adjust video volume.
- Mute audio.
- Unmute audio.
- Add locally stored music.
- Add locally stored soundtracks.
- Adjust supported audio tracks.
- Preview modified audio.
- Save videos with edited audio.

### Optional On-Device Audio Enhancement
- Detect sound categories.
- Reduce background noise.
- Reduce wind noise.
- Reduce crowd noise.
- Emphasize speech.
- Adjust supported music and voice levels.
- Compare enhanced audio with the original.
- Save enhanced audio within edited video files.

---

## 11. Stories, Memories, Slideshows, Collages, and GIFs

GoreeCloud Gallery includes creative features for presenting and combining photographs and videos.

### Stories and Memories
- Create stories manually.
- Add photographs to stories.
- Add videos to stories.
- Name stories.
- Edit story content.
- Remove media from stories.
- Rearrange story content.
- Generate supported local story suggestions.
- Create chronological memory collections.
- Revisit past photo collections.
- Generate local highlight videos where supported.
- Manage automatically generated stories.

### Collages
- Create photo collages.
- Select multiple images.
- Choose collage templates.
- Create freeform collages.
- Rearrange collage images.
- Resize individual images.
- Rotate images.
- Reposition images.
- Replace images.
- Adjust supported collage layouts.
- Save completed collages.
- Re-edit supported collage projects.

### GIFs and Animated Media
- View animated GIFs.
- Play supported animated WebP images.
- Edit supported GIFs.
- Trim GIF animations.
- Adjust GIF dimensions.
- Change GIF aspect ratios.
- Add decorations to GIFs.
- Create GIFs from supported local media.
- Export supported animated images.

### Motion Photos
- View supported motion photos.
- Play motion-photo animations.
- Extract still images.
- Convert motion photos to still photographs.
- Select individual frames.
- Use supported on-device best-frame selection.

---

## 12. Private Gallery, Encryption, and Security

GoreeCloud Gallery incorporates secure local media storage and privacy controls.

### Private Vault
- Private photo vault.
- Private video vault.
- Encrypted media storage.
- AES-based encryption for protected files.
- Separate protected and ordinary media libraries.
- Import media into the private vault.
- Remove media from the private vault.
- View encrypted photographs after unlocking.
- Play encrypted videos after unlocking.
- Prevent protected media from appearing in ordinary gallery views.

### Authentication and Locking
- PIN authentication.
- Pattern authentication.
- Fingerprint authentication.
- Supported biometric authentication.
- Gallery application lock.
- Private-vault lock.
- Protected album access.
- Calculator-style vault lock interface.
- Randomized PIN-entry mode where supported.
- Offline authentication and vault access.

### Media Privacy
- Hide individual media.
- Hide selected albums.
- Exclude private directories.
- Remove embedded location metadata.
- Remove selected EXIF information.
- Inspect metadata before exporting.
- Preserve user control of original files.
- Keep ordinary media management local.
- Avoid mandatory account registration.
- Avoid mandatory remote authentication.
- Avoid mandatory uploads.
- Avoid internet-dependent processing.
- Avoid unnecessary tracking dependencies in the offline implementation.

**Security requirement:** Hiding a file, locking the interface, and encrypting the file are distinct operations. The private vault must use actual encryption rather than relying solely on hidden directories.

---

## 13. Appearance, Themes, and Customization

GoreeCloud Gallery provides extensive interface customization.

### Appearance
- Light theme.
- Dark theme.
- Automatic system-theme matching.
- Material Design interface.
- Material You dynamic colors.
- Custom accent colors.
- Custom themes.
- Adjustable interface appearance.
- Theme-aware navigation.
- Edge-to-edge layouts.
- Customizable viewing preferences.

### Gallery Customization
- Adjustable thumbnails.
- Configurable grid columns.
- Grid layouts.
- List layouts.
- Mosaic layouts.
- Calendar layouts.
- Custom album covers.
- Custom album order.
- Hidden-album preferences.
- Customizable navigation tabs.
- Customizable bottom navigation.
- Configurable viewer controls.
- Gesture preferences.
- Slideshow interval settings.
- Animated-media playback settings.
- Full-screen scrolling preferences.
- Video thumbnail settings.
- Search preferences.
- Filtering preferences.
- Date and time display preferences.
- Application shortcuts.

### Accessibility and Localization
- Support Android font scaling.
- Support portrait layouts.
- Support landscape layouts.
- Adapt to tablet screen sizes.
- Respect supported reduced-animation preferences.
- Support standard Android accessibility controls.
- Support Android back gestures.
- Support multiple interface languages.
- Support alternative calendar systems where implemented.

---

## 14. Android and Device Integration

GoreeCloud Gallery integrates with local Android system capabilities.

### Android Integration
- Open media from Android file pickers.
- Act as an image viewer for installed applications.
- Support Android Photo Picker integration.
- Integrate with Android media storage.
- Respect media-access permissions.
- Provide home-screen photo widgets.
- Display selected photographs in widgets.
- Display supported album or story content in widgets.
- Provide application shortcuts.
- Integrate with supported device-level search.
- Set images as wallpaper.
- Copy supported media to the clipboard.
- Open media in installed image editors.
- Open videos in installed video players.
- Use removable SD cards.
- Access supported USB storage devices.
- Support offline local media transfer methods.
- Support Android biometric authentication.
- Support Android portrait and landscape orientations.
- Support compatible Android TV viewing environments.
- Provide supported photo screensaver functionality.
- Support compatible digital photo-frame presentations.

---

## 15. On-Device Intelligence and Smart Organization

GoreeCloud Gallery supports optional local media intelligence without internet-based processing.

### Smart Organization
- Identify visually similar photographs.
- Group related photographs.
- Recognize supported burst sequences.
- Recognize motion photos.
- Detect supported panoramas.
- Identify photo spheres.
- Recognize supported 360-degree media.
- Detect slow-motion videos.
- Categorize media using existing metadata.
- Maintain supported people-based collections through local processing.
- Organize related media using local analysis.
- Generate locally processed story suggestions.

### Smart Photo Enhancements
- Automatic image adjustments.
- Automatic photo remastering.
- Local object removal.
- Subject extraction.
- Supported portrait enhancements.
- Best-frame selection.
- On-device media categorization.
- On-device visual subject recognition.
- Local natural-language media search where supported.
- Locally generated photo highlights.

All optional intelligence features must use supported on-device models and processing components, without transmitting media or prompts to remote AI services.

---

## 16. Performance, Reliability, and Storage Efficiency

GoreeCloud Gallery is designed for efficient local media management.

### Performance
- Fast thumbnail generation.
- Efficient image decoding.
- Lazy loading.
- Incremental media scanning.
- Thumbnail caching.
- Local metadata indexing.
- Smooth scrolling through large collections.
- Efficient high-resolution image viewing.
- Hardware-accelerated video playback where available.
- Software-decoding fallback.
- Configurable video thumbnail generation.
- Responsive media selection.
- Efficient bulk file operations.
- Minimized unnecessary filesystem operations.
- Avoid unnecessary network dependencies.

### Local Storage Management
- Browse existing device media without mandatory duplication.
- Preserve original media paths where possible.
- Support internal storage.
- Support SD cards.
- Support attached USB storage.
- Manage locally generated thumbnails.
- Manage local metadata indexes.
- Preserve media during supported organizational operations.
- Save edited copies when requested.
- Preserve unmodified originals.
- Keep media accessible through ordinary filesystem tools where appropriate.

### Reliability and Recovery
- Recover supported deleted media from the recycle bin.
- Restore supported non-destructive edits.
- Report file-operation failures.
- Handle insufficient filesystem permissions.
- Handle unreadable files gracefully.
- Handle unsupported media formats gracefully.
- Refresh media indexes after file modifications.
- Detect changed or deleted media.
- Maintain access when internet connectivity is unavailable.
- Avoid remote dependencies for essential media operations.

---

## 17. Local Import, Export, and Settings Portability

GoreeCloud Gallery supports local media portability and settings management.

### Media Import and Export
- Import existing local media.
- Scan existing photo and video directories.
- Import media from supported removable storage.
- Export edited photographs.
- Export resized images.
- Export supported image formats.
- Export edited videos.
- Preserve original files.
- Export media with selected metadata.
- Export media with sensitive metadata removed.
- Copy original files to local storage destinations.
- Maintain supported media file compatibility outside the application.

### Settings and Configuration
- Export supported gallery settings.
- Import previously exported settings.
- Preserve supported appearance preferences.
- Preserve supported navigation preferences.
- Preserve supported album-cover configuration.
- Preserve supported favorites and organization data where export formats allow.
- Restore compatible settings locally.

### Private Media Portability
- Maintain locally recoverable encrypted vault data.
- Support authorized local vault backup and restoration.
- Require appropriate vault credentials or recovery material for protected content.
- Preserve encryption during supported vault transfers.

---

## 18. Offline Operation and Privacy Boundaries

GoreeCloud Gallery is designed to provide its primary functionality without internet access.

### Offline Capabilities
- Offline photo browsing.
- Offline video playback.
- Offline album management.
- Offline file operations.
- Offline searching.
- Offline sorting.
- Offline filtering.
- Offline metadata management.
- Offline photo editing.
- Offline video editing.
- Offline collage creation.
- Offline GIF viewing and editing.
- Offline slideshows.
- Offline stories and memories.
- Offline encrypted vault access.
- Offline theme and preference management.
- Offline local import and export.
- Offline local indexing.
- Offline on-device intelligence where supported.

### Excluded Online Capabilities
- Cloud photo storage.
- Cloud backup.
- Cloud synchronization.
- Remote media libraries.
- Online shared albums.
- Collaborative cloud collections.
- Internet-based account authentication.
- Remote file servers.
- Network-drive browsing.
- Online map services.
- Online reverse geocoding.
- Cloud-based generative editing.
- Internet-dependent AI enhancement.
- Online media streaming.
- Social-media integrations.
- Remote photo search.
- Internet-based media sharing.

---

## Overall Product Capabilities

GoreeCloud Gallery is designed to unify the following functionality into a single Android application:

1. Complete local photo and video library management.
2. Advanced albums, folders, and collections.
3. Flexible gallery layouts and navigation.
4. Broad image and video format compatibility.
5. Integrated image and video viewing.
6. Advanced local searching, sorting, and filtering.
7. Integrated media file management.
8. Detailed EXIF, IPTC, XMP, and metadata tools.
9. Comprehensive photo editing.
10. Integrated video editing and audio controls.
11. Stories, memories, slideshows, collages, and GIF creation.
12. Encrypted private photo and video storage.
13. Extensive interface customization.
14. Android system and device integration.
15. Optional fully on-device media intelligence.
16. Efficient performance and local storage management.
17. Local media import, export, and configuration portability.
18. Fully offline operation with privacy-focused data handling.

**Product Vision:** GoreeCloud Gallery is intended to be a comprehensive, customizable, privacy-focused, offline-first Android gallery that combines photo management, video playback, editing, organization, metadata inspection, and secure media storage into one unified application.

**Implementation note:** This document defines the proposed capabilities of GoreeCloud Gallery. Individual features require design, implementation, testing, and verification before being classified as implemented or released.
