package com.cubes_and_mods.order.security.session_checkers;

import org.springframework.stereotype.Component;

import com.cubes_and_mods.order.jpa.Order;
import com.cubes_and_mods.order.security.ClientSession;
import com.cubes_and_mods.order.security.ProtectedRequest;
import com.cubes_and_mods.order.security.annotations.CheckUserSession.SessionValidator;

@Component
public class SessionCheckerOrder implements SessionValidator {

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(ClientSession session, ProtectedRequest<?> realRequest) {
        
        var request = (ProtectedRequest<Order>) realRequest;
        if (!session.client.getId().equals(request.data.getClientOrder().getId())) return false;

        return true;
    }
}
