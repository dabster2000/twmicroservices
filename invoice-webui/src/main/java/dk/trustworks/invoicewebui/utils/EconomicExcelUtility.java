package dk.trustworks.invoicewebui.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dk.trustworks.invoicewebui.model.ExcelExpenseType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

@Component
public class EconomicExcelUtility {

    private static final Logger log = LoggerFactory.getLogger(EconomicExcelUtility.class);

    private static Table<LocalDate, ExcelExpenseType, Double> weightedGraph = HashBasedTable.create();

    public Table<LocalDate, ExcelExpenseType, Double> getExpenses(byte[] excelFile) {
        log.info("EconomicExcelUtility.getExpenses");
        log.info("excelFile = [" + Arrays.toString(excelFile) + "]");
        try {
            //InputStream excelFile = EconomicExcelUtility.class.getResourceAsStream(filename);
            Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelFile));
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet datatypeSheet = workbook.getSheetAt(0);
            ExcelExpenseType rowName = null;
            for (Row row : datatypeSheet) {
                for (Cell cell : row) {
                    if(cell.getColumnIndex() == 0 || cell.getColumnIndex() == 3) continue;
                    if(cell.getCellTypeEnum() == CellType.STRING) {
                        rowName = ExcelExpenseType.fromString(cell.getStringCellValue().trim());
                        //System.out.println("rowName = " + rowName);
                    } else {
                        if (rowName == null) continue;
                        if (cell.getCellTypeEnum().equals(CellType.NUMERIC))
                            weightedGraph.put(
                                    LocalDate.of(2017, 4, 1),
                                    rowName,
                                    cell.getNumericCellValue());
                        else if (cell.getCellTypeEnum().equals(CellType.STRING))
                            weightedGraph.put(
                                    LocalDate.of(2017, 4, 1),
                                    rowName,
                                    Double.parseDouble(cell.getStringCellValue()));
                        else if (cell.getCellTypeEnum().equals(CellType.FORMULA))
                            weightedGraph.put(
                                    LocalDate.of(2017, 4, 1),
                                    rowName,
                                    evaluator.evaluate(cell).getNumberValue());
                    }
                }
            }

            for (Table.Cell<LocalDate, ExcelExpenseType, Double> localDateExcelExpenseTypeDoubleCell : weightedGraph.cellSet()) {
                log.debug(localDateExcelExpenseTypeDoubleCell.getColumnKey()+": "+localDateExcelExpenseTypeDoubleCell.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weightedGraph;
    }
}