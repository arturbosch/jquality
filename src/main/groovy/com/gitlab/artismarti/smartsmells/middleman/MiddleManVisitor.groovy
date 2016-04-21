package com.gitlab.artismarti.smartsmells.middleman

import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class MiddleManVisitor extends Visitor<MiddleMan> {

    MiddleManVisitor(Path path) {
        super(path)
    }
}
