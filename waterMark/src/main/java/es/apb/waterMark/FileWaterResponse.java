package es.apb.waterMark;

import java.io.Serializable;

public class FileWaterResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String pdfbase64watermark;
	

	
	public String getPdfbase64watermark() {
		return pdfbase64watermark;
	}



	public void setPdfbase64watermark(String pdfbase64watermark) {
		this.pdfbase64watermark = pdfbase64watermark;
	}



	public FileWaterResponse(String pdfbase64watermark) {
		super();
		this.pdfbase64watermark = pdfbase64watermark;
	}



	public FileWaterResponse() {
	}

}
