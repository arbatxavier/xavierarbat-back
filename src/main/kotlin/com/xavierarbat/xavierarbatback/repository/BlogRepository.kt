package com.xavierarbat.xavierarbatback.repository

import com.xavierarbat.xavierarbatback.domain.Blog
import org.springframework.data.jpa.repository.JpaRepository

interface BlogRepository : JpaRepository<Blog, String>
