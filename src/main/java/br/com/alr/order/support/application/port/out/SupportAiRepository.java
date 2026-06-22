package br.com.alr.order.support.application.port.out;

import br.com.alr.order.support.application.dto.SupportAiRequest;
import br.com.alr.order.support.application.dto.SupportAiResult;
import br.com.alr.order.support.application.exception.AiSupportUnavailableException;

public interface SupportAiRepository {

  SupportAiResult chat(SupportAiRequest request) throws AiSupportUnavailableException;
}
