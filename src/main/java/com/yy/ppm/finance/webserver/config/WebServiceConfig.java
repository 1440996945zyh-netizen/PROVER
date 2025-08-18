package com.yy.ppm.finance.webserver.config;


import com.yy.ppm.finance.mapper.TFdCreditDebitBillMapper;
import com.yy.ppm.finance.mapper.TFdInvoiceMapper;
import com.yy.ppm.finance.webserver.service.impl.InvWebServiceImpl;
import jakarta.xml.ws.Endpoint;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;


@Component
@Getter
@Configuration
public class WebServiceConfig {

    @Resource
    private TFdInvoiceMapper invoiceMapper;
    @Resource
    private TFdCreditDebitBillMapper tFdCreditDebitBillMapper;

    //潍坊港西作业区码头有限公司(散货码头)
    @org.springframework.beans.factory.annotation.Value("${service.url}")
    private String url;

//    @Bean
    public Endpoint endpoint() {
        return Endpoint.publish("http://"+ url +":9093/webService", new InvWebServiceImpl(invoiceMapper,tFdCreditDebitBillMapper));
    }
}

