package com.springboot.EwayBillAPI.SavingEwayBillData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.EwayBillAPI.Authentication.EwayTokenGenerator;
import com.springboot.EwayBillAPI.Dao.EwayBillUserDao;
import com.springboot.EwayBillAPI.Entity.EwayBillUsers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Configuration
public class SavingDataScheduledTask {

    @Autowired
    public EwayBillUserDao userDao;

    @Autowired
    public SavingData savingData;

    @Autowired
    public EwayTokenGenerator ewayTokenGenerator;
    
    //Everyday at 12:05 am and 1:05 am
    @Scheduled(cron="0 05 0-1 ? * ?", zone="IST")
    @PostConstruct
    public void getRecieverList() throws URISyntaxException, IOException {

        String date=LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1).toString();
        Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).minusDays(1));

        List<EwayBillUsers> data = userDao.findByRole("RECIEVER");


        if(data.size()>0){
            for(EwayBillUsers userData: data){
                // First generating the authToken and sek then using it to get details by date
                try{
                    savingData.savingEwayBillData(userData, date, timestamp);
                }catch(Exception e){
                    log.info(e.toString());
                    continue;
                }
            }
        }
    }

    // 6 hour interval and at 11:57 pm, 11:58 pm and 11:59 pm
    @Scheduled(fixedRate = 21600000)
    @Scheduled(cron="0 57-59 23 ? * ?", zone="IST")
    public void getTransporterList() throws URISyntaxException, IOException {

        String date=LocalDate
                .now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("d/M/y"))
                .toString();
        Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        List<EwayBillUsers> data = userDao.findByRole("TRANSPORTER");

        if(data.size()>0){
            for(EwayBillUsers userData: data){
                try{
                    savingData.savingEwayBillData(userData, date, timestamp);
                }catch(Exception e){
                    log.info(e.toString());
                    continue;
                }
            }
        }
    }
}
