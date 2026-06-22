package br.com.alr.order.support.application.port.out;

import br.com.alr.order.support.domain.SupportPolicy;

import java.util.List;

public interface KnowledgeBaseRepository {

  List<SupportPolicy> findAll();
}
