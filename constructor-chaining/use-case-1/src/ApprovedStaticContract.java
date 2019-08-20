import java.util.UUID;
import java.util.Date;

public class ApprovedStaticContract{
    protected String id;
    protected String contractTypeId;
    protected String versionCode;
    protected String userAgent;
    protected String userIp;
    protected String userId;
    protected String platform;
    protected Date approvalDate;

    protected ApprovedStaticContract(String idSignature, String contractTypeId, String versionCode, String userId,
                                    String userIp, String userAgent, String platform, Date approvalDate){

        this.id =  idSignature + ":" + UUID.randomUUID().toString();
        this.approvalDate = approvalDate;
        this.userId = userId;
        this.userIp = userIp;
        this.contractTypeId = contractTypeId;
        this.platform = platform;
        this.userAgent = userAgent;
        this.versionCode = versionCode;
    }

    public ApprovedStaticContract(String contractTypeId, String versionCode, String userId,
                                 String userIp, String userAgent, String platform, Date approvalDate){

        this("ApprovedStaticContract", contractTypeId, versionCode, userId,
            userIp, userAgent, platform, approvalDate);
    }
}