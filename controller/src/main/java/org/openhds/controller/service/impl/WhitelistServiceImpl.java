package org.openhds.controller.service.impl;

import org.openhds.controller.service.WhitelistService;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("whitelistService")
public class WhitelistServiceImpl implements WhitelistService {

	private GenericDao genericDao;

    @Autowired
	public WhitelistServiceImpl(GenericDao genericDao) {
		this.genericDao = genericDao;
	}

	@Override
	public boolean isHostIpAddressWhitelisted(String hostIpAddress) {
		Whitelist whitelistRecord = genericDao.findByProperty(Whitelist.class, "address", hostIpAddress);
		if (whitelistRecord == null) {
			return false;
		}

		return true;
	}
}
