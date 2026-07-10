package cv.content

import cv.dictionaries.Schools
import cv.dictionaries.Universities
import cv.dsl.CvBuilder

/** The "Education" section: degrees and school milestones, most recent first. */
internal fun CvBuilder.educationSection() = education(title = "Education", icon = "faUserGraduate") {
    entry(
        years = "2018 – 2022",
        degree = "Bachelor’s Degree in Software Engineering and Computer Technologies",
        institution = Universities.ITMO,
        location = "St.Petersburg, Russia",
    )
    entry(
        years = "2014 – 2018",
        degree = "complete secondary",
        institution = Schools.PTHS,
        location = "St.Petersburg, Russia",
    )
    entry(
        years = "2018",
        degree = "school exchange program (1 month)",
        institution = Schools.IMSA,
        location = "Illinois, USA",
    )
}
