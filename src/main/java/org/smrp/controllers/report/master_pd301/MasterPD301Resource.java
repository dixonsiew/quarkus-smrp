package org.smrp.controllers.report.master_pd301;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.smrp.Utils;
import org.smrp.controllers.report.Master;
import org.smrp.filters.Auth;
import org.smrp.models.Pager;
import org.smrp.services.ReportService;

import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Tag(name = "Report/MasterPD301")
@SecurityRequirement(name = "BearerAuth")
@Path("/api/master-pd301")
@Auth
public class MasterPD301Resource extends Master {
    
    private final String mongodbPrefix;
    private final String facilityCode;

    private final SecurityIdentity securityIdentity;
    private final ReactiveMongoClient mongoClient;

    @Inject
    public MasterPD301Resource(
        @ConfigProperty(name = "mongodb.prefix") String mongodbPrefix,
        @ConfigProperty(name = "facilityCode") String facilityCode,
        SecurityIdentity securityIdentity,
        ReactiveMongoClient mongoClient,
        ReportService reportService
    ) {
        super(reportService);
        this.securityIdentity = securityIdentity;
        this.mongoClient = mongoClient;
        this.mongodbPrefix = mongodbPrefix;
        this.facilityCode = facilityCode;
    }

    @GET
    @Path("/export/rpt2")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> jsonrh101(
        @QueryParam("datefrom") @DefaultValue("2023-01-01") String datefrom,
        @QueryParam("dateto") @DefaultValue("2024-01-01") String dateto
    ) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoCollection<Document> col = getCollection(username, "1");

        String[] dt1 = datefrom.split("-");
        String[] dt2 = dateto.split("-");
        String ds1 = String.format("%s%s%s", dt1[2], dt1[1], dt1[0]);
        String ds2 = String.format("%s%s%s", dt2[2], dt2[1], dt2[0]);
        String filename = String.format("%s_%s_%s_RH301.json", facilityCode, ds1, ds2);

        return col.find().collect().asList().flatMap(ls -> {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("filename", filename);
            data.put("dischargeFrom", datefrom);
            data.put("dischargeTo", dateto);
            data.put("refServiceTypeCode", "02");
            data.put("facilityCode", facilityCode);

            if (ls.isEmpty()) {
                data.put("forms", List.of());
                return sendDeferred(data, filename);
            }

            return Uni.createFrom().item(() -> Utils.processDoc(ls))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                .onItem().transformToUniAndConcatenate(this::buildFormRpt2)
                .collect().asList()
                .flatMap(forms -> {
                    data.put("forms", forms);
                    return sendDeferred(data, filename);
                });
            });
    }

    @GET
    @Path("/export/rpt1")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> jsonpd101(
        @QueryParam("datefrom") @DefaultValue("2023-01-01") String datefrom,
        @QueryParam("dateto") @DefaultValue("2024-01-01") String dateto
    ) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoCollection<Document> col = getCollection(username, "0");

        String[] dt1 = datefrom.split("-");
        String[] dt2 = dateto.split("-");
        String ds1 = String.format("%s%s%s", dt1[2], dt1[1], dt1[0]);
        String ds2 = String.format("%s%s%s", dt2[2], dt2[1], dt2[0]);
        String filename = String.format("%s_%s_%s_PD301.json", facilityCode, ds1, ds2);

        return col.find().collect().asList().flatMap(ls -> {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("filename", filename);
            data.put("dischargeFrom", datefrom);
            data.put("dischargeTo", dateto);
            data.put("refServiceTypeCode", "01");
            data.put("facilityCode", facilityCode);

            if (ls.isEmpty()) {
                data.put("forms", List.of());
                return sendDeferred(data, filename);
            }

            return Uni.createFrom().item(() -> Utils.processDoc(ls))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                .onItem().transformToUniAndConcatenate(this::buildFormRpt1)
                .collect().asList()
                .flatMap(forms -> {
                    data.put("forms", forms);
                    return sendDeferred(data, filename);
                });
            });
    }

    @GET
    @Path("/export/rpt1/xlsx")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Uni<Response> xlsx(
        @QueryParam("vt") @DefaultValue("0") String vt,
        @QueryParam("datefrom") @DefaultValue("2023-01-01") String datefrom,
        @QueryParam("dateto") @DefaultValue("2024-01-01") String dateto
    ) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoCollection<Document> col = getCollection(username, vt);

        String[] dt1 = datefrom.split("-");
        String[] dt2 = dateto.split("-");
        String ds1 = String.format("%s%s%s", dt1[2], dt1[1], dt1[0]);
        String ds2 = String.format("%s%s%s", dt2[2], dt2[1], dt2[0]);
        String pf = "0".equals(vt) ? "PD301" : "RH301";
        String filename = String.format("%s_%s_%s_%s.xlsx", facilityCode, ds1, ds2, pf);

        return col.find().collect().asList().flatMap(ls -> {
            return Uni.createFrom().item(() -> {
                try {
                    List<Document> lr = Utils.processDoc(ls);
                    byte[] bx = Utils.getXlsx(ColMap.COLUMN_MAP, lr);
                    return send(bx, filename);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
        });
    }

    @GET
    @Path("/rpt1")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> list(
        @QueryParam("_page") @DefaultValue("1") Integer page,
        @QueryParam("_limit") @DefaultValue("20") Integer limit,
        @QueryParam("vt") @DefaultValue("0") String vt,
        @QueryParam("datefrom") @DefaultValue("") String datefrom,
        @QueryParam("dateto") @DefaultValue("") String dateto
    ) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoDatabase db = getDb(vt);
        ReactiveMongoCollection<Document> col = db.getCollection(String.format("__%s__", username));
        ReactiveMongoCollection<Document> col2 = db.getCollection(String.format("__%s-q__", username));
        return col.countDocuments().flatMap(total -> {
            return col2.find().collect().asList().flatMap(ld -> {
                final String dateFrom = !ld.isEmpty() ? ld.get(0).getString("datefrom") : datefrom;
                final String dateTo = !ld.isEmpty() ? ld.get(0).getString("dateto") : dateto;

                Pager pg = Pager.from(total, page, limit);
                FindOptions options = new FindOptions()
                        .skip(pg.getLowerBound())
                        .limit(pg.getPageSize());

                return col.find(options).collect().asList().flatMap(ls -> {
                    return Uni.createFrom().item(() -> {
                        List<Document> lr = Utils.processDoc(ls);
                        Map<String, Object> result = new LinkedHashMap<>();
                        result.put("columnmaps", ColMap.COLUMN_MAP);
                        result.put("total_count", total);
                        result.put("total_page", pg.getTotalPages());
                        result.put("page", pg.getPageNum());
                        result.put("data", lr);
                        result.put("datefrom", dateFrom);
                        result.put("dateto", dateTo);
                        return Response.ok(result).build();
                    }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
                });
            });
        });
    }

    @GET
    @Path("/rpt1/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> edit(@PathParam("id") String id, @QueryParam("vt") @DefaultValue("0") String vt) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoCollection<Document> col = getCollection(username, vt);
        try {
            ObjectId objectId = new ObjectId(id);
            return col.find(new Document("_id", objectId)).collect().first().flatMap(doc -> {
                if (doc == null) {
                    return Uni.createFrom().failure(new NotFoundException("Record not found"));
                }

                doc.put("_id", doc.getObjectId("_id").toString());
                return Uni.createFrom().item(Response.ok(doc).build());
            }).onItem().ifNull().switchTo(Uni.createFrom().failure(new NotFoundException("Record not found")));
        } catch (Exception e) {
            return Uni.createFrom().failure(new NotFoundException("Record not found"));
        }
    }

    @PUT
    @Path("/rpt1/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> update(@PathParam("id") String id, @QueryParam("vt") @DefaultValue("0") String vt, Document data) {
        String username = securityIdentity.getPrincipal().getName();
        ReactiveMongoCollection<Document> col = getCollection(username, vt);
        ObjectId objectId = new ObjectId(id);
        Document filter = new Document("_id", objectId);
        data.put("_id", objectId);
        Document updateData = new Document("$set", data);
        return col.findOneAndUpdate(filter, updateData).flatMap(x -> {
            HashMap<String, Object> mx = new HashMap<>();
            mx.put("success", 1);
            return Uni.createFrom().item(Response.ok(mx).build());
        });
    }

    private Uni<Map<String, Object>> buildFormRpt2(Document doc) {
        return getData(doc).map(md -> {
            Map<String, Object> person = new LinkedHashMap<>();
            person.put("refPersonTitleCode", md.get("refPersonTitleCode"));
            person.put("fullName", Utils.getStr(doc.get("PATIENT_NAME")));
            person.put("refIdentificationTypeCode", md.get("refIdentificationTypeCode"));
            person.put("identificationNo", Utils.getStr(doc.get("DOCUMENT_NUMBER")));
            person.put("refAddressTypeCode", "C");
            person.put("street1", Utils.getStr(doc.get("STREET1")));
            person.put("street2", Utils.getStr(doc.get("STREET2")));
            person.put("refCityCode", md.get("refCityCode"));
            person.put("refPostCode", Utils.getStr(doc.get("POSTCODE")));
            person.put("refStateCode", md.get("refStateCode"));
            person.put("refCountryCode", md.get("refCitizenshipCode"));
            person.put("refContactTypeCode", "02");
            person.put("contactInfo", Utils.getStr(doc.get("HOME_PHONE")));

            Map<String, Object> nok = new LinkedHashMap<>();
            nok.put("refPersonTitleCode", md.get("refPersonTitleCode"));
            nok.put("fullName", Utils.getStr(doc.get("PATIENT_NOK_NAME")));
            nok.put("refIdentificationTypeCode", md.get("refIdentificationTypeCodeNOK"));
            nok.put("identificationNo", Utils.getStr(doc.get("NOK_ID")));
            nok.put("refAddressTypeCode", "C");
            nok.put("street1", Utils.getStr(doc.get("NOK_STREET1")));
            nok.put("street2", Utils.getStr(doc.get("NOK_STREET2")));
            nok.put("refCityCode", md.get("refCityCodeNOK"));
            nok.put("refPostCode", Utils.getStr(doc.get("NOK_POSTCODE")));
            nok.put("refStateCode", md.get("refStateCodeNOK"));
            nok.put("refCountryCode", md.get("refCitizenshipCodeNOK"));
            nok.put("refContactTypeCode", "02");
            nok.put("contactInfo", Utils.getStr(doc.get("NOK_MOBILE_PHONE")));

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("rn", Utils.getStr(doc.get("ACCOUNT_NO")));
            m.put("mrn", Utils.getStr(doc.get("PRN")));
            m.put("eventDate", String.format("%s %s:00",
                    doc.get("REGISTRATION_DATE"),
                    doc.get("REGISTRATION_TIME")));
            m.put("isPoliceCase", "00");
            m.put("internalReferral", "false");
            m.put("refReferralSourceCode", md.get("refReferralSourceCode"));
            m.put("refGenderCode", md.get("refGenderCode"));
            m.put("dob", Utils.getStr(doc.get("DOB")));
            m.put("refMaritalStatusCode", md.get("refMaritalStatusCode"));
            m.put("refReligionCode", md.get("refReligionCode"));
            m.put("refCitizenshipCode", md.get("refCitizenshipCode"));
            m.put("refEthnicCode", md.get("refEthnicCode"));
            m.put("height", Utils.getNum(Utils.getStr(doc.get("HEIGHT"))));
            m.put("weight", Utils.getNum(Utils.getStr(doc.get("WEIGHT"))));
            m.put("refForeignerOriginCountryCode", md.get("refForeignerOriginCountryCode"));
            m.put("refForeignerResidenceCountryCode", md.get("refForeignerResidenceCountryCode"));
            m.put("refPersonCategoryCode", md.get("refPersonCategoryCode"));
            m.put("refRelationshipCode", md.get("refRelationshipCode"));
            m.put("totalDurationDay", "0");
            m.put("admissionDate", String.format("%s %s:00",
                    doc.get("ADMISSION_DATE"),
                    doc.get("ADMISSION_TIME")));
            m.put("refDischargeTypeCode", md.get("refDischargeTypeCode"));
            m.put("dischargeDateTime", String.format("%s %s:00", 
                    doc.get("DISCHARGE_DATE"),
                    doc.get("DISCHARGE_TIME")));
            m.put("refDischargeOfficerTypeCode", "02");
            m.put("mmc", "00");
            m.put("refDiagnosisItemTypeCode", md.get("refDiagnosisItemTypeCode"));
            m.put("description", Utils.getStr(doc.get("ICD10_DESCRIPTION")));
            m.put("refIcd10Main", Utils.getStr(doc.get("ICD10_CODE")));
            m.put("person", person);
            m.put("nextOfKins", nok);

            return m;
        });
    }

    private Uni<Map<String, Object>> buildFormRpt1(Document doc) {
        return getData(doc).map(md -> {
            Map<String, Object> person = new LinkedHashMap<>();
            person.put("refPersonTitleCode", md.get("refPersonTitleCode"));
            person.put("fullName", Utils.getStr(doc.get("PATIENT_NAME")));
            person.put("refIdentificationTypeCode", md.get("refIdentificationTypeCode"));
            person.put("identificationNo", Utils.getStr(doc.get("DOCUMENT_NUMBER")));
            person.put("refAddressTypeCode", "C");
            person.put("street1", Utils.getStr(doc.get("STREET1")));
            person.put("street2", Utils.getStr(doc.get("STREET2")));
            person.put("refCityCode", md.get("refCityCode"));
            person.put("refPostCode", Utils.getStr(doc.get("POSTCODE")));
            person.put("refStateCode", md.get("refStateCode"));
            person.put("refCountryCode", md.get("refCitizenshipCode"));
            person.put("refContactTypeCode", "02");
            person.put("contactInfo", Utils.getStr(doc.get("HOME_PHONE")));

            Map<String, Object> nok = new LinkedHashMap<>();
            nok.put("refPersonTitleCode", md.get("refPersonTitleCodeNOK"));
            nok.put("fullName", Utils.getStr(doc.get("PATIENT_NOK_NAME")));
            nok.put("refIdentificationTypeCode", md.get("refIdentificationTypeCodeNOK"));
            nok.put("identificationNo", Utils.getStr(doc.get("NOK_ID")));
            nok.put("refAddressTypeCode", "C");
            nok.put("street1", Utils.getStr(doc.get("NOK_STREET1")));
            nok.put("street2", Utils.getStr(doc.get("NOK_STREET2")));
            nok.put("refCityCode", md.get("refCityCodeNOK"));
            nok.put("refPostCode", Utils.getStr(doc.get("NOK_POSTCODE")));
            nok.put("refStateCode", md.get("refStateCodeNOK"));
            nok.put("refCountryCode", md.get("refCitizenshipCodeNOK"));
            nok.put("refContactTypeCode", "02");
            nok.put("contactInfo", Utils.getStr(doc.get("NOK_MOBILE_PHONE")));

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("rn", Utils.getStr(doc.get("ACCOUNT_NO")));
            m.put("mrn", Utils.getStr(doc.get("PRN")));
            m.put("eventDate", String.format("%s %s:00", 
                    doc.get("REGISTRATION_DATE"),
                    doc.get("REGISTRATION_TIME")));
            m.put("isPoliceCase", "02");
            m.put("internalReferral", "false");
            m.put("refReferralSourceCode", md.get("refReferralSourceCode"));
            m.put("refGenderCode", md.get("refGenderCode"));
            m.put("dob", Utils.getStr(doc.get("DOB")));
            m.put("refMaritalStatusCode", md.get("refMaritalStatusCode"));
            m.put("refReligionCode", md.get("refReligionCode"));
            m.put("refCitizenshipCode", md.get("refCitizenshipCode"));
            m.put("refEthnicCode", md.get("refEthnicCode"));
            m.put("height", Utils.getNum(Utils.getStr(doc.get("HEIGHT"))));
            m.put("weight", Utils.getNum(Utils.getStr(doc.get("WEIGHT"))));
            m.put("refForeignerOriginCountryCode", md.get("refForeignerOriginCountryCode"));
            m.put("refForeignerResidenceCountryCode", md.get("refForeignerResidenceCountryCode"));
            m.put("refPersonCategoryCode", md.get("refPersonCategoryCode"));
            m.put("refRelationshipCode", md.get("refRelationshipCode"));
            m.put("totalDurationDay", "0");
            m.put("refWardTransitionTypeCode", "A");
            m.put("wardDateTime", String.format("%s %s:00", 
                    doc.get("ADMISSION_DATE"),
                    doc.get("ADMISSION_TIME")));
            m.put("wardCode", Utils.getStr(doc.get("WARD_NO")));
            m.put("refDisciplineCode", md.get("refDisciplineCode"));
            m.put("refSpecialityCode", md.get("refDisciplineCode"));
            m.put("refSubSpecialityCode", md.get("refDisciplineCode"));
            m.put("refWardClassCode", md.get("refWardClassCode"));
            m.put("refWardCategoryCode", "00");
            m.put("refDischargeTypeCode", md.get("refDischargeTypeCode"));
            m.put("dischargeDateTime", String.format("%s %s:00", 
                    doc.get("DISCHARGE_DATE"),
                    doc.get("DISCHARGE_TIME")));
            m.put("refDischargeOfficerTypeCode", "02");
            m.put("mmc", "00");
            m.put("refDiagnosisItemTypeCode", md.get("refDiagnosisItemTypeCode"));
            m.put("description", Utils.getStr(doc.get("ICD10_DESCRIPTION")));
            m.put("refIcd10Main", Utils.getStr(doc.get("ICD10_CODE")));
            m.put("person", person);
            m.put("nextOfKins", nok);

            return m;
        });
    }

    private ReactiveMongoCollection<Document> getCollection(String username, String vt) {
        String s = String.format("__%s__", username);
        ReactiveMongoDatabase db = getDb(vt);
        return db.getCollection(s);
    }

    private ReactiveMongoDatabase getDb(String vt) {
        String suffix = "prod".equals(mongodbPrefix) ? "_prod" : "";
        ReactiveMongoDatabase db;
        if ("0".equals(vt)) {
            String s = String.format("master_pd301%s", suffix);
            db = mongoClient.getDatabase(s);
        } else {
            String s = String.format("master_rh301%s", suffix);
            db = mongoClient.getDatabase(s);
        }

        return db;
    }
}
