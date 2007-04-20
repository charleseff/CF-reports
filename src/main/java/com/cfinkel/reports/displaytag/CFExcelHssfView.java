package com.cfinkel.reports.displaytag;

import org.displaytag.export.BinaryExportView;
import org.displaytag.export.excel.ExcelHssfView;
import org.displaytag.model.*;
import org.displaytag.exception.BaseNestableJspTagException;
import org.displaytag.exception.SeverityEnum;
import org.displaytag.Messages;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.jsp.JspException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Calendar;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 15, 2006
 * Time: 2:44:21 PM
 * Temporarily solves Excel export incorrect date format problem
 */
public class CFExcelHssfView implements BinaryExportView {

    /**
     * TableModel to render.
     */
    private TableModel model;

    /**
     * export full list?
     */
    private boolean exportFull;

    /**
     * include header in export?
     */
    private boolean header;

    /**
     * decorate export?
     */
    private boolean decorated;

    /**
     * Generated sheet.
     */
    private HSSFSheet sheet;

    /**
     * @see org.displaytag.export.ExportView#setParameters(TableModel, boolean, boolean, boolean)
     */
    public void setParameters(TableModel tableModel, boolean exportFullList, boolean includeHeader,
                              boolean decorateValues)
    {
        this.model = tableModel;
        this.exportFull = exportFullList;
        this.header = includeHeader;
        this.decorated = decorateValues;
    }

    /**
     * @return "application/vnd.ms-excel"
     * @see org.displaytag.export.BaseExportView#getMimeType()
     */
    public String getMimeType()
    {
        return "application/vnd.ms-excel"; //$NON-NLS-1$
    }

    /**
     * @see org.displaytag.export.BinaryExportView#doExport(java.io.OutputStream)
     */
    public void doExport(OutputStream out) throws JspException
    {
        try
        {
            HSSFWorkbook wb = new HSSFWorkbook();
            sheet = wb.createSheet("-");

            int rowNum = 0;
            int colNum = 0;

            if (this.header)
            {
                // Create an header row
                HSSFRow xlsRow = sheet.createRow(rowNum++);

                HSSFCellStyle headerStyle = wb.createCellStyle();
                headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
                headerStyle.setFillBackgroundColor(HSSFColor.BLUE_GREY.index);
                HSSFFont bold = wb.createFont();
                bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                bold.setColor(HSSFColor.WHITE.index);
                headerStyle.setFont(bold);

                Iterator iterator = this.model.getHeaderCellList().iterator();

                while (iterator.hasNext())
                {
                    HeaderCell headerCell = (HeaderCell) iterator.next();

                    String columnHeader = headerCell.getTitle();

                    if (columnHeader == null)
                    {
                        columnHeader = StringUtils.capitalize(headerCell.getBeanPropertyName());
                    }

                    HSSFCell cell = xlsRow.createCell((short) colNum++);
                    cell.setCellValue(columnHeader);
                    cell.setCellStyle(headerStyle);
                    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                }
            }

            // get the correct iterator (full or partial list according to the exportFull field)
            RowIterator rowIterator = this.model.getRowIterator(this.exportFull);
            // iterator on rows

            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                HSSFRow xlsRow = sheet.createRow(rowNum++);
                colNum = 0;

                // iterator on columns
                ColumnIterator columnIterator = row.getColumnIterator(this.model.getHeaderCellList());

                while (columnIterator.hasNext())
                {
                    Column column = columnIterator.nextColumn();

                    // Get the value to be displayed for the column
                    Object value = column.getValue(this.decorated);

                    HSSFCell cell = xlsRow.createCell((short) colNum++);
                    cell.setEncoding(HSSFCell.ENCODING_UTF_16);

                    if (value instanceof Number)
                    {
                        Number num = (Number) value;
                        cell.setCellValue(num.doubleValue());
                    }
                    else if (value instanceof Calendar)
                    {
                        cell.setCellValue((Calendar) value);
                    }
                    else
                    {
                        cell.setCellValue(escapeColumnValue(value));
                    }

                }
            }
            wb.write(out);
        }
        catch (Exception e)
        {
            throw new ExcelGenerationException(e);
        }
    }

    // patch from Karsten Voges
    /**
     * Escape certain values that are not permitted in excel cells.
     * @param rawValue the object value
     * @return the escaped value
     */
    protected String escapeColumnValue(Object rawValue)
    {
        if (rawValue == null)
        {
            return null;
        }
        String returnString = ObjectUtils.toString(rawValue);
        // escape the String to get the tabs, returns, newline explicit as \t \r \n
        returnString = StringEscapeUtils.escapeJava(StringUtils.trimToEmpty(returnString));
        // remove tabs, insert four whitespaces instead
        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\t", "    ");
        // remove the return, only newline valid in excel
        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\r", " ");
        // unescape so that \n gets back to newline
        returnString = StringEscapeUtils.unescapeJava(returnString);
        return returnString;
    }

    /**
     * Wraps IText-generated exceptions.
     * @author Fabrizio Giustina
     * @version $Revision:10429 $ ($Author:charles $)
     */
    static class ExcelGenerationException extends BaseNestableJspTagException
    {

        /**
         * D1597A17A6.
         */
        private static final long serialVersionUID = 899149338534L;

        /**
         * Instantiate a new PdfGenerationException with a fixed message and the given cause.
         * @param cause Previous exception
         */
        public ExcelGenerationException(Throwable cause)
        {
            super(ExcelHssfView.class, Messages.getString("ExcelView.errorexporting"), cause); //$NON-NLS-1$
        }

        /**
         * @see org.displaytag.exception.BaseNestableJspTagException#getSeverity()
         */
        public SeverityEnum getSeverity()
        {
            return SeverityEnum.ERROR;
        }
    }

}
