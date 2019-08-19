import java.util.UUID;
import java.util.Date;

public class ApprovedDynamicContract extends ApprovedStaticContract{
    private String jsonString;
    private String searchKey;
    private String searchValue;

        public ApprovedDynamicContract(String contractTypeId, String versionCode, String userId,
                                  String userIp, String userAgent, String platform, Date approvalDate,
                                  String jsonString, String searchKey, String searchValue){
            
            super(contractTypeId, versionCode, userId, userIp, userAgent, platform, approvalDate);

            this.id = "ApprovedDynamicContract:" + UUID.randomUUID().toString();
            this.jsonString = jsonString;
            this.searchKey = searchKey;
            this.searchValue = searchValue;            
    }
}