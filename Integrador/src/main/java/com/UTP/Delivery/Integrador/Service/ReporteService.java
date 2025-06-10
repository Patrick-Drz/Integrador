package com.UTP.Delivery.Integrador.Service;

import com.UTP.Delivery.Integrador.Model.OrdenVenta;
import com.UTP.Delivery.Integrador.Model.DetalleOrdenVenta;
import com.UTP.Delivery.Integrador.Model.Producto;
import com.UTP.Delivery.Integrador.Model.Oferta;
import com.UTP.Delivery.Integrador.Model.Reclamacion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ReclamacionService reclamacionService;


    // Reporte Ventas
    public ByteArrayOutputStream generarReporteVentasExcel() throws IOException {
        List<OrdenVenta> ordenes = ventaService.getAllOrdenesVenta();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte de Ventas");


        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);

        CellStyle numericStyle = workbook.createCellStyle();
        numericStyle.cloneStyleFrom(dataStyle);
        DataFormat format = workbook.createDataFormat();
        numericStyle.setDataFormat(format.getFormat("#,##0.00"));
        numericStyle.setAlignment(HorizontalAlignment.RIGHT);

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(dataStyle);
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "ID Orden", "Fecha Orden", "Total Orden",
                "ID Usuario", "Nombre Usuario", "Cód. Estudiante", "Correo Usuario",
                "ID Ubicación", "Piso Ubicación", "Aula Ubicación",
                "Tipo Item", "Nombre Item", "Cantidad", "Precio Unitario Item", "Subtotal Item"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (OrdenVenta orden : ordenes) {

            if (orden.getItems().isEmpty()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(orden.getId());
                row.createCell(1).setCellValue(orden.getFechaOrden() != null ? orden.getFechaOrden().format(formatter) : "N/A");
                row.createCell(2).setCellValue(orden.getTotal() != null ? orden.getTotal().doubleValue() : 0.0);

                row.createCell(3).setCellValue(orden.getUsuario() != null ? orden.getUsuario().getId() : null);
                row.createCell(4).setCellValue(orden.getUsuario() != null ? orden.getUsuario().getNombreCompleto() : "N/A");
                row.createCell(5).setCellValue(orden.getUsuario() != null ? orden.getUsuario().getCodigoEstudiante() : "N/A");
                row.createCell(6).setCellValue(orden.getUsuario() != null ? orden.getUsuario().getCorreo() : "N/A");

                row.createCell(7).setCellValue(orden.getUbicacionEntrega() != null ? orden.getUbicacionEntrega().getId() : null);
                row.createCell(8).setCellValue(orden.getUbicacionEntrega() != null ? orden.getUbicacionEntrega().getPiso() : "N/A");
                row.createCell(9).setCellValue(orden.getUbicacionEntrega() != null ? orden.getUbicacionEntrega().getCodigoAula() : "N/A");

                for (int i = 0; i < headers.length; i++) {
                    if (row.getCell(i) == null) {
                        row.createCell(i);
                    }
                    row.getCell(i).setCellStyle(dataStyle);
                }

                row.getCell(2).setCellStyle(numericStyle);
            } else {
                for (DetalleOrdenVenta detalle : orden.getItems()) {
                    Row row = sheet.createRow(rowNum++);

                    Cell cellOrdenId = row.createCell(0); cellOrdenId.setCellValue(orden.getId()); cellOrdenId.setCellStyle(dataStyle);
                    Cell cellFechaOrden = row.createCell(1); cellFechaOrden.setCellValue(orden.getFechaOrden() != null ? orden.getFechaOrden().format(formatter) : "N/A"); cellFechaOrden.setCellStyle(dateStyle);
                    Cell cellTotalOrden = row.createCell(2); cellTotalOrden.setCellValue(orden.getTotal() != null ? orden.getTotal().doubleValue() : 0.0); cellTotalOrden.setCellStyle(numericStyle);

                    Cell cellUserId = row.createCell(3); cellUserId.setCellValue(orden.getUsuario() != null ? orden.getUsuario().getId() : null); cellUserId.setCellStyle(dataStyle);
                    Cell cellUserName = row.createCell(4); cellUserName.setCellValue(orden.getUsuario() != null ? orden.getUsuario().getNombreCompleto() : "N/A"); cellUserName.setCellStyle(dataStyle);
                    Cell cellUserCode = row.createCell(5); cellUserCode.setCellValue(orden.getUsuario() != null ? orden.getUsuario().getCodigoEstudiante() : "N/A"); cellUserCode.setCellStyle(dataStyle);
                    Cell cellUserEmail = row.createCell(6); cellUserEmail.setCellValue(orden.getUsuario() != null ? orden.getUsuario().getCorreo() : "N/A"); cellUserEmail.setCellStyle(dataStyle);

                    Cell cellUbiId = row.createCell(7); cellUbiId.setCellValue(orden.getUbicacionEntrega() != null ? orden.getUbicacionEntrega().getId() : null); cellUbiId.setCellStyle(dataStyle);
                    Cell cellUbiPiso = row.createCell(8); cellUbiPiso.setCellValue(orden.getUbicacionEntrega() != null ? orden.getUbicacionEntrega().getPiso() : "N/A"); cellUbiPiso.setCellStyle(dataStyle);
                    Cell cellUbiAula = row.createCell(9); cellUbiAula.setCellValue(orden.getUbicacionEntrega() != null ? orden.getUbicacionEntrega().getCodigoAula() : "N/A"); cellUbiAula.setCellStyle(dataStyle);

                    String tipoItem = "";
                    String nombreItem = "";
                    if (detalle.getProducto() != null) {
                        tipoItem = "Producto";
                        nombreItem = detalle.getProducto().getNombre();
                    } else if (detalle.getOferta() != null) {
                        tipoItem = "Oferta";
                        nombreItem = detalle.getOferta().getNombreOferta();
                    }
                    Cell cellTipoItem = row.createCell(10); cellTipoItem.setCellValue(tipoItem); cellTipoItem.setCellStyle(dataStyle);
                    Cell cellNombreItem = row.createCell(11); cellNombreItem.setCellValue(nombreItem); cellNombreItem.setCellStyle(dataStyle);
                    Cell cellCantidad = row.createCell(12); cellCantidad.setCellValue(detalle.getCantidad() != null ? detalle.getCantidad() : 0); cellCantidad.setCellStyle(numericStyle);
                    Cell cellPrecioUnitario = row.createCell(13); cellPrecioUnitario.setCellValue(detalle.getPrecioUnitarioAlMomento() != null ? detalle.getPrecioUnitarioAlMomento().doubleValue() : 0.0); cellPrecioUnitario.setCellStyle(numericStyle);
                    Cell cellSubtotalItem = row.createCell(14); cellSubtotalItem.setCellValue(detalle.getSubtotal() != null ? detalle.getSubtotal().doubleValue() : 0.0); cellSubtotalItem.setCellStyle(numericStyle);
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

    // Reporte Reclamaciones
    public ByteArrayOutputStream generarReporteReclamacionesExcel() throws IOException {
        List<Reclamacion> reclamaciones = reclamacionService.getAllReclamaciones();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte de Reclamaciones");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(dataStyle);
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "ID Reclamación", "Nombre Completo", "Correo",
                "Tipo de Reclamación", "Descripción", "Fecha Creación"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Reclamacion reclamacion : reclamaciones) {
            Row row = sheet.createRow(rowNum++);

            Cell cellId = row.createCell(0); cellId.setCellValue(reclamacion.getId()); cellId.setCellStyle(dataStyle);
            Cell cellNombre = row.createCell(1); cellNombre.setCellValue(reclamacion.getNombreCompleto()); cellNombre.setCellStyle(dataStyle);
            Cell cellCorreo = row.createCell(2); cellCorreo.setCellValue(reclamacion.getCorreo()); cellCorreo.setCellStyle(dataStyle);
            Cell cellTipo = row.createCell(3); cellTipo.setCellValue(reclamacion.getTipoReclamacion()); cellTipo.setCellStyle(dataStyle);
            Cell cellDescripcion = row.createCell(4); cellDescripcion.setCellValue(reclamacion.getDescripcion()); cellDescripcion.setCellStyle(dataStyle);
            Cell cellFechaCreacion = row.createCell(5); cellFechaCreacion.setCellValue(reclamacion.getFechaCreacion() != null ? reclamacion.getFechaCreacion().format(formatter) : "N/A"); cellFechaCreacion.setCellStyle(dateStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

}