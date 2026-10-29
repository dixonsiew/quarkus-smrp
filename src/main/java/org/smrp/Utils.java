package org.smrp;

import org.smrp.models.ColumnMap;
import org.bson.Document;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.BsonDateTime;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class Utils {

    private static final DateTimeFormatter OUT_FMT    = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter SLASH_FMT  = DateTimeFormatter.ofPattern("d/M/uuuu");
    private static final DateTimeFormatter DASH_FMT   = DateTimeFormatter.ofPattern("d-M-uuuu");

    public static String getDateStr(Object v) {
        String o;

        if (v instanceof BsonDateTime) {
            o = getStr(v);
            try {
                long iv = Long.parseLong(o);
                LocalDate d = Instant.ofEpochMilli(iv)
                                     .atZone(ZoneOffset.UTC)
                                     .toLocalDate();
                o = d.format(OUT_FMT);
            } catch (NumberFormatException e) {
                // Go ignores the Atoi error too — leave o as-is
            }
        } else {
            o = getStr(v);
        }

        String s = o;

        if (o.length() >= 10) {
            int i = s.indexOf('/');
            if (i > 0) {
                s = s.split(" ")[0];
                s = tryFormat(s, SLASH_FMT, o);
            } else {
                i = s.indexOf('-');
                if (i > 0) {
                    s = s.split(" ")[0];
                    s = tryFormat(s, DASH_FMT, o);
                }
            }
            s = s.split(" ")[0];
        } else {
            int i = s.indexOf('/');
            if (i > 0) {
                s = s.split(" ")[0];
                s = tryFormat(s, SLASH_FMT, o);
            } else {
                i = s.indexOf('-');
                if (i > 0) {
                    s = s.split(" ")[0];
                    s = tryFormat(s, DASH_FMT, o);
                }
            }
            s = s.split(" ")[0];
        }

        return s;
    }

    public static void setValue(Document x, String ofield, String srcField) {
        Object srcRaw = x.get(srcField);
        String v = srcRaw == null ? "" : srcRaw.toString();

        if (x.containsKey(ofield)) {
            Object ofieldRaw = x.get(ofield);
            String s = ofieldRaw == null ? "" : ofieldRaw.toString();
            if ("N/A".equals(s)) {
                x.put(ofield, v);
            }
        } else {
            x.put(ofield, v);
        }

        if ("undefined".equals(x.get(ofield))) {
            x.put(ofield, "N/A");
        }
    }

    public static String getStr(Object a) {
        if (a == null) return "";
        return String.valueOf(a);
    }

    public static long getNumber(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException | NullPointerException e) {
            return 0L;
        }
    }

    public static double getNum(String s) {
        if (s == null) return 0.0;
        String r = s.replaceAll("[^\\d.]*", "");
        try {
            return Double.parseDouble(r);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static byte[] getXlsx(List<ColumnMap> colmaps, List<Document> lx) throws Exception {
        Workbook wb = new XSSFWorkbook();
        ByteArrayOutputStream bx = new ByteArrayOutputStream();

        try {
            Sheet sh = wb.createSheet("Sheet1");

            // Bold style for header row
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);

            int colOffset = 4;

            // Header row
            for (int i = 0; i < colmaps.size(); i++) {
                // int j = i + 1; // 1-based, not used directly in POI but kept for width logic
                ColumnMap cx = colmaps.get(i);

                Row headerRow = sh.getRow(0);
                if (headerRow == null) headerRow = sh.createRow(0);

                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cx.getText());
                cell.setCellStyle(style);

                int n = cx.getText().length();
                sh.setColumnWidth(i, Math.min((n + colOffset) * 256, 255 * 256));
            }

            int k = 1; // (row 1 = header)
            for (Document x : lx) {
                Row row = sh.createRow(k); // POI rows are 0-based
                for (int i = 0; i < colmaps.size(); i++) {
                    ColumnMap cx = colmaps.get(i);
                    String field = cx.getField();

                    String s = "";
                    Object val = x.get(field);
                    if (val != null) {
                        s = getStr(val);
                    }

                    Cell cell = row.createCell(i);
                    cell.setCellValue(s);

                    int n = s.length();
                    int currentWidth = sh.getColumnWidth(i); // in 1/256 char units
                    int currentChars = currentWidth / 256;
                    if (n > (currentChars - colOffset)) {
                        sh.setColumnWidth(i, Math.min((n + colOffset) * 256, 255 * 256));
                    }
                }
                k++;
            }

            // for (int i = 0; i < colmaps.size(); i++) {
            //     sh.autoSizeColumn(i);
            // }

            wb.write(bx);
            return bx.toByteArray();
        }

        finally {
            try {
                if (wb != null) {
                    wb.close();
                }
                if (bx != null) {
                    bx.close();
                }
            } catch (Exception e) {
                
            }
        }
    }

    public static List<Document> processDoc(List<Document> lx) {
        List<Document> ls = new ArrayList<>();
        String na = "N/A";

        for (Document x : lx) {
            x.put("_id", x.getObjectId("_id").toString());
            if (x.containsKey("ADMISSION_DATE")) {
                x.put("ADMISSION_DATE", getDateStr(x.get("ADMISSION_DATE")));
            }

            if (x.containsKey("DISCHARGE_DATE")) {
                x.put("DISCHARGE_DATE", getDateStr(x.get("DISCHARGE_DATE")));
            }

            if (x.containsKey("DEATH_DATE")) {
                x.put("DEATH_DATE", getDateStr(x.get("DEATH_DATE")));
            }

            if (x.containsKey("DELIVERY_DATE")) {
                x.put("DELIVERY_DATE", getDateStr(x.get("DELIVERY_DATE")));
            }

            if (x.containsKey("PATIENT_NOK_NAME")) {
                Object v = x.get("PATIENT_NOK_NAME");
                if (na.equals(v)) {
                    x.put("NOK_STREET1", na);
                    x.put("NOK_STREET2", na);
                    x.put("NOK_CITYCODE", na);
                    x.put("NOK_POSTCODE", na);
                    x.put("NOK_OCITY", na);
                    x.put("NOK_NATIONALITY", na);
                } else {
                    setValue(x, "NOK_STREET1", "STREET1");
                    setValue(x, "NOK_STREET2", "STREET2");
                    setValue(x, "NOK_CITYCODE", "CITYCODE");
                    setValue(x, "NOK_POSTCODE", "POSTCODE");
                    setValue(x, "NOK_OCITY", "OCITY");
                    setValue(x, "NOK_NATIONALITY", "NATIONALITY");
                }
            } else {
                x.put("NOK_STREET1", na);
                x.put("NOK_STREET2", na);
                x.put("NOK_CITYCODE", na);
                x.put("NOK_POSTCODE", na);
                x.put("NOK_OCITY", na);
                x.put("NOK_NATIONALITY", na);
            }

            ls.add(x);
        }

        return ls;
    }

    private static String tryFormat(String input, DateTimeFormatter fmt, String fallback) {
        try {
            return LocalDate.parse(input, fmt).format(OUT_FMT);
        } catch (DateTimeParseException e) {
            return fallback;
        }
    }
}
