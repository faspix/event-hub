package com.faspix.service;

import com.faspix.dto.ConfirmedRequestsDTO;

public interface ConfirmedRequestService {

    void sendConfirmedRequestMsg(ConfirmedRequestsDTO message);

}
