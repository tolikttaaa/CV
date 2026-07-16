package cv.content

import cv.dictionaries.Companies
import cv.dsl.CvBuilder

/** The "Experience" section: employment history, most recent first. */
@Suppress("LongMethod") // Declarative CV content is intentionally kept as one chronological section.
internal fun CvBuilder.experienceSection() = experience(title = "Experience", icon = "faSuitcase", id = "experience") {
    work(
        role = "Backend Software Engineer",
        company = Companies.AZUL,
        location = "Limassol, Cyprus",
        dates = "January 2025 – June 2026",
        tags = listOf(
            "Kotlin", "Java", "AWS", "Kubernetes", "PostgreSQL", "MongoDB", "Spring Framework",
            "Spring Batch", "Kafka", "Gradle", "Grafana", "Prometheus", "GitLab CI", "JUnit 5",
            "Mockito", "Allure TestOps",
        ),
    ) {
        paragraph(
            """
            Designed and delivered multiple backend services for vulnerability data processing and enrichment
            within a JVM ecosystem, including AI-assisted detection of impacted code elements and a centralized
            CPE/CVE processing pipeline. Established system observability with Grafana to ensure reliability
            and performance at scale.
            """,
        )
        bullets {
            item(
                """
                Average CVE and CPE ingestion time decreased from 24h to 8h, ensuring compliance with
                a 24-hour security SLA.
                """,
            ) {
                bold("decreased from 24h to 8h")
            }
            item(
                """
                Class/Method-level vulnerability coverage improved from 15% to 75% through automation
                of data collection and analysis.
                """,
            ) {
                bold("improved from 15% to 75%")
            }
            item(
                """
                Migrated the system from a monolithic to a microservices architecture, enabling independent
                scaling of components and improving overall system flexibility and maintainability.
                """,
            )
        }
    }
    work(
        role = "Software Engineer in Test",
        company = Companies.AZUL,
        location = "Yerevan, Armenia (relocation to Cyprus)",
        dates = "April 2023 – January 2025",
        tags = listOf(
            "Java", "Allure TestOps", "JUnit 5", "Spring Framework", "Selenium", "Selenide",
            "PostgreSQL", "jtreg", "Jenkins", "GitLab CI",
        ),
    ) {
        paragraph(
            """
            Improved the quality, reliability, and performance of the automated testing pipeline by introducing
            structured test planning, expanding automation coverage, and optimizing test execution strategy.
            Collaborated with developers and product managers to define comprehensive test plans, implemented
            automated tests for new features, and refactored existing test code to improve maintainability
            and scalability.
            """,
        )
        bullets {
            item(
                """
                Automated test coverage increased from 60% to 80%, improving overall product quality
                and reducing regression risks.
                """,
            ) {
                bold("increased from 60% to 80%")
            }
            item(
                """
                Flaky test rate reduced from 30% to <1%, significantly increasing test reliability
                and developer trust in CI results.
                """,
            ) {
                bold("reduced from 30% to <1%")
            }
            item(
                """
                Average pipeline test execution time decreased from 2 hours to 1 hour by splitting tests
                by scope and executing only impacted tests per pipeline.
                """,
            ) {
                bold("decreased from 2 hours to 1 hour")
            }
        }
    }
    work(
        role = "Java Backend Developer",
        company = Companies.YANDEX,
        location = "Yerevan, Armenia",
        dates = "January 2023 – April 2023",
        tags = listOf("Java", "PostgreSQL", "Spring Framework", "Python", "JUnit 5", "Prometheus"),
    ) {
        paragraph(
            """
            Implemented a billing service designed for the creation and management of promotional offers and
            subscriptions. The system enabled quick and flexible setup of new promotions with configurable
            conditions. The system allows non-engineering teams to independently create and manage offers
            without requiring code changes or database migrations.
            """,
        )
        bullets {
            item(
                """
                Time-to-market for new promotions reduced from days to hours, eliminating dependency
                on engineering team.
                """,
            ) {
                bold("reduced from days to hours")
            }
        }
    }
    work(
        role = "Kotlin Backend Developer",
        company = Companies.TINKOFF,
        location = "St.Petersburg, Russia (relocation to Armenia)",
        dates = "May 2021 – January 2023",
        tags = listOf(
            "Kotlin", "PostgreSQL", "Oracle", "Spring Framework", "Kafka", "Camunda", "Quartz",
            "Swagger", "Allure TestOps", "JUnit 5", "WSDL", "Prometheus", "Grafana",
        ),
    ) {
        paragraph(
            """
            Designed and delivered multiple high-load backend services and infrastructure components, including
            workflow orchestration, low-latency data services, distributed data migration, and internal
            execution frameworks. Focused on scalability, reliability, and automation across distributed
            systems.
            """,
        )
        bullets {
            item(
                """
                Enabled stable handling of 10k+ RPS by building a customer data service, establishing
                a single source of truth for multiple internal platforms.
                """,
            ) {
                bold("10k+ RPS")
            }
            item(
                """
                Reduced manual data migration effort and automated large-scale transfers, processing millions
                of records across PostgreSQL and Oracle systems.
                """,
            )
            item(
                """
                Test coverage increased by 50% and regression cycle time reduced by 70% by introducing
                an automated testing framework integrated into CI/CD pipelines.
                """,
            ) {
                bold("increased by 50%")
                bold("reduced by 70%")
            }
        }
    }
}
