package com.UTP.Delivery.altumcaffe.Service;

import com.UTP.Delivery.altumcaffe.Model.OrdenVenta;
import com.UTP.Delivery.altumcaffe.Model.DetalleOrdenVenta;
import com.UTP.Delivery.altumcaffe.Model.Reclamacion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ReclamacionService reclamacionService;

    public ByteArrayOutputStream generarReporteVentasExcel() throws IOException {
        List<OrdenVenta> ordenes = ventaService.getAllOrdenesVenta();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            aplicarBordes(headerStyle);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            aplicarBordes(dataStyle);

            CellStyle currencyStyle = workbook.createCellStyle();
            aplicarBordes(currencyStyle);
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("\"S/\" #,##0.00")); 
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle dateStyle = workbook.createCellStyle();
            aplicarBordes(dateStyle);
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            XSSFSheet sheetDetalle = workbook.createSheet("Detalle de Ventas");

            Row headerRow = sheetDetalle.createRow(0);
            String[] headers = {
                    "ID Orden", "Fecha Orden", "Total Orden",
                    "Usuario", "Correo",
                    "Tipo Item", "Nombre Item", "Cantidad", "Precio Unit.", "Subtotal"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            
            BigDecimal granTotalVentas = BigDecimal.ZERO;
            int totalItemsVendidos = 0;
            Map<String, Integer> ventasPorProductoCantidad = new HashMap<>();
            Map<String, BigDecimal> ventasPorProductoIngreso = new HashMap<>();

            for (OrdenVenta orden : ordenes) {
                granTotalVentas = granTotalVentas.add(orden.getTotal());

                if (orden.getItems().isEmpty()) {
                    Row row = sheetDetalle.createRow(rowNum++);
                    llenarFilaBasica(row, orden, formatter, dataStyle, dateStyle, currencyStyle);
                } else {
                    for (DetalleOrdenVenta detalle : orden.getItems()) {
                        Row row = sheetDetalle.createRow(rowNum++);
                        
                        llenarFilaBasica(row, orden, formatter, dataStyle, dateStyle, currencyStyle);

                        String tipoItem = "";
                        String nombreItem = "Desconocido";
                        if (detalle.getProducto() != null) {
                            tipoItem = "Producto";
                            nombreItem = detalle.getProducto().getNombre();
                        } else if (detalle.getOferta() != null) {
                            tipoItem = "Oferta";
                            nombreItem = detalle.getOferta().getNombreOferta();
                        }

                        totalItemsVendidos += detalle.getCantidad();
                        ventasPorProductoCantidad.put(nombreItem, ventasPorProductoCantidad.getOrDefault(nombreItem, 0) + detalle.getCantidad());
                        BigDecimal subtotalActual = detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO;
                        ventasPorProductoIngreso.put(nombreItem, ventasPorProductoIngreso.getOrDefault(nombreItem, BigDecimal.ZERO).add(subtotalActual));

                        crearCelda(row, 5, tipoItem, dataStyle);
                        crearCelda(row, 6, nombreItem, dataStyle);
                        crearCeldaNumerica(row, 7, detalle.getCantidad(), dataStyle);
                        crearCeldaMoneda(row, 8, detalle.getPrecioUnitarioAlMomento(), currencyStyle);
                        crearCeldaMoneda(row, 9, subtotalActual, currencyStyle);
                    }
                }
            }

            for (int i = 0; i < headers.length; i++) sheetDetalle.autoSizeColumn(i);

            XSSFSheet sheetDashboard = workbook.createSheet("Dashboard - Resumen");
            
            Row tituloKpi = sheetDashboard.createRow(1);
            Cell celdaTitulo = tituloKpi.createCell(1);
            celdaTitulo.setCellValue("RESUMEN GENERAL DE VENTAS");
            celdaTitulo.setCellStyle(headerStyle);
            sheetDashboard.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));

            Row rowTotalIngresos = sheetDashboard.createRow(2);
            rowTotalIngresos.createCell(1).setCellValue("Ingresos Totales:");
            Cell cellIngresoValor = rowTotalIngresos.createCell(2);
            cellIngresoValor.setCellValue(granTotalVentas.doubleValue());
            cellIngresoValor.setCellStyle(currencyStyle);

            Row rowTotalOrdenes = sheetDashboard.createRow(3);
            rowTotalOrdenes.createCell(1).setCellValue("Total de Órdenes:");
            rowTotalOrdenes.createCell(2).setCellValue(ordenes.size());
            
            Row rowTotalItems = sheetDashboard.createRow(4);
            rowTotalItems.createCell(1).setCellValue("Items Vendidos:");
            rowTotalItems.createCell(2).setCellValue(totalItemsVendidos);

            int rowTableStart = 7;
            Row headerTable = sheetDashboard.createRow(rowTableStart);
            String[] headersDash = {"Producto / Oferta", "Unidades Vendidas", "Ingresos Generados"};
            for (int i = 0; i < headersDash.length; i++) {
                Cell c = headerTable.createCell(i + 1);
                c.setCellValue(headersDash[i]);
                c.setCellStyle(headerStyle);
            }

            int currentRow = rowTableStart + 1;
            for (Map.Entry<String, Integer> entry : ventasPorProductoCantidad.entrySet()) {
                String prodName = entry.getKey();
                Integer cantidad = entry.getValue();
                BigDecimal ingreso = ventasPorProductoIngreso.get(prodName);

                Row r = sheetDashboard.createRow(currentRow++);
                crearCelda(r, 1, prodName, dataStyle);
                crearCeldaNumerica(r, 2, cantidad, dataStyle);
                crearCeldaMoneda(r, 3, ingreso, currencyStyle);
            }
            
            sheetDashboard.setColumnWidth(1, 8000); 
            sheetDashboard.setColumnWidth(2, 4000);
            sheetDashboard.setColumnWidth(3, 5000);

            if (currentRow > rowTableStart + 1) { 
                XSSFDrawing drawing = sheetDashboard.createDrawingPatriarch();
                
                XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 2, 15, 20);

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("Unidades Vendidas por Producto");
                chart.setTitleOverlay(false);

                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.BOTTOM);

                XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
                bottomAxis.setTitle("Productos");
                XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
                leftAxis.setTitle("Cantidad");

                XDDFDataSource<String> xs = XDDFDataSourcesFactory.fromStringCellRange(sheetDashboard, 
                        new CellRangeAddress(rowTableStart + 1, currentRow - 1, 1, 1));
                
                XDDFNumericalDataSource<Double> ys = XDDFDataSourcesFactory.fromNumericCellRange(sheetDashboard, 
                        new CellRangeAddress(rowTableStart + 1, currentRow - 1, 2, 2));

                XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
                data.setBarDirection(BarDirection.BAR); 
                
                XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(xs, ys);
                series.setTitle("Ventas", null);
                
                chart.plot(data);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        }
    }

    private void aplicarBordes(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
    }

    private void llenarFilaBasica(Row row, OrdenVenta orden, DateTimeFormatter fmt, CellStyle txt, CellStyle date, CellStyle money) {
        crearCeldaNumerica(row, 0, orden.getId(), txt);
        crearCelda(row, 1, orden.getFechaOrden() != null ? orden.getFechaOrden().format(fmt) : "", date);
        crearCeldaMoneda(row, 2, orden.getTotal(), money);
        
        String usuario = orden.getUsuario() != null ? orden.getUsuario().getNombreCompleto() : "N/A";
        String correo = orden.getUsuario() != null ? orden.getUsuario().getCorreo() : "N/A";
        crearCelda(row, 3, usuario, txt);
        crearCelda(row, 4, correo, txt);
    }

    private void crearCelda(Row row, int col, String valor, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(valor);
        c.setCellStyle(style);
    }

    private void crearCeldaNumerica(Row row, int col, Number valor, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(valor.doubleValue());
        c.setCellStyle(style);
    }

    private void crearCeldaMoneda(Row row, int col, BigDecimal valor, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(valor != null ? valor.doubleValue() : 0.0);
        c.setCellStyle(style);
    }

    public ByteArrayOutputStream generarReporteReclamacionesExcel() throws IOException {
        List<Reclamacion> reclamaciones = reclamacionService.getAllReclamaciones();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            aplicarBordes(headerStyle);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            aplicarBordes(dataStyle);

            CellStyle dateStyle = workbook.createCellStyle();
            aplicarBordes(dateStyle);
            dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            Sheet sheetDetalle = workbook.createSheet("Detalle de Reclamaciones");
            Row headerRow = sheetDetalle.createRow(0);
            String[] headers = {"ID", "Nombre Completo", "Correo", "Tipo", "Descripción", "Fecha"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            Map<String, Integer> conteoPorTipo = new HashMap<>();

            for (Reclamacion reclamacion : reclamaciones) {
                String tipo = reclamacion.getTipoReclamacion();
                if(tipo == null) tipo = "OTROS";
                conteoPorTipo.put(tipo, conteoPorTipo.getOrDefault(tipo, 0) + 1);

                Row row = sheetDetalle.createRow(rowNum++);
                crearCeldaNumerica(row, 0, reclamacion.getId(), dataStyle);
                
                String nombre = (reclamacion.getUsuario() != null) ? reclamacion.getUsuario().getNombreCompleto() : "Desconocido";
                String correo = (reclamacion.getUsuario() != null) ? reclamacion.getUsuario().getCorreo() : "Desconocido";
                
                crearCelda(row, 1, nombre, dataStyle);
                crearCelda(row, 2, correo, dataStyle);
                crearCelda(row, 3, tipo, dataStyle);
                crearCelda(row, 4, reclamacion.getDescripcion(), dataStyle);
                crearCelda(row, 5, reclamacion.getFechaCreacion() != null ? reclamacion.getFechaCreacion().format(formatter) : "", dateStyle);
            }

            for (int i = 0; i < headers.length; i++) sheetDetalle.autoSizeColumn(i);

            XSSFSheet sheetDashboard = workbook.createSheet("Dashboard - Resumen");
            
            Row tituloKpi = sheetDashboard.createRow(1);
            Cell celdaTitulo = tituloKpi.createCell(1);
            celdaTitulo.setCellValue("ESTADÍSTICAS DE ATENCIÓN AL CLIENTE");
            celdaTitulo.setCellStyle(headerStyle);
            sheetDashboard.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));

            Row rowTotal = sheetDashboard.createRow(2);
            rowTotal.createCell(1).setCellValue("Total de Casos Recibidos:");
            Cell cellTotalVal = rowTotal.createCell(2);
            cellTotalVal.setCellValue(reclamaciones.size());
            cellTotalVal.setCellStyle(headerStyle);

            int rowTableStart = 5;
            Row headerTable = sheetDashboard.createRow(rowTableStart);
            Cell c1 = headerTable.createCell(1); c1.setCellValue("Tipo de Solicitud"); c1.setCellStyle(headerStyle);
            Cell c2 = headerTable.createCell(2); c2.setCellValue("Cantidad"); c2.setCellStyle(headerStyle);

            int currentRow = rowTableStart + 1;
            for (Map.Entry<String, Integer> entry : conteoPorTipo.entrySet()) {
                Row r = sheetDashboard.createRow(currentRow++);
                crearCelda(r, 1, entry.getKey(), dataStyle);
                crearCeldaNumerica(r, 2, entry.getValue(), dataStyle);
            }

            sheetDashboard.setColumnWidth(1, 6000);
            sheetDashboard.setColumnWidth(2, 3000);

            if (currentRow > rowTableStart + 1) {
                XSSFDrawing drawing = sheetDashboard.createDrawingPatriarch();
                XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 4, 2, 14, 18);

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("Distribución por Tipo de Caso");
                chart.setTitleOverlay(false);

                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.BOTTOM);

                XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
                bottomAxis.setTitle("Tipo");
                XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
                leftAxis.setTitle("Frecuencia");

                XDDFDataSource<String> xs = XDDFDataSourcesFactory.fromStringCellRange(sheetDashboard,
                        new CellRangeAddress(rowTableStart + 1, currentRow - 1, 1, 1));
                XDDFNumericalDataSource<Double> ys = XDDFDataSourcesFactory.fromNumericCellRange(sheetDashboard,
                        new CellRangeAddress(rowTableStart + 1, currentRow - 1, 2, 2));

                XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
                data.setBarDirection(BarDirection.BAR);
                
                XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(xs, ys);
                series.setTitle("Casos", null);
                
                chart.plot(data);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        }
    }
}