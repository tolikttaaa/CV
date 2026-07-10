package cv.dsl

import cv.model.Social

/**
 * Builder for one visual row of the contact block in the CV header.
 * Entries added here appear side by side on the same line.
 */
@CvDsl
class SocialRowBuilder {
    internal val entries = mutableListOf<Social>()

    /** Adds a phone number. */
    fun phone(number: String) { entries += Social.Phone(number) }

    /** Adds a Telegram handle (without the `@`). */
    fun telegram(handle: String) { entries += Social.Telegram(handle) }

    /** Adds an e-mail address. */
    fun email(address: String) { entries += Social.Email(address) }

    /** Adds a LinkedIn profile id. */
    fun linkedin(handle: String) { entries += Social.LinkedIn(handle) }

    /** Adds a LeetCode username. */
    fun leetcode(handle: String) { entries += Social.LeetCode(handle) }

    /** Adds a GitHub username. */
    fun github(handle: String) { entries += Social.GitHub(handle) }

    /** Adds a free-form location, e.g. a city or country. */
    fun address(text: String) { entries += Social.Address(text) }
}

/**
 * Builder for the contact block of the CV header: a list of [row]s,
 * each rendered on its own line.
 */
@CvDsl
class SocialBuilder {
    internal val rows = mutableListOf<List<Social>>()

    /** Starts a new line of contact entries. */
    fun row(block: SocialRowBuilder.() -> Unit) {
        rows += SocialRowBuilder().apply(block).entries.toList()
    }
}
