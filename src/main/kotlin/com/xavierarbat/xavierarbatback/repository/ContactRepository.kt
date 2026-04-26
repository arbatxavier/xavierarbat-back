package com.xavierarbat.xavierarbatback.repository

import com.xavierarbat.xavierarbatback.domain.Contact
import org.springframework.data.jpa.repository.JpaRepository

interface ContactRepository : JpaRepository<Contact, String>
