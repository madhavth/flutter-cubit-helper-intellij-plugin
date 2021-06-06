package com.madhavth.flutter_cubit_plugin.generator

import com.google.common.io.CharStreams
import org.apache.commons.lang.text.StrSubstitutor
import java.io.InputStreamReader
import java.lang.RuntimeException

abstract class CubitGenerator(private val name: String,
                              val templateName: String,
                              private val modelName: String? ="",
                              val isLoadedScreen: Boolean =false
                              ) {

    private val TEMPLATE_CUBIT_PASCAL_CASE = "cubit_pascal_case"
    private val TEMPLATE_CUBIT_SNAKE_CASE = "cubit_snake_case"
    private val TEMPLATE_MODEL = "MODEL"
    private val TEMPLATE_MODEL_SNAKE_CASE = "MODEL_SNAKE"
    private val ALL_CAPS_MODEL ="ALL_CAPS_MODEL"
    private val TEMPLATE_MODEL_SNAKE_FULL_CASE="MODEL_SNAKE_CASE"

    private val templateString: String
    private val templateValues: MutableMap<String, String>

    init {
        templateValues = mutableMapOf(
            TEMPLATE_CUBIT_PASCAL_CASE to pascalCase(),
            TEMPLATE_CUBIT_SNAKE_CASE to snakeCase(),
            TEMPLATE_MODEL to modelName.toString(),
            TEMPLATE_MODEL_SNAKE_CASE to modelName.firstSmall(),
            ALL_CAPS_MODEL to modelName.toString().uppercase(),
            TEMPLATE_MODEL_SNAKE_FULL_CASE to modelName.toString().toSnakeCase()
        )

        try {
            val templateFolder = "cubit_with_equatable"

            val resource = "/templates/$templateFolder/$templateName.dart.template"
            val resourceAsStream = CubitGenerator::class.java.getResourceAsStream(resource)
            templateString = CharStreams.toString(InputStreamReader(resourceAsStream, Charsets.UTF_8))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getName(): String {
        return name
    }

    abstract fun fileName(): String


    fun generate(): String {
        val substitutor = StrSubstitutor(templateValues)
        return substitutor.replace(templateString)
    }

    fun pascalCase(): String = name.toCamelCase().replace("Cubit", "")

    fun snakeCase() = name.toSnakeCase().replace("_cubit", "")

    fun fileExtension() = "dart"
}

fun String.toCamelCase() =
    split('_').joinToString("", transform = String::capitalize)

fun String.toSnakeCase() = replace(humps, "_").toLowerCase()
private val humps = "(?<=.)(?=\\p{Upper})".toRegex()

fun String?.firstSmall(): String {
    if(this!=null && this.isNotBlank())
        return this[0].lowercase() + substring(1)

    return ""
}