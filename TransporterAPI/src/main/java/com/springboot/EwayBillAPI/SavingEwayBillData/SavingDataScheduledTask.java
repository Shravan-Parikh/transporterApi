package com.springboot.EwayBillAPI.SavingEwayBillData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.EwayBillAPI.Authentication.EwayTokenGenerator;
import com.springboot.EwayBillAPI.Dao.EwayBillUserDao;
import com.springboot.EwayBillAPI.Entity.EwayBillUsers;

@Component
@Configuration
public class SavingDataScheduledTask {

    @Autowired
    public EwayBillUserDao credentialsDao;

    @Autowired
    public SavingData savingData;

    @Autowired
    public EwayTokenGenerator ewayTokenGenerator;
    
    //Everyday at 12:05 am and 1:05 am
    @Scheduled(cron="0 05 0-1 ? * ?", zone="IST")
    @PostConstruct
    public void getList() throws URISyntaxException, IOException {

        List<EwayBillUsers> data = credentialsDao.findAll();


        if(data.size()>0){
            for(EwayBillUsers credentialData: data){
                // First generating the authToken and sek then using it to get details by date
                try{
                ewayTokenGenerator.generateToken(credentialData.getUsername(), credentialData.getPassword(), credentialData.getGstin());
                savingData.savingEwayBillData(credentialData,ewayTokenGenerator.getAuthToken(), ewayTokenGenerator.getSek());
                }catch(Exception e){
                    continue;
                }
            }
        }
    }
}
