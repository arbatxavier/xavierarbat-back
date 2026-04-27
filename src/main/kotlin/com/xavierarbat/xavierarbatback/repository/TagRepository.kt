package com.xavierarbat.xavierarbatback.repository

import com.xavierarbat.xavierarbatback.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, String>
