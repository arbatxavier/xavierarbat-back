package com.xavierarbat.xavierarbatback.domain

import com.xavierarbat.xavierarbatback.config.JsonbListConverter
import com.xavierarbat.xavierarbatback.config.JsonbMapConverter
import com.xavierarbat.xavierarbatback.domain.enums.AspectRatio
import com.xavierarbat.xavierarbatback.domain.enums.ImageDisplay
import com.xavierarbat.xavierarbatback.domain.enums.TagKey
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "project")
data class Project(
    @Id
    @Column(length = 255)
    val slug: String,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(nullable = false, length = 512)
    val image: String,

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val title: Map<String, String> = emptyMap(),

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val description: Map<String, String> = emptyMap(),

    @Convert(converter = JsonbMapConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val content: Map<String, String> = emptyMap(),

    @ElementCollection(targetClass = TagKey::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "project_tag", joinColumns = [JoinColumn(name = "project_slug")])
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    val tags: Set<TagKey> = emptySet(),

    @Enumerated(EnumType.STRING)
    @Column(name = "image_display", nullable = false)
    val imageDisplay: ImageDisplay = ImageDisplay.COVER,

    @Enumerated(EnumType.STRING)
    @Column(name = "aspect_ratio", nullable = false)
    val aspectRatio: AspectRatio = AspectRatio.FOURTHIRDS,

    @Convert(converter = JsonbListConverter::class)
    @Column(name = "alt_images", columnDefinition = "jsonb", nullable = false)
    val altImages: List<String> = emptyList()
)
