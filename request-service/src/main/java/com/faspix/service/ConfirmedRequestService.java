package com.faspix.service;

import com.faspix.shared.dto.ConfirmedRequestsDTO;

public interface ConfirmedRequestService {

    void sendConfirmedRequestMsg(ConfirmedRequestsDTO message);

}
