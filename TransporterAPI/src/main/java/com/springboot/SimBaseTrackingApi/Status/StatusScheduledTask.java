package com.springboot.SimBaseTrackingApi.Status;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.SimBaseTrackingApi.Authentication.TokenValidator;
import com.springboot.SimBaseTrackingApi.Dao.TrackingDao;
import com.springboot.SimBaseTrackingApi.Entity.TrackingData;

@Component
@Configuration
public class StatusScheduledTask {

        @Autowired
        private TrackingDao trackingDao;

        @Autowired
        private TokenValidator tokenValidator;

        @Autowired
        private StatusGenerator statusGenerator;
    
    // 5 min with initial deplay 1 min
    @Scheduled(fixedRate = 300000, initialDelay = 60000)
    public void ConsentStatus() throws IOException, URISyntaxException{

        //First we are validating the token then updating status of the numbers that are pending and approved
            tokenValidator.validator("validateResource");

            List<TrackingData> listDataPending = trackingDao.findByStatus("PENDING");
            List<TrackingData> listDataApproved = trackingDao.findByStatus("APPROVED");
            List<TrackingData> listData=new ArrayList<TrackingData>();
            listData.addAll(listDataPending);
            listData.addAll(listDataApproved);
            
            for(TrackingData data:listData){
                statusGenerator.ConsentStatus(data);
            }
    }
}
