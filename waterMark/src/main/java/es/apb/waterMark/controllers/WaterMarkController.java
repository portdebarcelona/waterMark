package es.apb.waterMark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.apb.waterMark.AppAddWaterMark;
import es.apb.waterMark.FileWaterRequest;
import es.apb.waterMark.FileWaterResponse;

	

@RestController
class WaterMarkController {

	@Autowired
	private AppAddWaterMark appAddWaterMark;

	@GetMapping("/addMark")
	FileWaterResponse addMark(@RequestBody FileWaterRequest fileBase64) throws Exception {
		return appAddWaterMark.getFile(fileBase64);
	}
	

}
