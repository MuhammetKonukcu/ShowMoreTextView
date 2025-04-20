# ShowMoreTextView

A customizable Android TextView that automatically collapses long text content with a "Show More" indicator, allowing users to expand text with a simple click.

https://github.com/user-attachments/assets/99e7be11-d723-44e1-8163-b285369a43df

## Features

- Two trim modes: by number of lines or by text length
- Customizable "Show More" indicator text
- Configurable text colors for collapsed and expanded states
- Simple click-to-expand/collapse functionality
- Seamless integration with existing layouts

## Installation

### Add to your layout

```xml
<com.muhammetkonukcu.examples.views.ShowMoreTextView
    android:id="@+id/description_tv"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:trimLines="2"
    app:trimCollapsedText="@string/show_more_with_dots"
    app:colorClickableText="@color/blue_300"
    app:trimMode="trimModeLine"/>
```

## Usage

### Basic Usage

Simply add the `ShowMoreTextView` to your layout and set your text programmatically:

```kotlin
binding.showMoreTextView.text = "Your long text content goes here..."
```

### XML Attributes

| Attribute | Description | Default |
| --- | --- | --- |
| `app:trimCollapsedText` | The text displayed as the expand indicator | "... Show More" |
| `app:trimLength` | Maximum number of characters when using length trim mode | 240 |
| `app:colorClickableText` | Color of the clickable "Show More" text | blue_300 |
| `app:trimLines` | Number of lines to show when collapsed | 2 |
| `app:trimMode` | Trim mode: either by lines or length | trimModeLine (0) |

### Trim Modes

- `trimModeLine (0)`: Collapses text to show only the specified number of lines
- `trimModeLength (1)`: Trims text to a specific character length

### Programmatic Configuration

You can customize the component programmatically:

```kotlin
binding.showMoreTextView.apply {
    text = "Your long text content goes here..."
    setTrimMode(ShowMoreTextView.TRIM_MODE_LINES)
    setTrimLines(3)                            // Show 3 lines when collapsed
    setTrimLength(300)                         // When using length mode
    setCollapsedTextColor(R.color.grey_500)    // Color for collapsed state
    setExpandedTextColor(R.color.grey_700)     // Color for expanded state
    setExpandIndicator("... See More")         // Custom indicator text
}
```

## Customization Constants

```kotlin
companion object {
    // Available as static constants for programmatic configuration
    private const val TRIM_MODE_LINES = 0
    private const val TRIM_MODE_LENGTH = 1
    private const val DEFAULT_TRIM_LENGTH = 240
    private const val DEFAULT_TRIM_LINES = 2
}
```

## Contributing

Contributions are welcome! Feel free to submit a Pull Request.
