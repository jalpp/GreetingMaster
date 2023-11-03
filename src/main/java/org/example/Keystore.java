package org.example;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

public class Keystore {

    public static String Discord = "";

    public static String mongo = "";

    public static String databaseName = "";

    public static String collectionName = "";


    public static void startAuth(){
        String keyVaultName = System.getenv("KEY_VAULT_NAME");
        String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";

        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUri)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();

        KeyVaultSecret mongopass = secretClient.getSecret("mongo");
        KeyVaultSecret discordpass = secretClient.getSecret("discord");
        KeyVaultSecret name = secretClient.getSecret("mongoname");
        KeyVaultSecret collectionname = secretClient.getSecret("coll");

        mongo = mongopass.getValue();
        Discord = discordpass.getValue();
        databaseName = name.getValue();
        collectionName = collectionname.getValue();

    }


}
