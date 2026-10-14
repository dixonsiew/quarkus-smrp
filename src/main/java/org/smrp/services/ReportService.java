package org.smrp.services;

import org.bson.Document;
import org.smrp.Constants;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class ReportService {

    private final CommonSetupService commonSetupService;
    
    public ReportService(CommonSetupService commonSetupService) {
        this.commonSetupService = commonSetupService;
    }

    public Uni<String> refReferralSourceCode(Document doc) {
        return getCode("REFERRAL", doc, "referral");
    }

    public Uni<String> refPersonTitleCode(Document doc) {
        return getCode("TITLE", doc, "title");
    }

    public Uni<String> refGenderCode(Document doc) {
        return getCode("GENDER", doc, "gender");
    }

    public Uni<String> refGenderCode1(Document doc) {
        return getCode("CHILD_SEX", doc, "gender");
    }

    public Uni<String> refMaritalStatusCode(Document doc) {
        return getCode("MARITAL_STATUS", doc, "marital_status");
    }

    public Uni<String> refReligionCode(Document doc) {
        return getCode("RELIGION", doc, "religion");
    }

    public Uni<String> refCitizenshipCode(Document doc) {
        return getCode("NATIONALITY", doc, "country");
    }

    public Uni<String> refCitizenshipCodeNOK(Document doc) {
        return getCode("NOK_NATIONALITY", doc, "country");
    }

    public Uni<String> refEthnicCode(Document doc) {
        return getCode("ETHNIC_GROUP", doc, "ethnic_group");
    }

    public Uni<String> refForeignerOriginCountryCode(Document doc) {
        return getCode("COUNTRY_OF_BIRTH", doc, "country");
    }

    public Uni<String> refForeignerResidenceCountryCode(Document doc) {
        return getCode("REFFOREIGNRCOUNTRYCODE", doc, "country");
    }

    public Uni<String> refPersonCategoryCode(Document doc) {
        return getCode("REFPERSONCATEGORYCODE", doc, "person_category_code");
    }

    public Uni<String> refIdentificationTypeCode(Document doc) {
        return getCode("DOCUMENT_TYPE", doc, "id_type");
    }

    public Uni<String> refCityCode(Document doc) {
        return getCode("CITYCODE", doc, "city");
    }

    public Uni<String> refCityCodeNOK(Document doc) {
        return getCode("NOK_CITYCODE", doc, "city");
    }

    public Uni<String> refStateCode(Document doc) {
        return getCode("OCITY", doc, "state");
    }

    public Uni<String> refStateCodeNOK(Document doc) {
        return getCode("NOK_OCITY", doc, "state");
    }

    public Uni<String> refPersonTitleCodeNOK(Document doc) {
        return getCode("NOK_TITLE", doc, "title");
    }

    public Uni<String> refRelationshipCode(Document doc) {
        return getCode("RELATION_DESCRIPTION", doc, "relationship");
    }

    public Uni<String> refIdentificationTypeCodeNOK(Document doc) {
        return getCode("NOK_ID_TYPE", doc, "id_type");
    }

    public Uni<String> refDisciplineCode(Document doc) {
        return getCode("PRIMARY_SPECIALITY", doc, "speciality");
    }

    public Uni<String> refDisciplineCode1(Document doc) {
        return getCode("PRIMARY_SPECIALTY", doc, "speciality");
    }

    public Uni<String> refWardClassCode(Document doc) {
        return getCode("PAYMENT_CLASS_CODE", doc, "ward_class");
    }

    public Uni<String> refDischargeTypeCode(Document doc) {
        return getCode("DISCHARGE_REASON", doc, "discharge_type");
    }

    public Uni<String> refDiagnosisItemTypeCode(Document doc) {
        return getCode("DIAGNOSIS_DESC", doc, "diag_item_type");
    }

    public Uni<String> refLabourModeCode(Document doc) {
        return getCode("DELIVERY_TYPE", doc, "delivery_type");
    }

    public Uni<String> getCode(String key, Document doc, String table) {
        String x = Constants.NO_INFO;
        String s = get(key, doc);
        if (!"".equals(s)) {
            return commonSetupService.findByDesc(s, table).flatMap(o -> {
                if (o != null) {
                    return Uni.createFrom().item(o.getCode());
                }
                return Uni.createFrom().item(x);
            });
        }

        return Uni.createFrom().item(x);
    }

    public String get(String key, Document doc) {
        String s = "";
        Object v = doc.get(key);
        if (v != null) {
            s = String.valueOf(v);
            s = s.trim();
        }
        return s;
    }
}
