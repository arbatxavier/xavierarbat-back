package com.xavierarbat.xavierarbatback.config

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

@Converter(autoApply = false)
class JsonbListConverter : AttributeConverter<List<String>, String> {

    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): List<String>? {
        return dbData?.let { objectMapper.readValue(it) }
    }
}
