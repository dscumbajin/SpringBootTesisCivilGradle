package uce.edu.ec.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import uce.edu.ec.app.model.Bien;

public class ExcelBuilder extends AbstractXlsView {

	private Date name = new Date();

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + "bienes_" + dateFormat.format(name) + ".xls\"");

		@SuppressWarnings("unchecked")
		List<Bien> bienes = (List<Bien>) model.get("bienes");
		Sheet sheet = workbook.createSheet("Bienes List");
		sheet.setDefaultColumnWidth(15);
		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		style.setFillForegroundColor(HSSFColor.BLUE.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("ID");
		headerRow.getCell(0).setCellStyle(style);
		headerRow.createCell(1).setCellValue("Alta Nueva");
		headerRow.getCell(1).setCellStyle(style);
		headerRow.createCell(2).setCellValue("Alta Anterior");
		headerRow.getCell(2).setCellStyle(style);
		headerRow.createCell(3).setCellValue("Descripcion");
		headerRow.getCell(3).setCellStyle(style);
		headerRow.createCell(4).setCellValue("Serie");
		headerRow.getCell(4).setCellStyle(style);
		headerRow.createCell(5).setCellValue("Fecha de Ingreso");
		headerRow.getCell(5).setCellStyle(style);
		headerRow.createCell(6).setCellValue("Costo");
		headerRow.getCell(6).setCellStyle(style);
		headerRow.createCell(7).setCellValue("Vida Util");
		headerRow.getCell(7).setCellStyle(style);
		headerRow.createCell(8).setCellValue("Depreciacion");
		headerRow.getCell(8).setCellStyle(style);
		headerRow.createCell(9).setCellValue("Fecha fin de garantia");
		headerRow.getCell(9).setCellStyle(style);
		headerRow.createCell(10).setCellValue("Color");
		headerRow.getCell(10).setCellStyle(style);
		headerRow.createCell(11).setCellValue("Material");
		headerRow.getCell(11).setCellStyle(style);
		headerRow.createCell(12).setCellValue("Asignado");
		headerRow.getCell(12).setCellStyle(style);
		headerRow.createCell(13).setCellValue("Marca");
		headerRow.getCell(13).setCellStyle(style);
		headerRow.createCell(14).setCellValue("Modelo");
		headerRow.getCell(14).setCellStyle(style);
		headerRow.createCell(15).setCellValue("Estado");
		headerRow.getCell(15).setCellStyle(style);
		headerRow.createCell(16).setCellValue("Estatus");
		headerRow.getCell(16).setCellStyle(style);
		headerRow.createCell(17).setCellValue("Tipo");
		headerRow.getCell(17).setCellStyle(style);
		headerRow.createCell(18).setCellValue("Guarda Almacen");
		headerRow.getCell(18).setCellStyle(style);
		headerRow.createCell(19).setCellValue("Causionado");
		headerRow.getCell(19).setCellStyle(style);

		int row = 1;
		try {
			for (Bien bien : bienes) {
				Row dataRow = sheet.createRow(row++);
				dataRow.createCell(0).setCellValue(bien.getId());
				dataRow.createCell(1).setCellValue(bien.getAlta());
				dataRow.createCell(2).setCellValue(bien.getAnterior());
				dataRow.createCell(3).setCellValue(bien.getDescripcion());
				dataRow.createCell(4).setCellValue(bien.getSerie());
				dataRow.createCell(5).setCellValue(dateFormat.format(bien.getFecha_ingreso()));
				dataRow.createCell(6).setCellValue(bien.getCosto());
				dataRow.createCell(7).setCellValue(bien.getVida_util());
				dataRow.createCell(8).setCellValue(bien.getDepreciacion());
				dataRow.createCell(9).setCellValue(dateFormat.format(bien.getGarantia()));
				dataRow.createCell(10).setCellValue(bien.getColor());
				dataRow.createCell(11).setCellValue(bien.getMaterial());
				dataRow.createCell(12).setCellValue(bien.getDetalle().getAsignado());
				dataRow.createCell(13).setCellValue(bien.getDetalle().getMarca());
				dataRow.createCell(14).setCellValue(bien.getDetalle().getModelo());
				dataRow.createCell(15).setCellValue(bien.getDetalle().getEstado());
				dataRow.createCell(16).setCellValue(bien.getDetalle().getEstatus());
				dataRow.createCell(17).setCellValue(bien.getDetalle().getTipo());
				dataRow.createCell(18).setCellValue(bien.getDetalle().getGuarda_almacen());
				dataRow.createCell(19).setCellValue(bien.getDetalle().getCausionado());

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
