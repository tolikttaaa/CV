package cv.model

/**
 * A single contact / social entry shown in the CV header.
 *
 * Each variant knows only its raw value (a handle, a number, …); the renderers
 * derive the presentation from the type — the LaTeX renderer maps each variant
 * to the matching `cvdsl.cls` command (`\smartphone`, `\telegram`, …)
 * and the web renderer derives the icon, the display text, and the target URL.
 */
sealed interface Social {
    /** Phone number, rendered verbatim. */
    data class Phone(val number: String) : Social

    /** Telegram handle without the `@` (linked to `t.me/<handle>`). */
    data class Telegram(val handle: String) : Social

    /** E-mail address (linked as `mailto:`). */
    data class Email(val address: String) : Social

    /** LinkedIn profile id (linked to `linkedin.com/in/<handle>`). */
    data class LinkedIn(val handle: String) : Social

    /** LeetCode username (linked to `leetcode.com/u/<handle>`). */
    data class LeetCode(val handle: String) : Social

    /** GitHub username (linked to `github.com/<handle>`). */
    data class GitHub(val handle: String) : Social

    /** Free-form location text, e.g. a city or country. */
    data class Address(val text: String) : Social
}
