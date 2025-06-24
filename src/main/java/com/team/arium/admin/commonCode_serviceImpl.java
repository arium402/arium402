package com.team.arium.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.arium.domain.Common_Code;

@Service
public class commonCode_serviceImpl implements commonCode_service {
	@Autowired
    private commonCode_repo cm_cd_refo;

    public String getCodeDesc(String codeType, Integer codeId) {
        return cm_cd_refo.findByCodeTypeAndCodeId(codeType, codeId)
                .map(Common_Code::getCodeDesc)
                .orElse("코드없음");
    }
}
