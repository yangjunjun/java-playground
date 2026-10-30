# Date and Time (java.time)

Java 8 introduced the brand-new `java.time` API (JSR-310), which fully supersedes the old `java.util.Date` / `Calendar`. The new API is immutable, thread-safe, and semantically clear (dates, times, and time-zone–aware moments are modeled separately), making it the only recommended choice for date and time programming in modern Java. This module covers the commonly used capabilities from Java 8 through Java 25.

## 1. LocalDate / LocalTime / LocalDateTime (Java 8+)

None of the three carries a time zone; they represent a "date", a "time", and a "date + time" respectively, and all are **immutable**:

```java
LocalDate today = LocalDate.now();
LocalDate birthday = LocalDate.of(2000, 5, 20);
LocalTime time = LocalTime.of(8, 30);
LocalDateTime dt = LocalDateTime.of(2024, 1, 1, 12, 0);

// plus/minus add and subtract, with replaces a field; all return new objects
LocalDate nextWeek = today.plusWeeks(1);
LocalDate lastYear = today.minusYears(1);
LocalDate firstOfMonth = today.withDayOfMonth(1);
```

Common accessors: `getYear()`, `getMonthValue()`, `getDayOfMonth()`, `getDayOfWeek()`, `getDayOfYear()`.

## 2. Instant and the Clock Abstraction (Java 8+)

`Instant` is a point on the timeline (a UTC timestamp), suited to machine computation and storage across time zones:

```java
Instant now = Instant.now();          // e.g. 2024-06-01T03:15:30.123456Z
long epochMilli = now.toEpochMilli(); // milliseconds since 1970-01-01Z
```

`Clock` abstracts "obtain the current time", making it easy to inject a fixed clock in tests:

```java
Clock clock = Clock.systemUTC();
Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
Instant fixedNow = fixed.instant();   // always returns 2024-01-01T00:00:00Z
```

## 3. Duration / Period (Java 8+)

Both represent a span of time:

| Type | Meaning | Example |
|------|---------|---------|
| `Duration` | Time-based (seconds/nanos) | 2 hours 30 minutes |
| `Period` | Date-based (years/months/days) | 2 years, 3 months, 4 days |

```java
Duration d = Duration.ofHours(2).plusMinutes(30);
Period p = Period.of(2, 3, 4);
LocalDateTime a = LocalDateTime.of(2024, 1, 1, 0, 0);
LocalDateTime b = a.plus(d);
Duration between = Duration.between(a, b);
```

## 4. ZonedDateTime / ZoneId / ZoneOffset (Java 8+)

`ZonedDateTime` = local date-time + time zone rules (daylight saving and the like):

```java
ZoneId shanghai = ZoneId.of("Asia/Shanghai");
ZonedDateTime zdt = ZonedDateTime.now(shanghai);
ZonedDateTime ny = zdt.withZoneSameInstant(ZoneId.of("America/New_York"));
```

`ZoneOffset` is a fixed offset (e.g. `+08:00`); `ZoneId` also encodes daylight-saving rules. **Prefer `ZoneId` over `TimeZone`**: `TimeZone` belongs to the legacy API — its offset calculations depend on the default time zone, it is mutable, and it is full of pitfalls; `ZoneId` is immutable and works seamlessly with the rest of `java.time`.

## 5. DateTimeFormatter: Formatting and Parsing (Java 8+)

```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String text = LocalDateTime.now().format(fmt);
LocalDateTime parsed = LocalDateTime.parse("2024-01-01 08:30:00", fmt);
```

Built-in ISO and localized formats are also available:

```java
String iso = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
DateTimeFormatter cn = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        .withLocale(Locale.CHINA);
System.out.println(LocalDate.now().format(cn));
```

Failed parsing throws `DateTimeParseException`; a bad pattern passed to `ofPattern` throws `IllegalArgumentException`.

## 6. truncatedTo: Truncating Precision (Java 8+)

Truncate a time to a given unit:

```java
LocalTime t = LocalTime.of(14, 30, 45, 123_456_789);
LocalTime minute = t.truncatedTo(ChronoUnit.MINUTES); // 14:30
```

## 7. Legacy API Comparison and Conversion

Problems with `java.util.Date` / `Calendar`: mutable (not thread-safe), months are zero-based, and time zone handling is mixed up with `LocalDate` semantics. Converting between old and new:

```java
Date date = new Date();
Instant instant = date.toInstant();               // Date -> Instant
Date back = Date.from(instant);                    // Instant -> Date

// LocalDate <-> Date/Instant (requires a time zone)
LocalDate ld = LocalDate.now();
Date fromDate = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
LocalDate fromDate2 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
```

## 8. Common Utility Methods

```java
// Days between two dates
long days = ChronoUnit.DAYS.between(LocalDate.of(2024, 1, 1), LocalDate.now());

// First / last day of a month
LocalDate first = LocalDate.now().withDayOfMonth(1);
LocalDate last = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

// Leap year check
boolean leap = Year.of(2024).isLeap();
```

Validity checking: `LocalDate.of(2024, 2, 30)` throws `DateTimeException` outright; use try/catch to determine whether input is valid.

## 9. Running This Module

**VS Code**: open `src/com/learn/datetime/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|----------|
| `Main.java` | Main entry point, runs all examples in order |
| `LocalDateBasic.java` | Creating LocalDate/LocalTime/LocalDateTime and add/subtract/adjust |
| `LocalTimeAndDateTime.java` | Common LocalTime and LocalDateTime operations |
| `ZonedAndDuration.java` | ZonedDateTime/ZoneId and Duration/Period |
| `InstantAndClock.java` | Instant timestamps and the Clock abstraction |
| `DateTimeFormat.java` | DateTimeFormatter formatting and parsing |
| `DateLegacy.java` | Legacy Date/Calendar problems and old/new conversions |
| `DateTimeUtils.java` | Common utilities: between, firstDayOfMonth, and others |

## Common Pitfalls

1. In the legacy `java.util.Date`, months are zero-based (`getMonth()` returns 0 for January); `java.time` starts at 1
2. `Date`/`SimpleDateFormat` are mutable and not thread-safe; everything in `java.time` is immutable and inherently thread-safe
3. Parsing with `ofPattern("yyyy-MM-dd")` into a `LocalDateTime` fails because the time part is missing — the target type must match the pattern
4. `Period.of(1, 15, 0)` does not carry over into 1 year 3 months — keep that in mind when displaying; prefer `ChronoUnit` for computing differences
5. Time zone conversions involving daylight saving must use `withZoneSameInstant` — not `withZoneSameLocal` followed by simple hour arithmetic
6. For storage/transmission, standardize on `Instant` or a zone-aware `ZonedDateTime`; never treat a bare `LocalDateTime` as an "absolute moment"
