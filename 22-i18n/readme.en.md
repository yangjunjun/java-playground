# Internationalization (i18n)

Internationalization (i18n) lets a single codebase serve users in different languages and regions. Java's core tools: `Locale` (region), `ResourceBundle` (resource bundle), `MessageFormat` (message template), `NumberFormat` / `DateTimeFormatter` (localized numbers and dates), `Collator` (sorting).

## 1. Locale

`Locale` = language (lowercase) + country (uppercase) + variant; you can also use BCP 47 language tags:

```java
Locale zhCn = Locale.of("zh", "CN");          // Locale.of (Java 19+)
// old style: new Locale("zh", "CN")  // marked @Deprecated as of Java 25
Locale enUs = Locale.US;
Locale hans = Locale.forLanguageTag("zh-Hans-CN");  // Java 7+

Locale.getDefault();          // OS default
Locale.setDefault(Locale.US); // change the default (affects factory methods that take no Locale)
```

**Application code should always pass `Locale` explicitly; never rely on the default.**

## 2. ResourceBundle

Resource bundles fall back along a naming chain: `BaseName_language_COUNTRY.properties` → `BaseName_language.properties` → `BaseName.properties`.

This module's resource files live in `src/i18n/`:

| File | Coverage |
|------|----------|
| `Messages_zh_CN.properties` | Simplified Chinese - China |
| `Messages_en_US.properties` | English - United States |
| `Messages.properties` | Fallback default |

```java
ResourceBundle zh = ResourceBundle.getBundle("i18n.Messages", Locale.CHINA);
System.out.println(zh.getString("app.title"));
```

`run.sh` uses `java -cp out:src` to put `src/i18n/Messages*.properties` on the class path.
In Java 9+, `.properties` resource bundles are read as UTF-8 by default (JEP 226), so Chinese text can be written directly.

## 3. MessageFormat

Placeholders `{0}`, `{1}`, with embedded number/date formats:

```java
MessageFormat.format("Hello, {0}!", "John");

// composite format
new MessageFormat("Order {0}: {1,number,currency}, time {2,date,short}")
        .format(new Object[]{ 1001, 199.5, new Date() });
```

`ChoiceFormat` handles plural/quantifier branches:

```java
"{0,choice,0#no files|1#only one file|1<a total of {0,number,integer} files}"
```

**Pitfall 1**: in `MessageFormat`, the single quote is an escape character; a literal `'` must be written as `''`.
**Pitfall 2**: the Locale of sub-formats (`number,currency` / `date`) is fixed at **construction time**;
after `setLocale` you must call `applyPattern` again, or the currency degrades to the generic symbol `¤`.

## 4. NumberFormat

```java
NumberFormat.getCurrencyInstance(Locale.CHINA).format(1234567.89); // ￥1,234,567.89
NumberFormat.getPercentInstance(Locale.US).format(0.8567);         // 85.67%
NumberFormat.getIntegerInstance(Locale.JAPAN).format(1234567890);  // 1,234,567,890
```

Use `BigDecimal` for monetary calculations; never use `double` for money.

## 5. Date and Time Localization

```java
DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.CHINA);
DateTimeFormatter.ofPattern("yyyy-MM-dd EEEE", Locale.CHINA);

ZonedDateTime shanghai = dateTime.atZone(ZoneId.of("Asia/Shanghai"));
shanghai.withZoneSameInstant(ZoneId.of("UTC")); // convert for display in another time zone
```

Store and transmit in ISO-8601; format according to the user's `Locale` only at display time.

## 6. Collator Sorting

`String.compareTo` only looks at UTF-16 code points, which is meaningless for Chinese:

```java
List<String> cities = List.of("上海", "北京", "广州"); // Shanghai, Beijing, Guangzhou
cities.sorted(Collator.getInstance(Locale.CHINA)); // pinyin order: Beijing, Guangzhou, Shanghai
```

`Collator.PRIMARY / SECONDARY / TERTIARY / IDENTICAL` control comparison strength (whether case/accents are ignored).

## 7. Running This Module

**VS Code**: open `src/com/learn/i18n/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

`run.sh` uses `java -cp out:src` so that `src/i18n/*.properties` can be found by `ResourceBundle`.

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in sequence |
| `src/i18n/Messages*.properties` | zh_CN / en_US / fallback resource bundles |
| `LocaleDemo.java` | Locale language/country/script, switching defaults |
| `ResourceBundleDemo.java` | Resource bundle loading and the fallback chain |
| `MessageFormatDemo.java` | Placeholders, ChoiceFormat |
| `NumberFormatDemo.java` | Currency / percent / integer formatting |
| `DateFormatI18n.java` | DateTimeFormatter.ofLocalized* and time zones |
| `CollatorBrief.java` | Localized sorting and comparison strength |

## Common Pitfalls

1. Wrong resource file naming (`Messages_zh-CN.properties`) — the country code must be separated by an underscore and upper-cased: `Messages_zh_CN`
2. Using slashes in the `ResourceBundle` base name (`i18n/Messages`) — it must be dot-separated: `i18n.Messages`
3. Forgetting to put the resource directory on the class path → `MissingResourceException` at runtime
4. Unescaped single quotes in a `MessageFormat` template → quotes dropped from output or placeholders stop working
5. Sorting Chinese with `String.compareTo` → code-point order is not the pinyin/stroke order users expect
6. Hard-coding a date format like `yyyy/MM/dd` instead of using `Locale` → US users see ambiguous dates
7. Using the default `Locale` for persistent formatting → parsing breaks as soon as the server environment changes
