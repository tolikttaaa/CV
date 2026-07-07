package cv.content

import cv.dsl.CvBuilder

/** The "Skills and Abilities" section: a keyword table grouped by category. */
internal fun CvBuilder.skillsSection() =
    skills(title = "Skills and Abilities", icon = "faCode", webTitle = "Skills & Abilities") {
        entry("Communication", listOf("English (C1)", "Russian (native)"))
        entry("Programming Languages", listOf("Kotlin", "Java", "Python", "Bash", "C", "C++"))
        entry(
            "Frameworks & Libraries",
            listOf(
                "Spring Framework", "JUnit", "Mockito", "Allure TestOps", "Swagger", "JaCoCo",
                "Gradle", "Maven", "Quartz", "Prometheus", "Micrometer", "Selenium", "Selenide",
                "TestContainers", "WireMock",
            ),
        )
        entry("Databases & Storage", listOf("PostgreSQL", "MongoDB", "AWS RDS", "S3"))
        entry("Messaging & Streaming", listOf("Apache Kafka", "Apache Artemis", "AWS SQS"))
        entry("Cloud & Infrastructure", listOf("AWS", "Kubernetes", "Docker", "Helm"))
        entry("Protocols & Formats", listOf("JSON", "YAML", "Protobuf", "XML", "WSDL", "REST"))
        entry("Version Control", listOf("Git", "SVN"))
        entry("CI/CD & DevOps Tools", listOf("GitLab CI", "Jenkins"))
        entry("Monitoring & Observability", listOf("Grafana", "Prometheus", "Loki", "Micrometer"))
        entry("Work Process", listOf("Jira", "Confluence", "Scrum", "Kanban", "Agile", "GitLab", "Bitbucket"))
        entry("IDE & Tools", listOf("IntelliJ IDEA", "PyCharm", "DataGrip", "Postman"))
        entry(
            "Architecture & Principles",
            listOf(
                "Microservices", "Event-driven systems", "Clean Architecture",
                "Test Driven Development (TDD)", "Software Design", "System Architecture",
            ),
        )
        entry(
            "General Knowledge",
            listOf(
                "Programming", "Computer Science", "Algorithms", "Data Structures",
                "Math & Geometry", "Teaching", "Mentoring",
            ),
        )
    }
