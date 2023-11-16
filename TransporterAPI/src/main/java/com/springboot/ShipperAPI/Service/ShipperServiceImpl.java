package com.springboot.ShipperAPI.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.springboot.ShipperAPI.Dao.ShipperTransporterEmailDao;
import com.springboot.ShipperAPI.Entity.ShipperTransporterEmail;
import com.springboot.ShipperAPI.Response.ShipperGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.apache.commons.lang3.StringUtils;

import com.springboot.ShipperAPI.Constants.CommonConstants;
import com.springboot.ShipperAPI.Dao.ShipperDao;
import com.springboot.ShipperAPI.Entity.Shipper;
import com.springboot.ShipperAPI.Model.PostShipper;
import com.springboot.ShipperAPI.Model.UpdateShipper;
import com.springboot.ShipperAPI.Response.ShipperCreateResponse;
import com.springboot.ShipperAPI.Response.ShipperUpdateResponse;

import lombok.extern.slf4j.Slf4j;

import com.springboot.TransporterAPI.Dao.TransporterDao;
import com.springboot.TransporterAPI.Entity.Transporter;
import com.springboot.TransporterAPI.Exception.BusinessException;
import com.springboot.TransporterAPI.Exception.EntityNotFoundException;

@Slf4j
@Service
public class ShipperServiceImpl implements ShipperService {

	@Autowired
	ShipperDao shipperdao;

	@Autowired
	TransporterDao transporterdao;
	
	@Autowired
	ShipperTransporterEmailDao shipperTransporterEmailDao;
	@Transactional(rollbackFor = Exception.class)
	@Override
	public ShipperCreateResponse addShipper(PostShipper postshipper) {
		log.info("addShipper service is started");

		String temp="";
		Shipper shipper = new Shipper();
		ShipperCreateResponse response = new ShipperCreateResponse();

		//Optional<Shipper> s = shipperdao.findShipperByPhoneNo(postshipper.getPhoneNo());
		Optional<Shipper> s = shipperdao.findByEmailId(postshipper.getEmailId());
		if (s.isPresent()) {
			response.setShipperId(s.get().getShipperId());
			response.setPhoneNo(s.get().getPhoneNo());
			response.setEmailId(s.get().getEmailId());
			response.setShipperName(s.get().getShipperName());
			response.setShipperLocation(s.get().getShipperLocation());
			response.setCompanyName(s.get().getCompanyName());
			response.setKyc(s.get().getKyc());
			response.setGst(s.get().getGst());
			response.setCompanyStatus(s.get().getCompanyStatus());
			response.setCompanyApproved(s.get().isCompanyApproved());
			response.setAccountVerificationInProgress(s.get().isAccountVerificationInProgress());
			response.setMessage(CommonConstants.ACCOUNT_EXIST);
			response.setTimestamp(s.get().getTimestamp());
			return response;
		}

		temp="shipper:"+UUID.randomUUID();
		shipper.setShipperId(temp);
		response.setShipperId(temp);

		temp=postshipper.getShipperName();
		if (StringUtils.isNotBlank(temp)) {
			shipper.setShipperName(temp.trim());
			response.setShipperName(temp.trim());
		}

		temp=postshipper.getCompanyName();
		if(StringUtils.isNotBlank(temp)) {
			shipper.setCompanyName(temp.trim());
			response.setCompanyName(temp.trim());
		}

		temp=postshipper.getShipperLocation();
		if(StringUtils.isNotBlank(temp)) {
			shipper.setShipperLocation(temp.trim());
			response.setShipperLocation(temp.trim());
		}

		temp=postshipper.getPhoneNo();
		shipper.setPhoneNo(temp);
		response.setPhoneNo(temp);

		temp=postshipper.getEmailId();
		shipper.setEmailId(temp);
		response.setEmailId(temp);

		temp=postshipper.getKyc();
		if(StringUtils.isNotBlank(temp)) {
			shipper.setKyc(temp.trim());
			response.setKyc(temp.trim());
		}

		temp=postshipper.getGst();
		if(StringUtils.isNotBlank(temp)){
			shipper.setGst(temp.trim());
			response.setGst(temp.trim());
		}
		temp=postshipper.getCompanyStatus();
		if(StringUtils.isNotBlank(temp)){
			shipper.setCompanyStatus(temp.trim());
			response.setCompanyStatus(temp.trim());
		}

		shipper.setCompanyApproved(false);
		response.setCompanyApproved(false);

		shipper.setAccountVerificationInProgress(false);
		response.setAccountVerificationInProgress(false);

		shipperdao.save(shipper);
		if(postshipper.getTransporterList()!=null){
			for(ArrayList<String> shipperTransporterEmail:postshipper.getTransporterList()){
				ShipperTransporterEmail object=new ShipperTransporterEmail();
				object.setShipper(shipper);
				object.setEmail(shipperTransporterEmail.get(0));
				object.setName(shipperTransporterEmail.get(1));
				object.setPhoneNo(shipperTransporterEmail.get(2));
				object.setTransporterId(shipperTransporterEmail.get(3));
				shipperTransporterEmailDao.save(object);
			}
			response.setTransporterList(postshipper.getTransporterList());
		}

		log.info("shipper is saved to the database");

		response.setStatus(CommonConstants.PENDING);
		response.setMessage(CommonConstants.APPROVE_REQUEST);
		response.setTimestamp(shipper.getTimestamp());

		log.info("addShipper response is returned");
		return response;

	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	@Override
	public List<Shipper> getShippers(Boolean companyApproved, String phoneNo, String emailId, Integer pageNo) {
		log.info("getShippers service started");
		if(pageNo == null) {
			pageNo = 0;
		}
		Pageable page = PageRequest.of(pageNo, 15,  Sort.Direction.DESC, "timestamp");
		
		if(phoneNo != null) {
			String validate = "^[6-9]\\d{9}$";
			Pattern pattern = Pattern.compile(validate);
			Matcher m = pattern.matcher(phoneNo);
			if(m.matches()) {
				if(shipperdao.findShipperByPhoneNo(phoneNo).isPresent()) {
					List<Shipper> list = List.of(shipperdao.findShipperByPhoneNo(phoneNo).get());
					return list;
				}
				else {
					throw new EntityNotFoundException(Shipper.class, "phoneNo", phoneNo.toString());
				}
			}
			else {
				// MethodArgumentNotValidException
				throw new BusinessException("Invalid mobile number");
			}
			
		}

		if(emailId != null){
			String validate = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}";
			Pattern pattern = Pattern.compile(validate);
			Matcher m = pattern.matcher(emailId);
			if(m.matches()){
				if(shipperdao.findByEmailId(emailId).isPresent()) {
					List<Shipper> list = List.of(shipperdao.findByEmailId(emailId).get());
					return list;
				}
				else {
					throw new EntityNotFoundException(Shipper.class, "emailId", emailId.toString());
				}
			}
			else{
				throw new BusinessException("Invalid email Id");
			}
		}

		if(companyApproved == null) {
			List<Shipper> shipperList = shipperdao.getAll(page);
			//Collections.reverse(shipperList);
			return shipperList;
		} 
		List<Shipper> shipperList = shipperdao.findByCompanyApproved(companyApproved, page);
		//Collections.reverse(shipperList);
		log.info("getShippers response returned");
		return shipperList;
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	@Override
	public ShipperGetResponse getOneShipper(String shipperId) {
		log.info("getOneShipper service is started");
		Optional<Shipper> S = shipperdao.findById(shipperId);
		if(S.isEmpty()) {
			throw new EntityNotFoundException(Shipper.class, "id", shipperId.toString());
		}
		Shipper shipper=S.get();
		ShipperGetResponse shipperGetResponse=new ShipperGetResponse();

		shipperGetResponse.setShipperId(shipper.getShipperId());
		shipperGetResponse.setShipperName(shipper.getShipperName());
		shipperGetResponse.setCompanyName(shipper.getCompanyName());
		shipperGetResponse.setPhoneNo(shipper.getPhoneNo());
		shipperGetResponse.setEmailId(shipper.getEmailId());
		shipperGetResponse.setGst(shipper.getGst());
		shipperGetResponse.setCompanyStatus(shipper.getCompanyStatus());
		shipperGetResponse.setKyc(shipper.getKyc());
		shipperGetResponse.setShipperLocation(shipper.getShipperLocation());
		shipperGetResponse.setCompanyApproved(shipper.isCompanyApproved());
		shipperGetResponse.setAccountVerificationInProgress(shipper.isAccountVerificationInProgress());

		ArrayList<ShipperTransporterEmail> shipperTransporterEmailList=shipperTransporterEmailDao.findByShipperShipperId(S.get().getShipperId());
		ArrayList<ArrayList<String>> emailList=new ArrayList<>();
		for(ShipperTransporterEmail shipperTransporterEmail:shipperTransporterEmailList){
			ArrayList<String> temp=new ArrayList<>();
			temp.add(shipperTransporterEmail.getEmail());
			temp.add(shipperTransporterEmail.getName());
			temp.add(shipperTransporterEmail.getPhoneNo());
			temp.add(shipperTransporterEmail.getTransporterId());
			emailList.add(temp);
		}
		shipperGetResponse.setTransporterList(emailList);

		log.info("getOneShiper response is returned");
		return shipperGetResponse;
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Transporter getTransporterList(String shipperId) {
		log.info("getTransporterList service is started");
		String transporterId="";
		Optional<Shipper> S1 = shipperdao.findById(shipperId);
		if(S1.isEmpty()) {
			throw new EntityNotFoundException(Shipper.class, "id", shipperId.toString());
		}
		Shipper shipper=S1.get();
		Transporter transporter = new Transporter();
	    ArrayList<ShipperTransporterEmail> shipperTransporterEmailList=shipperTransporterEmailDao.findByShipperShipperId(S1.get().getShipperId());
		for(ShipperTransporterEmail shipperTransporterEmail:shipperTransporterEmailList) {
			transporterId = shipperTransporterEmail.getTransporterId(); 
			transporter.setTransporterId(transporterId);
	}
			
		Optional<Transporter> t = transporterdao.findByTransporterId(transporterId);
		if(t.isEmpty()) {
				throw new EntityNotFoundException(Transporter.class,"id",transporterId);
			}
		
		return t.get();
			
			
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ShipperUpdateResponse updateShipper(String shipperId, UpdateShipper updateShipper) {
		log.info("updateShipper service is started");
		ShipperUpdateResponse updateResponse = new ShipperUpdateResponse();
		Shipper shipper = new Shipper();
		Optional<Shipper> S = shipperdao.findById(shipperId);

		if(S.isEmpty()) {
			throw new EntityNotFoundException(Shipper.class, "id",shipperId.toString());
		}

		String temp="";
		shipper = S.get();

		if (updateShipper.getPhoneNo() != null) {			
			throw new BusinessException("Phone no. can't be updated");
		}

		if(updateShipper.getEmailId() != null){
			throw new BusinessException("Email Id can't be updated");
		}

		temp=updateShipper.getShipperName();
		if(StringUtils.isNotBlank(temp)) {
			shipper.setShipperName(temp.trim());
		}
		
		temp=updateShipper.getCompanyName();
		if(StringUtils.isNotBlank(temp)) {
			shipper.setCompanyName(temp.trim());
		}

		temp=updateShipper.getGst();
		if(StringUtils.isNotBlank(temp)){
			shipper.setGst(temp.trim());
		}

		temp=updateShipper.getCompanyStatus();
		if(StringUtils.isNotBlank(temp)){
			shipper.setCompanyStatus(temp.trim());
		}

		temp=updateShipper.getShipperLocation();
		if(StringUtils.isNotBlank(temp)) {
			shipper.setShipperLocation(temp.trim());
		}

		if (updateShipper.getKyc() != null) {
			shipper.setKyc(updateShipper.getKyc());
		}

		if(updateShipper.getCompanyApproved() != null) {
			shipper.setCompanyApproved(updateShipper.getCompanyApproved());
		}

		if(updateShipper.getAccountVerificationInProgress() != null) {
			shipper.setAccountVerificationInProgress(updateShipper.getAccountVerificationInProgress());
		}

		shipperdao.save(shipper);
		if(updateShipper.getTransporterList()!=null){
			shipperTransporterEmailDao.deleteAllByShipper(shipper);
			for(ArrayList<String> shipperTransporterEmail:updateShipper.getTransporterList()){
				ShipperTransporterEmail object=new ShipperTransporterEmail();
				object.setShipper(shipper);
				object.setEmail(shipperTransporterEmail.get(0));
				object.setName(shipperTransporterEmail.get(1));
				object.setPhoneNo(shipperTransporterEmail.get(2));
				object.setTransporterId(shipperTransporterEmail.get(3));
				shipperTransporterEmailDao.save(object);
			}
			updateResponse.setTransporterList(updateShipper.getTransporterList());
		}
		log.info("shipper is upadated and saved to the database");

		updateResponse.setShipperId(shipper.getShipperId());
		updateResponse.setPhoneNo(shipper.getPhoneNo());
		updateResponse.setEmailId(shipper.getEmailId());
		updateResponse.setGst(shipper.getGst());
		updateResponse.setCompanyStatus(shipper.getCompanyStatus());
		updateResponse.setShipperName(shipper.getShipperName());
		updateResponse.setCompanyName(shipper.getCompanyName());
		updateResponse.setShipperLocation(shipper.getShipperLocation());
		updateResponse.setKyc(shipper.getKyc());
		updateResponse.setCompanyApproved(shipper.isCompanyApproved());
		updateResponse.setAccountVerificationInProgress(shipper.isAccountVerificationInProgress());
		updateResponse.setStatus(CommonConstants.SUCCESS);
		updateResponse.setMessage(CommonConstants.UPDATE_SUCCESS);
		updateResponse.setTimestamp(shipper.getTimestamp());

		log.info("updateShipper response is returned");
		return updateResponse;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteShipper(String shipperId) {
		log.info("deleteShipper service is started");
		Optional<Shipper> S = shipperdao.findById(shipperId);

		if( S.isEmpty()) {
			throw new EntityNotFoundException(Shipper.class, "id",shipperId.toString());
		}
		shipperdao.delete(S.get());
		log.info("shipper is deleted in the database");
	}

}
