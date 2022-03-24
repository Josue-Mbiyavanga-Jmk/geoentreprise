package com.iduyatech.geoentreprise.Memory;


public class Parameters {


    //SERVER DE PRODUCTION
    private static final String BASE_URL = Config.ConfigSystem.SERV_PROD_HOST;
    public static String BASE_URL_PORT = Config.ConfigSystem.SERV_PROD_PORT;

    public static final int VOLLEY_RETRY_POLICY = 30000;

    /// URL ROOT
    public static final String URL_REQUEST_CATEGORIE =  "categorie/";
    public static final String URL_REQUEST_STATION =  "station/";
    public static final String URL_REQUEST_PRODUIT =  "produit/";
    public static final String URL_REQUEST_ENTREPRISE =  "entreprise/";
    public static final String URL_REQUEST_LOAD =  "load/";
    /// URL FEUILLE
    public static final String V1_LOGIN =  "v1/authentification";
    public static final String V1_GETALL =  "v1/getAll";
    public static final String V1_ADD =  "v1/add";
    public static final String V1_UPDATE =  "v1/update";
    public static final String V1_ADD_CATEGORIE =  "v1/addCategorie";
    public static final String V1_GET_ALL_MIN_DATA =  "v1/getAllMinimalData";
    public static final String V1_GET_ALL_MAX_DATA =  "v1/getAllMaximalData";
    public static final String V1_GET_ALL_ENTREPRISE =  "v1/getAllEntreprise";


    public static class Config {


        public static class ConfigSystem {
            //LOCALHOST
            static final String LOCAL_HOST = "http://10.195.48.111/";
            static String LOCAL_HOST_PORT = "http://10.195.48.111:80/";


            static final String SERV_PROD_HOST = "http://vps63737.lws-hosting.com/";
            static final String SERV_PROD_PORT = "http://vps63737.lws-hosting.com:8090/";


            public  static String IP(String ip, String port, String protole)
            {
                return   LOCAL_HOST_PORT=protole+"://"+ip+":"+port+"/";
            }
        }


        //FTP DIRECTORY
        public static class DirectoryFTP {

        }
        //URL SERVICES
        public static class URL_SERVICE {
            public static final String URL_SERVICE_HOST = "dgrhu_ws/api/services/";

        }
    }



}