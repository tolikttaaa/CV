package cv.render.web

import cv.model.Organization
import cv.model.Section
import cv.model.Social
import cv.render.ElementRenderer

internal fun Section.renderTitle(): String =
    """<div class="sec-title"><i class="fa-solid fa-${webIcon()}"></i>${h(webTitle)}</div>"""

internal fun Section.webIcon(): String = when (icon) {
    "faUser" -> "user"
    "faSuitcase" -> "briefcase"
    "faCode" -> "bolt"
    "faLaptop" -> "laptop-code"
    "faGraduationCap" -> "graduation-cap"
    "faUserGraduate" -> "book-open"
    "faQuoteLeft" -> "quote-left"
    else -> "circle"
}

internal fun List<String>.renderWeb(): String = if (isEmpty()) "" else
    "<div class=\"tags\">${joinToString("") { "<span class=\"tag\">${h(it)}</span>" }}</div>"

internal fun Organization.renderWeb(emphasized: Boolean): String {
    val label = if (emphasized) "<strong>${h(name)}</strong>" else h(name)
    return url?.let {
        "<a href=\"${h(it)}\" target=\"_blank\" rel=\"noopener\">$label</a>"
    } ?: label
}

internal object WebSocialRenderer : ElementRenderer<Social, WebRenderContext> {
    override fun render(element: Social, context: WebRenderContext): String {
        val contact = when (element) {
            is Social.Phone -> WebContact("fa-solid", "phone", null, element.number)
            is Social.Telegram -> WebContact(
                "fa-brands", "telegram", "https://t.me/${element.handle}", "t.me/${element.handle}",
            )
            is Social.Email -> WebContact("fa-solid", "envelope", "mailto:${element.address}", element.address)
            is Social.LinkedIn -> WebContact(
                "fa-brands", "linkedin", "https://www.linkedin.com/in/${element.handle}",
                "linkedin.com/in/${element.handle}",
            )
            is Social.LeetCode -> WebContact(
                "fa-solid", "code", "https://leetcode.com/u/${element.handle}",
                "leetcode.com/u/${element.handle}",
            )
            is Social.GitHub -> WebContact(
                "fa-brands", "github", "https://github.com/${element.handle}",
                "github.com/${element.handle}",
            )
            is Social.Address -> WebContact("fa-solid", "location-dot", null, element.text)
        }
        val content = "<i class=\"${contact.iconClass} fa-${contact.icon}\"></i><span>${h(contact.text)}</span>"
        return contact.url?.let {
            "<a class=\"contact-item\" href=\"${h(it)}\" target=\"_blank\" rel=\"noopener\">$content</a>"
        } ?: "<div class=\"contact-item\">$content</div>"
    }
}

internal fun h(value: String): String = HtmlText.escape(value)

private data class WebContact(
    val iconClass: String,
    val icon: String,
    val url: String?,
    val text: String,
)
