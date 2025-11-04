# Photo Gallery Application

A desktop photo viewer and annotation application built with Java Swing and JavaSwing MVC architecture. This application allows users to import photos, view them, and add annotations including freehand drawings and text.

## Features

- **Photo Import**: Load JPG, JPEG, and PNG image files
- **Photo Viewer**: View photos with automatic scaling to fit the window
- **Flip View**: Double-click to flip photos and view/add annotations on the back
- **Freehand Drawing**: Draw on the back of photos with mouse drag
- **Text Annotations**: Click to add text blocks on the back of photos
- **Color Selection**: Choose custom colors for annotations via the menu bar
- **Multi-Selection**: Select multiple annotations using Shift+Click
- **Drag and Move**: Move selected annotations by dragging
- **Color Change**: Change the color of selected annotations
- **Delete Photos**: Remove photos and all associated annotations
- **Status Bar**: Real-time feedback on application actions

## Requirements

- Java Development Kit (JDK) 25 or higher
- Java Swing (included in JDK)

## Project Structure

```
gallery/
├── src/
│   ├── Main.java                    # Application entry point
│   ├── GalleryWindow.java           # Main window and UI components
│   ├── PhotoComponent.java          # Photo display component (Controller)
│   ├── PhotoComponentModel.java     # Data model for photos and annotations
│   ├── PhotoComponentView.java      # Rendering logic (View)
│   ├── Drawable.java                # Interface for drawable objects
│   ├── Stroke.java                  # Freehand drawing strokes
│   └── TextBlock.java               # Text annotation blocks
├── .idea/                           # IntelliJ IDEA project files
├── .gitignore                       # Git ignore rules
├── gallery.iml                      # IntelliJ module file
└── README.md                        # This file
```

## Building and Running

### Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Ensure JDK 25 is configured as the project SDK
3. Run the `Main` class

### Using Command Line

```bash
# Compile
javac -d out src/*.java

# Run
java -cp out Main
```

## Usage

### Importing a Photo

1. Click **File → Import** in the menu bar
2. Select a JPG, JPEG, or PNG file
3. The photo will be displayed in the center panel

### Viewing Annotations

- Double-click on a photo to flip it and view the annotation side
- Double-click again to flip back to the photo

### Drawing

1. Flip the photo to the annotation side
2. Click and drag to draw freehand strokes
3. Release the mouse to complete a stroke

### Adding Text

1. Flip the photo to the annotation side
2. Single-click anywhere to create a text block
3. Type text directly (text wraps at photo edge)
4. Click elsewhere to create a new text block

### Selecting Annotations

- Click on a stroke or text block to select it
- Hold **Shift** and click to select multiple annotations
- Selected annotations can be dragged to move them

### Changing Colors

1. Click the color chooser button in the menu bar
2. Select a color from the dialog
3. New annotations will use this color
4. Selected annotations will change to the new color

### Deleting Photos

1. Click **File → Delete**
2. Confirm the deletion
3. The photo and all annotations will be removed

## Keyboard Shortcuts

- **Type** (when text block is active): Add characters to text block
- **Shift + Click**: Add to selection or remove from selection

## Mouse Controls

- **Single Click**: Create new text block or select annotation
- **Double Click**: Flip photo to annotation side and back
- **Click + Drag**: Draw freehand stroke or move selected annotations
- **Shift + Click**: Multi-select annotations

## Architecture

The application follows the Model-View-Controller (MVC) pattern:

- **Model** (`PhotoComponentModel`): Manages photo data, annotations, and selection state
- **View** (`PhotoComponentView`): Handles rendering of photos and annotations
- **Controller** (`PhotoComponent`): Manages user input and coordinates between model and view

## Known Limitations

- Only one photo can be loaded at a time
- Annotations are not persisted to disk
- Text blocks wrap at the photo edge with a maximum height limit
- Undo/redo functionality is not implemented

## License
This is an educational project.

## Author
Soheil Lotfi
Created as a Java Swing learning project.
