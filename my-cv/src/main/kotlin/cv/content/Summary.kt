package cv.content

import cv.dsl.CvBuilder

/** The "Summary" section: a short professional introduction. */
internal fun CvBuilder.summarySection() = summary(title = "Summary", icon = "faUser") {
    paragraph(
        """
        Software Engineer with a strong background in backend development, test automation, and system
        architecture. Skilled in Kotlin and Java, with hands-on expertise in building scalable microservices
        using Spring Framework, PostgreSQL, MongoDB, Kubernetes, and AWS. Proven track record of building
        reliable, high-performance systems in the fintech and cloud infrastructure domains.
        """,
    )
    paragraph(
        """
        Worked in multiple teams at Azul, Yandex, and Tinkoff, contributing to mission-critical services,
        CI/CD pipelines, monitoring dashboards, and automation frameworks. Passionate about clean code,
        maintainable architecture, and continuous learning.
        """,
    ) {
        bold("Azul")
        bold("Yandex")
        bold("Tinkoff")
    }
}
