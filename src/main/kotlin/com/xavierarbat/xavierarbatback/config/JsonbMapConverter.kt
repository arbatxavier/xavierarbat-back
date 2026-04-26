package com.xavierarbat.xavierarbatback.config

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

@Converter(autoApply = false)
class JsonbMapConverter : AttributeConverter<Map<String, String>, String> {

    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, String>?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, String>? {
        return dbData?.let { objectMapper.readValue(it) }
    }
}
