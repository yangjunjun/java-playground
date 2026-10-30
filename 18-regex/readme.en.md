# Regular Expressions

A regular expression describes the structure of text with a pattern language. Java provides support through the `java.util.regex` package: `Pattern` represents a compiled regex, and `Matcher` performs matching, finding, and replacement on a target input string. Regular expressions are well suited to text processing tasks such as validation, extraction, splitting, and redaction.

## 1. Pattern and Matcher

`Pattern.compile` compiles a regex into an object; `matcher()` creates a matcher over the input string:

| Method | Semantics |
|------|------|
| `matches()` | the **entire string** must match |
| `find()` | finds the **next** substring match in the string |
| `lookingAt()` | matches from the **beginning**, without requiring the whole string to be consumed |

```java
Pattern phone = Pattern.compile("\\d{3}-\\d{4}");
Matcher m = phone.matcher("Phone 123-4567 ext 8");
if (m.find()) {
    System.out.println(m.group()); // 123-4567
}
```

In Java source code, backslashes must be doubled: `\\d` in Java source means the regex `\d`.

## 2. Character Classes

| Syntax | Meaning |
|------|------|
| `\d` | digit `[0-9]` |
| `\w` | word character `[a-zA-Z0-9_]` |
| `\s` | whitespace character |
| `\D \W \S` | negations of the three above |
| `[a-f0-9]` | custom set |
| `[^0-9]` | negated set |

```java
Pattern.compile("\\d+");     // a run of digits
Pattern.compile("[^,;]+");   // a run of characters other than comma/semicolon
```

## 3. Quantifiers: Greedy / Lazy / Possessive

| Syntax | Meaning | Backtracking |
|------|------|------|
| `*` `+` `?` `{n,m}` | greedy: match as much as possible | backtracks |
| `*?` `+?` `??` `{n,m}?` | lazy: match as little as possible | backtracks |
| `*+` `++` `?+` `{n,m}+` | possessive: no backtracking, fails outright rather than giving back | no backtracking |

```java
"<.+>".matcher("<b>bold</b>").find();   // greedy: <b>bold</b>
"<.+?>".matcher("<b>bold</b>").find();  // lazy: <b>
```

## 4. Anchors

- `^` start of line/string, `$` end of line/string (with `MULTILINE` these become per-line boundaries)
- `\b` word boundary: `\bcat\b` will not match in `catalog`

```java
Pattern.matches("^\\d+$", "12345"); // the whole string is digits
```

## 5. Grouping and Replacement

- Ordinary capturing groups `(...)`: retrieve with `group(1)`, `group(2)`; numbering follows the order of opening parentheses
- Named groups `(?<name>...)` (Java 7+): retrieve with `group("name")`
- Non-capturing groups `(?:...)`: control precedence only; they do not take a number

```java
Matcher m = Pattern.compile("(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})")
        .matcher("2025-01-15");
if (m.matches()) {
    System.out.println(m.group("y")); // 2025
}

"2025-01-15".replaceAll("(\\d{4})-(\\d{2})-(\\d{2})", "$3/$2/$1"); // 15/01/2025
```

Use `appendReplacement` / `appendTail` to build the result piece by piece; `replaceFirst` replaces only the first occurrence.

## 6. Match Flags

| Flag | Effect |
|------|------|
| `CASE_INSENSITIVE` | ignore case |
| `MULTILINE` | `^` `$` match at the start/end of each line |
| `DOTALL` | `.` also matches line terminators |
| `UNICODE_CHARACTER_CLASS` | interpret `\w` etc. per Unicode |
| `LITERAL` | treat the entire pattern as a literal |

```java
Pattern.compile("^b", Pattern.MULTILINE);
Pattern.compile("(?i)hello"); // inline flag
```

## 7. String.split and Pattern.split

`String.split(regex)` is convenient but recompiles the regex every time; for complex scenarios use `Pattern.split`, and note that the second argument is a **limit**:

```java
Pattern.compile(",").split("a,b,c");        // [a, b, c]
Pattern.compile(",").split("a,b,c", 2);     // [a, b,c]
```

## 8. Performance: Precompilation and Backtracking

`String.matches` / `replaceAll` / `split` recompile the regex every time; on hot paths you should cache the `Pattern`:

```java
private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w.-]+\\.[a-z]{2,}");
```

**Catastrophic backtracking**: structures like `(a+)+b` — "nested quantifiers with overlapping matches" — must try every possible split when they fail, which is theoretically O(2ⁿ).
The classic attack input is a long run of `a` with the trailing `b` deliberately missing. Newer OpenJDK releases have mitigated the classic backtracking explosion,
but the same expression can still hang on JS / Python / older Java, and more complex patterns can still be exploited.

Defenses:

- Avoid nested quantifiers (a `*`/`+` inside `(...)+`)
- Use possessive quantifiers `(a++)+b`: no backtracking, fails immediately
- Limit the length of untrusted input before running it through a regex

## 9. Java 9 / 11+ Convenience APIs

- `Matcher.results()` (Java 9+): a `Stream<MatchResult>` of all matches
- `Pattern.asMatchPredicate()` / `asPredicate()` (Java 11+): use directly as a `Predicate<String>`

```java
List<String> hit = words.stream()
        .filter(Pattern.compile("cat").asMatchPredicate())  // whole-string match
        .toList();
```

## 10. Common Recipes (mind the limitations)

```java
// email-like: blocks obvious mistakes, but is not RFC 5322
"^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"

// integer
"[-+]?\\d+"

// date YYYY-MM-DD: validates the numeric format only; February 31st passes!
"^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"
```

When you need real calendar validity, use `LocalDate.parse` instead of relying on regex alone.

## 11. Running This Module

**VS Code**: open `src/com/learn/regex/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | main entry point, runs all examples in order |
| `PatternMatcher.java` | Pattern.compile / matches / find / lookingAt |
| `RegexSyntax.java` | character classes, quantifiers (greedy/lazy/possessive), anchors |
| `GroupAndReplace.java` | grouping, named groups, replacement and appendReplacement |
| `RegexFlags.java` | CASE_INSENSITIVE / MULTILINE / DOTALL |
| `RegexPerformance.java` | precompiled Pattern, catastrophic backtracking |
| `RegexJava11Plus.java` | asMatchPredicate / asPredicate / results and common recipes |

## Common Pitfalls

1. `matches()` matches the whole string, while `find()` searches for a substring; mixing them up leads to "it's clearly there but won't match"
2. In Java string literals, regexes need double backslashes: `"\\d"` not `"\d"` (the latter fails to compile)
3. The second argument of `String.replaceAll` is a **replacement template**; `$1` and `$` are special, and a literal `$` must be written `$$`
4. Nested quantifiers (such as `(a+)+`) can catastrophically backtrack on malicious input; never feed untrusted long text straight into a complex regex
5. Do not use `String.matches` on hot paths — it recompiles every time; cache the `Pattern` instead
