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

import uce.edu.ec.app.model.Bienes_Estaciones;

public class ExcelBuilderDetalle extends AbstractXlsView {

	private Date name = new Date();

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + "detalle_" + dateFormat.format(name) + ".xls\"");
		@SuppressWarnings("unchecked")
		List<Bienes_Estaciones> bienes_Estaciones = (List<Bienes_Estaciones>) model.get("bienes_Estaciones");
		Sheet sheet = workbook.createSheet("Detalle List");
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
		headerRow.createCell(0).setCellValue("Persona Usa");
		headerRow.getCell(0).setCellStyle(style);
		headerRow.createCell(1).setCellValue("Alta Nueva");
		headerRow.getCell(1).setCellStyle(style);
		headerRow.createCell(2).setCellValue("Alta Anterior");
		headerRow.getCell(2).setCellStyle(style);
		headerRow.createCell(3).setCellValue("Descripcion");
		headerRow.getCell(3).setCellStyle(style);
		headerRow.createCell(4).setCellValue("Marca");
		headerRow.getCell(4).setCellStyle(style);
		headerRow.createCell(5).setCellValue("Modelo");
		headerRow.getCell(5).setCellStyle(style);
		headerRow.createCell(6).setCellValue("Serie");
		headerRow.getCell(6).setCellStyle(style);
		headerRow.createCell(7).setCellValue("Guarda Almacen");
		headerRow.getCell(7).setCellStyle(style);
		headerRow.createCell(8).setCellValue("Causionado");
		headerRow.getCell(8).setCellStyle(style);
		headerRow.createCell(9).setCellValue("Lugar");
		headerRow.getCell(9).setCellStyle(style);
		headerRow.createCell(10).setCellValue("Ubicación");
		headerRow.getCell(10).setCellStyle(style);
		headerRow.createCell(11).setCellValue("Registro");
		headerRow.getCell(11).setCellStyle(style);
		headerRow.createCell(12).setCellValue("Actualización");
		headerRow.getCell(12).setCellStyle(style);

		int row = 1;
		try {
			for (Bienes_Estaciones bien_Estacion : bienes_Estaciones) {
				Row dataRow = sheet.createRow(row++);
				dataRow.createCell(0).setCellValue(bien_Estacion.getBien().getDetalle().getAsignado());
				dataRow.createCell(1).setCellValue(bien_Estacion.getBien().getAlta());
				dataRow.createCell(2).setCellValue(bien_Estacion.getBien().getAnterior());
				dataRow.createCell(3).setCellValue(bien_Estacion.getBien().getDescripcion());
				dataRow.createCell(4).setCellValue(bien_Estacion.getBien().getDetalle().getMarca());
				dataRow.createCell(5).setCellValue(bien_Estacion.getBien().getDetalle().getModelo());
				dataRow.createCell(6).setCellValue(bien_Estacion.getBien().getSerie());
				dataRow.createCell(7).setCellValue(bien_Estacion.getBien().getDetalle().getGuarda_almacen());
				dataRow.createCell(8).setCellValue(bien_Estacion.getBien().getDetalle().getCausionado());
				dataRow.createCell(9).setCellValue(bien_Estacion.getEstacion().getLugar());
				dataRow.createCell(10).setCellValue(bien_Estacion.getEstacion().getUbicacion());
				dataRow.createCell(11).setCellValue(dateFormat.format(bien_Estacion.getRegistro()));
				dataRow.createCell(12).setCellValue(dateFormat.format(bien_Estacion.getActualizacion()));

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}