package cv.content

import cv.dictionaries.Companies
import cv.dsl.CvBuilder

/** The "Personal projects" section: side projects worth showing off. */
internal fun CvBuilder.personalProjectsSection() =
    projects(title = "Personal projects", icon = "faLaptop", webTitle = "Personal Projects") {
        project(
            name = "System of retryable chain tasks",
            company = Companies.TINKOFF,
            location = "St.Petersburg, Russia",
            year = "2022",
            tags = listOf("Kotlin", "Spring", "Task Scheduler", "Quartz"),
        ) {
            paragraph(
                """
                Implemented a Java Spring library for delayed and retryable execution of task chains (similar
                to Quartz), supporting configurable retries, delays, metrics, and REST-based task management.
                """,
            ) {
                bold("Quartz")
            }
            bullets {
                item("Job success rate increased to 99.9% and recovery time reduced by 50%.") {
                    bold("increased to 99.9%")
                    bold("reduced by 50%")
                }
            }
        }
    }
