package es.apb.waterMark;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class AppAddWaterMark {
	
	
    @Autowired
	private Environment property;
  
  /**
   * 
   * @return
   */
  private static PDExtendedGraphicsState getPDExtendedGraphicsState () {
    PDExtendedGraphicsState gs = new PDExtendedGraphicsState();  
    gs.setNonStrokingAlphaConstant(0.25f);
    gs.setStrokingAlphaConstant(0.25f);
    gs.setBlendMode(BlendMode.MULTIPLY);
   // gs.setOverprintMode(0);
    return gs;
  }
  
  /**
   * 
   * @param cs
   * @param x
   * @param y
   * @param text
   * @throws IOException 
   */
  private static void pintarLineaTexto(PDPageContentStream cs, float x, float y, String text) throws IOException {
    cs.beginText();
    cs.newLineAtOffset(x, y);
    cs.newLine();
    cs.showText(text);
    cs.endText();
  }

  public static PDDocument getPDDocumentOverlay(String text) throws IOException {
    try (PDDocument doc = new PDDocument()) {
      PDPage page = new PDPage();
      doc.addPage(page);

      PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);
      try (PDPageContentStream cs = new PDPageContentStream(doc, page,
          PDPageContentStream.AppendMode.APPEND, true, true))
      // use this long constructor when working on existing PDFs
      {
        float fontHeight = 48;

        float width = page.getMediaBox().getWidth();
        float height = page.getMediaBox().getHeight();
        int rotation = page.getRotation();
        switch (rotation) {
        case 90:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(90), height, 0));
          break;
        case 180:
          cs.transform(Matrix.getRotateInstance(Math.toRadians(180), width, height));
          break;
        case 270:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));
          break;
        default:
          break;
        }

        float stringWidth = font.getStringWidth(text) / 1000 * fontHeight;
        float diagonalLength = (float) Math.sqrt(width * width + height * height);
        float angle = (float) Math.atan2(height, width);
        float x = (diagonalLength - stringWidth) / 2; // "horizontal" position in rotated world
        float y = -fontHeight / 4; // 4 is a trial-and-error thing, this lowers the text a bit
        cs.transform(Matrix.getRotateInstance(angle, 0, 0));

        cs.setFont(font, fontHeight);
        // cs.setRenderingMode(RenderingMode.STROKE); // for "hollow" effect
        
        cs.setGraphicsStateParameters(AppAddWaterMark.getPDExtendedGraphicsState());

        // some API weirdness here. When int, range is 0..255.
        // when float, this would be 0..1f
        // cs.setNonStrokingColor(255, 0, 0);
        // cs.setStrokingColor(255, 0, 0);

        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.newLine();
        cs.showText(text);
        cs.endText();
      }
      return doc;
    }
  }

  /**
   * 
   * @param doc
   * @param text
   * @return
   * @throws IOException
   */
  public static PDDocument addWaterMarkDiagonal(PDDocument doc, String text) throws IOException {

    PDPageTree paginas = doc.getPages();
    PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);

    for (int i = 0; i < paginas.getCount(); i++) {
      PDPage page = paginas.get(i);

      try (PDPageContentStream cs = new PDPageContentStream(doc, page,
          PDPageContentStream.AppendMode.APPEND, true, true))
      // use this long constructor when working on existing PDFs
      {
        float fontHeight = 48;

        float width = page.getMediaBox().getWidth();
        float height = page.getMediaBox().getHeight();
        int rotation = page.getRotation();
        switch (rotation) {
        case 90:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(90), height, 0));
          break;
        case 180:
          cs.transform(Matrix.getRotateInstance(Math.toRadians(180), width, height));
          break;
        case 270:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));
          break;
        default:
          break;
        }

        float stringWidth = font.getStringWidth(text) / 1000 * fontHeight;
        float diagonalLength = (float) Math.sqrt(width * width + height * height);
        float angle = (float) Math.atan2(height, width);
        float x = (diagonalLength - stringWidth) / 2; // "horizontal" position in rotated world
        float y = -fontHeight / 4; // 4 is a trial-and-error thing, this lowers the text a bit
        cs.transform(Matrix.getRotateInstance(angle, 0, 0));

        cs.setFont(font, fontHeight);
        cs.setGraphicsStateParameters(AppAddWaterMark.getPDExtendedGraphicsState());
        
        Color color = new Color(0, 0, 1f); //, 0.1f);
        cs.setNonStrokingColor(color);
        
        pintarLineaTexto(cs, x, y, text);
        
      }
    }

    return doc;
  }
  
  /**
   * 
   * @param doc
   * @param text
   * @return
   * @throws IOException
   */
  public static PDDocument addWaterMarkDiagonalRepeated(PDDocument doc, String text) throws IOException {

    PDPageTree paginas = doc.getPages();
    PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);

    for (int i = 0; i < paginas.getCount(); i++) {
      PDPage page = paginas.get(i);

      try (PDPageContentStream cs = new PDPageContentStream(doc, page,
          PDPageContentStream.AppendMode.APPEND, true, true))
      // use this long constructor when working on existing PDFs
      {
        float fontHeight = 20;

        float width = page.getMediaBox().getWidth();
        float height = page.getMediaBox().getHeight();
        int rotation = page.getRotation();
        switch (rotation) {
        case 90:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(90), height, 0));
          break;
        case 180:
          cs.transform(Matrix.getRotateInstance(Math.toRadians(180), width, height));
          break;
        case 270:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));
          break;
        default:
          break;
        }

        float stringWidth = font.getStringWidth(text) / 1000 * fontHeight;
        float diagonalLength = (float) Math.sqrt(width * width + height * height);
        float angle = (float) Math.atan2(height, width);
        //float x = (diagonalLength - stringWidth) / 2; // "horizontal" position in rotated world
        float x = fontHeight;
        float y = -fontHeight / 4; // 4 is a trial-and-error thing, this lowers the text a bit
        cs.transform(Matrix.getRotateInstance(angle, 0, 0));

        cs.setFont(font, fontHeight);
        cs.setGraphicsStateParameters(AppAddWaterMark.getPDExtendedGraphicsState());
        
        Color color = new Color(0, 0, 1f, 1f);
        cs.setNonStrokingColor(color);
        
        int separacion = 4; //Separación entre líneas 
        
        //Montamos la linea repitiendo el texto
        String linea = text;               
        while ( stringWidth < diagonalLength ) {
          linea = linea + String.join("", Collections.nCopies(separacion/2, " ")) + text;
          stringWidth = font.getStringWidth(linea) / 1000 * fontHeight;
        }
        
        pintarLineaTexto(cs, x, y, linea);
        
      }
    }

    return doc;
  }
  
  /**
   * 
   * @param doc
   * @param text
   * @return
   * @throws IOException
   */
  public static PDDocument addWaterMarkRepetitivo(PDDocument doc, String text) throws IOException {

    PDPageTree paginas = doc.getPages();
    PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);

    for (int i = 0; i < paginas.getCount(); i++) {
      PDPage page = paginas.get(i);

      try (PDPageContentStream cs = new PDPageContentStream(doc, page,
          PDPageContentStream.AppendMode.APPEND, true, true))
      // use this long constructor when working on existing PDFs
      {
        float fontHeight = 10;

        float width = page.getMediaBox().getWidth();
        float height = page.getMediaBox().getHeight();
        int rotation = page.getRotation();
        switch (rotation) {
        case 90:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(90), height, 0));
          break;
        case 180:
          cs.transform(Matrix.getRotateInstance(Math.toRadians(180), width, height));
          break;
        case 270:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));
          break;
        default:
          break;
        }
        
        float diagonalLength = (float) Math.sqrt(width * width + height * height);
        //45 grados
        cs.transform(Matrix.getRotateInstance(180/Math.PI, 0, 0));
        //Esto de abajo sería el ángulo real de la página
        //float angle = (float) Math.atan2(height, width);        
        //cs.transform(Matrix.getRotateInstance(angle, 0, 0));
        
        int separacion = 10; //Separación entre líneas        
        int lineas = Math.round(diagonalLength / fontHeight*separacion); //Total líneas a montar en la marca de agua
      
        String[] textoTotal = new String[lineas];
     
        //Montamos la linea repitiendo el texto
        String linea = text;               
        float stringWidth = font.getStringWidth(linea) / 1000 * fontHeight;   
        while ( stringWidth < diagonalLength ) {
          linea = linea + String.join("", Collections.nCopies(separacion/2, " ")) + text;
          stringWidth = font.getStringWidth(linea) / 1000 * fontHeight;
        }
        
        //Montamos el array de líneas a pintar 
        for( int l=0;l<lineas;l++ ) {
          if(l%2==0) textoTotal[l] = linea;
          else textoTotal[l] = String.join("", Collections.nCopies(separacion/2, " ")) + linea;
        }
          
        float x = 0; 
        float y = height-fontHeight; 
        cs.setFont(font, fontHeight);

        cs.setGraphicsStateParameters(AppAddWaterMark.getPDExtendedGraphicsState());

        //Poner 1f en el color que se quiera
        Color color = new Color(0, 0, 0, 0.1f);
        cs.setNonStrokingColor(color);

        //Pintamos las líneas en la página
        for(String line : textoTotal) {          
          pintarLineaTexto(cs, x, y, line);

          //Separamos el texto separación lineas          
          y -= fontHeight*separacion;
        }
      }
    }

    return doc;
  }
  
  /**
   * 
   * @param doc
   * @param text
   * @return
   * @throws IOException
   */
  public static PDDocument addWaterMarkCabeceraPie(PDDocument doc, String text) throws IOException {

    PDPageTree paginas = doc.getPages();
    PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);
    float fontHeightDiagonal = 48;
    float fontHeightPieCabecera = 12;
    
    Color color = new Color(0, 0, 1f, 0.1f);

    for (int i = 0; i < paginas.getCount(); i++) {
      PDPage page = paginas.get(i);
      float width = page.getMediaBox().getWidth();
      float height = page.getMediaBox().getHeight();

      try (PDPageContentStream cs = new PDPageContentStream(doc, page,
          PDPageContentStream.AppendMode.APPEND, true, true))
      {
                
        int rotation = page.getRotation();
        switch (rotation) {
        case 90:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(90), height, 0));
          break;
        case 180:
          cs.transform(Matrix.getRotateInstance(Math.toRadians(180), width, height));
          break;
        case 270:
          width = page.getMediaBox().getHeight();
          height = page.getMediaBox().getWidth();
          cs.transform(Matrix.getRotateInstance(Math.toRadians(270), 0, width));
          break;
        default:
          break;
        }

        float stringWidth = font.getStringWidth(text) / 1000 * fontHeightDiagonal;
        float diagonalLength = (float) Math.sqrt(width * width + height * height);
        float angle = (float) Math.atan2(height, width);
        float x = (diagonalLength - stringWidth) / 2; // "horizontal" position in rotated world
        float y = -fontHeightDiagonal / 4; // 4 is a trial-and-error thing, this lowers the text a bit
        cs.transform(Matrix.getRotateInstance(angle, 0, 0));

        cs.setFont(font, fontHeightDiagonal);
        cs.setGraphicsStateParameters(AppAddWaterMark.getPDExtendedGraphicsState());        
        cs.setNonStrokingColor(color);

        pintarLineaTexto(cs, x, y, text);
      }
            
      try (PDPageContentStream cs = new PDPageContentStream(doc, page,
          PDPageContentStream.AppendMode.APPEND, true, true))
      {        
      
        //Cabecera
        float stringWidth = font.getStringWidth(text) / 1000 * fontHeightPieCabecera;
        float x = width - stringWidth - fontHeightPieCabecera*4; 
        float y = height - fontHeightPieCabecera*2;

        cs.setFont(font, fontHeightPieCabecera);
        cs.setGraphicsStateParameters(AppAddWaterMark.getPDExtendedGraphicsState());        
        cs.setNonStrokingColor(color);

        pintarLineaTexto(cs, x, y, text);
        
        //Pie
        x = fontHeightPieCabecera*4; 
        y = fontHeightPieCabecera*2;
        
        pintarLineaTexto(cs, x, y, text);
        
      }

    }
    return doc;
  }

  
  public FileWaterResponse getFile(FileWaterRequest fileBase64) throws Exception {

	  String fileMark = null;
	  PDDocument realDoc = null;
	  ByteArrayOutputStream baos = null;
	  FileWaterResponse response = new FileWaterResponse();
	  try {

		  byte[] decode = Base64.getDecoder().decode(fileBase64.getPdfbase64());

		  realDoc = Loader.loadPDF(decode);
		  baos = new ByteArrayOutputStream();

		  // Diagonal - repetit
		  realDoc = Loader.loadPDF(decode);
		  PDDocument overlayDocDiagonalRepetit = addWaterMarkDiagonalRepeated(realDoc,fileBase64.getTestwatermark());
		  overlayDocDiagonalRepetit.save(baos);
		  //Comprobacion en local del pdf firmado
		  //overlayDocDiagonalRepetit.save("C:\\temp\\MarcaDeAgua\\resultDiagonal.pdf");
		  overlayDocDiagonalRepetit.close();
		  fileMark = Base64.getEncoder().encodeToString(baos.toByteArray());
		  response.setPdfbase64watermark(fileMark);

	  } catch (Exception err) {
		  throw err;
	  }
	  finally {
		  try {
			  if(realDoc != null) realDoc.close();
			  if(baos != null) baos.close();
		  } catch (IOException e) {
			//e.printStackTrace();
		}
	  }

	  return response;
  }
}
