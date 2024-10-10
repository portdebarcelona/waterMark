package es.apb.waterMark;

import java.io.Serializable;

public class FileWaterRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String pdfbase64;
	private String testwatermark;
	




	public String getPdfbase64() {
		return pdfbase64;
	}


	public void setPdfbase64(String pdfbase64) {
		this.pdfbase64 = pdfbase64;
	}


	public String getTestwatermark() {
		return testwatermark;
	}


	public void setTestwatermark(String testwatermark) {
		this.testwatermark = testwatermark;
	}


	public FileWaterRequest() {
	}

}
