package com.example.logiciel_RH.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.logiciel_RH.entity.JournalAudit;

public interface JournalAuditRepository extends JpaRepository<JournalAudit, Long>, JpaSpecificationExecutor<JournalAudit> {
    List<JournalAudit> findAllByOrderByDateActionDesc();
    List<JournalAudit> findByTypeEntite_CodeAndEntiteIdOrderByDateActionDesc(String codeTypeEntite, Long entiteId);
}
