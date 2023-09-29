package com.springboot.SimBaseTrackingApi.Location;

import java.io.IOException;
import java.net.URISyntaxException;
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
public class LocationScheduledTask {

    @Autowired
    private TrackingDao trackingDao;

    @Autowired
    private LocationGenerator location;

    @Autowired
    private TokenValidator tokenValidator;
    
    // 15 min with initial delay 2 min
    @Scheduled(fixedRate = 900000, initialDelay = 120000)
    public void getList() throws IOException, InterruptedException, URISyntaxException{

        tokenValidator.validator();

        List<TrackingData> listData = trackingDao.findByStatus("APPROVED");

        for(TrackingData data:listData){
            location.TrackingResponse(data);
        }
    }

}
