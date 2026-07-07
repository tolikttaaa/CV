package cv.dictionaries

import cv.model.Organization

/**
 * Dictionary of the universities referenced in the CV — in education milestones
 * and teaching positions. See also [Companies] and [Schools].
 */
internal object Universities {
    val ITMO = Organization("ITMO University", url = "https://en.itmo.ru/")
}
