package io.hunknownn.urljarvis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class UrlJarvisApplication

fun main(args: Array<String>) {
    runApplication<UrlJarvisApplication>(*args)
}
