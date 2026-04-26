package com.xavierarbat.xavierarbatback.repository

import com.xavierarbat.xavierarbatback.domain.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, String>
