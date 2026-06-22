package br.com.alr.order.support.application.port.in;

import br.com.alr.order.support.application.dto.SupportChatCommand;
import br.com.alr.order.support.application.dto.SupportChatResponse;
import br.com.alr.order.support.application.exception.AiSupportUnavailableException;
import br.com.alr.order.support.application.exception.InvalidSupportRequestException;

public interface ChatSupportUseCase {

  SupportChatResponse chat(SupportChatCommand command)
      throws InvalidSupportRequestException, AiSupportUnavailableException;
}
