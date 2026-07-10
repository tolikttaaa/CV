package cv.content

import cv.dictionaries.Companies
import cv.dsl.CvBuilder

/** The "References" section: people who can vouch for the work above. */
internal fun CvBuilder.referencesSection() = references(title = "References", icon = "faQuoteLeft") {
    referee(
        name = "Evgeny Gil",
        role = "Manager, Software Engineer",
        company = Companies.AZUL,
        period = "2025 - 2026",
        email = "egil@azul.com",
    )
    referee(
        name = "Sergey Grinev",
        role = "Senior Manager, QA Engineer",
        company = Companies.AZUL,
        period = "2023 - 2024",
        email = "sergey.grinev@azul.com",
    )
    referee(
        name = "Sergii Kurstak",
        role = "Senior Director, Engineering",
        company = Companies.AZUL,
        period = "2024 - 2026",
        email = "sergii@azul.com",
    )
    // referee(
    //     name = "Kostya Zolotnikov",
    //     role = "Senior Director, Engineering",
    //     company = Companies.AZUL,
    //     period = "since 2023",
    //     email = "kostya@azul.com",
    // )
    // referee(
    //     name = "Evgeny Shirankov",
    //     role = "Project lead",
    //     company = Companies.YANDEX,
    //     period = "2023",
    //     email = "shirankov@yandex-team.ru",
    // )
    // referee(
    //     name = "Mikhail Andreev",
    //     role = "Project lead",
    //     company = Companies.TINKOFF,
    //     period = "2021 - 2023",
    //     email = "m.v.andreev@tinkoff.ru",
    // )
    // referee(
    //     name = "Daniel Kuzikov",
    //     role = "Project lead",
    //     company = Organization("Xyleme Inc."),
    //     period = "2020",
    //     email = "daniel.kuzikov@xyleme.com",
    // )
}
