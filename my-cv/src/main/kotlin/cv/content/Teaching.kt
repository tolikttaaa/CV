package cv.content

import cv.dictionaries.Schools
import cv.dictionaries.Universities
import cv.dsl.CvBuilder

/** The "Teaching and Mentoring" section: teaching positions, most recent first. */
@Suppress("LongMethod") // Declarative CV content is intentionally kept as one chronological section.
internal fun CvBuilder.teachingSection() = experience(
    title = "Teaching and Mentoring",
    icon = "faGraduationCap",
    id = "teaching",
    webTitle = "Teaching & Mentoring",
) {
    work(
        role = "University Teaching Assistant",
        company = Universities.ITMO,
        location = "St.Petersburg, Russia",
        dates = "September 2020 – May 2023",
        tags = listOf("Python", "Java", "Programming", "Computer Science", "Algorithms", "Data Structures"),
    ) {
        paragraph(
            """
            Contributed to the development of laboratory assignments and teaching materials for the
            Information Technology course. Oversaw and assessed laboratory work submitted by
            undergraduate students.
            """,
        ) {
            bold("Information Technology")
        }
    }
    work(
        role = "Olympiad Programming Teacher",
        company = Schools.LETOVO,
        location = "Moscow, Russia",
        dates = "January 2020, May 2019",
        tags = listOf("Python", "Java", "C++", "Programming", "Algorithms", "Data Structures"),
    ) {
        paragraph(
            """
            Taught at the School of Olympiad Programming at Letovo School. Prepared and delivered lectures
            on Algorithms and Data Structures to 7th–8th grade high school students.
            """,
        ) {
            bold("Algorithms")
            bold("Data Structures")
        }
    }
    work(
        role = "Summer Computer Science School Teacher",
        company = Schools.UNIVERSUM,
        location = "Munich, Germany",
        dates = "May 2018",
        tags = listOf(
            "Python", "Java", "C++", "Programming", "Algorithms", "Data Structures",
            "Augmented and Virtual Reality",
        ),
    ) {
        paragraph(
            """
            Taught at the Summer Computer Science School UniverSum for Russian students. Prepared and
            delivered lectures on Algorithms, Augmented and Virtual Reality to 7th–11th grade high
            school students.
            """,
        ) {
            bold("Algorithms")
            bold("Augmented and Virtual Reality")
        }
    }
}
