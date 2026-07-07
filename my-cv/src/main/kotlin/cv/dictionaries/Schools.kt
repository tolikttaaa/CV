package cv.dictionaries

import cv.model.Organization

/**
 * Dictionary of the schools referenced in the CV — in education milestones and
 * teaching positions. See also [Companies] and [Universities].
 */
internal object Schools {
    val LETOVO = Organization("Letovo School", url = "https://en.letovo.ru/")
    val UNIVERSUM = Organization("UniverSum", url = "https://univer-sum.ru")
    val PTHS = Organization(
        "Physical Technical High School (PTHS)",
        url = "https://www.school.ioffe.ru/readings/pths_about_en.html",
    )
    val IMSA = Organization("Illinois Mathematical and Science Academy (IMSA)", url = "https://www.imsa.edu/")
}
