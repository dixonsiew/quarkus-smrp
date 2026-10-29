package org.smrp.controllers.report;

import java.util.Map;

import org.bson.Document;
import org.smrp.services.ReportService;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


public class Master {

    private final ReportService reportService;

    public Master(ReportService reportService) {
        this.reportService = reportService;
    }

    protected Response send(Map<String, Object> data, String filename) {
        return Response.ok(data)
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .header("filename", filename)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .build();
    }

    protected Response send(byte[] data, String filename) {
        return Response.ok(data)
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .header("filename", filename)
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .build();
    }
    
    protected Uni<Map<String, String>> getData(Document doc) {
        Uni<String> refPersonTitleCode = reportService.refPersonTitleCode(doc);
        Uni<String> refIdentificationTypeCode = reportService.refIdentificationTypeCode(doc);
        Uni<String> refCityCode = reportService.refCityCode(doc);
        Uni<String> refStateCode = reportService.refStateCode(doc);
        Uni<String> refCountryCode = reportService.refCitizenshipCode(doc);

        Uni<String> refPersonTitleCodeNOK = reportService.refPersonTitleCodeNOK(doc);
        Uni<String> refIdentificationTypeCodeNOK = reportService.refIdentificationTypeCodeNOK(doc);
        Uni<String> refCityCodeNOK = reportService.refCityCodeNOK(doc);
        Uni<String> refStateCodeNOK = reportService.refStateCodeNOK(doc);
        Uni<String> refCitizenshipCodeNOK = reportService.refCitizenshipCodeNOK(doc);

        Uni<String> refReferralSourceCode = reportService.refReferralSourceCode(doc);
        Uni<String> refGenderCode = reportService.refGenderCode(doc);
        Uni<String> refMaritalStatusCode = reportService.refMaritalStatusCode(doc);
        Uni<String> refReligionCode = reportService.refReligionCode(doc);
        Uni<String> refCitizenshipCode = reportService.refCitizenshipCode(doc);

        Uni<String> refEthnicCode = reportService.refEthnicCode(doc);
        Uni<String> refForeignerOriginCountryCode = reportService.refForeignerOriginCountryCode(doc);
        Uni<String> refForeignerResidenceCountryCode = reportService.refForeignerResidenceCountryCode(doc);
        Uni<String> refPersonCategoryCode = reportService.refPersonCategoryCode(doc);
        Uni<String> refRelationshipCode = reportService.refRelationshipCode(doc);

        Uni<String> refDisciplineCode = reportService.refDisciplineCode(doc);
        Uni<String> refDisciplineCode1 = reportService.refDisciplineCode1(doc);
        Uni<String> refWardClassCode = reportService.refWardClassCode(doc);

        return Uni.combine().all().unis(
            refPersonTitleCode, refIdentificationTypeCode, refCityCode, refStateCode, refCountryCode,
            refPersonTitleCodeNOK, refIdentificationTypeCodeNOK, refCityCodeNOK, refStateCodeNOK, refCitizenshipCodeNOK,
            refReferralSourceCode, refGenderCode, refMaritalStatusCode, refReligionCode, refCitizenshipCode,
            refEthnicCode, refForeignerOriginCountryCode, refForeignerResidenceCountryCode, refPersonCategoryCode, refRelationshipCode,
            refDisciplineCode, refDisciplineCode1, refWardClassCode).with(list -> {
                Map<String, String> m = Map.ofEntries(
                    Map.entry("refPersonTitleCode", list.get(0).toString()),
                    Map.entry("refIdentificationTypeCode", list.get(1).toString()),
                    Map.entry("refCityCode", list.get(2).toString()),
                    Map.entry("refStateCode", list.get(3).toString()),
                    Map.entry("refCountryCode", list.get(4).toString()),

                    Map.entry("refPersonTitleCodeNOK", list.get(5).toString()),
                    Map.entry("refIdentificationTypeCodeNOK", list.get(6).toString()),
                    Map.entry("refCityCodeNOK", list.get(7).toString()),
                    Map.entry("refStateCodeNOK", list.get(8).toString()),
                    Map.entry("refCitizenshipCodeNOK", list.get(9).toString()),

                    Map.entry("refReferralSourceCode", list.get(10).toString()),
                    Map.entry("refGenderCode", list.get(11).toString()),
                    Map.entry("refMaritalStatusCode", list.get(12).toString()),
                    Map.entry("refReligionCode", list.get(13).toString()),
                    Map.entry("refCitizenshipCode", list.get(14).toString()),

                    Map.entry("refEthnicCode", list.get(15).toString()),
                    Map.entry("refForeignerOriginCountryCode", list.get(16).toString()),
                    Map.entry("refForeignerResidenceCountryCode", list.get(17).toString()),
                    Map.entry("refPersonCategoryCode", list.get(18).toString()),
                    Map.entry("refRelationshipCode", list.get(19).toString()),

                    Map.entry("refDisciplineCode", list.get(20).toString()),
                    Map.entry("refDisciplineCode1", list.get(21).toString()),
                    Map.entry("refWardClassCode", list.get(22).toString())
                );

                return m;
            });
    }
}
