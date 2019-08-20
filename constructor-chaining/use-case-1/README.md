Problem Hakkında Biraz Detay

NOSQL (Couchbase) veritabanının aynı bucket'ı içerisine birden fazla çeşitli döküman kaydediliyor. Veritabanını açıp incelediğimiz zaman hangi dökümanının hangi entity'e ait oldukları kolayca anlaşılmıyor. Buna çözüm olarak dökümanların id'lerinin başlarına ait oldukları entity tipinin adının yazılmasına karar veriliyor. Yani ClassName:Id seklinde oluyor bir dökümanının id'si. ClassName: kısmının (bu kısma prefix diyeceğiz) clienta gösterilmemesi gerekli. Bunun içeride halledilmesi gerekli.

İki tip entitymiz var: ApprovedStaticContract ve ApprovedDynamicContract.
Id ile ilgili işlemlerin entity classlarında yapılması gerekli. Diğer class'lar bu entityleri yaratırken ya da kullanırken onlara id'nin nasıl üretildiği ne şekilde olduğu gibi bilgiler gerekli değil. Nasıl üretildiğini bilmelerine gerek yok ve id'leri üretmek de diğer sınıfların sorumluluğunda değil. Diğer sınıflara göre id'ler rastgele(!) üretiliyor. Dolayısıyla bu diğer sınıflar bu entity sınıflarını yaratırlarken id alanını set etmeyecek, parametre olarak bu alanı değil diğer gerekli alanları gönderecek.

Entity Sınıflarımız

    ApprovedStaticContract.java

   public class ApprovedStaticContract {
       protected String id;
       protected String contractTypeId;
       protected String versionCode;
       protected String userAgent;
       protected String userIp;
       protected String userId;
       protected String platform;
       protected Date approvalDate;
   }

    ApprovedDynamicContract.java

   public class ApprovedDynamicContract extends ApprovedStaticContract {
      private String jsonData;
      private String searchKey;
      private String searchValue;

   }

Adım - 1 : Constructorların oluşturulması

Aşağıdaki constructlar diğer sınıfların bu ilgili entityleri yaratabilmesi için kullanmasına izin verdiğimiz constructorlardır.

    ApprovedStaticContract.java

    public ApprovedStaticContract(String contractTypeId, String versionCode, String userId,          
                                  String userIp, String userAgent, String platform, Date approvalDate){

        this.id = "ApprovedStaticContract:" + UUID.randomUUID().toString();
        this.approvalDate = approvalDate;
        this.userId = userId;
        this.userIp = userIp;
        this.contractTypeId = contractTypeId;
        this.platform = platform;
        this.userAgent = userAgent;
        this.versionCode = versionCode;
    }

    ApprovedDynamicContract.java
    
    public ApprovedDynamicContract(String contractTypeId, String versionCode, String userId,
                                  String userIp, String userAgent, String platform, Date approvalDate,
                                  String jsonData, String searchKey, String searchValue){

        super(contractTypeId, versionCode, userId, userIp, userAgent, platform, approvalDate);
        
        this.id = "ApprovedDynamicContract:" + UUID.randomUUID().toString();
        this.jsonData = jsonData;
        this.searchKey = searchKey;
        this.searchValue = searchValue;
    }

Id'ye değer atama işlemi iki yerde de yapılmış oluyor DynamicContract için. Aslında bunların aralarındaki tek fark prefixleri yoksa id'lerinin üretilme mantığı aynı. Ve halihazırda parent class'ta setlenme işlemi yapılmakta. Bizim yapmamız gereken bu prefix'i parametrik hale getirerek istediğimiz bir şekilde id'nin setlenmesini sağlamak. By sayede hem kodtaki tekrardan da kurtulmuş olacağız.

Adım - 2 : Prefix'in Parametreleştirilmesi 

    ApprovedStaticContract.java

    public ApprovedStaticContract(String idSignature, String contractTypeId, String versionCode, String userId,
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

    ApprovedDynamicContract.java

    public ApprovedDynamicContract(String contractTypeId, String versionCode, String userId,
                                    String userIp, String userAgent, String platform, Date approvalDate,
                                    String searchKey, String searchValue, String jsonData){

        super("ApprovedDynamicContract",contractTypeId, versionCode, userId, userIp, userAgent, platform, approvalDate);

        this.searchKey = searchKey;
        this.searchValue = searchValue;
        this.jsonData = jsonData;
     }

Evet bu şekilde yaptığımız zaman işimiz çözülmüş gibi duruyor. Ama bu seferde en başta bahsetmiş olduğumuz şu problemle karşılıyoruz: bu entityleri yaratacak olan sınıfların id'lerin nasıl yaratıldıklarından haberdar olmaları gerekiyor. Bir nevi bu işi onların sorumluluğuna bırakmış oluyoruz. Bu bizim istemiş olduğumuz bir çözüm şekli değil dolayısıyla çünkü id üretilme işinin kontrolünün tamamen entity class'larında olması gerekiyor.

Onun yerine iki constructor yaratsak ? Bir tanesi disaridan id'yi alsın parametre olarak. Bir digeri ise bu objeyi yaratan nesneden gerekli field'ları alsın. Access modifier olarak protected secmemizin sebebi ise Dynamic contract'ın parent'a ait olan constructorları cagirabilmesi. Eger private yaparsak child class'lar cagiramaz o constructorı ve bizim de o field'ları child icinde set etmemiz gerekir bu da kod tekrarına neden olacaktır.

Adım - 3 : Parent Class'a İkinci Constructorın Eklenmesi

    ApprovedStaticContract.java

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


    ApprovedDynamicContract.java

    public ApprovedDynamicContract(String contractTypeId, String versionCode, String userId,
                                   String userIp, String userAgent, String platform, Date approvalDate,
                                   String searchKey, String searchValue, String jsonData){

        super("ApprovedDynamicContract",contractTypeId, versionCode, userId, userIp, userAgent, platform, approvalDate);

        this.searchKey = searchKey;
        this.searchValue = searchValue;
        this.jsonData = jsonData;
    }

ApprovedStaticContract constructor chaining yaparak id üretilmesi ile ilgili işlemlerin kontrolünü tekrar üstüne almış oldu. Bu entityleri yaratan sınıfların artık id'nin üretilme işlemi ile ilgilenmesine, nasıl olduğunu bilmesine gerek kalmadı.Sadece bir yerlerden topladıkları dataları parametre olarak bu entity classlarının construclarına geçiyorlar ve nesnenin üretilmesini sağlıyorlar.