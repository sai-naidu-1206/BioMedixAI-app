package com.biomedix

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BioMedixApplication

fun main(args: Array<String>) {
    runApplication<BioMedixApplication>(*args)
}
