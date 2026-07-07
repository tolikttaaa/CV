package cv.dictionaries

import cv.model.Organization

/**
 * Dictionary of the companies referenced in the CV. Content files reuse these
 * constants instead of repeating names and URLs, so an employer is defined
 * (and updated) in exactly one place. See also [Universities] and [Schools].
 */
internal object Companies {
    val AZUL = Organization("Azul", url = "https://www.azul.com/")
    val YANDEX = Organization("Yandex", url = "https://yandex.com/company")
    val TINKOFF = Organization("Tinkoff", url = "https://www.tinkoff.ru/software/")
}
